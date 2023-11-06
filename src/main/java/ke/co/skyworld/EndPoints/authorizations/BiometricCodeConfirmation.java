//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.authorizations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.EndPoints.SSE_CHANNEL.SSEHandler;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Set;

public class BiometricCodeConfirmation extends ServerSentEventHandler {
    public BiometricCodeConfirmation() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery = "select * from biometric_confirmation where identifier = (:identifier) and identifier_type = (:identifier_type) and program_id = (:program_id)";
        String sqlQuery2 = "update biometric_confirmation set status = 'VERIFIED' where identifier = (:identifier) and identifier_type = (:identifier_type) and program_id = (:program_id)";
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        Set<ServerSentEventConnection> connections;
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        QueryManager usersDao = new QueryManager();
        Gson gson = new Gson();
        LinkedHashMap userDetails = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        try {
            LinkedHashMap<String, Object> dbResults = usersDao.getUserSpecificAuthDetail(sqlQuery, userDetails);
            if (!dbResults.isEmpty()) {
                usersDao.update(sqlQuery2, userDetails);
                error.put("Message", ResponseCodes.SUCCESS);
                SSEHandler.sendToSpecificUser(userDetails.get("program_id").toString(), "BIOMETRIC_AUTH_STATUS", "4", null);

//                connections = AddToBiometricTable.connections;
//
//                for (ServerSentEventConnection conn : connections) {
//                    System.out.println("here");
//                    if (conn.getRequestHeaders().get("program_id").getFirst().equals(userDetails.get("program_id")) &&
//                            conn.getRequestHeaders().get("identifier").getFirst().equals(userDetails.get("identifier")) &&
//                            conn.getRequestHeaders().get("identifier_type").getFirst().equals(userDetails.get("identifier_type"))){
//                        conn.send("AUTHORIZED SUCCESSFULLY","BIOMETRIC_AUTH_STATUS","4",null);
//                    }
//                }

                ApiResponse.sendResponse(httpServerExchange, error, 200);
            } else {
                error.put("Error", ResponseCodes.EMPTY);
                error.put("Message", "Wrong Details Provided");
                ApiResponse.sendResponse(httpServerExchange, error, 400);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
            error.put("Message", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 400);
        }

    }
}
