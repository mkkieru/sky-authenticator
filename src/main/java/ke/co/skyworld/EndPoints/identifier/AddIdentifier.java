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
import java.util.HashMap;
import java.util.LinkedHashMap;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class AddIdentifier implements HttpHandler {
    public AddIdentifier() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HashMap<String, Object> error = new HashMap();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));
        if (responseCodes != ResponseCodes.ERROR) {
            if (responseCodes == ResponseCodes.SUCCESS) {
                if (access_token == null || access_token.equals("")) {
                    error.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
                    ApiResponse.sendResponse(exchange, error, 400);
                    return;
                }

                QueryManager usersDao = new QueryManager();
                String sqlQuery = "INSERT INTO public.identifier (user_id,identifier_type, identifier,state) VALUES (:user_id,:identifier_type,:identifier,'DISABLED') returning identifier_id";
                Gson gson = new Gson();
                Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
                }).getType();
                LinkedHashMap<String, Object> values = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);
                if (!values.containsKey("identifier_type")) {
                    error.put("Error", ResponseCodes.MISSING_FIELD);
                    error.put("Message", "Please input field 'identifier_type'");
                    ApiResponse.sendResponse(exchange, error, 400);
                    return;
                }

                if (!values.containsKey("identifier")) {
                    error.put("Error", ResponseCodes.MISSING_FIELD);
                    error.put("Message", "Please input field 'identifier'");
                    ApiResponse.sendResponse(exchange, error, 400);
                    return;
                }

                try {
                    Object object = usersDao.add(sqlQuery, values);
                    ApiResponse.sendResponse(exchange, object, 200);
                } catch (Exception var11) {
                    if (var11.getMessage().contains("duplicate key value violates")) {
                        error.put("Error", ResponseCodes.DUPLICATE);
                        ApiResponse.sendResponse(exchange, error, 400);
                        return;
                    }

                    var11.printStackTrace();
                    error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
                    ApiResponse.sendResponse(exchange, error, 500);
                }
            }

        }
    }
}
