//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld;

import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import java.sql.SQLException;

import io.undertow.util.Methods;
import ke.co.skyworld.EndPoints.CorsHandler.CorsHandler;
import ke.co.skyworld.EndPoints.SSE_CHANNEL.SSEHandler;
import ke.co.skyworld.EndPoints.auth_details.CreateAuthDetails;
import ke.co.skyworld.EndPoints.auth_details.DisableAuthCode;
import ke.co.skyworld.EndPoints.auth_details.GetAllAuthDetails;
import ke.co.skyworld.EndPoints.auth_details.GetProgramUserAuthDetail;
import ke.co.skyworld.EndPoints.auth_details.GetSpecificAuthDetails;
import ke.co.skyworld.EndPoints.authorizations.AddToBiometricTable;
import ke.co.skyworld.EndPoints.authorizations.BiometricCodeConfirmation;
import ke.co.skyworld.EndPoints.authorizations.CheckAccessTokenValidity;
import ke.co.skyworld.EndPoints.authorizations.Login;
import ke.co.skyworld.EndPoints.authorizations.ProgramBiometricConfirmation;
import ke.co.skyworld.EndPoints.buffer.AddToBuffer;
import ke.co.skyworld.EndPoints.buffer.GetSpecificFromBuffer;
import ke.co.skyworld.EndPoints.change_user_codes_status.ActivateUserCodes;
import ke.co.skyworld.EndPoints.change_user_codes_status.DeactivateUserCodes;
import ke.co.skyworld.EndPoints.confirmation.SendConfirmationDetails;
import ke.co.skyworld.EndPoints.identifier.AddIdentifier;
import ke.co.skyworld.EndPoints.identifier.ConfirmIdentifier;
import ke.co.skyworld.EndPoints.identifier.DeleteIdentifier;
import ke.co.skyworld.EndPoints.identifier.GetIdentifiers;
import ke.co.skyworld.EndPoints.identifier_type.AddIdentifierType;
import ke.co.skyworld.EndPoints.identifier_type.GetIdentifierType;
import ke.co.skyworld.EndPoints.link_program.LinkProgram;
import ke.co.skyworld.EndPoints.programs.*;
import ke.co.skyworld.EndPoints.users.*;
import ke.co.skyworld.UTILS.Dispatcher;
import ke.co.skyworld.UTILS.FallBack;
import ke.co.skyworld.UTILS.InvalidMethod;

public class UndertowRoutes {
    public UndertowRoutes() {
    }

    public static RoutingHandler users() throws SQLException {
        return Handlers.routing()
                .get("", new Dispatcher(new GetUsers()))
                .post("", new BlockingHandler(new CreateUsers()))
                .get("/{user_id}", new Dispatcher(new GetSpecificUser()))
                .post("/login", new BlockingHandler(new Login()))
                .post("/checkAccessToken", new BlockingHandler(new CheckAccessTokenValidity()))
                .post("/update/password", new BlockingHandler(new UpdatePassword()))
                .post("/update", new BlockingHandler(new UpdateUser()));
    }

    public static RoutingHandler authorization() throws SQLException {
        return Handlers.routing()
                .post("/login", new BlockingHandler(new Login()))
                .post("/biometric/add", new BlockingHandler(AddToBiometricTable.getInstance()))
                .post("/program/biometric/confirmation", new BlockingHandler(new ProgramBiometricConfirmation()))
                .post("/program/link_program", new BlockingHandler(new LinkProgram()))
                .post("/biometric/code_confirmation", new BlockingHandler(new BiometricCodeConfirmation()));
    }

    public static RoutingHandler programs() {
        return Handlers.routing().get("", new Dispatcher(new GetPrograms()))
                .post("", new BlockingHandler(new CreateProgram()))
                .post("/update", new BlockingHandler(new UpdatePrograms()))
                .post("/remove", new BlockingHandler(new RemoveUserProgram()))
                .post("/getById", new Dispatcher(new GetProgramByProgramID()))
                .post("/generate/link/token", new BlockingHandler(new GenerateProgramToken()))
                .post("/user/identifier/programs", new BlockingHandler(new userIdentifierPrograms()));
    }

    public static RoutingHandler identifier_type() {
        return Handlers.routing().get("", new BlockingHandler(new GetIdentifierType())).post("", new BlockingHandler(new AddIdentifierType()));
    }
    public static RoutingHandler status() {
        return Handlers.routing()
                .post("/activate", new BlockingHandler(new ActivateUserCodes()))
                .post("/deactivate", new BlockingHandler(new DeactivateUserCodes()));
    }

    public static RoutingHandler identifier() {
        return Handlers.routing()
                .post("/get", new BlockingHandler(new GetIdentifiers()))
                .post("", new BlockingHandler(new AddIdentifier()))
                .post("/delete", new BlockingHandler(new DeleteIdentifier()))
                .post("/confirm_identifier", new BlockingHandler(new ConfirmIdentifier()));
    }

    public static RoutingHandler buffer() {
        return Handlers.routing()
                .get("", new Dispatcher(new GetSpecificFromBuffer()))
                .post("", new BlockingHandler(new AddToBuffer()));
    }

    public static RoutingHandler auth_code_confirmation() {
        return Handlers.routing()
                .post("", new Dispatcher(new SendConfirmationDetails()))
                .get("/sse/{id}", new Dispatcher(SSEHandler.getInstance()))
                .add(Methods.OPTIONS, "*", new CorsHandler())
                .setInvalidMethodHandler(new InvalidMethod())
                .setFallbackHandler(new FallBack());
    }

    public static RoutingHandler auth_details() {
        return Handlers.routing().post("/specific", new BlockingHandler(new GetSpecificAuthDetails()))
                .post("/program/specific", new BlockingHandler(new GetProgramUserAuthDetail()))
                .post("/disable", new BlockingHandler(new DisableAuthCode()))
                .get("", new Dispatcher(new GetAllAuthDetails()))
                .post("", new BlockingHandler(new CreateAuthDetails()));
    }
}
