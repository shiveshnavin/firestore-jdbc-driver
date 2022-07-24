package io.shiveshnavin.firestore.jdbc;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import io.shiveshnavin.firestore.FJLogger;
import io.shiveshnavin.firestore.aspect.LoggingOperation;
import io.shiveshnavin.firestore.exceptions.FirestoreJDBCException;
import io.shiveshnavin.firestore.jdbc.metadata.FirestoreColDefinition;
import io.shiveshnavin.firestore.jdbc.metadata.FirestoreColType;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Blob;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class FirestoreJDBCStatement implements java.sql.Statement, PreparedStatement {

    private Firestore db;

    private Statement parsedQuery;
    private QueryType queryType;
    private String tableName;
    private FirestoreJDBCResultSet firestoreJDBCResultSet;
    private String originalQuery;
    private String query;
    private Map<Integer, Integer> paramPositionMap = new HashMap<>();


    private Map<String, FirestoreColDefinition> aliasToColumnMap;
    private Query conditionalQuery;

    private long limit = 0;
    private long offset = 0;
    private boolean isCountQuery = false;

    private enum QueryType {
        CREATE, INSERT, SELECT, UPDATE, DELETE, DROP
    }

    public FirestoreJDBCStatement(Firestore db) {
        this.db = db;
    }

    public void listTables() {

    }

    private String parseQuery() throws FirestoreJDBCException {
        try {

            parsedQuery = CCJSqlParserUtil.parse(query);
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            String tableName = tablesNamesFinder.getTableList(parsedQuery).stream().findFirst().get();
            this.tableName = tableName;
            if (parsedQuery instanceof CreateTable) {
                queryType = QueryType.CREATE;
            } else if (parsedQuery instanceof Insert) {
                queryType = QueryType.INSERT;
            } else if (parsedQuery instanceof Select) {
                queryType = QueryType.SELECT;
                parseSelect();
                parseWhere(((PlainSelect) ((Select) parsedQuery).getSelectBody()).getWhere());
            } else if (parsedQuery instanceof Update) {
                queryType = QueryType.UPDATE;
                parseWhere(((Update) parsedQuery).getWhere());
            } else if (parsedQuery instanceof Delete) {
                queryType = QueryType.DELETE;
                parseWhere(((Delete) parsedQuery).getWhere());
            } else if (parsedQuery instanceof Drop) {
                queryType = QueryType.DROP;
            }

            int paramNo = 1;
            for (int i = 0; i < query.length(); i++) {
                if (query.charAt(i) == '?') {
                    paramPositionMap.put(paramNo++, i);
                }
            }

            FJLogger.debug("QueryInfo: " + queryType.name() + " from table : " + tableName);
            return tableName;
        } catch (JSQLParserException e) {
            throw new FirestoreJDBCException(e);
        }
    }

    private void checkIsCount() {
        List<SelectItem> projection = (((PlainSelect) ((Select) parsedQuery).getSelectBody()).getSelectItems());
        isCountQuery = projection.stream().anyMatch(item -> {
            var exp = ((SelectExpressionItem) item).getExpression();
            return exp == null;
        });
    }

    private void parseSelect() {
        Select stmt = (Select) parsedQuery;
        aliasToColumnMap = new HashMap<>();
        Limit limits = (((PlainSelect) ((Select) parsedQuery).getSelectBody()).getLimit());
        if (limits != null) {
            if (limits.getRowCount() instanceof LongValue) {
                limit = ((LongValue) limits.getOffset()).getValue();
            }
            if (limits.getOffset() instanceof LongValue) {
                offset = ((LongValue) limits.getOffset()).getValue();
            }
//            limit = limits.getRowCount();
//            offset = limits.getOffset();
        }

        AtomicInteger integer = new AtomicInteger(0);
        for (SelectItem selectItem : ((PlainSelect) stmt.getSelectBody()).getSelectItems()) {
            selectItem.accept(new SelectItemVisitorAdapter() {
                @Override
                public void visit(SelectExpressionItem item) {
                    Expression expr = item.getExpression();
                    Alias alias = item.getAlias();

                    if (item.getExpression() instanceof Function) {
                        String funcName = ((Function) item.getExpression()).getName();
                        if (funcName.equals("count")) {
                            isCountQuery = true;
                        } else {
                            throw new FirestoreJDBCException("Function " + funcName + " not supported");
                        }
                    }
                    aliasToColumnMap.put(alias.getName(), new FirestoreColDefinition(integer.getAndAdd(1), expr.toString(), FirestoreColType.STRING));
                }
            });
        }

    }

    /**
     * < less than
     * <= less than or equal to
     * == equal to
     * > greater than
     * >= greater than or equal to
     * != not equal to
     * array-contains
     * array-contains-any
     * in
     * not-in
     */
    private void parseWhere(Expression statement) {
        CollectionReference collRef = db.collection(tableName);
        Query query = collRef;
        if (statement != null)
            query = scanWhere(statement, query);
        setConditionalQuery(query);
    }

    public Query getConditionalQuery() {
        return conditionalQuery;
    }

    public void setConditionalQuery(Query conditionalQuery) {
        this.conditionalQuery = conditionalQuery;
    }


    private Query scanWhere(Expression cur, Query query) {
        if (cur instanceof AndExpression) {
            query = scanWhere(((AndExpression) cur).getLeftExpression(), query);
            query = scanWhere(((AndExpression) cur).getRightExpression(), query);
        } else {
            ComparisonOperator exp = (ComparisonOperator) cur;
            String colName = ((Column) ((ComparisonOperator) cur).getLeftExpression()).getColumnName();
            Object value = exp.getRightExpression().toString();

            if (exp.getRightExpression() instanceof StringValue) {
                value = ((StringValue) exp.getRightExpression()).getValue();
            } else if (exp.getRightExpression() instanceof net.sf.jsqlparser.expression.DoubleValue) {
                value = (((DoubleValue) exp.getRightExpression()).getValue());
            } else if (exp.getRightExpression() instanceof net.sf.jsqlparser.expression.DateValue) {
                value = ((DateValue) exp.getRightExpression()).getValue();
            } else if (exp.getRightExpression() instanceof net.sf.jsqlparser.expression.LongValue) {
                value = ((LongValue) exp.getRightExpression()).getValue();
            }

            if (cur instanceof EqualsTo) {
                query = query.whereEqualTo(colName, value);
            } else if (cur instanceof GreaterThan) {
                query = query.whereGreaterThan(colName, value);
            } else if (cur instanceof GreaterThanEquals) {
                query = query.whereGreaterThanOrEqualTo(colName, value);
            } else if (cur instanceof MinorThan) {
                query = query.whereLessThan(colName, value);
            } else if (cur instanceof MinorThanEquals) {
                query = query.whereLessThanOrEqualTo(colName, value);
            } else if (cur instanceof NotEqualsTo) {
                query = query.whereNotEqualTo(colName, value);
            } else if (cur instanceof InExpression) {
                query = query.whereIn(colName, (List<? extends Object>) value);
            } else if (cur instanceof IsNullExpression) {
                query = query.whereEqualTo(colName, null);
            } else if (cur instanceof Between) {
                query = query.whereGreaterThanOrEqualTo(colName, value);
            } else {
                throw new FirestoreJDBCException("Operation not supported " + cur.toString());
            }
        }
        return query;
    }


    private ResultSet performSelectQuery() {
        Query query = getConditionalQuery();
        if (limit > 0) {
            query = query.limit((int) limit);
        }
        if (offset > 0) {
            query = query.offset((int) offset);
        }

        ApiFuture<QuerySnapshot> queryFuture = query.get();
        try {
            QuerySnapshot querySnapshot = queryFuture.get();
            firestoreJDBCResultSet = new FirestoreJDBCResultSet(aliasToColumnMap);
            if (isCountQuery) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("id", "count");
                aliasToColumnMap.keySet().forEach(key -> {
                    data.put(key, querySnapshot.size());
                });
                firestoreJDBCResultSet.setQueryResult(List.of(new QuerySnapshotWrapper(null, data)));
            } else {
                firestoreJDBCResultSet.setQueryResult(querySnapshot.getDocuments().stream()
                        .map(doc -> new QuerySnapshotWrapper(doc, doc.getData())).collect(Collectors.toList()));
            }
            return firestoreJDBCResultSet;
        } catch (Exception e) {
            throw new FirestoreJDBCException(e);
        }
    }

    @LoggingOperation
    @Override
    public ResultSet executeQuery(String sql) {
        query = sql;
        originalQuery = sql;
        parseQuery();
        if (queryType == QueryType.SELECT) {
            return performSelectQuery();
        } else {
            throw new FirestoreJDBCException("Query not supported exception");
        }

    }

    private int executeWOResult(String query) {
        return 1;
    }

    @Override
    public int executeUpdate(String s) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public int executeUpdate(String s, int i) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public int executeUpdate(String s, int[] ints) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public int executeUpdate(String s, String[] strings) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public boolean execute(String s, int i) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @Override
    public boolean execute(String s, int[] ints) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @Override
    public boolean execute(String s, String[] strings) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }


    @Override
    public boolean execute(String s) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return executeWOResult(query) > 0;
    }


    @Override
    public int[] executeBatch() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return new int[0];
    }


    @Override
    public ResultSet getResultSet() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return firestoreJDBCResultSet;
    }


    @Override
    public void close() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public void setMaxFieldSize(int i) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public int getMaxRows() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public void setMaxRows(int i) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setEscapeProcessing(boolean b) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public int getQueryTimeout() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public void setQueryTimeout(int i) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void cancel() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setCursorName(String s) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }


    @Override
    public int getUpdateCount() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @Override
    public void setFetchDirection(int i) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public int getFetchDirection() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public void setFetchSize(int i) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public int getFetchSize() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public void addBatch(String s) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void clearBatch() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public Connection getConnection() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @Override
    public boolean getMoreResults(int i) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }


    @Override
    public int getResultSetHoldability() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @Override
    public void setPoolable(boolean b) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public boolean isPoolable() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }


    ////////////////// PREPARED STATEMENT

    private int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }


    private void insertParameter(String s, int pos, String c) {

        query = s.substring(0, pos) + c + s.substring(pos + 1);
        for (var key : paramPositionMap.keySet()) {
            if (paramPositionMap.get(key) > pos) {
                paramPositionMap.put(key, paramPositionMap.get(key) + c.length() - 1);
            }
        }
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return executeQuery(query);
    }

    @Override
    public int executeUpdate() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return 0;
    }

    @Override
    public void setNull(int i, int i1) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + i1);


    }

    @Override
    public void setBoolean(int i, boolean b) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + b);


    }

    @Override
    public void setByte(int i, byte b) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + b);


    }

    @Override
    public void setShort(int i, short i1) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + i1);


    }

    @Override
    public void setInt(int i, int i1) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + i1);

    }

    @Override
    public void setLong(int i, long l) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + l);


    }

    @Override
    public void setFloat(int i, float v) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + v);


    }

    @Override
    public void setDouble(int i, double v) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + v);


    }

    @Override
    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + bigDecimal);


    }

    @Override
    public void setString(int i, String s) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, ordinalIndexOf(query, "?", i), "'" + s + "'");
    }

    @Override
    public void setBytes(int i, byte[] bytes) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + new String(bytes));


    }

    @Override
    public void setDate(int i, Date date) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + date);


    }

    @Override
    public void setTime(int i, Time time) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + time);


    }

    @Override
    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + timestamp);


    }

    @Override
    public void setAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setUnicodeStream(int i, InputStream inputStream, int i1) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        try {
            insertParameter(query, paramPositionMap.get(i), "" + CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new SQLException(e);
        }

    }

    @Override
    public void clearParameters() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        query = originalQuery;


    }

    @Override
    public void setObject(int i, Object o, int i1) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + new Gson().toJson(o));


    }

    @Override
    public void setObject(int i, Object o) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        insertParameter(query, paramPositionMap.get(i), "" + new Gson().toJson(o));


    }

    @Override
    public boolean execute() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return false;
    }

    @Override
    public void addBatch() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setCharacterStream(int i, Reader reader, int i1) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setRef(int i, Ref ref) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setBlob(int i, Blob blob) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        try {
            insertParameter(query, paramPositionMap.get(i), new String(blob.getBinaryStream().readAllBytes()));
        } catch (IOException e) {
            throw new SQLException(e);
        }


    }

    @Override
    public void setClob(int i, Clob clob) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

    }

    @Override
    public void setArray(int i, Array array) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @Override
    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        setDate(i, date);

    }

    @Override
    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        setTime(i, time);

    }

    @Override
    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        setTimestamp(i, timestamp);

    }

    @Override
    public void setNull(int i, int i1, String s) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        setNull(i, i1);

    }

    @Override
    public void setURL(int i, URL url) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();
        setString(i, url.toString());

    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();

        return null;
    }

    @Override
    public void setRowId(int i, RowId rowId) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setNString(int i, String s) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setNCharacterStream(int i, Reader reader, long l) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setNClob(int i, NClob nClob) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setClob(int i, Reader reader, long l) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setBlob(int i, InputStream inputStream, long l) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setNClob(int i, Reader reader, long l) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setObject(int i, Object o, int i1, int i2) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setCharacterStream(int i, Reader reader, long l) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setAsciiStream(int i, InputStream inputStream) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setBinaryStream(int i, InputStream inputStream) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setCharacterStream(int i, Reader reader) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setNCharacterStream(int i, Reader reader) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setClob(int i, Reader reader) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setBlob(int i, InputStream inputStream) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }

    @Override
    public void setNClob(int i, Reader reader) throws SQLException {
        FirestoreJDBCResultSet.givenCurrentThread_whenGetStackTrace_thenFindMethod();


    }


    public void setQuery(String query) {
        this.query = query;
        originalQuery = query;
        parseQuery();
    }

    public String getQuery() {
        return query;
    }

}
