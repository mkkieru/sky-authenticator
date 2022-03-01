package ke.co.skyworld.auth_details;

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

public class GetAuthDetails implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        HashMap<String, Object> error = new HashMap<>();

        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        }
        Query_manager usersDao = new Query_manager();
        String sqlQuery = "SELECT * FROM public.auth_details";
        List<LinkedHashMap<String, Object>> value;


        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange,access_token, String.valueOf(exchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        } else if (responseCodes == ResponseCodes.SUCCESS) {

            try {

                value = usersDao.getAll(sqlQuery);

                if (value.size() > 0) {
                    ApiResponse.sendResponse(exchange, value, StatusCodes.OK);

                } else {
                    error.put("Message", "No records found");
                    ApiResponse.sendResponse(exchange, error, StatusCodes.OK);
                }
            } catch (Exception e) {
                e.printStackTrace();
                error.put("Message", e.getMessage());
                ApiResponse.sendResponse(exchange, error, StatusCodes.OK);
            }
        }

    }
}
