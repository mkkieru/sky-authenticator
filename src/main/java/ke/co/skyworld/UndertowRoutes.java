package ke.co.skyworld;

import io.undertow.server.handlers.BlockingHandler;
import ke.co.skyworld.EndPoints.users.CreateUsers;
import ke.co.skyworld.EndPoints.users.GetSpecificUser;
import ke.co.skyworld.EndPoints.users.GetUsers;
import ke.co.skyworld.EndPoints.users.UpdateUser;
import ke.co.skyworld.UTILS.Dispatcher;
import ke.co.skyworld.EndPoints.auth_details.CreateAuthDetails;
import ke.co.skyworld.EndPoints.auth_details.GetAuthDetails;
import ke.co.skyworld.EndPoints.authorizations.Login;
import ke.co.skyworld.EndPoints.authorizations.Auth_code_confirmation;
import ke.co.skyworld.EndPoints.confirmation.SendConfirmationDetails;
import ke.co.skyworld.EndPoints.identifier_confirmation.add_to_identifier_confirmation;
import ke.co.skyworld.EndPoints.identifier_confirmation.get_identifier_confirmation;
import ke.co.skyworld.EndPoints.identifier.AddIdentifier;
import ke.co.skyworld.EndPoints.identifier.GetIdentifiers;
import ke.co.skyworld.EndPoints.identifier.confirmIdentifier;
import ke.co.skyworld.EndPoints.identifier_type.AddIdentifierType;
import ke.co.skyworld.EndPoints.identifier_type.GetIdentifierType;
import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import ke.co.skyworld.EndPoints.programs.CreateProgram;
import ke.co.skyworld.EndPoints.programs.UpdatePrograms;
import ke.co.skyworld.EndPoints.programs.GetPrograms;

public class UndertowRoutes {

    public static RoutingHandler users(){
        return Handlers.routing()
                .get("", new Dispatcher(new GetUsers()))
                .post("", new BlockingHandler(new CreateUsers()))
                .get("/{user_id}", new Dispatcher(new GetSpecificUser()))
                .post("/login", new BlockingHandler(new Login()))
                .post("/update", new BlockingHandler(new UpdateUser()));
    }
    public static RoutingHandler authorization(){
        return Handlers.routing()
                .post("/login", new BlockingHandler(new Login()))
                .post("/code_confirmation", new BlockingHandler(new Auth_code_confirmation()));
    }

    public static RoutingHandler programs(){
        return Handlers.routing()
                .get("", new Dispatcher(new GetPrograms()))
                .post("", new BlockingHandler(new CreateProgram()))
                .post("/update", new BlockingHandler(new UpdatePrograms()));
    }

    public static RoutingHandler identifier_type(){
        return Handlers.routing()
                .get("", new BlockingHandler(new GetIdentifierType()))
                .post("", new BlockingHandler(new AddIdentifierType()));
    }

    public static RoutingHandler identifier(){
        return Handlers.routing()
                .get("", new Dispatcher(new GetIdentifiers()))
                .post("", new BlockingHandler(new AddIdentifier()))
                .post("/confirm_identifier", new BlockingHandler(new confirmIdentifier()));
    }

    public static RoutingHandler identifier_confirmation(){
        return Handlers.routing()
                .get("", new Dispatcher(new get_identifier_confirmation()))
                .post("", new BlockingHandler(new add_to_identifier_confirmation()));
    }
    public static RoutingHandler auth_code_confirmation(){
        return Handlers.routing()
                .post("", new Dispatcher(new SendConfirmationDetails()));
    }
    public static RoutingHandler auth_details(){
        return Handlers.routing()
                .get("", new Dispatcher(new GetAuthDetails()))
                .post("", new BlockingHandler(new CreateAuthDetails()));
    }

}
