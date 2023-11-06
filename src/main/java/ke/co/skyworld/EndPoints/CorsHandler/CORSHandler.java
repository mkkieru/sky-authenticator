package ke.co.skyworld.EndPoints.CorsHandler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class CORSHandler implements HttpHandler {
    private final HttpHandler httpHandler;

    public CORSHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
//        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "POST, GET, OPTIONS, PUT, PATCH, DELETE");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Headers"), "Content-Type,Accept,HandlerAuthorizationLayer,Authorization,AuthToken,RequestReference");
        if (this.httpHandler != null) {
            this.httpHandler.handleRequest(exchange);
        }

    }
}