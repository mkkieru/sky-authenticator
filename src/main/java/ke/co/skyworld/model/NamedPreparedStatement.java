//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

public class NamedPreparedStatement extends PreparedStatementImpl {
    private String originalSQL;
    private final List<String> lstParameters;

    private NamedPreparedStatement(PreparedStatement preparedStatement, String originalSQL, List<String> orderedParameters) {
        super(preparedStatement);
        this.originalSQL = originalSQL.trim();
        this.lstParameters = orderedParameters;
    }

    public static NamedPreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {
        Object[] parameters = getNamedPreparedStatementParameters(sql);
        return new NamedPreparedStatement(conn.prepareStatement(String.valueOf(parameters[0])), sql, (List)parameters[1]);
    }

    public static NamedPreparedStatement prepareStatement(Connection conn, String sql, int var1) throws SQLException {
        Object[] parameters = getNamedPreparedStatementParameters(sql);
        return new NamedPreparedStatement(conn.prepareStatement(String.valueOf(parameters[0]), var1), sql, (List)parameters[1]);
    }

    public static NamedPreparedStatement prepareStatement(Connection conn, String sql, int var1, int var2) throws SQLException {
        Object[] parameters = getNamedPreparedStatementParameters(sql);
        return new NamedPreparedStatement(conn.prepareStatement(String.valueOf(parameters[0]), var1, var2), sql, (List)parameters[1]);
    }

    public static NamedPreparedStatement prepareStatement(Connection conn, String sql, int var1, int var2, int var3) throws SQLException {
        Object[] parameters = getNamedPreparedStatementParameters(sql);
        return new NamedPreparedStatement(conn.prepareStatement(String.valueOf(parameters[0]), var1, var2, var3), sql, (List)parameters[1]);
    }

    public static NamedPreparedStatement prepareStatement(Connection conn, String sql, int[] var) throws SQLException {
        Object[] parameters = getNamedPreparedStatementParameters(sql);
        return new NamedPreparedStatement(conn.prepareStatement(String.valueOf(parameters[0]), var), sql, (List)parameters[1]);
    }

    public static NamedPreparedStatement prepareStatement(Connection conn, String sql, String[] var) throws SQLException {
        Object[] parameters = getNamedPreparedStatementParameters(sql);
        return new NamedPreparedStatement(conn.prepareStatement(String.valueOf(parameters[0]), var), sql, (List)parameters[1]);
    }

    public static Object[] getNamedPreparedStatementParameters(String sql) {
        List<String> orderedParameters = new ArrayList();
        int length = sql.length();
        StringBuilder parsedQuery = new StringBuilder(length);
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inSingleLineComment = false;
        boolean inMultiLineComment = false;

        for(int i = 0; i < length; ++i) {
            char c = sql.charAt(i);
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else if (inMultiLineComment) {
                if (c == '*' && sql.charAt(i + 1) == '/') {
                    inMultiLineComment = false;
                }
            } else if (inSingleLineComment) {
                if (c == '\n') {
                    inSingleLineComment = false;
                }
            } else if (c == '\'') {
                inSingleQuote = true;
            } else if (c == '"') {
                inDoubleQuote = true;
            } else if (c == '/' && sql.charAt(i + 1) == '*') {
                inMultiLineComment = true;
            } else if (c == '-' && sql.charAt(i + 1) == '-') {
                inSingleLineComment = true;
            } else if (c == ':' && i + 1 < length && Character.isJavaIdentifierStart(sql.charAt(i + 1))) {
                int j;
                for(j = i + 2; j < length && Character.isJavaIdentifierPart(sql.charAt(j)); ++j) {
                }

                String name = sql.substring(i + 1, j);
                orderedParameters.add(name);
                c = '?';
                i += name.length();
            }

            parsedQuery.append(c);
        }

        return new Object[]{parsedQuery.toString(), orderedParameters};
    }

    private Collection<Integer> getParameterIndexes(String parameter) {
        Collection<Integer> indexes = new ArrayList();

        for(int i = 0; i < this.lstParameters.size(); ++i) {
            if (((String)this.lstParameters.get(i)).equalsIgnoreCase(parameter)) {
                indexes.add(i + 1);
            }
        }

        if (indexes.isEmpty()) {
            throw new IllegalArgumentException(String.format("SQL statement doesn't contain the parameter '%s'", parameter));
        } else {
            return indexes;
        }
    }

