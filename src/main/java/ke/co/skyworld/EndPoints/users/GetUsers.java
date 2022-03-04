package ke.co.skyworld.EndPoints.users;

import io.undertow.util.StatusCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.query_manager.Query_manager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.*;

public class GetUsers implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        LinkedHashMap<String, String> error = new LinkedHashMap<>();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            error.put("Message", "If you do not have a token please login to get one. ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        }

        String sqlQuery = "SELECT * FROM public.user ORDER BY user_id LIMIT (:page_size) OFFSET (:offset)";
        Query_manager usersDao = new Query_manager();
        List<LinkedHashMap<String, Object>> returnedValues;
        HashMap<String, Object> limits = new HashMap<>();


        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please login to continue");
            ApiResponse.sendResponse(exchange, error, StatusCodes.UNAUTHORIZED);
            return;

        } else if (responseCodes == ResponseCodes.SUCCESS) {

            limits.put("page_size", ExchangeUtils.getQueryParam(exchange, "pageSize"));
            limits.put("page", ExchangeUtils.getQueryParam(exchange, "page"));


            try {
                returnedValues = usersDao.getAll(sqlQuery, limits);
                if (returnedValues.size() > 0) {

                    ApiResponse.sendResponse(exchange, returnedValues, StatusCodes.OK);

                } else {

                    error.put("Message", "No records found");
                    ApiResponse.sendResponse(exchange, error, StatusCodes.OK);

                }
            } catch (Exception e) {

                e.printStackTrace();
                error.put("error", "Something went wrong. Please try again later");
                ApiResponse.sendResponse(exchange, error, StatusCodes.INTERNAL_SERVER_ERROR);

            }
        }
    }
}
