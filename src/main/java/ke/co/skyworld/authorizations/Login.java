package ke.co.skyworld.authorizations;

import com.google.common.hash.Hashing;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.ApiResponse;
import ke.co.skyworld.ExchangeUtils;
import ke.co.skyworld.XmlReader;
import ke.co.skyworld.query_manager.Query_manager;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

public class Login implements HttpHandler {

    Query_manager usersDao = new Query_manager();
    Object userid;

    String sqlQuery1 = "SELECT * FROM public.user WHERE username = (:username)";
    String sqlQuery2 = "INSERT INTO public.access_token (user_id,time_to_live, units, access_token,ip_address) VALUES (:user_id,:time_to_live,:units,:access_token,:ip_address) RETURNING access_token;";
    String sqlQuery3 = "SELECT * FROM public.access_token WHERE user_id = (:user_id) ";
    String sqlQuery5 = "SELECT COUNT(*) FROM public.access_token WHERE user_id = (:user_id)";
    String sqlQuery6 = "SELECT * FROM public.access_token WHERE user_id = (:user_id) and ip_address = (:ip_address) ";
    String sqlQuery7 = "DELETE FROM public.access_token WHERE user_id = (:user_id) and ip_address = (:ip_address) ";
    String sqlQuery8 = "UPDATE public.user SET account_status = 'DISABLED' WHERE user_id = (:user_id)";
    String sqlQuery9 = "UPDATE public.user SET personal_conf = (:personal_conf) WHERE username = (:username)";

    HashMap<String, Object> userDetails;

    HashMap<String, Object> returnedAccessToken;
    HashMap<String, Object> userConfigDetails;
    LinkedHashMap<String, Object> accessTokenValues = new LinkedHashMap<>();
    LinkedHashMap<String, Object> values2 = new LinkedHashMap<>();
    LinkedHashMap<String, Object> values = new LinkedHashMap<>();
    HashMap<String, Object> error = new HashMap<>();

    HashMap<String, Object> returnedValues;

    String personalConfigDetails;
    String userType;
    String authDetails;
    String userName;
    String ip_address;
    Object user_id;
    boolean applicable;
    Integer loginTriesLimit;
    Integer multipleLoginsLimit;

    public static String generateRandomNumberAndHash() {
        byte[] array = new byte[6]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);
        Timestamp now = new Timestamp(System.currentTimeMillis());

        return Hashing.sha256().hashString(now + generatedString, StandardCharsets.UTF_8).toString();
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        values.clear();
        values2.clear();
        authDetails = httpServerExchange.getRequestHeaders().get("auth").getFirst();
        userName = ExchangeUtils.getQueryParam(httpServerExchange, "username");
        ip_address = httpServerExchange.getSourceAddress().getAddress().toString().replace("/", "");

        if (userName == null) {
            error.put("Error", "Please enter username");
            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            return;
        }

        try {

            values.put("username", userName);

            userDetails = usersDao.getSpecific(sqlQuery1, values);

            if (userDetails.isEmpty()) {

                error.put("Error", "Username does not exist ");
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
                return;
            }


            personalConfigDetails = (String) userDetails.get("personal_conf");
            userType = (String) userDetails.get("user_type");
            user_id = userDetails.get("user_id");
            String accountStatus = (String) userDetails.get("account_status");

            if (accountStatus.equals("DISABLED")) {

                error.put("Error", "Account Locked");
                error.put("Message", "Please contact customer care for support");
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.UNAUTHORIZED);
                return;

            }

            values2.put("user_id", user_id);

            List<LinkedHashMap<String, Object>> test = usersDao.getSpecificUsers(sqlQuery3, values2);


            userConfigDetails = XmlReader.getUserDetails(personalConfigDetails, userType);
            System.out.println(userConfigDetails);
            //return;

            applicable = (userConfigDetails.get("check").equals("YES"));
            multipleLoginsLimit = (Integer) userConfigDetails.get("login_limit");
            loginTriesLimit = (Integer) userConfigDetails.get("login_tries_limit");

