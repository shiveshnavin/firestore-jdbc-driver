package io.shiveshnavin.firestore.jdbc;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import io.shiveshnavin.firestore.FJLogger;
import io.shiveshnavin.firestore.aspect.LoggingOperation;
import io.shiveshnavin.firestore.jdbc.metadata.FirestoreColDefinition;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FirestoreJDBCResultSet implements ResultSet {

    private int index = -1;
    private int size = 0;
    private List<QuerySnapshotWrapper> queryDocumentSnapshots;
    Map<String, FirestoreColDefinition> colDefinitionMap;

    public FirestoreJDBCResultSet(Map<String, FirestoreColDefinition> aliasToColumnMap) {
        this.colDefinitionMap = aliasToColumnMap;
    }

    public void setQueryResult(List<QuerySnapshotWrapper> queryResult) {

        queryDocumentSnapshots = queryResult;
        size = queryDocumentSnapshots.size();
        FJLogger.debug("Retrieved " + size + " rows in result set");
    }

    public List<QuerySnapshotWrapper> getQueryResult() {

        return queryDocumentSnapshots;
    }

    private QuerySnapshotWrapper getDocumentPointer() {
        return queryDocumentSnapshots.get(index);
    }

    @LoggingOperation
    @Override
    public boolean next() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        index++;
        return getFetchSize() > index;
    }

    public static void givenCurrentThread_whenGetStackTrace_thenFindMethod() {
        StackTraceElement[] stackTrace = Thread.currentThread()
                .getStackTrace();
        System.out.println("Method : " + stackTrace[2].getClassName() +
                "[" + stackTrace[2].getLineNumber() + "] - " + stackTrace[2].getMethodName() + "()");

    }


    @LoggingOperation
    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getDocumentPointer().toObject(aClass);
    }

    @LoggingOperation
    @Override
    public int getFetchSize() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();
        return size;
    }

    @LoggingOperation
    @Override
    public <T> T getObject(int i, Class<T> aClass) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public <T> T getObject(String s, Class<T> aClass) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    private String getQualComName(String alias) {
        FirestoreColDefinition col = colDefinitionMap.get(alias);
        String colname = FirestoreColDefinition.getColNameFromQualified(col.getColumnName());
        return colname;
    }

    @LoggingOperation
    @Override
    public String getString(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();
        FirestoreColDefinition col = colDefinitionMap.get(s);
        String colname = FirestoreColDefinition.getColNameFromQualified(col.getColumnName());
        String value;
        if (getDocumentPointer().contains(colname)) {
            value = getDocumentPointer().getString(colname);
        } else {
            value = getDocumentPointer().getString(s);
        }
        return value;
    }

    @LoggingOperation
    @Override
    public boolean getBoolean(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return false;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getBoolean(s);
        return getDocumentPointer().getBoolean(getQualComName(s));
    }

    @LoggingOperation
    @Override
    public byte getByte(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getBlob(s).toBytes()[0];

        return getDocumentPointer().getBlob(getQualComName(s)).toBytes()[0];
    }

    @LoggingOperation
    @Override
    public short getShort(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();
        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return (short) getDocumentPointer().getLong(s).intValue();
        return (short) getDocumentPointer().getLong(getQualComName(s)).intValue();
    }

    @LoggingOperation
    @Override
    public int getInt(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getLong(s).intValue();

        return getDocumentPointer().getLong(getQualComName(s)).intValue();
    }

    @LoggingOperation
    @Override
    public long getLong(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getLong(s);

        return getDocumentPointer().getLong(getQualComName(s));
    }

    @LoggingOperation
    @Override
    public float getFloat(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getDouble(s).floatValue();

        return getDocumentPointer().getDouble(getQualComName(s)).floatValue();
    }

    @LoggingOperation
    @Override
    public double getDouble(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getDouble(s);

        return getDocumentPointer().getDouble(getQualComName(s));
    }

    @LoggingOperation
    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return new BigDecimal(getDocumentPointer().getDouble(getQualComName(s)));
    }

    @LoggingOperation
    @Override
    public byte[] getBytes(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return null;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getBlob(s).toBytes();
        return getDocumentPointer().getBlob(getQualComName(s)).toBytes();
    }

    @LoggingOperation
    @Override
    public Date getDate(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();
        if (getDocumentPointer().contains(s))
            return new Date(getDocumentPointer().getDate(s).getTime());

        return new Date(getDocumentPointer().getDate(getQualComName(s)).getTime());
    }

    @LoggingOperation
    @Override
    public Time getTime(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();
        if (getDocumentPointer().contains(s))
            return new Time(getDocumentPointer().getDate((s)).getTime());

        return new Time(getDocumentPointer().getDate(getQualComName(s)).getTime());
    }

    @LoggingOperation
    @Override
    public Timestamp getTimestamp(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();
        if (getDocumentPointer().contains(s))
            return new Timestamp(getDocumentPointer().getDate((s)).getTime());

        return new Timestamp(getDocumentPointer().getDate(getQualComName(s)).getTime());
    }

    private String getColNameFromIndex(int idx) {
        return colDefinitionMap.entrySet().stream().
                filter(e -> e.getValue().getIndex() == idx).
                findAny().get().getValue().getColumnName();
    }

    @LoggingOperation
    @Override
    public String getString(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getString(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public boolean getBoolean(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getBoolean(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public byte getByte(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getByte(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public short getShort(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getShort(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public int getInt(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getInt(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public long getLong(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getLong(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public float getFloat(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getFloat(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public double getDouble(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getDouble(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getBigDecimal(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public byte[] getBytes(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getBytes(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public Date getDate(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getDate(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public Time getTime(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getTime(getColNameFromIndex(i));
    }

    @LoggingOperation
    @Override
    public Timestamp getTimestamp(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return getTimestamp(getColNameFromIndex(i));
    }


    @LoggingOperation
    @Override
    public void close() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public boolean wasNull() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public InputStream getAsciiStream(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public InputStream getUnicodeStream(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public InputStream getBinaryStream(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public InputStream getAsciiStream(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public InputStream getUnicodeStream(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public InputStream getBinaryStream(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public SQLWarning getWarnings() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public void clearWarnings() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public String getCursorName() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Object getObject(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Object getObject(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public int findColumn(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @LoggingOperation
    @Override
    public Reader getCharacterStream(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Reader getCharacterStream(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public boolean isBeforeFirst() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public boolean isAfterLast() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public boolean isFirst() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public boolean isLast() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public void beforeFirst() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void afterLast() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public boolean first() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public boolean last() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public int getRow() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @LoggingOperation
    @Override
    public boolean absolute(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public boolean relative(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public boolean previous() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public void setFetchDirection(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public int getFetchDirection() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @LoggingOperation
    @Override
    public void setFetchSize(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public int getType() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @LoggingOperation
    @Override
    public int getConcurrency() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @LoggingOperation
    @Override
    public boolean rowUpdated() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public boolean rowInserted() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public boolean rowDeleted() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public void updateNull(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBoolean(int i, boolean b) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateByte(int i, byte b) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateShort(int i, short i1) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateInt(int i, int i1) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateLong(int i, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateFloat(int i, float v) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateDouble(int i, double v) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateString(int i, String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateDate(int i, Date date) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateTime(int i, Time time) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateObject(int i, Object o, int i1) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateObject(int i, Object o) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNull(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBoolean(String s, boolean b) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateByte(String s, byte b) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateShort(String s, short i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateInt(String s, int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateLong(String s, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateFloat(String s, float v) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateDouble(String s, double v) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateString(String s, String s1) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateDate(String s, Date date) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateTime(String s, Time time) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateObject(String s, Object o, int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateObject(String s, Object o) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void insertRow() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateRow() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void deleteRow() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void refreshRow() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void cancelRowUpdates() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void moveToInsertRow() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void moveToCurrentRow() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public Statement getStatement() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Ref getRef(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Blob getBlob(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Clob getClob(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Array getArray(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Object getObject(String s, Map<String, Class<?>> map) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Ref getRef(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Blob getBlob(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Clob getClob(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Array getArray(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public URL getURL(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public URL getURL(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public void updateRef(int i, Ref ref) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateRef(String s, Ref ref) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBlob(int i, Blob blob) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBlob(String s, Blob blob) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateClob(int i, Clob clob) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateClob(String s, Clob clob) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateArray(int i, Array array) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateArray(String s, Array array) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public RowId getRowId(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public RowId getRowId(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public int getHoldability() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @LoggingOperation
    @Override
    public boolean isClosed() throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @LoggingOperation
    @Override
    public void updateNString(int i, String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNString(String s, String s1) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public NClob getNClob(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public NClob getNClob(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public SQLXML getSQLXML(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public SQLXML getSQLXML(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public String getNString(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public String getNString(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Reader getNCharacterStream(int i) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public Reader getNCharacterStream(String s) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @LoggingOperation
    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateClob(int i, Reader reader) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateClob(String s, Reader reader) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNClob(int i, Reader reader) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public void updateNClob(String s, Reader reader) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @LoggingOperation
    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

}
