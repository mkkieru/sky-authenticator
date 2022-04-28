//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.buffer;

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

public class GetSpecificFromBuffer implements HttpHandler {
    public GetSpecificFromBuffer() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        QueryManager usersDao = new QueryManager();
        String sqlQuery = "SELECT * FROM public.buffer where token = (:token) and identifier_type = (:identifier_type) and identifier = (:identifier) and program_id = (:program_id)";
        HashMap<String, Object> error = new HashMap();
        Gson json = new Gson();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap bufferDetails = (LinkedHashMap)json.fromJson(ExchangeUtils.getRequestBody(exchange), type);

        try {
            HashMap<String, Object> values = usersDao.getSpecific(sqlQuery, bufferDetails);
            if (values.size() > 0) {
                ApiResponse.sendResponse(exchange, values, 200);
            } else {
                error.put("Message", ResponseCodes.EMPTY);
                ApiResponse.sendResponse(exchange, error, 200);
            }
        } catch (Exception var10) {
            var10.printStackTrace();
            error.put("Error", var10.getMessage());
            ApiResponse.sendResponse(exchange, error, 500);
        }

    }
}
