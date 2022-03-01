package ke.co.skyworld.identifier;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.ApiResponse;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.ResponseCodes;
import ke.co.skyworld.query_manager.Query_manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GetIdentifiers implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        HashMap<String, String> error = new HashMap<>();

        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        }

        String sqlQuery = "SELECT* FROM public.identifier";
        Query_manager usersDao = new Query_manager();
        List<LinkedHashMap<String, Object>> valuesMap;

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange,access_token, String.valueOf(exchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        } else if (responseCodes == ResponseCodes.SUCCESS) {

            try {
                valuesMap = usersDao.getAll(sqlQuery);
                if (valuesMap.size() > 0) {
                    ApiResponse.sendResponse(exchange, valuesMap, StatusCodes.OK);
                } else {
                    error.put("Message", "No records found");
                    ApiResponse.sendResponse(exchange, error, StatusCodes.OK);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
