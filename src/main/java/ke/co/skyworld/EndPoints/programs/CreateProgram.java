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
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class CreateProgram implements HttpHandler {
    public CreateProgram() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HashMap<String, Object> error = new HashMap();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            QueryManager usersDao = new QueryManager();
            String sqlQuery = "INSERT INTO public.programs (program_id,program_name) VALUES (:program_id,:program_name) RETURNING program_id";
            String sqlQuery1 = "SELECT * FROM public.programs WHERE program_id = :program_id";
            Gson gson = new Gson();
            Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
            }).getType();
            LinkedHashMap<String, Object> value = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, String.valueOf(exchange.getSourceAddress()));
            if (responseCodes != ResponseCodes.ERROR) {
                if (responseCodes == ResponseCodes.SUCCESS) {
                    if (!value.containsKey("program_id")) {
                        error.put("error", ResponseCodes.MISSING_FIELD);
                        error.put("Message", "Please the field 'program_id'");
                        ApiResponse.sendResponse(exchange, error, 400);
                        return;
                    }

                    if (!value.containsKey("program_name")) {
                        error.put("error", ResponseCodes.MISSING_FIELD);
                        error.put("Message", "Please the field 'program_name'");
                        ApiResponse.sendResponse(exchange, error, 400);
                        return;
                    }

                    try {
                        Object object = usersDao.add(sqlQuery, value);
                        LinkedHashMap<String, Object> map = new LinkedHashMap();
                        map.put("program_id", object);
                        ApiResponse.sendResponse(exchange, usersDao.getSpecific(sqlQuery1, map), 200);
                    } catch (Exception var13) {
                        var13.printStackTrace();
                        error.put("message", ResponseCodes.SOMETHING_WENT_WRONG);
                        ApiResponse.sendResponse(exchange, error, 500);
                    }
                }

            }
        } else {
            error.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
            ApiResponse.sendResponse(exchange, error, 400);
        }
    }
}
