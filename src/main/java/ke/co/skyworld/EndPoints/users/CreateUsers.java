package ke.co.skyworld.EndPoints.users;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.query_manager.Query_manager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class CreateUsers implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        Query_manager usersDao = new Query_manager();
        Gson gson = new Gson();

        String sqlQuery = "INSERT INTO public.user (first_name,last_name,username,national_id,password,user_type,personal_conf) VALUES (:first_name,:last_name,:username,:national_id,:password,:user_type,:personal_conf) RETURNING user_id";
        String sqlQuery1 = "SELECT * FROM public.user WHERE user_id = :user_id";

        String member_conf_details =
                "<CONFIG >\n" +
                        "<USER_LOGIN_ACCOUNT_LIMIT APPLICABLE = \"NO\">3</USER_LOGIN_ACCOUNT_LIMIT>\n" +
                        "<USER_LOGIN_TRIES_LEFT>5</USER_LOGIN_TRIES_LEFT>" +
                        "</CONFIG>\n";

        String admin_conf_details =
                "<CONFIG >\n" +
                        "<ADMIN_USER_LOGIN_ACCOUNT_LIMIT APPLICABLE = \"NO\">2</ADMIN_USER_LOGIN_ACCOUNT_LIMIT>\n" +
                        "<ADMIN_USER_LOGIN_TRIES_LEFT>2</ADMIN_USER_LOGIN_TRIES_LEFT>\n" +
                        "</CONFIG>\n";

        HashMap<String, Object> userDetails = new HashMap<>();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType();
        LinkedHashMap<String, Object> valuesMap = gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);

        if (verifyFields(valuesMap, exchange) == ResponseCodes.ERROR) return;

        try {
            String password = valuesMap.get("password").toString();
            String hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();

            valuesMap.replace("password", hashedPassword);
            String userType = (String) valuesMap.get("user_type");

            if (userType.equals("ADMINISTRATOR")) {

                valuesMap.put("personal_conf", admin_conf_details);

                Object i = usersDao.add(sqlQuery, valuesMap);

                if (i.equals(ResponseCodes.ERROR)) {

                    HashMap<String, Object> errorMap = new HashMap<>();
                    errorMap.put("error", i);
                    ApiResponse.sendResponse(exchange, errorMap, StatusCodes.BAD_REQUEST);

                    return;
                }

                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("user_id", i);

                userDetails = usersDao.getSpecific(sqlQuery1, map);

            } else if (userType.equals("MEMBER")) {
                valuesMap.put("personal_conf", member_conf_details);

                Object i = usersDao.add(sqlQuery, valuesMap);

                if (i.equals(ResponseCodes.ERROR)) {

                    HashMap<String, String> errorMap = new HashMap<>();
                    errorMap.put("error", "User with provided national id already exists ");
                    ApiResponse.sendResponse(exchange, errorMap, StatusCodes.BAD_REQUEST);

                    return;
                }

                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("user_id", i);

                userDetails = usersDao.getSpecific(sqlQuery1, map);
            }

            ApiResponse.sendResponse(exchange, userDetails, StatusCodes.OK);

        } catch (Exception e) {

            e.printStackTrace();
            HashMap<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "An error occurred while processing your request");
            ApiResponse.sendResponse(exchange, errorMap, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    private Enum verifyFields(LinkedHashMap<String, Object> valuesMap, HttpServerExchange exchange) {
        if (!valuesMap.containsKey("first_name")) {
            HashMap<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "Missing field ");
            errorMap.put("message", "Please provide field 'first_name'");

            ApiResponse.sendResponse(exchange, errorMap, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        } else if (!valuesMap.containsKey("last_name")) {
            HashMap<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "Missing field ");
            errorMap.put("message", "Please provide field 'last_name'");

            ApiResponse.sendResponse(exchange, errorMap, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        } else if (!valuesMap.containsKey("national_id")) {
            HashMap<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "Missing field ");
            errorMap.put("message", "Please provide field 'national_id'");

            ApiResponse.sendResponse(exchange, errorMap, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        } else if (!valuesMap.containsKey("password")) {
            HashMap<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "Missing field ");
            errorMap.put("message", "Please provide field 'password'");

            ApiResponse.sendResponse(exchange, errorMap, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        }
        return ResponseCodes.SUCCESS;
    }
}
