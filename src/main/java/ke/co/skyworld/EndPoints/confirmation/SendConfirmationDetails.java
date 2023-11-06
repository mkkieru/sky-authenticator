//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.confirmation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class SendConfirmationDetails implements HttpHandler {
    public SendConfirmationDetails() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        Gson gson = new Gson();
        HashMap<String, Object> error = new HashMap();
        QueryManager queryManager = new QueryManager();
        String sqlQuery = "SELECT i.user_id,i.identifier, i.identifier_type , a.program_id , a.auth_code\n" +
                "FROM public.identifier AS i \n" +
                "INNER JOIN public.auth_details AS a\n" +
                "ON i.user_id = a.user_id\n" +
                "WHERE a.auth_code = (:auth_code) and i.identifier = (:identifier) and a.identifier = (:identifier) and i.identifier_type = (:identifier_type) and a.program_id = (:program_id) and a.status = 'ACTIVE'";
        String sqlQuery2 = "update biometric_confirmation set status = 'VERIFIED' where identifier = (:identifier) and identifier_type = (:identifier_type) and program_id = (:program_id)";
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap userDetails = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);



        try {
            HashMap<String, Object> returnedValues = queryManager.getSpecific(sqlQuery, userDetails);
            if (!returnedValues.isEmpty()) {
                userDetails.remove("auth_code");
                queryManager.update(sqlQuery2, userDetails);
                ApiResponse.sendResponse(httpServerExchange, returnedValues, 200);
            } else {
                error.put("Message", ResponseCodes.EMPTY);
                ApiResponse.sendResponse(httpServerExchange, error, 401);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
            error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 500);
        }

    }
}
