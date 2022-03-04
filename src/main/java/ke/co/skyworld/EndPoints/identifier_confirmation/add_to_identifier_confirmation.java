package ke.co.skyworld.EndPoints.identifier_confirmation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.query_manager.Query_manager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class add_to_identifier_confirmation implements HttpHandler {
    HashMap<String, String> error = new HashMap<>();

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String sqlQuery = "INSERT INTO public.identifier_confirmation_table (program_id,token,identifier_type,identifier) VALUES (:program_id,:token,:identifier_type,:identifier) RETURNING buffer_id";

        Query_manager usersDao = new Query_manager();
        Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
        LinkedHashMap<String, Object> values = new Gson().fromJson(ExchangeUtils.getRequestBody(exchange), type);

        if(checkInputFields(values,exchange) == ResponseCodes.ERROR) return;

        try{
            values.remove("user_id");
            usersDao.add(sqlQuery,values);
            ApiResponse.sendResponse(exchange,values, StatusCodes.CREATED);
        }catch (Exception e){
            e.printStackTrace();
            error.put("error", "Something went wrong. Please try again later");
            ApiResponse.sendResponse(exchange,error, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    private Enum checkInputFields(LinkedHashMap<String, Object> values, HttpServerExchange exchange) {

        if (!values.containsKey("program_id")){
            error.put("error", "Missing field ");
            error.put("message", "Please provide field 'program_id'");

            ApiResponse.sendResponse(exchange,error, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        }
        else if (!values.containsKey("token")){
            error.put("error", "Missing field ");
            error.put("message", "Please provide field 'token'");

            ApiResponse.sendResponse(exchange,error, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        }
        else if (!values.containsKey("identifier_type")){
            error.put("error", "Missing field ");
            error.put("message", "Please provide field 'identifier_type'");

            ApiResponse.sendResponse(exchange,error, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        }
        else if (!values.containsKey("identifier")){
            error.put("error", "Missing field ");
            error.put("message", "Please provide field 'identifier'");

            ApiResponse.sendResponse(exchange,error, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;
        }
        return ResponseCodes.SUCCESS;

    }
}
