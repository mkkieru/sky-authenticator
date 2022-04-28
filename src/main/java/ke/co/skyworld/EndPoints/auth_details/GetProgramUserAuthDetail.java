//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.auth_details;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;
import org.postgresql.util.PGInterval;

public class GetProgramUserAuthDetail implements HttpHandler {
    public GetProgramUserAuthDetail() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String sqlQuery = "SELECT age(now(),date_updated), auth_code ,time_to_live, time_to_live_units,program_id FROM public.auth_details WHERE identifier = (:identifier) and program_id = (:program_id) and status = 'ACTIVE'";
        String sqlQuery2 = "SELECT * FROM public.programs WHERE program_name = (:program_name)";
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        QueryManager queryManager = new QueryManager();
        Gson gson = new Gson();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap<String, Object> userValues = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);
        String programName = (String)userValues.get("program_name");
        if (programName != null && !programName.trim().equals("")) {
            try {
                error.put("program_name", programName);
                Object obj = queryManager.getSpecific(sqlQuery2, error).get("program_id");
                String id = String.valueOf(obj);
                error.clear();
                if (id.equals(userValues.get("program_id"))) {
                    userValues.replace("program_id", obj);
                    userValues.remove("program_name");
                    LinkedHashMap<String, Object> specificAuthDetails = queryManager.getUserSpecificAuthDetail(sqlQuery, userValues);
                    PGInterval interval = (PGInterval)specificAuthDetails.get("age");
                    if (specificAuthDetails.isEmpty()) {
                        error.put("Error", ResponseCodes.EMPTY);
                        ApiResponse.sendResponse(exchange, error, 404);
                        return;
                    }

                    specificAuthDetails.replace("age", interval.getWholeSeconds());
                    ApiResponse.sendResponse(exchange, specificAuthDetails, 200);
                    return;
                }

                error.put("Error", ResponseCodes.UNAUTHORIZED);
                error.put("Message", "Invalid program ID");
                ApiResponse.sendResponse(exchange, error, 401);
            } catch (Exception var14) {
                var14.printStackTrace();
                error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
                ApiResponse.sendResponse(exchange, error, 500);
            }

        } else {
            error.put("Error", ResponseCodes.UNAUTHORIZED);
            error.put("Message", "Missing header value");
        }
    }
}
