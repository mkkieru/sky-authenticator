package ke.co.skyworld.EndPoints.auth_details;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.query_manager.Query_manager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CreateAuthDetails implements HttpHandler {


    HashMap<String, String> error = new HashMap<>();

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {


        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        }

        String sqlQuery = "INSERT INTO public.auth_details (user_id,program_id,auth_code,time_to_live,time_to_live_units) VALUES (:user_id,:program_id,:auth_code,:time_to_live,:time_to_live_units) RETURNING id";
        Query_manager usersDao = new Query_manager();
        Gson gson = new Gson();

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, String.valueOf(exchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {

            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;

        } else if (responseCodes == ResponseCodes.SUCCESS) {

            Type type = new TypeToken<LinkedHashMap<String, Object>>() {
            }.getType();
            LinkedHashMap<String, Object> values = gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);

            if (validateInput(values, exchange) == ResponseCodes.ERROR) return;

            try {
                usersDao.add(sqlQuery, values);
                ApiResponse.sendResponse(exchange, values, StatusCodes.CREATED);
            } catch (Exception e) {
                e.printStackTrace();
                error.put("error", e.getMessage());
                ApiResponse.sendResponse(exchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public Enum validateInput(LinkedHashMap<String, Object> values, HttpServerExchange exchange) {

        if (!values.containsKey("user_id")) {
            error.put("error", "Missing field ");
            error.put("message", "Please field 'user_id'");

            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;

        } else if (!values.containsKey("program_id")) {
            error.put("error", "Missing field ");
            error.put("message", "Please field 'program_id'");

            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("auth_code")) {
            error.put("error", "Missing field ");
            error.put("message", "Please field 'auth_code'");

            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("time_to_live")) {
            error.put("error", "Missing field ");
            error.put("message", "Please field 'time_to_live'");

            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("time_to_live_units")) {
            error.put("error", "Missing field ");
            error.put("message", "Please field 'time_to_live_units'");

            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        }

        return ResponseCodes.SUCCESS;
    }
}
