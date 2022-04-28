//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.auth_details;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
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

public class GetSpecificAuthDetails implements HttpHandler {
    public GetSpecificAuthDetails() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String sqlQuery = "SELECT age(now(),date_updated), * FROM public.auth_details WHERE user_id = (:user_id) and identifier = (:identifier) and program_id = (:program_id) and status = 'ACTIVE'";
        HashMap<String, Object> error = new HashMap();
        QueryManager queryManager = new QueryManager();
        Gson gson = new Gson();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));
            if (responseCodes != ResponseCodes.ERROR) {
                LinkedHashMap userValues = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);

                try {
                    LinkedHashMap<String, Object> specificAuthDetails = queryManager.getUserSpecificAuthDetail(sqlQuery, userValues);
                    ApiResponse.sendResponse(exchange, specificAuthDetails, 200);
                } catch (Exception var12) {
                    var12.printStackTrace();
                    error.put("Message", ResponseCodes.SOMETHING_WENT_WRONG);
                    ApiResponse.sendResponse(exchange, error, 500);
                }

            }
        } else {
            error.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
            ApiResponse.sendResponse(exchange, error, 400);
        }
    }
}
