package ke.co.skyworld.identifier_type;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.ApiResponse;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.ResponseCodes;
import ke.co.skyworld.query_manager.Query_manager;

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
        Query_manager usersDao = new Query_manager();

        List<LinkedHashMap<String,Object>> values = usersDao.getAll(sqlQuery);

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange,access_token, String.valueOf(exchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        } else if (responseCodes == ResponseCodes.SUCCESS) {

            try {
                ApiResponse.sendResponse(exchange, values, StatusCodes.OK);

            } catch (Exception e) {
                e.printStackTrace();
                error.put("error", "Something went wrong. Please try again later");
                ApiResponse.sendResponse(exchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
