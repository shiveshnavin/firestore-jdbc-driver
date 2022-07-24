package io.shiveshnavin.firestore.jdbc;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import io.shiveshnavin.firestore.FJLogger;
import io.shiveshnavin.firestore.aspect.LoggingOperation;

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

        this.queryResult = queryResult;
        queryDocumentSnapshots = queryResult.getDocuments();
        size = queryDocumentSnapshots.size();
        FJLogger.debug("Retrieved " + size + " rows in result set");
    }

    public QuerySnapshot getQueryResult() {

        return queryResult;
    }
    @LoggingOperation
    @Override
    public boolean next() throws SQLException {

        index++;
        return getFetchSize() > index;
    }
    @LoggingOperation
    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {

        return queryDocumentSnapshots.get(index).toObject(aClass);
    }
    @LoggingOperation
    @Override
    public int getFetchSize() throws SQLException {

        return size;
    }
    @LoggingOperation
    @Override
    public <T> T getObject(int i, Class<T> aClass) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public <T> T getObject(String s, Class<T> aClass) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public String getString(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public boolean getBoolean(String s) throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public byte getByte(String s) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public short getShort(String s) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public int getInt(String s) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public long getLong(String s) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public float getFloat(String s) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public double getDouble(String s) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public byte[] getBytes(String s) throws SQLException {

        return new byte[0];
    }
    @LoggingOperation
    @Override
    public Date getDate(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Time getTime(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Timestamp getTimestamp(String s) throws SQLException {

        return null;
    }

    @LoggingOperation
    @Override
    public String getString(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public boolean getBoolean(int i) throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public byte getByte(int i) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public short getShort(int i) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public int getInt(int i) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public long getLong(int i) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public float getFloat(int i) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public double getDouble(int i) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public byte[] getBytes(int i) throws SQLException {

        return new byte[0];
    }
    @LoggingOperation
    @Override
    public Date getDate(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Time getTime(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Timestamp getTimestamp(int i) throws SQLException {

        return null;
    }































    @LoggingOperation
    @Override
    public void close() throws SQLException {


    }
    @LoggingOperation
    @Override
    public boolean wasNull() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public InputStream getAsciiStream(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public InputStream getUnicodeStream(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public InputStream getBinaryStream(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public InputStream getAsciiStream(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public InputStream getUnicodeStream(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public InputStream getBinaryStream(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public SQLWarning getWarnings() throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public void clearWarnings() throws SQLException {


    }
    @LoggingOperation
    @Override
    public String getCursorName() throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Object getObject(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Object getObject(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public int findColumn(String s) throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public Reader getCharacterStream(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Reader getCharacterStream(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public boolean isBeforeFirst() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public boolean isAfterLast() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public boolean isFirst() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public boolean isLast() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public void beforeFirst() throws SQLException {


    }
    @LoggingOperation
    @Override
    public void afterLast() throws SQLException {


    }
    @LoggingOperation
    @Override
    public boolean first() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public boolean last() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public int getRow() throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public boolean absolute(int i) throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public boolean relative(int i) throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public boolean previous() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public void setFetchDirection(int i) throws SQLException {


    }
    @LoggingOperation
    @Override
    public int getFetchDirection() throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public void setFetchSize(int i) throws SQLException {


    }

    @LoggingOperation
    @Override
    public int getType() throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public int getConcurrency() throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public boolean rowUpdated() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public boolean rowInserted() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public boolean rowDeleted() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public void updateNull(int i) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBoolean(int i, boolean b) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateByte(int i, byte b) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateShort(int i, short i1) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateInt(int i, int i1) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateLong(int i, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateFloat(int i, float v) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateDouble(int i, double v) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateString(int i, String s) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateDate(int i, Date date) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateTime(int i, Time time) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateObject(int i, Object o, int i1) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateObject(int i, Object o) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNull(String s) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBoolean(String s, boolean b) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateByte(String s, byte b) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateShort(String s, short i) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateInt(String s, int i) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateLong(String s, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateFloat(String s, float v) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateDouble(String s, double v) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateString(String s, String s1) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateDate(String s, Date date) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateTime(String s, Time time) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateObject(String s, Object o, int i) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateObject(String s, Object o) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void insertRow() throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateRow() throws SQLException {


    }
    @LoggingOperation
    @Override
    public void deleteRow() throws SQLException {


    }
    @LoggingOperation
    @Override
    public void refreshRow() throws SQLException {


    }
    @LoggingOperation
    @Override
    public void cancelRowUpdates() throws SQLException {


    }
    @LoggingOperation
    @Override
    public void moveToInsertRow() throws SQLException {


    }
    @LoggingOperation
    @Override
    public void moveToCurrentRow() throws SQLException {


    }
    @LoggingOperation
    @Override
    public Statement getStatement() throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Ref getRef(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Blob getBlob(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Clob getClob(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Array getArray(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Object getObject(String s, Map<String, Class<?>> map) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Ref getRef(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Blob getBlob(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Clob getClob(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Array getArray(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public URL getURL(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public URL getURL(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public void updateRef(int i, Ref ref) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateRef(String s, Ref ref) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBlob(int i, Blob blob) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBlob(String s, Blob blob) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateClob(int i, Clob clob) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateClob(String s, Clob clob) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateArray(int i, Array array) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateArray(String s, Array array) throws SQLException {


    }
    @LoggingOperation
    @Override
    public RowId getRowId(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public RowId getRowId(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException {


    }
    @LoggingOperation
    @Override
    public int getHoldability() throws SQLException {

        return 0;
    }
    @LoggingOperation
    @Override
    public boolean isClosed() throws SQLException {

        return false;
    }
    @LoggingOperation
    @Override
    public void updateNString(int i, String s) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNString(String s, String s1) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException {


    }
    @LoggingOperation
    @Override
    public NClob getNClob(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public NClob getNClob(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public SQLXML getSQLXML(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public SQLXML getSQLXML(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {


    }
    @LoggingOperation
    @Override
    public String getNString(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public String getNString(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Reader getNCharacterStream(int i) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public Reader getNCharacterStream(String s) throws SQLException {

        return null;
    }
    @LoggingOperation
    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateClob(int i, Reader reader) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateClob(String s, Reader reader) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNClob(int i, Reader reader) throws SQLException {


    }
    @LoggingOperation
    @Override
    public void updateNClob(String s, Reader reader) throws SQLException {


    }

    @LoggingOperation
    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {

        return false;
    }

}
