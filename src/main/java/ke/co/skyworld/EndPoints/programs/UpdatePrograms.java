package ke.co.skyworld.EndPoints.programs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.query_manager.Query_manager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class UpdatePrograms implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        HashMap<String, String> error = new HashMap<>();

        String access_token = httpServerExchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            return;
        }

        String sqlQuery = "UPDATE public.programs SET program_name = (:program_name), date_updated = current_timestamp WHERE program_id = (:program_id) RETURNING program_id";

        Gson gson = new Gson();
        Query_manager usersDao = new Query_manager();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType();

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(httpServerExchange, access_token, String.valueOf(httpServerExchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
            return;
        } else if (responseCodes == ResponseCodes.SUCCESS) {

            LinkedHashMap<String, Object> value = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

            if (!value.containsKey("program_name")) {
                error.put("error", "missing field");
                error.put("Message", "Please provide field 'program_name'");
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
                return;
            } else if (!value.containsKey("program_id")) {
                error.put("error", "missing field");
                error.put("Message", "Please provide field 'program_id'");
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.BAD_REQUEST);
                return;
            }

            try {
                usersDao.update(sqlQuery, value);
            } catch (Exception e) {
                e.printStackTrace();
                error.put("Error", e.getMessage());
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
