import io.undertow.server.handlers.sse.ServerSentEventHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class test extends ServerSentEventHandler {

    private void doRequest(){
        this.getConnections();
    }

    public static void main(String[] args) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        int sec = now.getSecond();
        System.out.println(sec);
    }
}
