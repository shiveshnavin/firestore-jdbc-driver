package io.github.shiveshnavin.firestore.jdbc.metadata;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import io.github.shiveshnavin.firestore.exceptions.FirestoreJDBCException;
import io.github.shiveshnavin.firestore.jdbc.QuerySnapshotWrapper;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static io.github.shiveshnavin.firestore.jdbc.FirestoreJDBCResultSet.printCurrentMethod;

public class FirestoreResultSetMetaData implements ResultSetMetaData {
    private final QueryDocumentSnapshot snapshotSample;
    private final QuerySnapshotWrapper sample;
    private final Map<String, Object> data;
    private final Map<String, FirestoreColDefinition> colDefinitionMap;

    public FirestoreResultSetMetaData(QuerySnapshotWrapper sample, Map<String, FirestoreColDefinition> colDefinitionMap) {
        this.sample = sample;
        this.data = sample.getData();
        this.colDefinitionMap = colDefinitionMap;
        this.snapshotSample = sample.getSnapshot();
    }

    public Map<String, FirestoreColDefinition> getColDefinitionMap() {
        return colDefinitionMap;
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
    public int getColumnCount() throws SQLException {
        printCurrentMethod();
        return data.size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        printCurrentMethod();
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        printCurrentMethod();
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        printCurrentMethod();
        return false;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        printCurrentMethod();
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        printCurrentMethod();
        return 0;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        printCurrentMethod();
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        printCurrentMethod();
        return 0;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        printCurrentMethod();
        return getColNameFromIndex(column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        printCurrentMethod();
        return getColNameFromIndex(column);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        printCurrentMethod();
        return getColNameFromIndex(column);
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        printCurrentMethod();
        return 0;
    }

    @Override
    public int getScale(int column) throws SQLException {
        printCurrentMethod();
        return 0;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        printCurrentMethod();
        try {
            return this.snapshotSample.getReference().getParent().getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        printCurrentMethod();
        return null;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        printCurrentMethod();
        return 0;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        printCurrentMethod();
        Object o = data.get(getColNameFromIndex(column));
        if (o == null)
            return null;
        return o.getClass().getSimpleName();
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        printCurrentMethod();
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        printCurrentMethod();
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        printCurrentMethod();
        return false;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        printCurrentMethod();
        Object o = data.get(getColumnTypeName(column));
        if (o == null)
            return null;
        return o.getClass().getName();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        printCurrentMethod();
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        printCurrentMethod();
        return false;
    }
}
