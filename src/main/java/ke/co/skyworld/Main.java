//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

import java.sql.SQLException;

import ke.co.skyworld.StatusCodesUpdaters.ThreadPools;
import ke.co.skyworld.XmlReader.XmlReader;
import ke.co.skyworld.query_manager.QueryManager;

public class Main {
    public Main() {
    }

    public static void main(String[] args) throws SQLException {
        QueryManager.setAccessKeys();
        XmlReader.getAccessKeys();
        String api = "sky-auth";
        PathHandler handler = Handlers.path().addPrefixPath("/sky-auth/users", UndertowRoutes.users()).addPrefixPath("/sky-auth/programs", UndertowRoutes.programs()).addPrefixPath("/sky-auth/identifier_type", UndertowRoutes.identifier_type()).addPrefixPath("/sky-auth/identifier", UndertowRoutes.identifier()).addPrefixPath("/sky-auth/identifier_confirmation", UndertowRoutes.identifier_confirmation()).addPrefixPath("/sky-auth/auth_details", UndertowRoutes.auth_details()).addPrefixPath("/sky-auth/authorization", UndertowRoutes.authorization()).addPrefixPath("/sky-auth/confirmation", UndertowRoutes.auth_code_confirmation());
        Undertow server = Undertow.builder().addHttpListener(XmlReader.portNumber, XmlReader.undertowHost).setHandler(handler).build();
        server.start();
        ThreadPools.startThreads();
    }
}
