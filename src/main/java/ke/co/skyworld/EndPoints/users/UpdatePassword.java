package ke.co.skyworld.EndPoints.users;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class UpdatePassword implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery = "Update public.user set password = :password , password_updated = 'YES' where user_id = :user_id";
        LinkedHashMap<String, Object> values;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        QueryManager queryManager = new QueryManager();
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType();

        values = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        try {
            String password = values.get("password").toString();
            String hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
            values.replace("password", hashedPassword);
            queryManager.update(sqlQuery, values);
            response.put("Message","Password updated successfully");
            ApiResponse.sendResponse(httpServerExchange,response,StatusCodes.OK);


        } catch (Exception e) {
            e.printStackTrace();
            response.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, response, StatusCodes.INTERNAL_SERVER_ERROR);
        }

    }
}
