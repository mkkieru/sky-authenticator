//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.users;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class CreateUsers implements HttpHandler {
    public CreateUsers() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        QueryManager usersDao = new QueryManager();
        Gson gson = new Gson();
        String sqlQuery = "INSERT INTO public.user (first_name,last_name,username,national_id,password,user_type,personal_conf) VALUES (:first_name,:last_name,:username,:national_id,:password,:user_type,:personal_conf) RETURNING user_id";
        String sqlQuery1 = "SELECT * FROM public.user WHERE user_id = :user_id";
        String member_conf_details = "<CONFIG >\n<USER_LOGIN_ACCOUNT_LIMIT APPLICABLE = \"NO\">3</USER_LOGIN_ACCOUNT_LIMIT>\n<USER_LOGIN_TRIES_LEFT>5</USER_LOGIN_TRIES_LEFT></CONFIG>\n";
        String admin_conf_details = "<CONFIG >\n<ADMIN_USER_LOGIN_ACCOUNT_LIMIT APPLICABLE = \"NO\">2</ADMIN_USER_LOGIN_ACCOUNT_LIMIT>\n<ADMIN_USER_LOGIN_TRIES_LEFT>2</ADMIN_USER_LOGIN_TRIES_LEFT>\n</CONFIG>\n";
        HashMap<String, Object> userDetails = null;
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap<String, Object> valuesMap = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);
        if (this.verifyFields(valuesMap, exchange) != ResponseCodes.ERROR) {
            try {
                String password = valuesMap.get("password").toString();
                String hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
                valuesMap.replace("password", hashedPassword);
                String userType = (String)valuesMap.get("user_type");
                Object i;
                HashMap response;
                if (userType.equals("ADMINISTRATOR")) {
                    valuesMap.put("personal_conf", admin_conf_details);
                    i = usersDao.add(sqlQuery, valuesMap);
                    if (i.equals(ResponseCodes.ERROR)) {
                        response = new HashMap();
                        response.put("Error", i);
                        ApiResponse.sendResponse(exchange, response, 400);
                        return;
                    }

                    LinkedHashMap<String, Object> map = new LinkedHashMap();
                    map.put("user_id", i);
                    userDetails = usersDao.getSpecific(sqlQuery1, map);
                } else if (userType.equals("MEMBER")) {
                    valuesMap.put("personal_conf", member_conf_details);
                    i = usersDao.add(sqlQuery, valuesMap);
                    response = new HashMap();
                    if (i.getClass() == response.getClass()) {
                        HashMap<String, String> errorMap = new HashMap();
                        errorMap.put("error", (String)((HashMap)i).get("reason"));
                        ApiResponse.sendResponse(exchange, errorMap, 400);
                        return;
                    }

                    LinkedHashMap<String, Object> map = new LinkedHashMap();
                    map.put("user_id", i);
                    userDetails = usersDao.getSpecific(sqlQuery1, map);
                }

                ApiResponse.sendResponse(exchange, userDetails, 200);
            } catch (Exception var17) {
                var17.printStackTrace();
                HashMap<String, Object> errorMap = new HashMap();
                errorMap.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
                ApiResponse.sendResponse(exchange, errorMap, 500);
            }

        }
    }

    private Enum verifyFields(LinkedHashMap<String, Object> valuesMap, HttpServerExchange exchange) {
        HashMap errorMap;
        if (!valuesMap.containsKey("first_name")) {
            errorMap = new HashMap();
            errorMap.put("Error", ResponseCodes.MISSING_FIELD);
            errorMap.put("message", "Please provide field 'first_name'");
            ApiResponse.sendResponse(exchange, errorMap, 400);
            return ResponseCodes.ERROR;
        } else if (!valuesMap.containsKey("last_name")) {
            errorMap = new HashMap();
            errorMap.put("error", ResponseCodes.MISSING_FIELD);
            errorMap.put("message", "Please provide field 'last_name'");
            ApiResponse.sendResponse(exchange, errorMap, 400);
            return ResponseCodes.ERROR;
        } else if (!valuesMap.containsKey("national_id")) {
            errorMap = new HashMap();
            errorMap.put("error", ResponseCodes.MISSING_FIELD);
            errorMap.put("message", "Please provide field 'national_id'");
            ApiResponse.sendResponse(exchange, errorMap, 400);
            return ResponseCodes.ERROR;
        } else if (!valuesMap.containsKey("password")) {
            errorMap = new HashMap();
            errorMap.put("error", ResponseCodes.MISSING_FIELD);
            errorMap.put("message", "Please provide field 'password'");
            ApiResponse.sendResponse(exchange, errorMap, 400);
            return ResponseCodes.ERROR;
        } else {
            return ResponseCodes.SUCCESS;
        }
    }
}
