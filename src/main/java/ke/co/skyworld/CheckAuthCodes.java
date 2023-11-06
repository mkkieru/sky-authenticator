//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld;

import io.undertow.server.HttpServerExchange;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;

public class CheckAuthCodes {
    public CheckAuthCodes() {
    }

    public static ResponseCodes checkAndUpdateAccessTokens(HttpServerExchange exchange, String token, String user_ip_address) throws Exception {
        QueryManager usersDao = new QueryManager();
        LinkedHashMap<String, Object> tokenValue = new LinkedHashMap();
        HashMap<String, Object> error = new HashMap();
        String sqlQuery = "SELECT * FROM public.access_token WHERE access_token = (:access_token)";
        String sqlQuery2 = "DELETE FROM public.access_token WHERE access_token = (:access_token)";
        tokenValue.put("access_token", token);
        HashMap<String, Object> allAccessTokens = usersDao.getSpecific(sqlQuery, tokenValue);
        if (allAccessTokens.isEmpty()) {
            error.put("Error", ResponseCodes.ACCESS_TOKEN_EXPIRED);
            ApiResponse.sendResponse(exchange, error, 401);
            return ResponseCodes.ERROR;
        } else {
            long timeToLive = (Long)allAccessTokens.get("time_to_live");
            String timeToLiveUnits = (String)allAccessTokens.get("units");
            String token_ip_address = (String)allAccessTokens.get("ip_address");
            Timestamp date_created = (Timestamp)allAccessTokens.get("date_created");
            Timestamp now = new Timestamp(System.currentTimeMillis());
            long dateCreatedTime = date_created.getTime();
            long timeNow = now.getTime();
//            if (!user_ip_address.equals(token_ip_address)) {
//                System.out.println("checking ip address... ");
//                error.put("Error", "ip address doesn't match. Please log in again to continue ... ");
//                usersDao.update(sqlQuery2, tokenValue);
//                ApiResponse.sendResponse(exchange, error, 401);
//                return ResponseCodes.ERROR;
//            } else {
//                if (timeToLiveUnits.toLowerCase().trim().equals("seconds")) {
//                    if (dateCreatedTime + 1000L * timeToLive < timeNow) {
//                        error.put("Error", ResponseCodes.ACCESS_TOKEN_EXPIRED);
//                        usersDao.update(sqlQuery2, tokenValue);
//                        ApiResponse.sendResponse(exchange, error, 401);
//                        return ResponseCodes.ERROR;
//                    }
//                } else if (timeToLiveUnits.toLowerCase().trim().equals("minutes")) {
//                    if (dateCreatedTime + timeToLive * 60L * 1000L < timeNow) {
//                        error.put("Error", ResponseCodes.ACCESS_TOKEN_EXPIRED);
//                        usersDao.update(sqlQuery2, tokenValue);
//                        ApiResponse.sendResponse(exchange, error, 401);
//                        return ResponseCodes.ERROR;
//                    }
//                } else if (timeToLiveUnits.toLowerCase().trim().equals("hours") && dateCreatedTime + timeToLive * 60L * 60L * 1000L < timeNow) {
//                    error.put("Error", ResponseCodes.ACCESS_TOKEN_EXPIRED);
//                    usersDao.update(sqlQuery2, tokenValue);
//                    ApiResponse.sendResponse(exchange, error, 401);
//                    return ResponseCodes.ERROR;
//                }
//
//                return ResponseCodes.SUCCESS;
//            }
            return ResponseCodes.SUCCESS;
        }
    }
}
