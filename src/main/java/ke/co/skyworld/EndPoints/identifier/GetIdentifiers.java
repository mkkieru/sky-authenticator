//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.identifier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class GetIdentifiers implements HttpHandler {
    public GetIdentifiers() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HashMap<String, Object> error = new HashMap();
        Gson gson = new Gson();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        System.out.println("headers"+  exchange.getRequestHeaders());
        String access_token ;

        access_token = exchange.getRequestHeaders().get("access_token").getFirst();



        if (access_token != null && !access_token.equals("")) {
            String sqlQuery = "SELECT* FROM public.identifier where user_id = (:user_id)";
            QueryManager usersDao = new QueryManager();
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));
            if (responseCodes != ResponseCodes.ERROR) {
                if (responseCodes == ResponseCodes.SUCCESS) {
                    LinkedHashMap userDetails = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);

                    try {
                        List<LinkedHashMap<String, Object>> valuesMap = usersDao.getSpecificUsers(sqlQuery, userDetails);
                        if (valuesMap.size() > 0) {
                            ApiResponse.sendResponse(exchange, valuesMap, 200);
                        } else {
                            error.put("Message", ResponseCodes.EMPTY);
                            ApiResponse.sendResponse(exchange, error, 200);
                        }
                    } catch (Exception var12) {
                        var12.printStackTrace();
                        error.put("Message", ResponseCodes.SOMETHING_WENT_WRONG);
                        ApiResponse.sendResponse(exchange, error, 500);
                    }
                }
            }
        } else {
            error.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
            ApiResponse.sendResponse(exchange, error, 400);
        }
    }
}
