package io.github.shiveshnavin.firestore.jdbc;

import com.google.gson.Gson;
import io.github.shiveshnavin.firestore.FJLogger;
import io.github.shiveshnavin.firestore.exceptions.FirestoreJDBCException;
import io.github.shiveshnavin.firestore.jdbc.metadata.FirestoreColDefinition;
import io.github.shiveshnavin.firestore.jdbc.metadata.FirestoreColType;
import io.github.shiveshnavin.firestore.jdbc.metadata.FirestoreResultSetMetaData;

import javax.sql.rowset.RowSetMetaDataImpl;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class FirestoreJDBCResultSet implements ResultSet {

    private int index = -1;
    private int size = 0;
    private static Gson gson = new Gson();

    private List<QuerySnapshotWrapper> queryDocumentSnapshots;
    private final Map<String, FirestoreColDefinition> colDefinitionMap;

    public FirestoreJDBCResultSet(Map<String, FirestoreColDefinition> aliasToColumnMap) {
        if (aliasToColumnMap == null)
            aliasToColumnMap = new LinkedHashMap<>();
        this.colDefinitionMap = aliasToColumnMap;
    }

    public Map<String, FirestoreColDefinition> getColDefinitionMap() {
        return colDefinitionMap;
    }

    public void setQueryResult(List<QuerySnapshotWrapper> queryResult) {

        queryDocumentSnapshots = queryResult;
        size = queryDocumentSnapshots.size();
        if (size > 0) {

            QuerySnapshotWrapper sample = queryDocumentSnapshots.get(0);
            Set<String> keys = new HashSet<>();
            if (sample.getSnapshot() != null) {
                keys = sample.getSnapshot().getData().keySet();
            } else if (sample.getData() != null) {
                keys = sample.getData().keySet();
            }
            int i = getColDefinitionMap().values()
                    .stream().mapToInt(FirestoreColDefinition::getIndex)
                    .max()
                    .orElse(-1);

            for (String key : keys) {
                if (!getColDefinitionMap().containsKey(key)
                        && getColDefinitionMap().values()
                        .stream().noneMatch(col -> col.getColumnName().equals(key))) {
                    getColDefinitionMap().put(key, new FirestoreColDefinition(++i, key, FirestoreColType.UNKNOWN));
                }
            }
        }
        FJLogger.debug("Retrieved " + size + " rows in result set");
    }

    public List<QuerySnapshotWrapper> getQueryResult() {

        return queryDocumentSnapshots;
    }

    private QuerySnapshotWrapper getDocumentPointer() {
        if (index < 0 || index >= queryDocumentSnapshots.size()) {
            throw new FirestoreJDBCException("Index out of bounds. Must call next() responsibly");
        }
        return queryDocumentSnapshots.get(index);
    }


    @Override
    public boolean next() throws SQLException {
        printCurrentMethod();

        index++;
        return getFetchSize() > index;
    }

    public static void printCurrentMethod() {
        StackTraceElement[] stackTrace = Thread.currentThread()
                .getStackTrace();
        FJLogger.debug("Method : " + stackTrace[2].getClassName() +
                "[" + stackTrace[2].getLineNumber() + "] - " + stackTrace[2].getMethodName() + "()");

    }


    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        printCurrentMethod();

        return getDocumentPointer().toObject(aClass);
    }


    @Override
    public int getFetchSize() throws SQLException {
        printCurrentMethod();
        return size;
    }


    @Override
    public <T> T getObject(int i, Class<T> aClass) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public <T> T getObject(String s, Class<T> aClass) throws SQLException {
        printCurrentMethod();

        return null;
    }

    private String getQualComName(String alias) {
        FirestoreColDefinition col = getColDefinitionMap().get(alias);
        String colname = FirestoreColDefinition.getColNameFromQualified(col.getColumnName());
        return colname;
    }


    @Override
    public String getString(String s) throws SQLException {
        printCurrentMethod();
        FirestoreColDefinition col = getColDefinitionMap().get(s);
        String colname = FirestoreColDefinition.getColNameFromQualified(col.getColumnName());
        Object value;
        if (getDocumentPointer().contains(colname)) {
            value = getDocumentPointer().get(colname);
        } else {
            value = getDocumentPointer().get(s);
        }
        if (value instanceof String)
            return (String) value;
        else if (value != null)
            return gson.toJson(value);
        return null;
    }


    @Override
    public boolean getBoolean(String s) throws SQLException {
        printCurrentMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return false;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getBoolean(s);
        return getDocumentPointer().getBoolean(getQualComName(s));
    }


    @Override
    public byte getByte(String s) throws SQLException {
        printCurrentMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getBlob(s).toBytes()[0];

        return getDocumentPointer().getBlob(getQualComName(s)).toBytes()[0];
    }


    @Override
    public short getShort(String s) throws SQLException {
        printCurrentMethod();
        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return (short) getDocumentPointer().getLong(s).intValue();
        return (short) getDocumentPointer().getLong(getQualComName(s)).intValue();
    }


    @Override
    public int getInt(String s) throws SQLException {
        printCurrentMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getLong(s).intValue();

        return getDocumentPointer().getLong(getQualComName(s)).intValue();
    }


    @Override
    public long getLong(String s) throws SQLException {
        printCurrentMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getLong(s);

        return getDocumentPointer().getLong(getQualComName(s));
    }


    @Override
    public float getFloat(String s) throws SQLException {
        printCurrentMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getDouble(s).floatValue();

        return getDocumentPointer().getDouble(getQualComName(s)).floatValue();
    }


    @Override
    public double getDouble(String s) throws SQLException {
        printCurrentMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return 0;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getDouble(s);

        return getDocumentPointer().getDouble(getQualComName(s));
    }


    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException {
        printCurrentMethod();

        return new BigDecimal(getDocumentPointer().getDouble(getQualComName(s)));
    }


    @Override
    public byte[] getBytes(String s) throws SQLException {
        printCurrentMethod();

        if (!getDocumentPointer().contains(getQualComName(s)) && !getDocumentPointer().contains(s)) return null;
        if (getDocumentPointer().contains(s))
            return getDocumentPointer().getBlob(s).toBytes();
        return getDocumentPointer().getBlob(getQualComName(s)).toBytes();
    }


    @Override
    public Date getDate(String s) throws SQLException {
        printCurrentMethod();
        if (getDocumentPointer().contains(s))
            return new Date(getDocumentPointer().getDate(s).getTime());

        return new Date(getDocumentPointer().getDate(getQualComName(s)).getTime());
    }


    @Override
    public Time getTime(String s) throws SQLException {
        printCurrentMethod();
        if (getDocumentPointer().contains(s))
            return new Time(getDocumentPointer().getDate((s)).getTime());

        return new Time(getDocumentPointer().getDate(getQualComName(s)).getTime());
    }


    @Override
    public Timestamp getTimestamp(String s) throws SQLException {
        printCurrentMethod();
        if (getDocumentPointer().contains(s))
            return new Timestamp(getDocumentPointer().getDate(getQualComName(s)).getTime());

        return new Timestamp(getDocumentPointer().getDate(getQualComName(s)).getTime());
    }

    private String getColNameFromIndex(int idx) {
        if (getColDefinitionMap().isEmpty()) {
            throw new FirestoreJDBCException("Retrieving columns by index not supported at present.");
        }
        return getColDefinitionMap().entrySet().stream().
                filter(e -> e.getValue().getIndex() == idx).
                findAny().get().getKey();
    }


    @Override
    public String getString(int i) throws SQLException {
        printCurrentMethod();

        return getString(getColNameFromIndex(i));
    }


    @Override
    public boolean getBoolean(int i) throws SQLException {
        printCurrentMethod();

        return getBoolean(getColNameFromIndex(i));
    }


    @Override
    public byte getByte(int i) throws SQLException {
        printCurrentMethod();

        return getByte(getColNameFromIndex(i));
    }


    @Override
    public short getShort(int i) throws SQLException {
        printCurrentMethod();

        return getShort(getColNameFromIndex(i));
    }


    @Override
    public int getInt(int i) throws SQLException {
        printCurrentMethod();

        return getInt(getColNameFromIndex(i));
    }


    @Override
    public long getLong(int i) throws SQLException {
        printCurrentMethod();

        return getLong(getColNameFromIndex(i));
    }


    @Override
    public float getFloat(int i) throws SQLException {
        printCurrentMethod();

        return getFloat(getColNameFromIndex(i));
    }


    @Override
    public double getDouble(int i) throws SQLException {
        printCurrentMethod();

        return getDouble(getColNameFromIndex(i));
    }


    @Override
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException {
        printCurrentMethod();

        return getBigDecimal(getColNameFromIndex(i));
    }


    @Override
    public byte[] getBytes(int i) throws SQLException {
        printCurrentMethod();

        return getBytes(getColNameFromIndex(i));
    }


    @Override
    public Date getDate(int i) throws SQLException {
        printCurrentMethod();

        return getDate(getColNameFromIndex(i));
    }


    @Override
    public Time getTime(int i) throws SQLException {
        printCurrentMethod();

        return getTime(getColNameFromIndex(i));
    }


    @Override
    public Timestamp getTimestamp(int i) throws SQLException {
        printCurrentMethod();

        return getTimestamp(getColNameFromIndex(i));
    }


    @Override
    public void close() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public boolean wasNull() throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public InputStream getAsciiStream(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public InputStream getUnicodeStream(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public InputStream getBinaryStream(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public InputStream getAsciiStream(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public InputStream getUnicodeStream(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public InputStream getBinaryStream(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public SQLWarning getWarnings() throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public void clearWarnings() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public String getCursorName() throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        printCurrentMethod();
        if (this.getQueryResult() == null || this.getQueryResult().isEmpty()) {
            throw new FirestoreJDBCException("Cannot get metadata if the query result is empty.");
        }
        QuerySnapshotWrapper sample = this.getQueryResult().get(0);
        ResultSetMetaData metaData = new FirestoreResultSetMetaData(sample, getColDefinitionMap());
        return metaData;
    }


    @Override
    public Object getObject(int i) throws SQLException {
        printCurrentMethod();

        return getDocumentPointer().get(getColNameFromIndex(i));
    }


    @Override
    public Object getObject(String s) throws SQLException {
        printCurrentMethod();

        return getDocumentPointer().get(s);
    }


    @Override
    public int findColumn(String s) throws SQLException {
        printCurrentMethod();

        return getColDefinitionMap().get(s).getIndex();
    }


    @Override
    public Reader getCharacterStream(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Reader getCharacterStream(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException {
        printCurrentMethod();
        if (getDocumentPointer().getString(s) == null) {
            return null;
        }
        return new BigDecimal(getDocumentPointer().getString(s));

    }


    @Override
    public boolean isBeforeFirst() throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public boolean isAfterLast() throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public boolean isFirst() throws SQLException {
        printCurrentMethod();

        return index == 0;
    }


    @Override
    public boolean isLast() throws SQLException {
        printCurrentMethod();

        return index >= queryDocumentSnapshots.size() - 1;
    }


    @Override
    public void beforeFirst() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void afterLast() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public boolean first() throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public boolean last() throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public int getRow() throws SQLException {
        printCurrentMethod();

        return 0;
    }


    @Override
    public boolean absolute(int i) throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public boolean relative(int i) throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public boolean previous() throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public void setFetchDirection(int i) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public int getFetchDirection() throws SQLException {
        printCurrentMethod();

        return 0;
    }


    @Override
    public void setFetchSize(int i) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public int getType() throws SQLException {
        printCurrentMethod();

        return 0;
    }


    @Override
    public int getConcurrency() throws SQLException {
        printCurrentMethod();

        return 0;
    }


    @Override
    public boolean rowUpdated() throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public boolean rowInserted() throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public boolean rowDeleted() throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public void updateNull(int i) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBoolean(int i, boolean b) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateByte(int i, byte b) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateShort(int i, short i1) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateInt(int i, int i1) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateLong(int i, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateFloat(int i, float v) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateDouble(int i, double v) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateString(int i, String s) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateDate(int i, Date date) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateTime(int i, Time time) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateObject(int i, Object o, int i1) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateObject(int i, Object o) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNull(String s) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBoolean(String s, boolean b) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateByte(String s, byte b) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateShort(String s, short i) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateInt(String s, int i) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateLong(String s, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateFloat(String s, float v) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateDouble(String s, double v) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateString(String s, String s1) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateDate(String s, Date date) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateTime(String s, Time time) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateObject(String s, Object o, int i) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateObject(String s, Object o) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void insertRow() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateRow() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void deleteRow() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void refreshRow() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void cancelRowUpdates() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void moveToInsertRow() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void moveToCurrentRow() throws SQLException {
        printCurrentMethod();


    }


    @Override
    public Statement getStatement() throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Ref getRef(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Blob getBlob(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Clob getClob(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Array getArray(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Object getObject(String s, Map<String, Class<?>> map) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Ref getRef(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Blob getBlob(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Clob getClob(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Array getArray(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public URL getURL(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public URL getURL(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public void updateRef(int i, Ref ref) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateRef(String s, Ref ref) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBlob(int i, Blob blob) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBlob(String s, Blob blob) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateClob(int i, Clob clob) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateClob(String s, Clob clob) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateArray(int i, Array array) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateArray(String s, Array array) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public RowId getRowId(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public RowId getRowId(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public int getHoldability() throws SQLException {
        printCurrentMethod();

        return 0;
    }


    @Override
    public boolean isClosed() throws SQLException {
        printCurrentMethod();

        return false;
    }


    @Override
    public void updateNString(int i, String s) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNString(String s, String s1) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public NClob getNClob(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public NClob getNClob(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public SQLXML getSQLXML(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public SQLXML getSQLXML(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public String getNString(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public String getNString(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Reader getNCharacterStream(int i) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public Reader getNCharacterStream(String s) throws SQLException {
        printCurrentMethod();

        return null;
    }


    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateClob(int i, Reader reader) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateClob(String s, Reader reader) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNClob(int i, Reader reader) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public void updateNClob(String s, Reader reader) throws SQLException {
        printCurrentMethod();


    }


    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        printCurrentMethod();

        return false;
    }

}
