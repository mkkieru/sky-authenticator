//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.EndPoints.authorizations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.StatusCodesUpdaters.StatusCodesRefresher;
import ke.co.skyworld.UTILS.ExchangeUtils;
import ke.co.skyworld.UTILS.GenerateQRcode;
import ke.co.skyworld.UserResponse.ApiResponse;
import ke.co.skyworld.query_manager.QueryManager;
import org.apache.commons.io.FileUtils;

public class AddToBiometricTable implements HttpHandler {
    public AddToBiometricTable() {
    }

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery = "insert into biometric_confirmation (program_id,identifier,identifier_type) values (:program_id,:identifier,:identifier_type) returning id";
        String sqlQuery3 = "select auth_code from auth_details where program_id = (:program_id) and identifier = (:identifier) and identifier_type = (:identifier_type)";
        QueryManager queryManager = new QueryManager();
        LinkedHashMap<String, Object> error = new LinkedHashMap();
        Gson gson = new Gson();
        Type type = (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType();
        LinkedHashMap userAndProgramDetails = (LinkedHashMap)gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

        try {
            HashMap<String, Object> QRdetails = queryManager.getSpecific(sqlQuery3, userAndProgramDetails);
            if (QRdetails == null || QRdetails.isEmpty()) {
                error.put("Error", ResponseCodes.EMPTY);
                error.put("Message", "Auth code for user doesn't exist ");
                ApiResponse.sendResponse(httpServerExchange, error, 400);
                return;
            }

            QRdetails.put("identifier", userAndProgramDetails.get("identifier"));
            QRdetails.put("identifier_type", userAndProgramDetails.get("identifier_type"));
            QRdetails.put("program_id", userAndProgramDetails.get("program_id"));
            String randomFileName = StatusCodesRefresher.genAuthCode(7);
            String filePath = "QRimages/" + randomFileName + ".png";
            String fileType = "png";
            File qrFile = new File(filePath);
            String QRdetailsString = gson.toJson(QRdetails);
            GenerateQRcode.createQRImage(qrFile, QRdetailsString, 250, fileType);
            byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            File myObj = new File(filePath);
            myObj.delete();
            queryManager.add(sqlQuery, userAndProgramDetails);
            LinkedHashMap<String, Object> temp = new LinkedHashMap();
            temp.put("Base64 QR image", encodedString);
            ApiResponse.sendResponse(httpServerExchange, temp, 200);
        } catch (Exception var19) {
            var19.printStackTrace();
            error.put("Error", ResponseCodes.SOMETHING_WENT_WRONG);
            ApiResponse.sendResponse(httpServerExchange, error, 500);
        }

    }
}
