//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.auth_details;

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

public class DisableAuthCode implements HttpHandler {
    public DisableAuthCode() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery = "update auth_details set status = (:status) where user_id = (:user_id) and identifier = (:identifier) and program_id = (:program_id)";
        String sqlQuery2 = "update user_identifier_programs set status = (:status) where user_id = (:user_id) and identifier = (:identifier) and program_id = (:program_id)";
        QueryManager queryManager = new QueryManager();
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        Gson gson = new Gson();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap userDetails = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        try {
            queryManager.update(sqlQuery, userDetails);
            queryManager.update(sqlQuery2, userDetails);
            error.put("Message", ResponseCodes.UPDATED);
            ApiResponse.sendResponse(httpServerExchange, error, 200);
        } catch (Exception var10) {
            var10.printStackTrace();
            error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 200);
        }

    }
}
