//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.auth_details;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class GetAllAuthDetails implements HttpHandler {
    public GetAllAuthDetails() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HashMap<String, Object> error = new HashMap();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            QueryManager usersDao = new QueryManager();
            String sqlQuery = "SELECT age(now(),date_updated) , * FROM public.auth_details where user_id = (:user_id) and identifier = (:identifier) and status = 'ACTIVE'";
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));
            if (responseCodes != ResponseCodes.ERROR) {
                if (responseCodes == ResponseCodes.SUCCESS) {
                    LinkedHashMap<String, Object> values = new LinkedHashMap();
                    int userId = Integer.parseInt(ExchangeUtils.getQueryParam(exchange, "user_id"));
                    String program_id = ExchangeUtils.getQueryParam(exchange, "identifier");
                    values.put("user_id", userId);
                    values.put("identifier", program_id);

                    try {
                        List<LinkedHashMap<String, Object>> value = usersDao.getUserSpecificAuthDetails(sqlQuery, values);
                        if (value.size() > 0) {
                            ApiResponse.sendResponse(exchange, value, 200);
                        } else {
                            error.put("Message", ResponseCodes.EMPTY);
                            ApiResponse.sendResponse(exchange, error, 200);
                        }
                    } catch (Exception var12) {
                        var12.printStackTrace();
                        error.put("Message", var12.getMessage());
                        ApiResponse.sendResponse(exchange, error, 500);
                    }
                }

            }
        } else {
            error.put("Error", "Access token is required ");
            ApiResponse.sendResponse(exchange, error, 400);
        }
    }
}
