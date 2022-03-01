package ke.co.skyworld;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class ApiResponse {

    static Gson gson = new GsonBuilder()
               .setDateFormat("yyyy-MM-dd hh:mm:ss.S")
               .create();

    public static void sendResponse(HttpServerExchange exchange, Object result, Integer statusCode){
        exchange.getResponseHeaders().put(new HttpString("Content-type"), "application/json");
        exchange.setStatusCode(statusCode);
        exchange.getResponseSender().send(gson.toJson(result));
    }
}
