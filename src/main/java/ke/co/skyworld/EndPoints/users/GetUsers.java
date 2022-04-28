//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.users;

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

public class GetUsers implements HttpHandler {
    public GetUsers() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            String sqlQuery = "SELECT * FROM public.user ORDER BY user_id LIMIT (:page_size) OFFSET (:offset)";
            QueryManager usersDao = new QueryManager();
            HashMap<String, Object> limits = new HashMap();
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));
            if (responseCodes != ResponseCodes.ERROR) {
                if (responseCodes == ResponseCodes.SUCCESS) {
                    limits.put("page_size", ExchangeUtils.getQueryParam(exchange, "pageSize"));
                    limits.put("page", ExchangeUtils.getQueryParam(exchange, "page"));

                    try {
                        List<LinkedHashMap<String, Object>> returnedValues = usersDao.getAll(sqlQuery, limits);
                        if (returnedValues.size() > 0) {
                            ApiResponse.sendResponse(exchange, returnedValues, 200);
                        } else {
                            error.put("Message", ResponseCodes.EMPTY);
                            ApiResponse.sendResponse(exchange, error, 200);
                        }
                    } catch (Exception var10) {
                        var10.printStackTrace();
                        error.put("error", ResponseCodes.SOMETHING_WENT_WRONG);
                        ApiResponse.sendResponse(exchange, error, 500);
                    }
                }

            }
        } else {
            error.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
            error.put("Message", "If you do not have a token please login to get one. ");
            ApiResponse.sendResponse(exchange, error, 400);
        }
    }
}
