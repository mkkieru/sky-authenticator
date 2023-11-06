//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.identifier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.StatusCodesUpdaters.StatusCodesRefresher;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.XmlReader.XmlReader;
import ke.co.skyworld.query_manager.QueryManager;

public class ConfirmIdentifier implements HttpHandler {
    public ConfirmIdentifier() {
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        LinkedHashMap<String, Object> success = new LinkedHashMap();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            String sqlquery = "SELECT * FROM public.buffer WHERE identifier_type = (:identifier_type) and identifier = (:identifier) and token = (:token)";
            String sqlquery2 = "UPDATE public.buffer SET status = 'ACTIVATED' WHERE identifier_type = (:identifier_type) and identifier = (:identifier) and token = (:token)";
            String sqlquery3 = "INSERT INTO public.auth_details (user_id,program_id,auth_code,time_to_live,time_to_live_units,identifier,identifier_type) VALUES(:user_id,:program_id,:auth_code,:time_to_live,:time_to_live_units,:identifier,:identifier_type) returning user_id";
            String sqlquery4 = "UPDATE public.identifier SET state = 'ENABLED ' WHERE identifier_type = (:identifier_type) and identifier = (:identifier) and user_id = (:user_id)";
            String sqlquery5 = "INSERT INTO public.user_identifier_programs (user_id,identifier,program_id,identifier_type) VALUES(:user_id,:identifier,:program_id,:identifier_type) returning id";
            LinkedHashMap<String, Object> values2 = new LinkedHashMap();
            QueryManager usersDao = new QueryManager();
            Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
            }).getType();
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));
            if (responseCodes != ResponseCodes.ERROR) {
                LinkedHashMap<String, Object> values = (LinkedHashMap)(new Gson()).fromJson(ExchangeUtils.getRequestBody(exchange), type);
                System.out.println("User details : "+values);
                Object token = values.get("token");
                Object userId = values.get("user_id");
                Object identifier_type = values.get("identifier_type");
                Object identifier = values.get("identifier");
                LinkedHashMap<String, Object> valuesToUpdateIdentifier = values;
                values.remove("token");
                values2.put("user_id", userId);
                values.remove("user_id");
                values.put("token", token);

                try {
                    HashMap<String, Object> bufferDetails = usersDao.getSpecific(sqlquery, values);

                    System.out.println("Database Results : "+bufferDetails);
                    if (!bufferDetails.isEmpty()) {
                        Object programId = bufferDetails.get("program_id");
                        usersDao.update(sqlquery2, values);
                        valuesToUpdateIdentifier.remove("token");
                        valuesToUpdateIdentifier.put("user_id", userId);
                        usersDao.update(sqlquery4, valuesToUpdateIdentifier);
                        values2.put("program_id", programId);
                        values2.put("auth_code", StatusCodesRefresher.genAuthCode(0));
                        values2.put("time_to_live", XmlReader.AUTH_CODE_TTL);
                        values2.put("time_to_live_units", XmlReader.AUTH_CODE_TTL_UNITS);
                        values2.put("identifier_type", identifier_type);
                        values2.put("identifier", identifier);
                        System.out.println(values2.get("user_id"));
                        usersDao.add(sqlquery3, values2);
                        values2.clear();
                        values2.put("user_id", userId);
                        values2.put("identifier", identifier);
                        values2.put("program_id", programId);
                        values2.put("identifier_type", identifier_type);
                        usersDao.add(sqlquery5, values2);
                        success.put("Message", ResponseCodes.SUCCESS);
                        ApiResponse.sendResponse(exchange, success, 200);
                    } else {
                        error.put("Error", ResponseCodes.EMPTY);
                        ApiResponse.sendResponse(exchange, error, 400);
                    }
                } catch (Exception var22) {
                    System.out.println("Here ... ");
                    var22.printStackTrace();
                    error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
                    error.put("Message", var22.getMessage());
                    ApiResponse.sendResponse(exchange, error, 500);
                }

            }
        } else {
            error.put("Error", "Access token is required ");
            error.put("Message", "If you do not have a token please login to get one.");
            ApiResponse.sendResponse(exchange, error, 400);
        }
    }
}
