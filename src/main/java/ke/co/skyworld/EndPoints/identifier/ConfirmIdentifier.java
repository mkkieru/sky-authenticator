package ke.co.skyworld.EndPoints.identifier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.*;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.StatusCodesUpdaters.StatusCodesRefresher;
import ke.co.skyworld.XmlReader.XmlReader;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.Query_manager;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class confirmIdentifier implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        LinkedHashMap<String, String> error = new LinkedHashMap<>();
        LinkedHashMap<String, String> success = new LinkedHashMap<>();
        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();

        if (access_token == null || access_token.equals("")) {
            error.put("Error", "Access token is required ");
            error.put("Message", "If you do not have a token please login to get one.");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        }


        String sqlquery = "SELECT * FROM public.identifier_confirmation_table WHERE program_id = (:program_id) and token = (:token)";
        String sqlquery2 = "UPDATE public.identifier_confirmation_table SET status = 'ACTIVATED' WHERE program_id = (:program_id) and token = (:token)";
        String sqlquery3 = "INSERT INTO public.auth_details (user_id,program_id,auth_code,time_to_live,time_to_live_units) VALUES(:user_id,:program_id,:auth_code,:time_to_live,:time_to_live_units)";

        LinkedHashMap<String, Object> values;
        LinkedHashMap<String, Object> values2 = new LinkedHashMap<>();
        Query_manager usersDao = new Query_manager();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType();

        ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));

        if (responseCodes == ResponseCodes.ERROR) {
            error.put("Error", "Please log in to continue ");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return;
        } else {

            values = new Gson().fromJson(ExchangeUtils.getRequestBody(exchange), type);

            values2.put("user_id", values.get("user_id"));
            values.remove("user_id");

            try {

                if (!usersDao.getSpecific(sqlquery, values).isEmpty()) {

                    usersDao.update(sqlquery2, values);

                    values2.put("program_id", values.get("program_id"));
                    values2.put("auth_code", StatusCodesRefresher.genAuthCode());
                    values2.put("time_to_live", XmlReader.AUTH_CODE_TTL);
                    values2.put("time_to_live_units", XmlReader.AUTH_CODE_TTL_UNITS);
                    System.out.println(values2.get("user_id"));
                    usersDao.add(sqlquery3, values2);

                    success.put("Message", "Status code sent");
                    ApiResponse.sendResponse(exchange, success, StatusCodes.BAD_REQUEST);


                } else {
                    error.put("Error", "No program with that token found");
                    ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
                }
            } catch (Exception e) {
                e.printStackTrace();
                error.put("Error", e.getMessage());
                ApiResponse.sendResponse(exchange, error, StatusCodes.INTERNAL_SERVER_ERROR);
            }

        }

    }
}
