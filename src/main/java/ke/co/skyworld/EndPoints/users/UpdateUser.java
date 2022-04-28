//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.users;

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

public class UpdateUser implements HttpHandler {
    HashMap<String, Object> error = new HashMap();

    public UpdateUser() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String access_token = httpServerExchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            String sqlQuery = "UPDATE public.user SET first_name = (:first_name), last_name =(:last_name), national_id = (:national_id), date_updated = current_timestamp WHERE user_id = (:user_id)";
            Gson gson = new Gson();
            QueryManager usersDao = new QueryManager();
            Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
            }).getType();
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(httpServerExchange, access_token, String.valueOf(httpServerExchange.getSourceAddress()));
            if (responseCodes != ResponseCodes.ERROR) {
                if (responseCodes == ResponseCodes.SUCCESS) {
                    LinkedHashMap<String, Object> values = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);
                    if (this.validateInput(values, httpServerExchange) == ResponseCodes.ERROR) {
                        return;
                    }

                    try {
                        usersDao.update(sqlQuery, values);
                    } catch (Exception var10) {
                        var10.printStackTrace();
                        this.error.put("error", ResponseCodes.SOMETHING_WENT_WRONG);
                        ApiResponse.sendResponse(httpServerExchange, this.error, 500);
                    }
                }

            }
        } else {
            this.error.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
            ApiResponse.sendResponse(httpServerExchange, this.error, 400);
        }
    }

    private Enum validateInput(LinkedHashMap<String, Object> values, HttpServerExchange httpServerExchange) {
        if (!values.containsKey("first_name")) {
            this.error.put("error", ResponseCodes.MISSING_FIELD);
            this.error.put("Message", "Please provide field 'first_name'");
            ApiResponse.sendResponse(httpServerExchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("last_name")) {
            this.error.put("error", ResponseCodes.MISSING_FIELD);
            this.error.put("Message", "Please provide field 'last_name'");
            ApiResponse.sendResponse(httpServerExchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("national_id")) {
            this.error.put("error", ResponseCodes.MISSING_FIELD);
            this.error.put("Message", "Please provide field 'national_id'");
            ApiResponse.sendResponse(httpServerExchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else if (!values.containsKey("user_id")) {
            this.error.put("error", ResponseCodes.MISSING_FIELD);
            this.error.put("Message", "Please provide field 'user_id'");
            ApiResponse.sendResponse(httpServerExchange, this.error, 400);
            return ResponseCodes.ERROR;
        } else {
            return ResponseCodes.SUCCESS;
        }
    }
}
