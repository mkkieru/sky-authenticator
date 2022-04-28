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
import java.util.HashMap;
import java.util.LinkedHashMap;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class GetProgramByProgramID implements HttpHandler {
    public GetProgramByProgramID() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery = "select * from programs where program_id = (:program_id)";
        QueryManager queryManager = new QueryManager();
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        Gson gson = new Gson();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap values = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        try {
            HashMap<String, Object> programDetails = queryManager.getSpecific(sqlQuery, values);
            if (!programDetails.isEmpty()) {
                ApiResponse.sendResponse(httpServerExchange, programDetails, 200);
                return;
            }

            error.put("Error", ResponseCodes.EMPTY);
            ApiResponse.sendResponse(httpServerExchange, error, 400);
        } catch (Exception var10) {
            var10.printStackTrace();
            error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 500);
        }

    }
}
