package io.shiveshnavin.firestore.jdbc;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import io.shiveshnavin.firestore.FJLogger;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class FirestoreJDBCResultSet implements ResultSet {

    private QuerySnapshot queryResult;
    private int index = -1;
    private int size = 0;
    private List<QueryDocumentSnapshot> queryDocumentSnapshots;

    public void setQueryResult(QuerySnapshot queryResult) {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        this.queryResult = queryResult;
        queryDocumentSnapshots = queryResult.getDocuments();
        size = queryDocumentSnapshots.size();
        FJLogger.debug("Retrieved " + size + " rows in result set");
    }

    public QuerySnapshot getQueryResult() {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return queryResult;
    }

    @Override
    public boolean next() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        index++;
        return getFetchSize() > index;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return queryDocumentSnapshots.get(index).toObject(aClass);
    }

    @Override
    public int getFetchSize() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return size;
    }

    @Override
    public <T> T getObject(int i, Class<T> aClass) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public <T> T getObject(String s, Class<T> aClass) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public String getString(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public boolean getBoolean(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public byte getByte(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public short getShort(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public int getInt(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public long getLong(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public float getFloat(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public double getDouble(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public byte[] getBytes(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return new byte[0];
    }

    @Override
    public Date getDate(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Time getTime(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Timestamp getTimestamp(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }


    @Override
    public String getString(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public boolean getBoolean(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public byte getByte(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public short getShort(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public int getInt(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public long getLong(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public float getFloat(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public double getDouble(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public byte[] getBytes(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return new byte[0];
    }

    @Override
    public Date getDate(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Time getTime(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Timestamp getTimestamp(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }
































    @Override
    public void close() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public boolean wasNull() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public InputStream getAsciiStream(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public InputStream getUnicodeStream(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public InputStream getBinaryStream(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public InputStream getAsciiStream(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public InputStream getUnicodeStream(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public InputStream getBinaryStream(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public String getCursorName() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Object getObject(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Object getObject(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public int findColumn(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public Reader getCharacterStream(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Reader getCharacterStream(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public boolean isFirst() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public boolean isLast() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public void beforeFirst() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void afterLast() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public boolean first() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public boolean last() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public int getRow() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public boolean absolute(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public boolean relative(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public boolean previous() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public void setFetchDirection(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public int getFetchDirection() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public void setFetchSize(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }


    @Override
    public int getType() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public int getConcurrency() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public void updateNull(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBoolean(int i, boolean b) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateByte(int i, byte b) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateShort(int i, short i1) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateInt(int i, int i1) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateLong(int i, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateFloat(int i, float v) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateDouble(int i, double v) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateString(int i, String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateDate(int i, Date date) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateTime(int i, Time time) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateObject(int i, Object o, int i1) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateObject(int i, Object o) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNull(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBoolean(String s, boolean b) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateByte(String s, byte b) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateShort(String s, short i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateInt(String s, int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateLong(String s, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateFloat(String s, float v) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateDouble(String s, double v) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateString(String s, String s1) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateDate(String s, Date date) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateTime(String s, Time time) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateObject(String s, Object o, int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateObject(String s, Object o) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void insertRow() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateRow() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void deleteRow() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void refreshRow() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void moveToInsertRow() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public Statement getStatement() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Ref getRef(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Blob getBlob(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Clob getClob(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Array getArray(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Object getObject(String s, Map<String, Class<?>> map) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Ref getRef(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Blob getBlob(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Clob getClob(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Array getArray(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public URL getURL(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public URL getURL(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public void updateRef(int i, Ref ref) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateRef(String s, Ref ref) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBlob(int i, Blob blob) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBlob(String s, Blob blob) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateClob(int i, Clob clob) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateClob(String s, Clob clob) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateArray(int i, Array array) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateArray(String s, Array array) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public RowId getRowId(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public RowId getRowId(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public int getHoldability() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

    @Override
    public void updateNString(int i, String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNString(String s, String s1) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public NClob getNClob(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public NClob getNClob(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public SQLXML getSQLXML(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public SQLXML getSQLXML(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public String getNString(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public String getNString(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Reader getNCharacterStream(int i) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public Reader getNCharacterStream(String s) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return null;
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateClob(int i, Reader reader) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateClob(String s, Reader reader) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNClob(int i, Reader reader) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }

    @Override
    public void updateNClob(String s, Reader reader) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");

    }


    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        System.out.println("FJLogger : $CLASS_NAME$.$METHOD_NAME$");
        return false;
    }

}
