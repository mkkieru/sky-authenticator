package ke.co.skyworld.query_manager;

import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.XmlReader.XmlReader;
import ke.co.skyworld.model.NamedPreparedStatement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class Query_manager {

    private static String DatabaseUserName;
    private static String DatabasePassword, dbHost, dbPort, dbName;

    public static void setAccessKeys(){

        HashMap<String, Object> accessKeys = new XmlReader().getAccessKeys();

        dbHost = (String) accessKeys.get("dbHost");
        dbPort = (String) accessKeys.get("dbPort");
        dbName = (String) accessKeys.get("dbName");
        DatabaseUserName = (String) accessKeys.get("Database Username");
        DatabasePassword = (String) accessKeys.get("Database Password");
    }

    /*//LOGIN
    public HashMap<String, Object> login(String query, HashMap<String, Object> values) throws Exception {

        Class.forName("org.postgresql.Driver");
        java.sql.Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + dbHost + dbPort + dbName,
                DatabaseUserName, DatabasePassword);

        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);

        for (Map.Entry<String, Object> entry : values.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);

        }

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<HashMap<String, Object>> userFound = new ArrayList<>();

        HashMap<String, Object> user = new HashMap<>();
        while (resultSet.next()) {

            for (int i = 1; i < columnCount; i++) {
                user.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            //userFound.add(user);

        }
        resultSet.close();
        connection.close();
        return user;
    }*/

    //INSERT DATA TO DB
    public Object add(String query, LinkedHashMap<String, Object> valuesMap) throws Exception {
        Class.forName("org.postgresql.Driver");
        java.sql.Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + dbHost + dbPort + dbName,
                DatabaseUserName, DatabasePassword);

        /*Class.forName("org.postgresql.Driver");
        java.sql.Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + "damark" + ":22" + "/sky_authenticator",
                "postgres", "MarkKieru55");*/

        /*<POSTGRESSDATABASE>
        <DBHOST>localhost</DBHOST>
        <DBPORT>:5432</DBPORT>
        <DBNAME>/sky_authenticator</DBNAME>
        <USER>postgres</USER>
        <PASSWORD TYPE="ENCRYPTED">NafgQ01uaxdMR5gcCb40og==</PASSWORD>
    </POSTGRESSDATABASE>*/

        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);

        for (Map.Entry<String, Object> entry : valuesMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            statement.setObject(key, value);

        }

        Object object;
        try {
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            int columnCount = resultSetMetaData.getColumnCount();
            HashMap<String, Object> returnedValue = new HashMap<>();
            String columnLabel = null;

            while (resultSet.next()) {

                for (int x = 1; x <= columnCount; x++) {

                    returnedValue.put(resultSetMetaData.getColumnLabel(x), resultSet.getObject(x));
                    columnLabel = resultSetMetaData.getColumnLabel(x);

                }
            }

            object = returnedValue.get(columnLabel);

            connection.close();

            return object;

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println(e.getMessage());
            return ResponseCodes.ERROR;
        }
    }


    public List<LinkedHashMap<String, Object>> getAll(String query) throws Exception {
        return getAll(query, null);
    }

    //READ DATA FROM DB
    public List<LinkedHashMap<String, Object>> getAll(String query, HashMap<String, Object> limits) throws Exception {

        Class.forName("org.postgresql.Driver");
        java.sql.Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + dbHost + dbPort + dbName,
                DatabaseUserName, DatabasePassword);

        boolean isEmpty = false;
        int pageSize = 0, page = 0, offset;
        List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();

        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);

        if (limits != null && !limits.isEmpty()) {

            if (!(limits.get("page_size") == null)) {
                pageSize = Integer.parseInt(limits.get("page_size").toString());
            }

            if (!(limits.get("page") == null)) {

                page = Integer.parseInt(limits.get("page").toString());

            } else {
                pageSize = Integer.parseInt(null);
                isEmpty = true;

            }


            if (isEmpty) {
                statement.setObject("page_size", null);
                statement.setObject("offset", null);
            } else {
                offset = pageSize * (page - 1);

                statement.setObject("page_size", pageSize);
                statement.setObject("offset", offset);
            }

            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            int totalColumns = metaData.getColumnCount();

            while (resultSet.next()) {

                LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
                for (int x = 1; x <= totalColumns; x++) {
                    hashMap.put(metaData.getColumnLabel(x), resultSet.getObject(x));
                }
                resultList.add(hashMap);
            }

            resultSet.close();
        } else {
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int totalColumns = metaData.getColumnCount();

            while (resultSet.next()) {

                LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
                for (int i = 1; i <= totalColumns; i++) {
                    hashMap.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }
                resultList.add(hashMap);
            }
            resultSet.close();
        }
        connection.close();

        return resultList;
    }

    //READ DATA FROM DB
    public HashMap<String, Object> getSpecific(String query, LinkedHashMap<String, Object> valuesMap) throws Exception {

        Class.forName("org.postgresql.Driver");
        java.sql.Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + dbHost + dbPort + dbName,
                DatabaseUserName, DatabasePassword);

        //List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();

        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);

        for (Map.Entry<String, Object> entry : valuesMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            statement.setObject(key, value);
        }

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        int i = metaData.getColumnCount();
        while (resultSet.next()) {
            for (int x = 1; x <= i; x++) {
                hashMap.put(metaData.getColumnLabel(x), resultSet.getObject(x));

                //System.out.println(metaData.getColumnLabel(x)+" "+resultSet.getObject(x));
            }
            //resultList.add(hashMap);
        }
        return hashMap;
    }

    //UPDATE DATA TO DB
    public void update(String query, LinkedHashMap<String, Object> values) throws Exception {

        Class.forName("org.postgresql.Driver");
        java.sql.Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + dbHost + dbPort + dbName,
                DatabaseUserName, DatabasePassword);

        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }
        statement.executeUpdate();
        connection.close();
    }


    //GET TABLE COUNT
    public static Object getTableCount(String sqlQuery, HashMap<String, Object> values) throws Exception {

        Class.forName("org.postgresql.Driver");
        java.sql.Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + dbHost + dbPort + dbName,
                DatabaseUserName, DatabasePassword);

        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, sqlQuery);

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }

        ResultSet resultSet = statement.executeQuery();
        connection.close();

        Object object = new Object();

        while (resultSet.next()) {
            object = resultSet.getObject(1);
        }

        return object;
    }

    //GET ALL ACCESS CODES RECORDS FROM DB
    public static Object getAccessCodes(String sqlQuery) throws Exception {

        return getAccessCodes(sqlQuery, null);
    }

    public static Object getAccessCodes(String sqlQuery, LinkedHashMap<String, Object> values) throws Exception {

        Class.forName("org.postgresql.Driver");
        java.sql.Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + dbHost + dbPort + dbName,
                DatabaseUserName, DatabasePassword);

        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, sqlQuery);

        if (values != null) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                statement.setObject(key, value);
            }
        }

        ResultSet resultSet = statement.executeQuery();
        connection.close();

        Object object = new Object();

        while (resultSet.next()) {
            object = resultSet.getObject(1);
        }

        return object;
    }

    //DELETE DATA FROM DB
    public List<LinkedHashMap<String, Object>> getAccessCodesPerLimit(String query, LinkedHashMap<String, Object> values) throws Exception {

        Class.forName("org.postgresql.Driver");
        java.sql.Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + dbHost + dbPort + dbName,
                DatabaseUserName, DatabasePassword);

        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);

        List<LinkedHashMap<String, Object>> access_codes = new ArrayList<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }
        ResultSet resultSet = statement.executeQuery();
        connection.close();

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        while (resultSet.next()) {

            LinkedHashMap<String, Object> map = new LinkedHashMap<>();

            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {

                map.put(resultSetMetaData.getColumnLabel(i), resultSet.getObject(i));

                access_codes.add(map);

            }

        }
        return access_codes;
    }

    public List<LinkedHashMap<String, Object>> getSpecificUsers(String query, LinkedHashMap<String, Object> valuesMap) throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
        java.sql.Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + dbHost + dbPort + dbName,
                DatabaseUserName, DatabasePassword);

        List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();

        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);

        for (Map.Entry<String, Object> entry : valuesMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            statement.setObject(key, value);
        }

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        int i = metaData.getColumnCount();
        while (resultSet.next()) {
            for (int x = 1; x <= i; x++) {
                hashMap.put(metaData.getColumnLabel(x), resultSet.getObject(x));

                //System.out.println(metaData.getColumnLabel(x)+" "+resultSet.getObject(x));
            }
            resultList.add(hashMap);
        }
        return resultList;
    }


    //UPDATE STATUS DETAILS
    public void updateStatusDetails(String sqlquery){

    }
}