    public void setNull(String parameter, int sqlType) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format((String)null, NamedPreparedStatement.FormatType.NULL)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setNull(i, sqlType);
        }

    }

    public void setBoolean(String parameter, boolean x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.BOOLEAN)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setBoolean(i, x);
        }

    }

    public void setByte(String parameter, byte x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.BYTE)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setByte(i, x);
        }

    }

    public void setShort(String parameter, short x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.SHORT)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setShort(i, x);
        }

    }

    public void setInt(String parameter, int x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.INTEGER)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setInt(i, x);
        }

    }

    public void setLong(String parameter, long x) throws SQLException {
        for(Iterator var4 = this.getParameterIndexes(parameter).iterator(); var4.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.LONG)))) {
            Integer i = (Integer)var4.next();
            this.getPreparedStatement().setLong(i, x);
        }

    }

    public void setFloat(String parameter, float x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.FLOAT)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setFloat(i, x);
        }

    }

    public void setDouble(String parameter, double x) throws SQLException {
        for(Iterator var4 = this.getParameterIndexes(parameter).iterator(); var4.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.DOUBLE)))) {
            Integer i = (Integer)var4.next();
            this.getPreparedStatement().setDouble(i, x);
        }

    }

    public void setBigDecimal(String parameter, BigDecimal x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.BIGDECIMAL)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setBigDecimal(i, x);
        }

    }

    public void setString(String parameter, String x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.STRING)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setString(i, x);
        }

    }

    public void setObject(String parameter, Object x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.OBJECT)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setObject(i, x);
        }

    }

    public void setBytes(String parameter, byte[] x) throws SQLException {
        String fval;
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(fval, NamedPreparedStatement.FormatType.STRING)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setBytes(i, x);
            fval = "";

            for(int j = 0; j < x.length; ++j) {
                fval = fval + (char)x[j] + ",";
            }

            if (fval.endsWith(",")) {
                fval = fval.substring(0, fval.length() - 1);
            }
        }

    }

    public void setDate(String parameter, Date x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.DATE)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setDate(i, x);
        }

    }

    public void setTime(String parameter, Time x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.TIME)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setTime(i, x);
        }

    }

    public void setTimestamp(String parameter, Timestamp x) throws SQLException {
        for(Iterator var3 = this.getParameterIndexes(parameter).iterator(); var3.hasNext(); this.originalSQL = this.originalSQL.replaceFirst("(?i):" + parameter, Matcher.quoteReplacement(this.format(x, NamedPreparedStatement.FormatType.TIMESTAMP)))) {
            Integer i = (Integer)var3.next();
            this.getPreparedStatement().setTimestamp(i, x);
        }

    }

    public String getQuery() {
        return this.originalSQL.trim();
    }

    private String format(Object o, NamedPreparedStatement.FormatType type) {
        String returnParam = "";

        try {
            switch(type) {
                case NULL:
                    returnParam = "NULL";
                    break;
                case OBJECT:
                    if (o != null) {
                        if (o instanceof String) {
                            returnParam = "'" + o.toString() + "'";
                        } else {
                            returnParam = o.toString();
                        }
                    } else {
                        returnParam = "NULL";
                    }
                    break;
                case BIGDECIMAL:
                    returnParam = o == null ? "NULL" : "'" + ((BigDecimal)o).toString() + "'";
                    break;
                case BOOLEAN:
                    returnParam = o == null ? "NULL" : "'" + ((Boolean)o == Boolean.TRUE ? "1" : "0") + "'";
                    break;
                case BYTE:
                    returnParam = o == null ? "NULL" : "'" + ((Byte)o).intValue() + "'";
                    break;
                case DATE:
                    returnParam = o == null ? "NULL" : "'" + (new SimpleDateFormat("yyyy-MM-dd")).format((Date)o) + "'";
                    break;
                case DOUBLE:
                    returnParam = o == null ? "NULL" : "'" + ((Double)o).toString() + "'";
                    break;
                case FLOAT:
                    returnParam = o == null ? "NULL" : "'" + ((Float)o).toString() + "'";
                    break;
                case INTEGER:
                    returnParam = o == null ? "NULL" : "'" + ((Integer)o).toString() + "'";
                    break;
                case LONG:
                    returnParam = o == null ? "NULL" : "'" + ((Long)o).toString() + "'";
                    break;
                case SHORT:
                    returnParam = o == null ? "NULL" : "'" + ((Short)o).toString() + "'";
                    break;
                case STRING:
                    returnParam = o == null ? "NULL" : "'" + o.toString() + "'";
                    break;
                case STRINGLIST:
                    returnParam = o == null ? "NULL" : "'" + o.toString() + "'";
                    break;
                case TIME:
                    returnParam = o == null ? "NULL" : "'" + (new SimpleDateFormat("hh:mm:ss a")).format(o) + "'";
                    break;
                case TIMESTAMP:
                    returnParam = o == null ? "NULL" : "'" + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")).format(o) + "'";
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return returnParam.trim();
    }

    private static enum FormatType {
        NULL,
        BOOLEAN,
        BYTE,
        SHORT,
        INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        BIGDECIMAL,
        STRING,
        STRINGLIST,
        DATE,
        TIME,
        TIMESTAMP,
        OBJECT;

        private FormatType() {
        }
    }
}
