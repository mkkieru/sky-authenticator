package ke.co.skyworld.EndPoints.programs;

import io.undertow.util.StatusCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.query_manager.Query_manager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GetPrograms implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        LinkedHashMap<String, String> error = new LinkedHashMap<>();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            error.put("Message", "If you do not have a token please login to get one.");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        }


        Query_manager usersDao = new Query_manager();
        String sqlQuery = "SELECT * FROM public.programs ORDER BY program_name LIMIT (:page_size) OFFSET (:offset)";
        HashMap<String, Object> limits = new HashMap<>();

        List<LinkedHashMap<String, Object>> value;

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange,access_token, String.valueOf(exchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        } else if (responseCodes == ResponseCodes.SUCCESS) {

            limits.put("page_size", ExchangeUtils.getQueryParam(exchange, "pageSize"));
            limits.put("page", ExchangeUtils.getQueryParam(exchange, "page"));


            value = usersDao.getAll(sqlQuery, limits);

            try {
                if (value.size() > 0) {
                    ApiResponse.sendResponse(exchange, value, StatusCodes.OK);
                } else {
                    error.put("Message", "No records found");
                    ApiResponse.sendResponse(exchange, error, StatusCodes.OK);
                }

            } catch (Exception e) {
                e.printStackTrace();
                error.put("Error", e.getMessage());
                ApiResponse.sendResponse(exchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
