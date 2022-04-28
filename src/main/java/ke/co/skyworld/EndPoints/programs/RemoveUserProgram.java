//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.programs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class RemoveUserProgram implements HttpHandler {
    public RemoveUserProgram() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery = "delete from user_identifier_programs where program_id = (:program_id) and identifier = (:identifier) and user_id = (:user_id)";
        String sqlQuery2 = "delete from auth_details where program_id = (:program_id) and identifier = (:identifier) and user_id = (:user_id)";
        String sqlQuery3 = "delete from buffer where program_id = (:program_id) and identifier = (:identifier)";
        QueryManager queryManager = new QueryManager();
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        Gson gson = new Gson();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap userDetails = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        try {
            queryManager.delete(sqlQuery, userDetails);
            queryManager.delete(sqlQuery2, userDetails);
            userDetails.remove("user_id");
            queryManager.delete(sqlQuery3, userDetails);
            error.put("Message", ResponseCodes.UPDATED);
            ApiResponse.sendResponse(httpServerExchange, error, 200);
        } catch (Exception var11) {
            var11.printStackTrace();
            error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 500);
        }

    }
}
