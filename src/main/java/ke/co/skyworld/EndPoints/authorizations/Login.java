//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.authorizations;

import com.google.common.hash.Hashing;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.XmlReader.XmlReader;
import ke.co.skyworld.query_manager.QueryManager;

public class Login implements HttpHandler {
    QueryManager usersDao = new QueryManager();
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
    LinkedHashMap<String, Object> accessTokenValues = new LinkedHashMap();
    LinkedHashMap<String, Object> values2 = new LinkedHashMap();
    LinkedHashMap<String, Object> values = new LinkedHashMap();
    HashMap<String, Object> error = new HashMap();
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

    public Login() throws SQLException {
    }

    public static String generateRandomNumberAndHash() {
        byte[] array = new byte[6];
        (new Random()).nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return Hashing.sha256().hashString(now + generatedString, StandardCharsets.UTF_8).toString();
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        this.values.clear();
        this.values2.clear();
        this.authDetails = httpServerExchange.getRequestHeaders().get("auth").getFirst();
        this.userName = ExchangeUtils.getQueryParam(httpServerExchange, "username");
        this.ip_address = httpServerExchange.getSourceAddress().getAddress().toString().replace("/", "");
        if (this.userName == null) {
            this.error.put("Error", "Please enter username");
            ApiResponse.sendResponse(httpServerExchange, this.error, 400);
        } else {
            try {
                this.values.put("username", this.userName);
                this.userDetails = this.usersDao.getSpecific(this.sqlQuery1, this.values);
                if (this.userDetails.isEmpty()) {
                    this.error.put("Error", "Username does not exist ");
                    ApiResponse.sendResponse(httpServerExchange, this.error, 400);
                    return;
                }

                this.personalConfigDetails = (String)this.userDetails.get("personal_conf");
                this.userType = (String)this.userDetails.get("user_type");
                this.user_id = this.userDetails.get("user_id");
                String accountStatus = (String)this.userDetails.get("account_status");
                if (accountStatus.equals("DISABLED")) {
                    this.error.put("Error", "Account Locked");
                    this.error.put("Message", "Please contact customer care for support");
                    ApiResponse.sendResponse(httpServerExchange, this.error, 401);
                    return;
                }

                this.values2.put("user_id", this.user_id);
                List<LinkedHashMap<String, Object>> test = this.usersDao.getSpecificUsers(this.sqlQuery3, this.values2);
                this.userConfigDetails = XmlReader.getUserDetails(this.personalConfigDetails, this.userType);
                this.applicable = this.userConfigDetails.get("check").equals("YES");
                this.multipleLoginsLimit = (Integer)this.userConfigDetails.get("login_limit");
                this.loginTriesLimit = (Integer)this.userConfigDetails.get("login_tries_limit");
                if (!test.isEmpty()) {
                    long userCount = (Long)QueryManager.getTableCount(this.sqlQuery5, this.values2);
                    if (this.multipleLoginsLimit != null && userCount >= (long)this.multipleLoginsLimit) {
                        this.error.put("Error", ResponseCodes.ACCESS_DENIED);
                        this.error.put("Message", "Maximum concurrent logins limit reached.");
                        ApiResponse.sendResponse(httpServerExchange, this.error, 401);
                        return;
                    }

                    this.values2.put("ip_address", this.ip_address);
                    HashMap<String, Object> getWithIp = this.usersDao.getSpecific(this.sqlQuery6, this.values2);
                    if (!getWithIp.isEmpty()) {
                        this.usersDao.update(this.sqlQuery7, this.values2);
                        this.values2.remove("ip_address", this.ip_address);
                    }
                }

                this.login(httpServerExchange);
            } catch (Exception var7) {
                var7.printStackTrace();
                this.error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
                ApiResponse.sendResponse(httpServerExchange, this.error, 500);
            }

        }
    }

