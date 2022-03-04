package ke.co.skyworld.EndPoints.confirmation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.query_manager.Query_manager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SendConfirmationDetails implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        Gson gson = new Gson();
        HashMap<String, Object> returnedValues;
        LinkedHashMap<String, Object> userDetails;
        HashMap<String, Object> error = new HashMap<>();
        Query_manager queryManager = new Query_manager();
        String sqlQuery =
                "SELECT i.user_id,i.identifier, i.identifier_type , a.program_id , a.auth_code\n" +
                        "FROM public.identifier AS i \n" +
                        "INNER JOIN public.auth_details AS a\n" +
                        "ON i.user_id = a.user_id\n" +
                        "WHERE a.auth_code = (:auth_code) and i.identifier = (:identifier) and i.identifier_type = (:identifier_type) and a.program_id = (:program_id);";

        Type type = new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType();

        userDetails = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        try {

            returnedValues = queryManager.getSpecific(sqlQuery, userDetails);

            if (!returnedValues.isEmpty()) {


                ApiResponse.sendResponse(httpServerExchange, returnedValues, StatusCodes.OK);

            } else {
                error.put("Message", "Auth code for that program and identifier details does not exist.");
                ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.UNAUTHORIZED);

            }
        }catch (Exception e){
            e.printStackTrace();
            error.put("Error", e.getMessage());
            ApiResponse.sendResponse(httpServerExchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
