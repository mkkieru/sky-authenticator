package ke.co.skyworld.authorizations;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.ApiResponse;
import ke.co.skyworld.ExchangeUtils;
import ke.co.skyworld.query_manager.Query_manager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Auth_code_confirmation implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        String sqlQuery = "SELECT * FROM public.access_token WHERE user_id = (:user_id) and access_token.access_token = (:access_token)";

        Query_manager usersDao = new Query_manager();
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();

        HashMap<String, Object> query;
        HashMap<String, Object> result;
        HashMap<String, Object> response = new HashMap<>();

        query = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange),type);

        result = usersDao.getSpecific(sqlQuery, (LinkedHashMap<String, Object>) query);

        if (!result.isEmpty()){

            response.put("Message", "User authorization successful");
            System.out.println("Authorization successful ... ");
            ApiResponse.sendResponse(httpServerExchange,response, StatusCodes.OK);

        }
        else {
            response.put("Error", "wrong access token provided");
            ApiResponse.sendResponse(httpServerExchange,response, StatusCodes.UNAUTHORIZED);
        }
    }
}
