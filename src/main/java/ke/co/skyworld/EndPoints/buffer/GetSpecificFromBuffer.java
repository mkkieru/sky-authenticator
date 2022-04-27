package ke.co.skyworld.EndPoints.identifier_confirmation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GetIdentifierConfirmation implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        QueryManager usersDao = new QueryManager();
        String sqlQuery = "SELECT * FROM public.buffer where token = (:token) and identifier_type = (:identifier_type) and identifier = (:identifier) and program_id = (:program_id)";
        String sqlQuery2 = "insert into auth_details (user_id,program_id,auth_code,time_to_live,time_to_live_units) values (58,100000,333333,30,'seconds');";
        HashMap<String, Object> values ;
        LinkedHashMap<String, Object> bufferDetails;
        HashMap<String, String> error = new HashMap<>();
        Gson json = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType();

        bufferDetails = json.fromJson(ExchangeUtils.getRequestBody(exchange),type);

        try{
            values = usersDao.getSpecific(sqlQuery,bufferDetails);
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