            if (!test.isEmpty()) {
                long userCount = (long) Query_manager.getTableCount(sqlQuery5, values2);

                if (multipleLoginsLimit != null && userCount >= multipleLoginsLimit) {
                    error.put("Error", "Login Denied");
                    error.put("Message", "Maximum concurrent logins limit reached.");
                    ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.UNAUTHORIZED);
                    return;
                }

                values2.put("ip_address", ip_address);

                HashMap<String, Object> getWithIp = usersDao.getSpecific(sqlQuery6, values2);

                if (!getWithIp.isEmpty()) {

                    usersDao.update(sqlQuery7, values2);
                    values2.remove("ip_address", ip_address);

                }
            }

            login(httpServerExchange);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(HttpServerExchange httpServerExchange) throws Exception {

        returnedValues = usersDao.getSpecific(sqlQuery1, values);

        if (!returnedValues.isEmpty()) {

            String password = (String) returnedValues.get("password");
            String check = Hashing.sha256().hashString(userName + password, StandardCharsets.UTF_8).toString();

            if (check.equals(authDetails)) {

                String accessToken = generateRandomNumberAndHash().trim();

                userid = returnedValues.get("user_id");
                accessTokenValues.put("user_id", userid);
                accessTokenValues.put("time_to_live", XmlReader.ACCESS_TOKEN_TTL);
                accessTokenValues.put("units", XmlReader.ACCESS_TOKEN_TTL_UNITS);
                accessTokenValues.put("access_token", accessToken);
                accessTokenValues.put("ip_address", ip_address);

                usersDao.add(sqlQuery2, accessTokenValues);
                values2.remove("ip_address", ip_address);

                returnedAccessToken = usersDao.getSpecific(sqlQuery3, values2);

                ApiResponse.sendResponse(httpServerExchange, returnedAccessToken, StatusCodes.OK);

            } else {

                if (applicable) {

                    if (userType.equals("ADMINISTRATOR")) {

                        if (loginTriesLimit > 0) {

                            int rem = loginTriesLimit - 1;

                            error.put("Error", "Wrong credentials");
                            error.put("Message", "Wrong password inserted. You have " + rem + " before account is locked.");

                            values.put("personal_conf", "<CONFIG >\n    " +
                                                            "<ADMIN_USER_LOGIN_ACCOUNT_LIMIT APPLICABLE = \"YES\">2</ADMIN_USER_LOGIN_ACCOUNT_LIMIT>\n" +
                                                            "<ADMIN_USER_LOGIN_TRIES_LEFT>" + rem +"</ADMIN_USER_LOGIN_TRIES_LEFT>\n   " +
                                                        " </CONFIG>");

                            usersDao.update(sqlQuery9, values);

                            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.UNAUTHORIZED);
                            return;

                        } else {
                            error.put("Error", "Account Locked");
                            error.put("Message", "Please contact customer care for support");
                            usersDao.update(sqlQuery8, values2);
                            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.UNAUTHORIZED);
                        }
                        return;

                    }
                    if (userType.equals("MEMBER")) {

                        if (loginTriesLimit > 0) {

                            error.put("Error", "Wrong credentials");

                            int rem = loginTriesLimit - 1;

                            error.put("Message", "Wrong password inserted. You have " + rem + " tries before account is locked.");

                            values.put("personal_conf", "<CONFIG > " +
                                                            "<USER_LOGIN_ACCOUNT_LIMIT APPLICABLE = \"YES\">3</USER_LOGIN_ACCOUNT_LIMIT>\n" +
                                                            "<USER_LOGIN_TRIES_LEFT>" + rem + "</USER_LOGIN_TRIES_LEFT> " +
                                                        "</CONFIG>");

                            usersDao.update(sqlQuery9, values);

                            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.UNAUTHORIZED);

                        } else {
                            error.put("Error", "Account Locked");
                            error.put("Message", "Please contact customer care for support");
                            usersDao.update(sqlQuery8, values2);
                            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.UNAUTHORIZED);
                        }
                    }
                } else {

                    error.put("Error", "Access denied");
                    error.put("Message", "Wrong credentials inserted");
                    ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.UNAUTHORIZED);
                }

            }
        } else {
            error.put("Error", "Not found");
            error.put("Message", "Username doesn't exist");
            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.UNAUTHORIZED);

        }
    }
}
