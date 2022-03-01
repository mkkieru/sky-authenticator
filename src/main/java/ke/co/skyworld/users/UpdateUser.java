package ke.co.skyworld.users;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.ApiResponse;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.ExchangeUtils;
import ke.co.skyworld.ResponseCodes;
import ke.co.skyworld.query_manager.Query_manager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class UpdateUser implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        HashMap<String, String> error = new HashMap<>();

        String access_token = httpServerExchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            return;
        }

        String sqlQuery = "UPDATE public.user SET first_name = (:first_name), last_name =(:last_name), national_id = (:national_id), date_updated = current_timestamp WHERE user_id = (:user_id)";
        Gson gson = new Gson();
        Query_manager usersDao = new Query_manager();

        Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(httpServerExchange,access_token, String.valueOf(httpServerExchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            return;
        } else if (responseCodes == ResponseCodes.SUCCESS) {
            LinkedHashMap<String, Object> values = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

            if (!values.containsKey("first_name")) {
                error.put("error", "Missing field");
                error.put("Message", "Please provide field 'first_name'");
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            } else if (!values.containsKey("last_name")) {
                error.put("error", "Missing field");
                error.put("Message", "Please provide field 'last_name'");
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            } else if (!values.containsKey("national_id")) {
                error.put("error", "Missing field");
                error.put("Message", "Please provide field 'national_id'");
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            } else if (!values.containsKey("user_id")) {
                error.put("error", "Missing field");
                error.put("Message", "Please provide field 'user_id'");
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            }

            try {
                usersDao.update(sqlQuery, values);
                //ApiResponse.sendResponse(httpServerExchange,usersDao.update(sqlQuery,values), StatusCodes.BAD_REQUEST);

            } catch (Exception e) {
                e.printStackTrace();
                error.put("error", "Something went wrong. Please try again later");
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
