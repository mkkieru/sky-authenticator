//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.users;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.util.HashMap;
import java.util.LinkedHashMap;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class GetSpecificUser implements HttpHandler {
    public GetSpecificUser() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        HashMap<String, Object> error = new HashMap();
        String access_token = httpServerExchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            QueryManager usersDao = new QueryManager();
            String sqlQuery = "SELECT * FROM public.user WHERE user_id = :user_id";
            LinkedHashMap<String, Object> value = new LinkedHashMap();
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(httpServerExchange, access_token, String.valueOf(httpServerExchange.getSourceAddress()));
            if (responseCodes != ResponseCodes.ERROR) {
                if (responseCodes == ResponseCodes.SUCCESS) {
                    value.put("user_id", Integer.parseInt(ExchangeUtils.getPathVar(httpServerExchange, "user_id")));

                    try {
                        HashMap<String, Object> returnedValues = usersDao.getSpecific(sqlQuery, value);
                        if (returnedValues.size() > 0) {
                            ApiResponse.sendResponse(httpServerExchange, returnedValues, 200);
                        } else {
                            error.put("Message", ResponseCodes.EMPTY);
                            ApiResponse.sendResponse(httpServerExchange, error, 200);
                        }
                    } catch (Exception var10) {
                        var10.printStackTrace();
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
