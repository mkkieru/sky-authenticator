package ke.co.skyworld;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.Query_manager;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CheckAuthCodes {

    public static ResponseCodes checkAndUpdateAccessTokens(HttpServerExchange exchange, String token, String user_ip_address) throws Exception {

        Query_manager usersDao = new Query_manager();

        HashMap<String, Object> allAccessTokens;
        LinkedHashMap<String, Object> tokenValue = new LinkedHashMap<>();
        HashMap<String, Object> error = new HashMap<>();

        String sqlQuery = "SELECT * FROM public.access_token WHERE access_token = (:access_token)";
        String sqlQuery2 = "DELETE FROM public.access_token WHERE access_token = (:access_token)";

        tokenValue.put("access_token", token);

        allAccessTokens = usersDao.getSpecific(sqlQuery, tokenValue);

        if (allAccessTokens.isEmpty()) {

            error.put("Error", "Please log in to continue");
            ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
            return ResponseCodes.ERROR;

        }

        long timeToLive = (long) allAccessTokens.get("time_to_live");
        String timeToLiveUnits = (String) allAccessTokens.get("units");
        String token_ip_address = (String) allAccessTokens.get("ip_address");

        Timestamp date_created = (Timestamp) allAccessTokens.get("date_created");
        Timestamp now = new Timestamp(System.currentTimeMillis());

        long dateCreatedTime = date_created.getTime();
        long timeNow = now.getTime();

        if (!user_ip_address.equals(token_ip_address)) {

            System.out.println("checking ip address... ");
            error.put("Error", "ip address doesn't match. Please log in again to continue ... ");
            usersDao.update(sqlQuery2, tokenValue);
            return ResponseCodes.ERROR;

        } else if (timeToLiveUnits.toLowerCase().trim().equals("seconds")) {

            if ((dateCreatedTime + (1000 * timeToLive)) < timeNow) {

                System.out.println("checking time to live ... ");
                error.put("Error", "Please log in to continue ... ");
                usersDao.update(sqlQuery2, tokenValue);
                ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
                return ResponseCodes.ERROR;
            }

        } else if (timeToLiveUnits.toLowerCase().trim().equals("minutes")) {

            if ((dateCreatedTime + (timeToLive * 60 * 1000)) < timeNow) {

                System.out.println("checking time to live ... ");
                error.put("Error", "Please log in to continue ... ");
                usersDao.update(sqlQuery2, tokenValue);
                ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
                return ResponseCodes.ERROR;
            }

        } else if (timeToLiveUnits.toLowerCase().trim().equals("hours")) {

            if ((dateCreatedTime + (timeToLive * 60 * 60 * 1000)) < timeNow) {

                System.out.println("checking time to live ... ");
                error.put("Error", "Please log in to continue ... ");
                usersDao.update(sqlQuery2, tokenValue);
                ApiResponse.sendResponse(exchange, error, StatusCodes.BAD_REQUEST);
                return ResponseCodes.ERROR;
            }

        }
        return ResponseCodes.SUCCESS;
    }
}
