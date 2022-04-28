//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.model;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

class PreparedStatementImpl implements PreparedStatement {
    private final PreparedStatement preparedStatement;

    protected PreparedStatement getPreparedStatement() {
        return this.preparedStatement;
    }

    public PreparedStatementImpl(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.preparedStatement.unwrap(iface);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        return this.preparedStatement.executeQuery(sql);
    }

    public ResultSet executeQuery() throws SQLException {
        return this.preparedStatement.executeQuery();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.preparedStatement.isWrapperFor(iface);
    }

    public int executeUpdate(String sql) throws SQLException {
        return this.preparedStatement.executeUpdate(sql);
    }

    public int executeUpdate() throws SQLException {
        return this.preparedStatement.executeUpdate();
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        this.preparedStatement.setNull(parameterIndex, sqlType);
    }

    public void close() throws SQLException {
        this.preparedStatement.close();
    }

    public int getMaxFieldSize() throws SQLException {
        return this.preparedStatement.getMaxFieldSize();
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        this.preparedStatement.setBoolean(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        this.preparedStatement.setByte(parameterIndex, x);
    }

    public void setMaxFieldSize(int max) throws SQLException {
        this.preparedStatement.setMaxFieldSize(max);
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        this.preparedStatement.setShort(parameterIndex, x);
    }

    public int getMaxRows() throws SQLException {
        return this.preparedStatement.getMaxRows();
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        this.preparedStatement.setInt(parameterIndex, x);
    }

    public void setMaxRows(int max) throws SQLException {
        this.preparedStatement.setMaxRows(max);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        this.preparedStatement.setLong(parameterIndex, x);
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        this.preparedStatement.setEscapeProcessing(enable);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        this.preparedStatement.setFloat(parameterIndex, x);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        this.preparedStatement.setDouble(parameterIndex, x);
    }

    public int getQueryTimeout() throws SQLException {
        return this.preparedStatement.getQueryTimeout();
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        this.preparedStatement.setQueryTimeout(seconds);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        this.preparedStatement.setBigDecimal(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        this.preparedStatement.setString(parameterIndex, x);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        this.preparedStatement.setBytes(parameterIndex, x);
    }

    public void cancel() throws SQLException {
        this.preparedStatement.cancel();
    }

    public SQLWarning getWarnings() throws SQLException {
        return this.preparedStatement.getWarnings();
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        this.preparedStatement.setDate(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        this.preparedStatement.setTime(parameterIndex, x);
    }

    public void clearWarnings() throws SQLException {
        this.preparedStatement.clearWarnings();
    }

    public void setCursorName(String name) throws SQLException {
        this.preparedStatement.setCursorName(name);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        this.preparedStatement.setTimestamp(parameterIndex, x);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.preparedStatement.setAsciiStream(parameterIndex, x, length);
    }

    public boolean execute(String sql) throws SQLException {
        return this.preparedStatement.execute(sql);
    }

    /** @deprecated */
    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.preparedStatement.setUnicodeStream(parameterIndex, x, length);
    }

    public ResultSet getResultSet() throws SQLException {
        return this.preparedStatement.getResultSet();
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.preparedStatement.setBinaryStream(parameterIndex, x, length);
    }

    public int getUpdateCount() throws SQLException {
        return this.preparedStatement.getUpdateCount();
    }

    public boolean getMoreResults() throws SQLException {
        return this.preparedStatement.getMoreResults();
    }

    public void clearParameters() throws SQLException {
        this.preparedStatement.clearParameters();
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        this.preparedStatement.setObject(parameterIndex, x, targetSqlType);
    }

    public void setFetchDirection(int direction) throws SQLException {
        this.preparedStatement.setFetchDirection(direction);
    }

    public int getFetchDirection() throws SQLException {
        return this.preparedStatement.getFetchDirection();
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        this.preparedStatement.setObject(parameterIndex, x);
    }

    public void setFetchSize(int rows) throws SQLException {
        this.preparedStatement.setFetchSize(rows);
    }

    public int getFetchSize() throws SQLException {
        return this.preparedStatement.getFetchSize();
    }

    public int getResultSetConcurrency() throws SQLException {
        return this.preparedStatement.getResultSetConcurrency();
    }

    public boolean execute() throws SQLException {
        return this.preparedStatement.execute();
    }

    public int getResultSetType() throws SQLException {
        return this.preparedStatement.getResultSetType();
    }

    public void addBatch(String sql) throws SQLException {
        this.preparedStatement.addBatch(sql);
    }

    public void clearBatch() throws SQLException {
        this.preparedStatement.clearBatch();
    }

    public void addBatch() throws SQLException {
        this.preparedStatement.addBatch();
    }

    public int[] executeBatch() throws SQLException {
        return this.preparedStatement.executeBatch();
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        this.preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    public void setRef(int parameterIndex, Ref x) throws SQLException {
        this.preparedStatement.setRef(parameterIndex, x);
    }

    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        this.preparedStatement.setBlob(parameterIndex, x);
    }

    public void setClob(int parameterIndex, Clob x) throws SQLException {
        this.preparedStatement.setClob(parameterIndex, x);
    }

    public Connection getConnection() throws SQLException {
        return this.preparedStatement.getConnection();
    }

    public void setArray(int parameterIndex, Array x) throws SQLException {
        this.preparedStatement.setArray(parameterIndex, x);
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return this.preparedStatement.getMetaData();
    }

    public boolean getMoreResults(int current) throws SQLException {
        return this.preparedStatement.getMoreResults(current);
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        this.preparedStatement.setDate(parameterIndex, x, cal);
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return this.preparedStatement.getGeneratedKeys();
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        this.preparedStatement.setTime(parameterIndex, x, cal);
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return this.preparedStatement.executeUpdate(sql, autoGeneratedKeys);
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        this.preparedStatement.setTimestamp(parameterIndex, x, cal);
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        this.preparedStatement.setNull(parameterIndex, sqlType, typeName);
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return this.preparedStatement.executeUpdate(sql, columnIndexes);
    }

    public void setURL(int parameterIndex, URL x) throws SQLException {
        this.preparedStatement.setURL(parameterIndex, x);
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return this.preparedStatement.executeUpdate(sql, columnNames);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return this.preparedStatement.getParameterMetaData();
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        this.preparedStatement.setRowId(parameterIndex, x);
    }

    public void setNString(int parameterIndex, String value) throws SQLException {
        this.preparedStatement.setNString(parameterIndex, value);
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return this.preparedStatement.execute(sql, autoGeneratedKeys);
    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        this.preparedStatement.setNCharacterStream(parameterIndex, value, length);
    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        this.preparedStatement.setNClob(parameterIndex, value);
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        this.preparedStatement.setClob(parameterIndex, reader, length);
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return this.preparedStatement.execute(sql, columnIndexes);
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        this.preparedStatement.setBlob(parameterIndex, inputStream, length);
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        this.preparedStatement.setNClob(parameterIndex, reader, length);
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return this.preparedStatement.execute(sql, columnNames);
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        this.preparedStatement.setSQLXML(parameterIndex, xmlObject);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        this.preparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    public int getResultSetHoldability() throws SQLException {
        return this.preparedStatement.getResultSetHoldability();
    }

    public boolean isClosed() throws SQLException {
        return this.preparedStatement.isClosed();
    }

    public void setPoolable(boolean poolable) throws SQLException {
        this.preparedStatement.setPoolable(poolable);
    }

    public boolean isPoolable() throws SQLException {
        return this.preparedStatement.isPoolable();
    }

    public void closeOnCompletion() throws SQLException {
        this.preparedStatement.closeOnCompletion();
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        this.preparedStatement.setAsciiStream(parameterIndex, x, length);
    }

    public boolean isCloseOnCompletion() throws SQLException {
        return this.preparedStatement.isCloseOnCompletion();
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        this.preparedStatement.setBinaryStream(parameterIndex, x, length);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        this.preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        this.preparedStatement.setAsciiStream(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        this.preparedStatement.setBinaryStream(parameterIndex, x);
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        this.preparedStatement.setCharacterStream(parameterIndex, reader);
    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        this.preparedStatement.setNCharacterStream(parameterIndex, value);
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        this.preparedStatement.setClob(parameterIndex, reader);
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        this.preparedStatement.setBlob(parameterIndex, inputStream);
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        this.preparedStatement.setNClob(parameterIndex, reader);
    }
}
