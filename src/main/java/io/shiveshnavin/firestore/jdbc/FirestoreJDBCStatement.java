package io.shiveshnavin.firestore.jdbc;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import io.shiveshnavin.firestore.FJLogger;
import io.shiveshnavin.firestore.exceptions.FirestoreJDBCException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.concurrent.ExecutionException;


public class FirestoreJDBCStatement implements java.sql.Statement {

    private Firestore db;
    private Statement parsedQuery;
    private QueryType queryType;
    private String tableName;
    private FirestoreJDBCResultSet firestoreJDBCResultSet;

    private enum QueryType {
        CREATE, INSERT, SELECT, UPDATE, DELETE, DROP
    }

    public FirestoreJDBCStatement(Firestore db) {
        this.db = db;
    }


    private String parseQuery(String sql) throws FirestoreJDBCException {
        try {

            parsedQuery = CCJSqlParserUtil.parse(sql);
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            String tableName = tablesNamesFinder.getTableList(parsedQuery).stream().findFirst().get();

            if (parsedQuery instanceof CreateTable) {
                queryType = QueryType.CREATE;
            } else if (parsedQuery instanceof Insert) {
                queryType = QueryType.INSERT;
            } else if (parsedQuery instanceof Select) {
                queryType = QueryType.SELECT;
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


    @Override
    public ResultSet executeQuery(String sql) {
        parseQuery(sql);
        CollectionReference query = db.collection(tableName);
        ApiFuture<QuerySnapshot> queryFuture = query.get();
        try {
            QuerySnapshot querySnapshot = queryFuture.get();
            firestoreJDBCResultSet = new FirestoreJDBCResultSet();
            firestoreJDBCResultSet.setQueryResult(querySnapshot);
            return firestoreJDBCResultSet;
        } catch (Exception e) {
            throw new FirestoreJDBCException(e);
        }
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
        return false;
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
}
