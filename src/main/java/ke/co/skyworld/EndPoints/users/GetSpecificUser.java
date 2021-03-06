package ke.co.skyworld.EndPoints.users;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.query_manager.Query_manager;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class GetSpecificUser implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        HashMap<String, String> error = new HashMap<>();

        String access_token = httpServerExchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            return;
        }

        HashMap<String, Object> returnedValues;

        Query_manager usersDao = new Query_manager();

        String sqlQuery = "SELECT * FROM public.user WHERE user_id = :user_id";
        LinkedHashMap<String, Object> value = new LinkedHashMap<>();

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(httpServerExchange, access_token, String.valueOf(httpServerExchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            return;
        } else if (responseCodes == ResponseCodes.SUCCESS) {
            value.put("user_id", Integer.parseInt(ExchangeUtils.getPathVar(httpServerExchange, "user_id")));

            try {
                returnedValues = usersDao.getSpecific(sqlQuery, value);

                if (returnedValues.size() > 0) {

                    ApiResponse.sendResponse(httpServerExchange, returnedValues, StatusCodes.OK);
                } else {
                    error.put("Message", "No records found");
                    ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.OK);
                }
            } catch (Exception e) {
                e.printStackTrace();
                error.put("Error", e.getMessage());
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
