//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.identifier_type;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class GetIdentifierType implements HttpHandler {
    public GetIdentifierType() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HashMap<String, Object> error = new HashMap();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            String sqlQuery = "SELECT * FROM public.identifier_type";
            QueryManager usersDao = new QueryManager();
            List<LinkedHashMap<String, Object>> values = usersDao.getAll(sqlQuery);
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));
            if (responseCodes != ResponseCodes.ERROR) {
                if (responseCodes == ResponseCodes.SUCCESS) {
                    try {
                        ApiResponse.sendResponse(exchange, values, 200);
                    } catch (Exception var9) {
                        var9.printStackTrace();
                        error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
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
