//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.query_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import ke.co.skyworld.CustomResponseCodes.ResponseCodes;
import ke.co.skyworld.XmlReader.XmlReader;
import ke.co.skyworld.model.NamedPreparedStatement;

public class QueryManager {
    public static String DatabaseUserName;
    public static String DatabasePassword;
    public static String dbHost;
    public static String dbPort;
    public static String dbName;

    public QueryManager() {
    }

    public static void setAccessKeys() {
        new XmlReader();
        HashMap<String, Object> accessKeys = XmlReader.getAccessKeys();
        dbHost = (String)accessKeys.get("dbHost");
        dbPort = (String)accessKeys.get("dbPort");
        dbName = (String)accessKeys.get("dbName");
        DatabaseUserName = (String)accessKeys.get("Database Username");
        DatabasePassword = (String)accessKeys.get("Database Password");
    }

    public Object add(String query, LinkedHashMap<String, Object> valuesMap) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);
        Iterator var5 = valuesMap.entrySet().iterator();

        while(var5.hasNext()) {
            Entry<String, Object> entry = (Entry)var5.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }

        try {
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            HashMap<String, Object> returnedValue = new HashMap();
            String columnLabel = null;

            while(resultSet.next()) {
                for(int x = 1; x <= columnCount; ++x) {
                    returnedValue.put(resultSetMetaData.getColumnLabel(x), resultSet.getObject(x));
                    columnLabel = resultSetMetaData.getColumnLabel(x);
                }
            }

            Object object = returnedValue.get(columnLabel);
            resultSet.close();
            connection.close();
            return object;
        } catch (Exception var12) {
            HashMap<String, Object> response = new HashMap();
            response.put("Response", ResponseCodes.ERROR);
            response.put("Reason", var12.getMessage());
            throw new Exception(var12.getMessage());
        }
    }

    public List<LinkedHashMap<String, Object>> getAll(String query) throws Exception {
        return this.getAll(query, (HashMap)null);
    }

    public List<LinkedHashMap<String, Object>> getAll(String query, HashMap<String, Object> limits) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        boolean isEmpty = false;
        int pageSize = 0;
        int page = 0;
        List<LinkedHashMap<String, Object>> resultList = new ArrayList();
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);
        ResultSet resultSet;
        ResultSetMetaData metaData;
        int totalColumns;
        LinkedHashMap hashMap;
        int x;
        if (limits != null && !limits.isEmpty()) {
            if (limits.get("page_size") != null) {
                pageSize = Integer.parseInt(limits.get("page_size").toString());
            }

            if (limits.get("page") != null) {
                page = Integer.parseInt(limits.get("page").toString());
            } else {
                isEmpty = true;
            }

            if (isEmpty) {
                statement.setObject("page_size", (Object)null);
                statement.setObject("offset", (Object)null);
            } else {
                int offset = pageSize * (page - 1);
                statement.setObject("page_size", pageSize);
                statement.setObject("offset", offset);
            }

            resultSet = statement.executeQuery();
            metaData = resultSet.getMetaData();
            totalColumns = metaData.getColumnCount();

            while(resultSet.next()) {
                hashMap = new LinkedHashMap();

                for(x = 1; x <= totalColumns; ++x) {
                    hashMap.put(metaData.getColumnLabel(x), resultSet.getObject(x));
                }

                resultList.add(hashMap);
            }

            resultSet.close();
        } else {
            resultSet = statement.executeQuery();
            metaData = resultSet.getMetaData();
            totalColumns = metaData.getColumnCount();

            while(resultSet.next()) {
                hashMap = new LinkedHashMap();

                for(x = 1; x <= totalColumns; ++x) {
                    hashMap.put(metaData.getColumnLabel(x), resultSet.getObject(x));
                }

                resultList.add(hashMap);
            }

            resultSet.close();
        }

        connection.close();
        return resultList;
    }

    public HashMap<String, Object> getSpecific(String query, LinkedHashMap<String, Object> valuesMap) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap();
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);
        Iterator var6 = valuesMap.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, Object> entry = (Entry)var6.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int i = metaData.getColumnCount();

        while(resultSet.next()) {
            for(int x = 1; x <= i; ++x) {
                hashMap.put(metaData.getColumnLabel(x), resultSet.getObject(x));
            }
        }

        resultSet.close();
        connection.close();
        return hashMap;
    }

    public void update(String query, LinkedHashMap<String, Object> values) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);
        Iterator var5 = values.entrySet().iterator();

        while(var5.hasNext()) {
            Entry<String, Object> entry = (Entry)var5.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }

        statement.executeUpdate();
        connection.close();
    }

    public static Object getTableCount(String sqlQuery, HashMap<String, Object> values) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, sqlQuery);
        Iterator var4 = values.entrySet().iterator();

        while(var4.hasNext()) {
            Entry<String, Object> entry = (Entry)var4.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }

        ResultSet resultSet = statement.executeQuery();

        Object object;
        for(object = new Object(); resultSet.next(); object = resultSet.getObject(1)) {
        }

        resultSet.close();
        connection.close();
        return object;
    }

    public static Object getAccessCodes(String sqlQuery) throws Exception {
        return getAccessCodes(sqlQuery, (LinkedHashMap)null);
    }

    public static Object getAccessCodes(String sqlQuery, LinkedHashMap<String, Object> values) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, sqlQuery);
        if (values != null) {
            Iterator var4 = values.entrySet().iterator();

            while(var4.hasNext()) {
                Entry<String, Object> entry = (Entry)var4.next();
                String key = (String)entry.getKey();
                Object value = entry.getValue();
                statement.setObject(key, value);
            }
        }

        ResultSet resultSet = statement.executeQuery();

        Object object;
        for(object = new Object(); resultSet.next(); object = resultSet.getObject(1)) {
        }

        resultSet.close();
        connection.close();
        return object;
    }

    public void delete(String sqlQuery) throws SQLException, ClassNotFoundException {
        this.delete(sqlQuery, (LinkedHashMap)null);
    }

    public void delete(String sqlQuery, LinkedHashMap<String, Object> values) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, sqlQuery);
        if (values == null) {
            statement.executeUpdate();
            connection.close();
        } else {
            Iterator var5 = values.entrySet().iterator();

            while(var5.hasNext()) {
                Entry<String, Object> entry = (Entry)var5.next();
                String key = (String)entry.getKey();
                Object value = entry.getValue();
                statement.setObject(key, value);
            }

            statement.executeUpdate();
            connection.close();
        }
    }

    public List<LinkedHashMap<String, Object>> getAccessCodesPerLimit(String query, LinkedHashMap<String, Object> values) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);
        List<LinkedHashMap<String, Object>> access_codes = new ArrayList();
        Iterator var6 = values.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, Object> entry = (Entry)var6.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        while(resultSet.next()) {
            LinkedHashMap<String, Object> map = new LinkedHashMap();

            for(int i = 1; i <= resultSetMetaData.getColumnCount(); ++i) {
                map.put(resultSetMetaData.getColumnLabel(i), resultSet.getObject(i));
                access_codes.add(map);
            }
        }

        resultSet.close();
        connection.close();
        return access_codes;
    }

    public List<LinkedHashMap<String, Object>> getSpecificUsers(String query, LinkedHashMap<String, Object> valuesMap) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        List<LinkedHashMap<String, Object>> resultList = new ArrayList();
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, query);
        Iterator var6 = valuesMap.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, Object> entry = (Entry)var6.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int i = metaData.getColumnCount();

        while(resultSet.next()) {
            LinkedHashMap<String, Object> hashMap = new LinkedHashMap();

            for(int x = 1; x <= i; ++x) {
                hashMap.put(metaData.getColumnLabel(x), resultSet.getObject(x));
            }

            resultList.add(hashMap);
        }

        resultSet.close();
        connection.close();
        return resultList;
    }

    public LinkedHashMap<String, Object> getUserSpecificAuthDetail(String sqlquery, LinkedHashMap<String, Object> values) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        NamedPreparedStatement preparedStatement = NamedPreparedStatement.prepareStatement(connection, sqlquery);
        LinkedHashMap<String, Object> resultList = new LinkedHashMap();
        Iterator var6 = values.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, Object> entry = (Entry)var6.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            preparedStatement.setObject(key, value);
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int i = metaData.getColumnCount();

        while(resultSet.next()) {
            for(int j = 1; j < i; ++j) {
                resultList.put(metaData.getColumnLabel(j), resultSet.getObject(j));
            }
        }

        resultSet.close();
        return resultList;
    }

    public List<LinkedHashMap<String, Object>> getUserSpecificAuthDetails(String sqlquery, LinkedHashMap<String, Object> values) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + dbPort + dbName, DatabaseUserName, DatabasePassword);
        NamedPreparedStatement preparedStatement = NamedPreparedStatement.prepareStatement(connection, sqlquery);
        List<LinkedHashMap<String, Object>> resultList = new ArrayList();
        Iterator var6 = values.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, Object> entry = (Entry)var6.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            preparedStatement.setObject(key, value);
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int i = metaData.getColumnCount();

        while(resultSet.next()) {
            LinkedHashMap<String, Object> data = new LinkedHashMap();

            for(int j = 1; j < i; ++j) {
                data.put(metaData.getColumnLabel(j), resultSet.getObject(j));
            }

            resultList.add(data);
        }

        resultSet.close();
        connection.close();
        return resultList;
    }
}
