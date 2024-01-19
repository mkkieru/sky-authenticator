package ke.co.skyworld.EndPoints.programs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.EndPoints.authorizations.AddToBiometricTable;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

public class GenerateProgramToken implements HttpHandler {


    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery = "insert into program_link_token (token,program_id,identifier,identifier_type) values(:token,:program_id,:identifier,:identifier_type) returning token ";

        LinkedHashMap<String, Object> userDetails;
        LinkedHashMap<String, Object> error = new LinkedHashMap();

        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        QueryManager usersDao = new QueryManager();
        Gson gson = new Gson();

        try {
            userDetails = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);
//            System.out.println("User details: " + userDetails);

            String token = getRandomNumberString();

            userDetails.put("token", token);

            usersDao.add(sqlQuery, userDetails);

            ApiResponse.sendResponse(httpServerExchange, token, 200);

        } catch (Exception var11) {
            var11.printStackTrace();
            error.put("Message", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 500);
        }

    }

    public String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}
