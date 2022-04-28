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
import java.util.LinkedHashMap;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class BiometricCodeConfirmation implements HttpHandler {
    public BiometricCodeConfirmation() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery = "select * from biometric_confirmation where identifier = (:identifier) and identifier_type = (:identifier_type) and program_id = (:program_id)";
        String sqlQuery2 = "update biometric_confirmation set status = 'AUTHORIZED' where identifier = (:identifier) and identifier_type = (:identifier_type) and program_id = (:program_id)";
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        QueryManager usersDao = new QueryManager();
        Gson gson = new Gson();
        LinkedHashMap userDetails = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        try {
            LinkedHashMap<String, Object> dbResults = usersDao.getUserSpecificAuthDetail(sqlQuery, userDetails);
            if (!dbResults.isEmpty()) {
                usersDao.update(sqlQuery2, userDetails);
                error.put("Message", ResponseCodes.SUCCESS);
                ApiResponse.sendResponse(httpServerExchange, error, 200);
            } else {
                error.put("Message", ResponseCodes.EMPTY);
                ApiResponse.sendResponse(httpServerExchange, error, 400);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
            error.put("Message", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 400);
        }

    }
}
