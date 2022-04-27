package ke.co.skyworld;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import ke.co.skyworld.StatusCodesUpdaters.Thread_pools;
import ke.co.skyworld.XmlReader.XmlReader;
import ke.co.skyworld.query_manager.Query_manager;

import java.util.LinkedHashMap;

public class main {
    public static void main(String[] args){
        Query_manager.setAccessKeys();
        XmlReader.getAccessKeys();

        PathHandler handler = Handlers.path()
                .addPrefixPath("/sky-auth/users", UndertowRoutes.users())
                .addPrefixPath("/sky-auth/programs", UndertowRoutes.programs())
                .addPrefixPath("/sky-auth/identifier_type", UndertowRoutes.identifier_type())
                .addPrefixPath("/sky-auth/identifier", UndertowRoutes.identifier())
                .addPrefixPath("/sky-auth/identifier_confirmation", UndertowRoutes.identifier_confirmation())
                .addPrefixPath("/sky-auth/auth_details", UndertowRoutes.auth_details())
                .addPrefixPath("/sky-auth/authorization", UndertowRoutes.authorization())
                .addPrefixPath("/sky-auth/confirmation", UndertowRoutes.auth_code_confirmation());

        Undertow server = Undertow.builder()
                .addHttpListener(XmlReader.portNumber,XmlReader.undertowHost)
                .setHandler(handler)
                .build();
        server.start();

        Thread_pools.startThreads();
        //access token : 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
    }
}
