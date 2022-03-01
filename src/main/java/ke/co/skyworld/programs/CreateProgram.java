package ke.co.skyworld.programs;

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

public class CreateProgram implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        HashMap<String,Object> error = new HashMap<>();

        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        }

        Query_manager usersDao = new Query_manager();
        String sqlQuery = "INSERT INTO public.programs (program_id,program_name) VALUES (:program_id,:program_name) RETURNING program_id";
        String sqlQuery1 = "SELECT * FROM public.programs WHERE program_id = :program_id";
        Gson gson = new Gson();

        Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
        LinkedHashMap<String, Object> value = gson.fromJson(ExchangeUtils.getRequestBody(exchange),type);

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange,access_token, String.valueOf(exchange.getSourceAddress()));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        } else if (responseCodes == ResponseCodes.SUCCESS) {

            if (!value.containsKey("program_id")) {
                error.put("error", "Missing field");
                error.put("Message", "Please the field 'program_id'");
                ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);

                return;

            } else if (!value.containsKey("program_name")) {
                error.put("error", "Missing field");
                error.put("Message", "Please the field 'program_name'");
                ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
                return;

            }

            try {
                Object object = usersDao.add(sqlQuery, value);
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("program_id", object);
                ApiResponse.sendResponse(exchange, usersDao.getSpecific(sqlQuery1, map), StatusCodes.OK);

            } catch (Exception e) {

                e.printStackTrace();
                HashMap<String, String> errorMap = new HashMap<>();
                errorMap.put("error", "An error occurred while processing your request");
                errorMap.put("message", e.getMessage());
                ApiResponse.sendResponse(exchange, errorMap, StatusCodes.INTERNAL_SERVER_ERROR);

            }
        }
    }
}
