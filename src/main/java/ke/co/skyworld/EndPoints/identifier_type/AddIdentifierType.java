//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.identifier_type;

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

public class AddIdentifierType implements HttpHandler {
    public AddIdentifierType() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HashMap<String, Object> errorMap = new HashMap();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            QueryManager usersDao = new QueryManager();
            String sqlQuery = "INSERT INTO public.identifier_type (identifier_type) VALUES (:identifier_type) RETURNING identifier_type";
            Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
            }).getType();
            LinkedHashMap<String, Object> valuesMap = (LinkedHashMap)(new Gson()).fromJson(ExchangeUtils.getRequestBody(exchange), type);
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, String.valueOf(exchange.getSourceAddress()));
            if (responseCodes != ResponseCodes.ERROR) {
                if (responseCodes == ResponseCodes.SUCCESS) {
                    if (!valuesMap.containsKey("identifier_type")) {
                        errorMap.put("Error", ResponseCodes.MISSING_FIELD);
                        errorMap.put("Message", "Please enter the identifier type");
                        ApiResponse.sendResponse(exchange, errorMap, 400);
                        return;
                    }

                    try {
                        usersDao.add(sqlQuery, valuesMap);
                        ApiResponse.sendResponse(exchange, valuesMap, 200);
                    } catch (Exception var10) {
                        var10.printStackTrace();
                        errorMap.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
                        ApiResponse.sendResponse(exchange, errorMap, 500);
                    }
                }

            }
        } else {
            errorMap.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
            ApiResponse.sendResponse(exchange, errorMap, 400);
        }
    }
}
