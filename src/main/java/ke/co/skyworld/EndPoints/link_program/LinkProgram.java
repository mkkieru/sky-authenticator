package ke.co.skyworld.EndPoints.link_program;

import com.google.common.hash.Hashing;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.CheckAuthCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.StatusCodesUpdaters.StatusCodesRefresher;
import ke.co.skyworld.StatusCodesUpdaters.ThreadPools;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.XmlReader.XmlReader;
import ke.co.skyworld.query_manager.QueryManager;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LinkProgram implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        //TODO: EXPECTED TO GET USER ID FROM EXCHANGE

        //TODO: USE TOKEN FROM EXCHANGE TO GET USER DETAILS FROM BUFFER
        String sqlQuery = "select * from public.program_link_token where token = (:token)";
        String sqlQuery6 = "select * from public.identifier where user_id = :user_id and identifier_type = :identifier_type and identifier = :identifier";
        String sqlQuery2 = "INSERT INTO public.identifier (user_id,identifier_type, identifier,state) VALUES (:user_id,:identifier_type,:identifier,'ENABLED ') returning identifier_id";
        String sqlQuery3 = "UPDATE public.buffer SET status = 'ACTIVATED' WHERE token = (:token)";
        String sqlQuery4 = "INSERT INTO public.auth_details (user_id,program_id,auth_code,time_to_live,time_to_live_units,identifier,identifier_type,time_remaining,isnew) VALUES(:user_id,:program_id,:auth_code,:time_to_live,:time_to_live_units,:identifier,:identifier_type,:time_remaining,:isnew) returning user_id";
        String sqlQuery5 = "INSERT INTO public.user_identifier_programs (user_id,identifier,program_id,identifier_type) VALUES(:user_id,:identifier,:program_id,:identifier_type) returning id";

        LinkedHashMap<String, Object> values;
        HashMap<String, Object> dbResults;
        HashMap<String, Object> tempDBesults;
        HashMap<String, Object> error = new HashMap<>();
        QueryManager queryManager = new QueryManager();
        LinkedHashMap<String, Object> tempValues = new LinkedHashMap<>();
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType();

        String access_token = exchange.getRequestHeaders().get("access_token").getFirst();
        if (access_token != null && !access_token.equals("")) {
            ResponseCodes responseCodes = CheckAuthCodes.checkAndUpdateAccessTokens(exchange, access_token, exchange.getSourceAddress().getAddress().toString().replace("/", ""));
            if (responseCodes != ResponseCodes.ERROR) {

                try {
                    // GET DETAILS FROM BUFFER
                    values = gson.fromJson(ExchangeUtils.getRequestBody(exchange), type);
//                    tempValues.put("token", Hashing.sha256().hashString((String) values.get("token"), StandardCharsets.UTF_8).toString());
                    tempValues.put("token", values.get("token"));
                    dbResults = queryManager.getSpecific(sqlQuery, tempValues);

                    if (values.get("user_id") != null) {
                        if (!dbResults.isEmpty()) {

                            //CHECK IF IDENTIFIER EXISTS AND IF NOT ADD IDENTIFIER
                            tempValues.clear();
                            tempValues.put("identifier", dbResults.get("identifier"));
                            tempValues.put("user_id", values.get("user_id"));
                            tempValues.put("identifier_type", dbResults.get("identifier_type"));
                            tempDBesults = queryManager.getSpecific(sqlQuery6, tempValues);
                            if (tempDBesults.isEmpty()) {
                                queryManager.add(sqlQuery2, tempValues);
                            }

                            //GET CURRENT SECONDS FROM SYSTEM DATE AND TIME
                            int timeRemaining = 0;

                            LocalDateTime timeServerStarted = ThreadPools.timeServerStarted;
                            LocalDateTime timeNow = LocalDateTime.now();

                            int secondServerStarted = timeServerStarted.getSecond();
                            int secondNow = timeNow.getSecond();

                            if (secondNow < secondServerStarted) {
                                timeRemaining = secondServerStarted - secondNow;
                                System.out.println("Here 1 " + secondNow);
                            } else if (secondNow == secondServerStarted) {
                                timeRemaining = 0;
                                System.out.println("Here 2 " + secondNow);
                            } else {
                                timeRemaining = (secondServerStarted + 60) - secondNow;
                                System.out.println("Here 3 " + secondNow);
                            }


                            //ADD TO AUTH DETAILS
                            tempValues.clear();
                            tempValues.put("user_id", values.get("user_id"));
                            tempValues.put("program_id", dbResults.get("program_id"));
                            tempValues.put("auth_code", StatusCodesRefresher.genAuthCode(0));
                            tempValues.put("time_to_live", XmlReader.AUTH_CODE_TTL);
                            tempValues.put("time_remaining", timeRemaining);
                            tempValues.put("time_to_live_units", XmlReader.AUTH_CODE_TTL_UNITS);
                            tempValues.put("identifier", dbResults.get("identifier"));
                            tempValues.put("identifier_type", dbResults.get("identifier_type"));
                            tempValues.put("isnew", "YES");
                            queryManager.add(sqlQuery4, tempValues);

                            //UPDATE BUFFER STATE TO ACTIVATED
                            tempValues.clear();
                            tempValues.put("token", Hashing.sha256().hashString((String) values.get("token"), StandardCharsets.UTF_8).toString());
                            queryManager.update(sqlQuery3, tempValues);

                            //AD TO USER IDENTIFIER PROGRAMS
                            tempValues.clear();
                            tempValues.put("user_id", values.get("user_id"));
                            tempValues.put("program_id", dbResults.get("program_id"));
                            tempValues.put("identifier", dbResults.get("identifier"));
                            tempValues.put("identifier_type", dbResults.get("identifier_type"));
                            queryManager.add(sqlQuery5, tempValues);

                            //SEND SUCCESS RESPONSE
                            error.put("Message", "Program Added Successfully");
                            ApiResponse.sendResponse(exchange, error, StatusCodes.OK);
                            return;
                        } else {
                            error.put("Error", ResponseCodes.ERROR);
                            error.put("Message", "Wrong Token Provided");
                            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
                            return;
                        }
                    } else {

                        error.put("Error", ResponseCodes.MISSING_FIELD);
                        error.put("Message", "Please provide USER_ID");
                        ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
                        return;
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                    error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
                    ApiResponse.sendResponse(exchange, error, StatusCodes.INTERNAL_SERVER_ERROR);

                }
            }
        }
    }
}
