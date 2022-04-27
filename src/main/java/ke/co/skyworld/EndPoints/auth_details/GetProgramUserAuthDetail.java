package ke.co.skyworld.EndPoints.auth_details;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GetSpecificAuthDetails implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String sqlQuery = "SELECT age(now(),date_updated), * FROM public.auth_details WHERE user_id = (:user_id) and identifier = (:identifier) and program_id = (:program_id) and status = 'ACTIVE'";
        LinkedHashMap<String, Object> specificAuthDetails;
        LinkedHashMap<String, Object> userValues;
        HashMap<String, Object> error = new HashMap<>();
        QueryManager queryManager = new QueryManager();
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType();


        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        }
        userValues = gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);

        try {
            specificAuthDetails = queryManager.getUserSpecificAuthDetail(sqlQuery, userValues);
            ApiResponse.sendResponse(exchange, specificAuthDetails, StatusCodes.OK);
        } catch (Exception e) {
            e.printStackTrace();
            error.put("Message", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(exchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
        }


    }


}
