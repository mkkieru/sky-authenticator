//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.UserResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class ApiResponse {
    static Gson gson = (new GsonBuilder()).setDateFormat("yyyy-MM-dd hh:mm:ss.S").create();

    public ApiResponse() {
    }

    public static void sendResponse(HttpServerExchange exchange, Object result, Integer statusCode) {
        exchange.getResponseHeaders().put(new HttpString("Content-type"), "application/json").put(new HttpString("Access-Control-Allow-Origin"), "*").put(new HttpString("Access-Control-Allow-Headers"), "*");
        exchange.setStatusCode(statusCode);
        exchange.getResponseSender().send(gson.toJson(result));
    }
}
