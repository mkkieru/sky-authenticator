package ke.co.skyworld.EndPoints.change_user_codes_status;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class ActivateUserCodes implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        String sqlQuery = "update public.auth_details set status = 'ACTIVE' where user_id = :user_id and status = 'INACTIVE' " ;
        QueryManager queryManager = new QueryManager();
        LinkedHashMap<String, Object> values ;
        LinkedHashMap<String, Object> error = new LinkedHashMap<>();
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();

        values = gson.fromJson (ExchangeUtils.getRequestBody(httpServerExchange),type);
        String access_token = httpServerExchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(httpServerExchange, access_token, httpServerExchange.getSourceAddress().getAddress().toString().replace("/", ""));
            if (responseCodes != ResponseCodes.ERROR) {
                try {
                    queryManager.update(sqlQuery, values);
                    error.put("Message", ResponseCodes.SUCCESS);
                    ApiResponse.sendResponse(httpServerExchange, error, 200);

                } catch (Exception e) {
                    e.printStackTrace();
                    error.put("Error", ResponseCodes.ERROR);
                    ApiResponse.sendResponse(httpServerExchange, error, 500);
                }
            }
        } else {
            error.put("Error", ResponseCodes.ACCESS_TOKEN_REQUIRED);
            ApiResponse.sendResponse(httpServerExchange, error, 400);
        }
    }
}
