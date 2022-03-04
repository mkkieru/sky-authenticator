package ke.co.skyworld.EndPoints.identifier;

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

public class AddIdentifier implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        HashMap<String, String> error = new HashMap<>();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();

        ResponseCodes responseCodes  = CheckAuthCodes.checkAndUpdateAccessTokens(exchange,access_token, String.valueOf(exchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;

        } else if (responseCodes == ResponseCodes.SUCCESS) {

            if (access_token == null || access_token.equals("")) {
                error.put("Error", "Access token is required ");
                ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
                return;
            }

            Query_manager usersDao = new Query_manager();
            String sqlQuery = "INSERT INTO public.identifier (identifier_type, identifier) VALUES (:identifier_type,:identifier)";
            Gson gson = new Gson();

            Type type = new TypeToken<LinkedHashMap<String, Object>>() {
            }.getType();
            LinkedHashMap<String, Object> values = gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);

            if (!values.containsKey("identifier_type")) {
                error.put("Error", "Missing field");
                error.put("Message", "Please input field 'identifier_type'");
                ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
                return;

            } else if (!values.containsKey("identifier")) {
                error.put("Error", "Missing field");
                error.put("Message", "Please input field 'identifier'");
                ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
                return;
            }

            try {
                usersDao.getAll(sqlQuery);
                ApiResponse.sendResponse(exchange, values, StatusCodes.OK);
            } catch (Exception e) {
                e.printStackTrace();
                error.put("Error", e.getMessage());
                ApiResponse.sendResponse(exchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
