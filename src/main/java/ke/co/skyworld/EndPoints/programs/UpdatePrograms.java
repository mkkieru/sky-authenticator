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

public class UpdatePrograms implements HttpHandler {
    public UpdatePrograms() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        HashMap<String, Object> error = new HashMap();
        String access_token = httpServerExchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            String sqlQuery = "UPDATE public.programs SET program_name = (:program_name), date_updated = current_timestamp WHERE program_id = (:program_id) RETURNING program_id";
            Gson gson = new Gson();
            QueryManager usersDao = new QueryManager();
            Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
            }).getType();
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(httpServerExchange, access_token, String.valueOf(httpServerExchange.getSourceAddress()));
            if (responseCodes != ResponseCodes.ERROR) {
                if (responseCodes == ResponseCodes.SUCCESS) {
                    LinkedHashMap<String, Object> value = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);
                    if (!value.containsKey("program_name")) {
                        error.put("Error", ResponseCodes.MISSING_FIELD);
                        error.put("Message", "Please provide field 'program_name'");
                        ApiResponse.sendResponse(httpServerExchange, error, 400);
                        return;
                    }

                    if (!value.containsKey("program_id")) {
                        error.put("Error", ResponseCodes.MISSING_FIELD);
                        error.put("Message", "Please provide field 'program_id'");
                        ApiResponse.sendResponse(httpServerExchange, error, 400);
                        return;
                    }

                    try {
                        usersDao.update(sqlQuery, value);
                        error.put("Message", ResponseCodes.SUCCESS);
                        ApiResponse.sendResponse(httpServerExchange, error, 200);
                    } catch (Exception var11) {
                        var11.printStackTrace();
                        error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
                        ApiResponse.sendResponse(httpServerExchange, error, 500);
                    }
                }

            }
        } else {
            error.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
            ApiResponse.sendResponse(httpServerExchange, error, 400);
        }
    }
}
