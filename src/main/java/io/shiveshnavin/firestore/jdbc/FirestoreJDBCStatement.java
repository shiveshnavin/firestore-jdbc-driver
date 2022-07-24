package io.shiveshnavin.firestore.jdbc;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import io.shiveshnavin.firestore.FJLogger;
import io.shiveshnavin.firestore.aspect.LoggingOperation;
import io.shiveshnavin.firestore.exceptions.FirestoreJDBCException;
import io.shiveshnavin.firestore.jdbc.metadata.FirestoreColDefinition;
import io.shiveshnavin.firestore.jdbc.metadata.FirestoreColType;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class FirestoreJDBCStatement implements java.sql.Statement, PreparedStatement {

    private Firestore db;
    private Statement parsedQuery;
    private QueryType queryType;
    private String tableName;
    private FirestoreJDBCResultSet firestoreJDBCResultSet;
    private String query;


    private Map<String, FirestoreColDefinition> aliasToColumnMap;

    private enum QueryType {
        CREATE, INSERT, SELECT, UPDATE, DELETE, DROP
    }

    public FirestoreJDBCStatement(Firestore db) {
        this.db = db;
    }

    public void listTables(){

    }

    private String parseQuery() throws FirestoreJDBCException {
        try {

            parsedQuery = CCJSqlParserUtil.parse(query);
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            String tableName = tablesNamesFinder.getTableList(parsedQuery).stream().findFirst().get();

            if (parsedQuery instanceof CreateTable) {
                queryType = QueryType.CREATE;
            } else if (parsedQuery instanceof Insert) {
                queryType = QueryType.INSERT;
            } else if (parsedQuery instanceof Select) {
                queryType = QueryType.SELECT;
                parseSelect();
            } else if (parsedQuery instanceof Update) {
                queryType = QueryType.UPDATE;
            } else if (parsedQuery instanceof Delete) {
                queryType = QueryType.DELETE;
            } else if (parsedQuery instanceof Drop) {
                queryType = QueryType.DROP;
            }




            FJLogger.debug("QueryInfo: " + queryType.name() + " from table : " + tableName);
            this.tableName = tableName;
            return tableName;
        } catch (JSQLParserException e) {
            throw new FirestoreJDBCException(e);
        }
    }

    private void parseSelect(){
        Select stmt = (Select) parsedQuery;
        aliasToColumnMap = new HashMap<>();

        for (SelectItem selectItem : ((PlainSelect)stmt.getSelectBody()).getSelectItems()) {
            selectItem.accept(new SelectItemVisitorAdapter() {
                @Override
                public void visit(SelectExpressionItem item) {
                    Expression expr = item.getExpression();
                    Alias alias = item.getAlias();
                    aliasToColumnMap.put(alias.getName(), new FirestoreColDefinition(expr.toString(), FirestoreColType.STRING));
                }
            });
        }
    }



    @LoggingOperation
    @Override
    public ResultSet executeQuery(String sql) {
        query = sql;
        parseQuery();
        CollectionReference query = db.collection(tableName);
        ApiFuture<QuerySnapshot> queryFuture = query.get();
        try {
            QuerySnapshot querySnapshot = queryFuture.get();
            firestoreJDBCResultSet = new FirestoreJDBCResultSet(aliasToColumnMap);
            firestoreJDBCResultSet.setQueryResult(querySnapshot);
            return firestoreJDBCResultSet;
        } catch (Exception e) {
            throw new FirestoreJDBCException(e);
        }
    }

    private int executeWOResult(String query){
        return 1;
    }

    @Override
    public int executeUpdate(String s) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String s, int i) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String s, int[] ints) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String s, String[] strings) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String s, int i) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String s, int[] ints) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String s, String[] strings) throws SQLException {
        return false;
    }


    @Override
    public boolean execute(String s) throws SQLException {
        return executeWOResult(query) > 0;
    }


    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }


    @Override
    public ResultSet getResultSet() throws SQLException {
        return firestoreJDBCResultSet;
    }


    @Override
    public void close() throws SQLException {

    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int i) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int i) throws SQLException {

    }

    @Override
    public void setEscapeProcessing(boolean b) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int i) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String s) throws SQLException {

    }


    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int i) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int i) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String s) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public boolean getMoreResults(int i) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }


    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean b) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }


    ////////////////// PREPARED STATEMENT

    @Override
    public ResultSet executeQuery() throws SQLException {
        return executeQuery(query);
    }

    @Override
    public int executeUpdate() throws SQLException {
        return 0;
    }

    @Override
    public void setNull(int i, int i1) throws SQLException {

    }

    @Override
    public void setBoolean(int i, boolean b) throws SQLException {

    }

    @Override
    public void setByte(int i, byte b) throws SQLException {

    }

    @Override
    public void setShort(int i, short i1) throws SQLException {

    }

    @Override
    public void setInt(int i, int i1) throws SQLException {

    }

    @Override
    public void setLong(int i, long l) throws SQLException {

    }

    @Override
    public void setFloat(int i, float v) throws SQLException {

    }

    @Override
    public void setDouble(int i, double v) throws SQLException {

    }

    @Override
    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {

    }

    @Override
    public void setString(int i, String s) throws SQLException {

    }

    @Override
    public void setBytes(int i, byte[] bytes) throws SQLException {

    }

    @Override
    public void setDate(int i, Date date) throws SQLException {

    }

    @Override
    public void setTime(int i, Time time) throws SQLException {

    }

    @Override
    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {

    }

    @Override
    public void setAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {

    }

    @Override
    public void setUnicodeStream(int i, InputStream inputStream, int i1) throws SQLException {

    }

    @Override
    public void setBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {

    }

    @Override
    public void clearParameters() throws SQLException {

    }

    @Override
    public void setObject(int i, Object o, int i1) throws SQLException {

    }

    @Override
    public void setObject(int i, Object o) throws SQLException {

    }

    @Override
    public boolean execute() throws SQLException {
        return false;
    }

    @Override
    public void addBatch() throws SQLException {

    }

    @Override
    public void setCharacterStream(int i, Reader reader, int i1) throws SQLException {

    }

    @Override
    public void setRef(int i, Ref ref) throws SQLException {

    }

    @Override
    public void setBlob(int i, Blob blob) throws SQLException {

    }

    @Override
    public void setClob(int i, Clob clob) throws SQLException {

    }

    @Override
    public void setArray(int i, Array array) throws SQLException {

    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setDate(int i, Date date, Calendar calendar) throws SQLException {

    }

    @Override
    public void setTime(int i, Time time, Calendar calendar) throws SQLException {

    }

    @Override
    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {

    }

    @Override
    public void setNull(int i, int i1, String s) throws SQLException {

    }

    @Override
    public void setURL(int i, URL url) throws SQLException {

    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setRowId(int i, RowId rowId) throws SQLException {

    }

    @Override
    public void setNString(int i, String s) throws SQLException {

    }

    @Override
    public void setNCharacterStream(int i, Reader reader, long l) throws SQLException {

    }

    @Override
    public void setNClob(int i, NClob nClob) throws SQLException {

    }

    @Override
    public void setClob(int i, Reader reader, long l) throws SQLException {

    }

    @Override
    public void setBlob(int i, InputStream inputStream, long l) throws SQLException {

    }

    @Override
    public void setNClob(int i, Reader reader, long l) throws SQLException {

    }

    @Override
    public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {

    }

    @Override
    public void setObject(int i, Object o, int i1, int i2) throws SQLException {

    }

    @Override
    public void setAsciiStream(int i, InputStream inputStream, long l) throws SQLException {

    }

    @Override
    public void setBinaryStream(int i, InputStream inputStream, long l) throws SQLException {

    }

    @Override
    public void setCharacterStream(int i, Reader reader, long l) throws SQLException {

    }

    @Override
    public void setAsciiStream(int i, InputStream inputStream) throws SQLException {

    }

    @Override
    public void setBinaryStream(int i, InputStream inputStream) throws SQLException {

    }

    @Override
    public void setCharacterStream(int i, Reader reader) throws SQLException {

    }

    @Override
    public void setNCharacterStream(int i, Reader reader) throws SQLException {

    }

    @Override
    public void setClob(int i, Reader reader) throws SQLException {

    }

    @Override
    public void setBlob(int i, InputStream inputStream) throws SQLException {

    }

    @Override
    public void setNClob(int i, Reader reader) throws SQLException {

    }



    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

}