    public void login(HttpServerExchange httpServerExchange) {
        try {
            this.returnedValues = this.usersDao.getSpecific(this.sqlQuery1, this.values);
            if (!this.returnedValues.isEmpty()) {
                String password = (String)this.returnedValues.get("password");
                String check = Hashing.sha256().hashString(this.userName + password, StandardCharsets.UTF_8).toString();
                if (check.equals(this.authDetails)) {
                    String accessToken = generateRandomNumberAndHash().trim();
                    this.userid = this.returnedValues.get("user_id");
                    this.accessTokenValues.put("user_id", this.userid);
                    this.accessTokenValues.put("time_to_live", XmlReader.ACCESS_TOKEN_TTL);
                    this.accessTokenValues.put("units", XmlReader.ACCESS_TOKEN_TTL_UNITS);
                    this.accessTokenValues.put("access_token", accessToken);
                    this.accessTokenValues.put("ip_address", this.ip_address);
                    this.usersDao.add(this.sqlQuery2, this.accessTokenValues);
                    this.values2.remove("ip_address", this.ip_address);
                    this.returnedAccessToken = this.usersDao.getSpecific(this.sqlQuery3, this.values2);
                    ApiResponse.sendResponse(httpServerExchange, this.returnedAccessToken, 200);
                } else if (this.applicable) {
                    int rem;
                    if (this.userType.equals("ADMINISTRATOR")) {
                        if (this.loginTriesLimit > 0) {
                            rem = this.loginTriesLimit - 1;
                            this.error.put("Error", ResponseCodes.UNAUTHORIZED);
                            this.error.put("Message", "Wrong password inserted. You have " + rem + " before account is locked.");
                            this.values.put("personal_conf", "<CONFIG >\n    <ADMIN_USER_LOGIN_ACCOUNT_LIMIT APPLICABLE = \"YES\">2</ADMIN_USER_LOGIN_ACCOUNT_LIMIT>\n<ADMIN_USER_LOGIN_TRIES_LEFT>" + rem + "</ADMIN_USER_LOGIN_TRIES_LEFT>\n    </CONFIG>");
                            this.usersDao.update(this.sqlQuery9, this.values);
                            ApiResponse.sendResponse(httpServerExchange, this.error, 401);
                            return;
                        }

                        this.error.put("Error", ResponseCodes.LOCKED);
                        this.error.put("Message", "Please contact customer care for support");
                        this.usersDao.update(this.sqlQuery8, this.values2);
                        ApiResponse.sendResponse(httpServerExchange, this.error, 401);
                        return;
                    }

                    if (this.userType.equals("MEMBER")) {
                        if (this.loginTriesLimit > 0) {
                            this.error.put("Error", ResponseCodes.UNAUTHORIZED);
                            rem = this.loginTriesLimit - 1;
                            this.error.put("Message", "Wrong password inserted. You have " + rem + " tries before account is locked.");
                            this.values.put("personal_conf", "<CONFIG > <USER_LOGIN_ACCOUNT_LIMIT APPLICABLE = \"YES\">3</USER_LOGIN_ACCOUNT_LIMIT>\n<USER_LOGIN_TRIES_LEFT>" + rem + "</USER_LOGIN_TRIES_LEFT> </CONFIG>");
                            this.usersDao.update(this.sqlQuery9, this.values);
                            ApiResponse.sendResponse(httpServerExchange, this.error, 401);
                        } else {
                            this.error.put("Error", ResponseCodes.LOCKED);
                            this.error.put("Message", "Please contact customer care for support");
                            this.usersDao.update(this.sqlQuery8, this.values2);
                            ApiResponse.sendResponse(httpServerExchange, this.error, 401);
                        }
                    }
                } else {
                    this.error.put("Error", ResponseCodes.ACCESS_DENIED);
                    this.error.put("Message", "Wrong credentials inserted");
                    ApiResponse.sendResponse(httpServerExchange, this.error, 401);
                }
            } else {
                this.error.put("Error", ResponseCodes.EMPTY);
                this.error.put("Message", "Username doesn't exist");
                ApiResponse.sendResponse(httpServerExchange, this.error, 401);
            }
        } catch (Exception var5) {
            var5.printStackTrace();
            this.error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, this.error, 500);
        }

    }
}
