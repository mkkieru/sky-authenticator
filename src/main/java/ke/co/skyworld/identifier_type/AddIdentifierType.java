package ke.co.skyworld.identifier_type;

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

public class AddIdentifierType implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        HashMap<String, Object> errorMap = new HashMap<>();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            errorMap.put("Error", "Access token is required ");
            ApiResponse.sendResponse(exchange, errorMap, StatusCodes.BAD_REQUEST);
            return;
        }

        Query_manager usersDao = new Query_manager();
        String sqlQuery = "INSERT INTO public.identifier_type (identifier_type) VALUES (:identifier_type) RETURNING identifier_type";

        Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
        LinkedHashMap<String, Object> valuesMap = new Gson().fromJson(ExchangeUtils.getRequestBody(exchange), type);

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange,access_token, String.valueOf(exchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {
            errorMap.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(exchange, errorMap, StatusCodes.BAD_REQUEST);
            return;
        } else if (responseCodes == ResponseCodes.SUCCESS) {

            if (!valuesMap.containsKey("identifier_type")) {
                errorMap.put("error", "Missing field ");
                errorMap.put("message", "Please enter the identifier type");

                ApiResponse.sendResponse(exchange, errorMap, StatusCodes.BAD_REQUEST);
                return;
            }

            try {
                usersDao.add(sqlQuery, valuesMap);
                ApiResponse.sendResponse(exchange, valuesMap, StatusCodes.OK);

            } catch (Exception e) {

                e.printStackTrace();
                errorMap.put("error", "An error occurred while processing your request");

                ApiResponse.sendResponse(exchange, errorMap, StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
