//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.identifier;

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

public class DeleteIdentifier implements HttpHandler {
    public DeleteIdentifier() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery = "delete from identifier where user_id = (:user_id) and identifier_type= (:identifier_type) and identifier = (:identifier)";
        String sqlQuery2 = "delete from auth_details where user_id = (:user_id) and identifier_type= (:identifier_type) and identifier = (:identifier)";
        String sqlQuery3 = "delete from public.user_identifier_programs where user_id = (:user_id) and identifier_type= (:identifier_type) and identifier = (:identifier)";
        QueryManager queryManager = new QueryManager();
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        Gson gson = new Gson();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap userDetails = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        try {
            queryManager.update(sqlQuery, userDetails);
            queryManager.update(sqlQuery2, userDetails);
            queryManager.update(sqlQuery3, userDetails);
            error.put("Message", ResponseCodes.DELETED);
            ApiResponse.sendResponse(httpServerExchange, error, 200);
        } catch (Exception var11) {
            var11.printStackTrace();
            error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 500);
        }

    }
}
