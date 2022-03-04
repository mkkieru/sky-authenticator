package ke.co.skyworld.EndPoints.identifier_confirmation;

import io.undertow.util.StatusCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.Query_manager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class get_identifier_confirmation implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Query_manager usersDao = new Query_manager();
        String sqlQuery = "SELECT * FROM public.buffer";
        List<LinkedHashMap<String, Object>> values ;
        HashMap<String, String> error = new HashMap<>();

        try{
            values = usersDao.getAll(sqlQuery);
            if (values.size() > 0) {
                ApiResponse.sendResponse(exchange, values, StatusCodes.OK);
            }
            else {
                error.put("Message", "No records found");
                ApiResponse.sendResponse(exchange,error, StatusCodes.OK);
            }
        }catch (Exception e){
            e.printStackTrace();
            error.put("Error", e.getMessage());
            ApiResponse.sendResponse(exchange,error, StatusCodes.INTERNAL_SERVER_ERROR);
        }

    }
}
