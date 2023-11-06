//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.authorizations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class CheckAccessTokenValidity implements HttpHandler {
    public CheckAccessTokenValidity() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
//        String sqlQuery = "Select age(now(),date_created),user_id  from access_token where user_id = (:user_id) and ip_address = (:ip_address)";
        String sqlQuery = "Select age(now(),date_created),user_id  from access_token where user_id = (:user_id) ";
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        QueryManager usersDao = new QueryManager();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        Gson gson = new Gson();
        LinkedHashMap userDetails = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        userDetails.remove("ip_address");

        try {
            LinkedHashMap<String, Object> databaseResult = usersDao.getUserSpecificAuthDetail(sqlQuery, userDetails);
            if (!databaseResult.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Integer> age = (Map)objectMapper.convertValue(databaseResult.get("age"), Map.class);
//                int ageDays = (Integer)age.get("days");
//                if (ageDays >= 14) {
//                    error.put("Message", ResponseCodes.ACCESS_TOKEN_EXPIRED);
//                    ApiResponse.sendResponse(httpServerExchange, error, 401);
//                    return;
//                }

                error.put("Message", ResponseCodes.ACCESS_TOKEN_IS_VALID);
                ApiResponse.sendResponse(httpServerExchange, error, 200);
                return;
            }

            error.put("Message", ResponseCodes.EMPTY);
            ApiResponse.sendResponse(httpServerExchange, error, 400);
        } catch (Exception var12) {
            var12.printStackTrace();
            error.put("Message", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 500);
        }

    }
}
