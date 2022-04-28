//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.auth_details;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class CreateAuthDetails implements HttpHandler {
    HashMap<String, Object> error = new HashMap();

    public CreateAuthDetails() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            String sqlQuery = "INSERT INTO public.auth_details (user_id,program_id,auth_code,time_to_live,time_to_live_units) VALUES (:user_id,:program_id,:auth_code,:time_to_live,:time_to_live_units) RETURNING id";
            QueryManager usersDao = new QueryManager();
            Gson gson = new Gson();
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));
            if (responseCodes != ResponseCodes.ERROR) {
                if (responseCodes == ResponseCodes.SUCCESS) {
                    Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
                    }).getType();
                    LinkedHashMap<String, Object> values = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);
                    if (this.validateInput(values, exchange) == ResponseCodes.ERROR) {
                        return;
                    }

                    try {
                        usersDao.add(sqlQuery, values);
                        ApiResponse.sendResponse(exchange, values, 201);
                    } catch (Exception var10) {
                        var10.printStackTrace();
                        this.error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
                        ApiResponse.sendResponse(exchange, this.error, 500);
                    }
                }

            }
        } else {
            this.error.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
            ApiResponse.sendResponse(exchange, this.error, 400);
        }
    }

    public Enum validateInput(LinkedHashMap<String, Object> values, HttpServerExchange exchange) {
        if (!values.containsKey("user_id")) {
            this.error.put("error", "Missing field ");
            this.error.put("message", "Please field 'user_id'");
            ApiResponse.sendResponse(exchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("program_id")) {
            this.error.put("error", "Missing field ");
            this.error.put("message", "Please field 'program_id'");
            ApiResponse.sendResponse(exchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("auth_code")) {
            this.error.put("error", "Missing field ");
            this.error.put("message", "Please field 'auth_code'");
            ApiResponse.sendResponse(exchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("time_to_live")) {
            this.error.put("error", "Missing field ");
            this.error.put("message", "Please field 'time_to_live'");
            ApiResponse.sendResponse(exchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("time_to_live_units")) {
            this.error.put("error", "Missing field ");
            this.error.put("message", "Please field 'time_to_live_units'");
            ApiResponse.sendResponse(exchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else {
            return ResponseCodes.SUCCESS;
        }
    }
}
