package ke.co.skyworld.EndPoints.identifier_type;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.query_manager.QueryManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GetIdentifierType implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        HashMap<String, String> error = new HashMap();

        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        }

        String sqlQuery = "SELECT * FROM public.identifier_type";
        QueryManager usersDao = new QueryManager();

        List<LinkedHashMap<String,Object>> values = usersDao.getAll(sqlQuery);

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));


        if (responseCodes == ResponseCodes.ERROR) {
            return;
        } else if (responseCodes == ResponseCodes.SUCCESS) {

            try {
                ApiResponse.sendResponse(exchange, values, StatusCodes.OK);

            } catch (Exception e) {
                e.printStackTrace();
                error.put("Error", e.getMessage());
                ApiResponse.sendResponse(exchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
