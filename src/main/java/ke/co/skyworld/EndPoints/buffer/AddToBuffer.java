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

public class AddToBuffer implements HttpHandler {
    HashMap<String, Object> error = new HashMap();

    public AddToBuffer() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String sqlQuery = "INSERT INTO public.buffer (program_id,token,identifier_type,identifier) VALUES (:program_id,:token,:identifier_type,:identifier) RETURNING buffer_id";
        QueryManager usersDao = new QueryManager();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap<String, Object> values = (LinkedHashMap)(new Gson()).fromJson(ExchangeUtils.getRequestBody(exchange), type);
        if (this.checkInputFields(values, exchange) != ResponseCodes.ERROR) {
            try {
                usersDao.add(sqlQuery, values);
                ApiResponse.sendResponse(exchange, values, 201);
            } catch (Exception var7) {
                if (var7.getMessage().contains("duplicate key value violates")) {
                    this.error.put("Error", ResponseCodes.DUPLICATE);
                    ApiResponse.sendResponse(exchange, this.error, 400);
                    return;
                }

                var7.printStackTrace();
                this.error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
                ApiResponse.sendResponse(exchange, this.error, 500);
            }

        }
    }

    private Enum checkInputFields(LinkedHashMap<String, Object> values, HttpServerExchange exchange) {
        if (!values.containsKey("program_id")) {
            this.error.put("error", ResponseCodes.MISSING_FIELD);
            this.error.put("message", "Please provide field 'program_id'");
            ApiResponse.sendResponse(exchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("token")) {
            this.error.put("error", ResponseCodes.MISSING_FIELD);
            this.error.put("message", "Please provide field 'token'");
            ApiResponse.sendResponse(exchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("identifier_type")) {
            this.error.put("error", ResponseCodes.MISSING_FIELD);
            this.error.put("message", "Please provide field 'identifier_type'");
            ApiResponse.sendResponse(exchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("identifier")) {
            this.error.put("error", ResponseCodes.MISSING_FIELD);
            this.error.put("message", "Please provide field 'identifier'");
            ApiResponse.sendResponse(exchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else {
            return ResponseCodes.SUCCESS;
        }
    }
}
