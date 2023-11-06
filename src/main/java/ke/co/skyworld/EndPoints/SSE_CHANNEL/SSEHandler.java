package ke.co.skyworld.EndPoints.SSE_CHANNEL;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import io.undertow.util.HttpString;

import java.util.Deque;
import java.util.Timer;
import java.util.TimerTask;

public class SSEHandler extends ServerSentEventHandler {

    private static final SSEHandler sseHandler = new SSEHandler();

    private SSEHandler() {
    }

    public static SSEHandler getInstance() {
        return sseHandler;
    }


    /**
     * @param strUserId just a random thing for now
     * @param data      the data you want to send to the SSE, can be a json string or just a normal string
     * @param event     the NAME OF THE SSE EVENT. This is important and will be used by the SSE consumer to listen to specific events of an SSE.
     * @param id        a unique ID of the SSE session. Can be a GUID
     */
    public static void sendToSpecificUser(String strUserId, String data, String event, String id) {
//        System.out.println("Number Of Connections .... : " + sseHandler.getConnections().size());


        //Loop Through killing duplicate connections
        for (ServerSentEventConnection connection : sseHandler.getConnections()) {

            //Checks for the SSE with a specific user id and then sends to that user specifically.
            //You can use any other ways of deciding which SSEs to send data to.
            Deque<String> dequeUserId = connection.getQueryParameters().get("id");
            System.out.println("********************");
            System.out.println("deque id : " + dequeUserId);
            System.out.println("strUserId : " + strUserId.split("\\.")[0]);
            System.out.println("same : " + (dequeUserId.getFirst().equalsIgnoreCase(strUserId.split("\\.")[0])));
            System.out.println("********************\n");

            if (dequeUserId != null && dequeUserId.getFirst() != null) {

                if ((dequeUserId.getFirst().equalsIgnoreCase(strUserId.split("\\.")[0]))) {

                    connection.send(data, event, id, null);
                }
            }
        }
    }

    public static void broadcastToAllConnections(String data, String event, String id) {
        for (ServerSentEventConnection connection : sseHandler.getConnections()) {
            //Send the Data to all the connections
            connection.send(data, event, id, null);
        }
    }

    public static void SendKeepAlivesToAllConnections(String id) {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    for (ServerSentEventConnection connection : sseHandler.getConnections()) {
                        //Send the Data to all the connections
                        connection.send("", ":keepalive", id, null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error: " + e.getMessage() + " Occurred while sending keep alives'");
                }
            }
//        }, 0, 2000);
        }, 0, 15000);
    }


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

//      sseHandler.getConnections();

        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
      exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Credentials"), "true");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "POST, GET, OPTIONS, PUT, PATCH, DELETE");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Headers"), "Content-Type,Accept,last-event-id");
        exchange.getResponseHeaders().put(new HttpString("Cache-Control"), "no-cache");
        exchange.getResponseHeaders().put(new HttpString("X-Accel-Buffering"), "no");

        //printRequestInfo(httpServerExchange);
        super.handleRequest(exchange);
    }
}
