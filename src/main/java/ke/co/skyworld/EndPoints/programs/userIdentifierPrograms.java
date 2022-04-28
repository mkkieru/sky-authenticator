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
import java.util.List;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class userIdentifierPrograms implements HttpHandler {
    public userIdentifierPrograms() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery1 = "select program_id,identifier,status  from public.user_identifier_programs where user_id = (:user_id)";
        QueryManager queryManager = new QueryManager();
        Gson gson = new Gson();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        LinkedHashMap userValues = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        try {
            List<LinkedHashMap<String, Object>> dbValues = queryManager.getSpecificUsers(sqlQuery1, userValues);
            if (!dbValues.isEmpty()) {
                ApiResponse.sendResponse(httpServerExchange, dbValues, 200);
                return;
            }

            error.put("Message", ResponseCodes.EMPTY);
            ApiResponse.sendResponse(httpServerExchange, error, 400);
        } catch (Exception var10) {
            var10.printStackTrace();
            error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 500);
        }

    }
}
