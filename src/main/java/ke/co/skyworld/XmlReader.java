package ke.co.skyworld;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

public class XmlReader {

    public static String
            ACCESS_TOKEN_TTL_UNITS,
            AUTH_CODE_TTL_UNITS,
            undertowHost;

    public static Integer
            ACCESS_TOKEN_TTL,
            AUTH_CODE_TTL,
            AUTH_CODE_LENGTH,
            portNumber;

    public static HashMap<String, Object> getAccessKeys() {

        String dbHost, dbPort, dbName, DatabaseUserName, DatabasePassword, encryptedPassword, decryptedPassword;

        HashMap<String, Object> accessKeys = new HashMap<>();

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse("conf.xml");
            XPath xp = XPathFactory.newInstance().newXPath();

            NodeList nl = (NodeList) xp.compile("//UNDERTOW").evaluate(d, XPathConstants.NODESET);
            NodeList nl1 = (NodeList) xp.compile("//POSTGRESSDATABASE").evaluate(d, XPathConstants.NODESET);
            NodeList nl2 = (NodeList) xp.compile("//TOKENS").evaluate(d, XPathConstants.NODESET);

            Node node = d.getElementsByTagName("PASSWORD").item(0);
            String format = node.getAttributes().getNamedItem("TYPE").getTextContent();

            if (format.equals("CLEARTEXT")) {

                portNumber = Integer.parseInt(xp.compile("./PORT").evaluate(nl.item(0)));
                undertowHost = xp.compile("./HOST").evaluate(nl.item(0));
                dbHost = xp.compile("./DBHOST").evaluate(nl1.item(0));
                dbPort = xp.compile("./DBPORT").evaluate(nl1.item(0));
                dbName = xp.compile("./DBNAME").evaluate(nl1.item(0));
                DatabaseUserName = xp.compile("./USER").evaluate(nl1.item(0));
                DatabasePassword = xp.compile("./PASSWORD").evaluate(nl1.item(0));

                accessKeys.put("Port Number", portNumber);
                accessKeys.put("Host", undertowHost);
                accessKeys.put("dbHost", dbHost);
                accessKeys.put("dbPort", dbPort);
                accessKeys.put("dbName", dbName);
                accessKeys.put("Database Username", DatabaseUserName);
                accessKeys.put("Database Password", DatabasePassword);

                ACCESS_TOKEN_TTL = Integer.valueOf(xp.compile("./ACCESS_TOKEN_TTL").evaluate(nl2.item(0)));
                ACCESS_TOKEN_TTL_UNITS = xp.compile("./ACCESS_TOKEN_TTL_UNITS").evaluate(nl2.item(0));

                AUTH_CODE_TTL = Integer.valueOf(xp.compile("./AUTH_CODE_TTL").evaluate(nl2.item(0)));
                AUTH_CODE_TTL_UNITS = xp.compile("./AUTH_CODE_TTL_UNITS").evaluate(nl2.item(0));

                //Encrypt database password
                encryptedPassword = AES_EncryptionUtil.encrypt(DatabasePassword);

                //Write back to conf.xml file to save back encrypted password
                node.getAttributes().getNamedItem("TYPE").setTextContent("ENCRYPTED");
                node.setTextContent(encryptedPassword);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(d);
                StreamResult result = new StreamResult(new File("conf.xml"));
                transformer.transform(source, result);

                System.out.println("Done. Password encrypted");

            } else {
                portNumber = Integer.parseInt(xp.compile("./PORT").evaluate(nl.item(0)));
                undertowHost = xp.compile("./HOST").evaluate(nl.item(0));
                dbHost = xp.compile("./DBHOST").evaluate(nl1.item(0));
                dbPort = xp.compile("./DBPORT").evaluate(nl1.item(0));
                dbName = xp.compile("./DBNAME").evaluate(nl1.item(0));
                DatabaseUserName = xp.compile("./USER").evaluate(nl1.item(0));
                DatabasePassword = xp.compile("./PASSWORD").evaluate(nl1.item(0));

                //Decrypt Password
                decryptedPassword = AES_EncryptionUtil.decrypt(DatabasePassword);
                System.out.println(decryptedPassword);

                accessKeys.put("Port Number", portNumber);
                accessKeys.put("Host", undertowHost);
                accessKeys.put("dbHost", dbHost);
                accessKeys.put("dbPort", dbPort);
                accessKeys.put("dbName", dbName);
                accessKeys.put("Database Username", DatabaseUserName);
                accessKeys.put("Database Password", decryptedPassword);

                ACCESS_TOKEN_TTL = Integer.valueOf(xp.compile("./ACCESS_TOKEN_TTL").evaluate(nl2.item(0)));
                ACCESS_TOKEN_TTL_UNITS = xp.compile("./ACCESS_TOKEN_TTL_UNITS").evaluate(nl2.item(0));

                AUTH_CODE_TTL = Integer.valueOf(xp.compile("./AUTH_CODE_TTL").evaluate(nl2.item(0)));
                AUTH_CODE_LENGTH = Integer.valueOf(xp.compile("./AUTH_CODE_LENGTH").evaluate(nl2.item(0)));
                AUTH_CODE_TTL_UNITS = xp.compile("./AUTH_CODE_TTL_UNITS").evaluate(nl2.item(0));

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return accessKeys;
    }

    public static HashMap<String, Object> getUserDetails(String userDetails, String userType) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        HashMap<String, Object> accessDetails = new HashMap<>();

        int loginLimit,
                loginTriesLimit;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(userDetails));
        Document d = db.parse(is);

        XPath xp = XPathFactory.newInstance().newXPath();

        if(checkCredentials(d)) {

            NodeList nl = (NodeList) xp.compile("//CONFIG").evaluate(d, XPathConstants.NODESET);

            if (userType.equals("ADMINISTRATOR")) {

                loginLimit = Integer.parseInt(xp.compile("./ADMIN_USER_LOGIN_ACCOUNT_LIMIT").evaluate(nl.item(0)));
                loginTriesLimit = Integer.parseInt(xp.compile("./ADMIN_USER_LOGIN_TRIES_LEFT").evaluate(nl.item(0)));

                accessDetails.put("check", "YES");
                accessDetails.put("login_limit", loginLimit);
                accessDetails.put("login_tries_limit", loginTriesLimit);
            }
            if (userType.equals("MEMBER")) {

                loginLimit = Integer.parseInt(xp.compile("./USER_LOGIN_ACCOUNT_LIMIT").evaluate(nl.item(0)));
                loginTriesLimit = Integer.parseInt(xp.compile("./USER_LOGIN_TRIES_LEFT").evaluate(nl.item(0)));
                accessDetails.put("check", "YES");
                accessDetails.put("login_limit", loginLimit);
                accessDetails.put("login_tries_limit", loginTriesLimit);
            }
        }
        else {
            accessDetails.put("check", "NO");

        }

        return accessDetails;
    }
    public static boolean checkCredentials(Document document) {

        boolean check ;
        Node node;

        node = document.getElementsByTagName("ADMIN_USER_LOGIN_ACCOUNT_LIMIT").item(0);

        if (node == null){
            node = document.getElementsByTagName("USER_LOGIN_ACCOUNT_LIMIT").item(0);
        }

        String applicable = node.getAttributes().getNamedItem("APPLICABLE").getTextContent();

        check = applicable.equals("YES");

        return check;
    }
}
