//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.authorizations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.*;

import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

//public class ProgramBiometricConfirmation implements HttpHandler {
public class ProgramBiometricConfirmation extends ServerSentEventHandler {
    public ProgramBiometricConfirmation() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery = "select * from biometric_confirmation where identifier = (:identifier) and identifier_type = (:identifier_type) and program_id = (:program_id)";
        String sqlQuery2 = "update biometric_confirmation set status = 'VERIFIED' where identifier = (:identifier) and identifier_type = (:identifier_type) and program_id = (:program_id)";
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        QueryManager usersDao = new QueryManager();
        Gson gson = new Gson();
        LinkedHashMap userDetails = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        //Connection connections =  httpServerExchange.getConnection();

        try {
            LinkedHashMap<String, Object> dbResults = usersDao.getUserSpecificAuthDetail(sqlQuery, userDetails);
            if (!dbResults.isEmpty()) {
                if (dbResults.get("status").equals("AUTHORIZED")) {
                    usersDao.update(sqlQuery2, userDetails);
                    error.put("Message", ResponseCodes.SUCCESS);
                    ApiResponse.sendResponse(httpServerExchange, error, 200);
                } else {
                    error.put("Message", ResponseCodes.PENDING);
                    ApiResponse.sendResponse(httpServerExchange, error, 202);
                }
            } else {
                error.put("Message", ResponseCodes.EMPTY);
                ApiResponse.sendResponse(httpServerExchange, error, 400);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
            error.put("Message", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 500);
        }

    }
}
