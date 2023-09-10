package io.github.shiveshnavin.firestore.jdbc;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import io.github.shiveshnavin.firestore.FJLogger;
import io.github.shiveshnavin.firestore.exceptions.FirestoreJDBCException;
import io.github.shiveshnavin.firestore.jdbc.metadata.FirestoreColDefinition;
import io.github.shiveshnavin.firestore.jdbc.metadata.FirestoreColType;
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
import net.sf.jsqlparser.statement.update.UpdateSet;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Blob;
import java.sql.Date;
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

    private List<OrderByElement> orderByElements;
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
            } else {
                throw new FirestoreJDBCException("Query not supported exception : " + parsedQuery.getClass().getSimpleName());
            }

            int paramNo = 1;
            for (int i = 0; i < query.length(); i++) {
                if (query.charAt(i) == '?') {
                    paramPositionMap.put(paramNo++, i);
                }
            }

            FJLogger.debug("QueryInfo: " + queryType.name() + " from table : " + tableName + " " + (query.contains("?") ? "(Prefilled Query)" : "(Fully filled query)"));
            if (!(query.contains("?"))) {
                FJLogger.debug(query);
            }
            return tableName;
        } catch (JSQLParserException e) {
            throw new FirestoreJDBCException(e);
        }
    }

    private void parseSelect() {
        Select stmt = (Select) parsedQuery;
        aliasToColumnMap = new HashMap<>();
        Limit limits = (((PlainSelect) ((Select) parsedQuery).getSelectBody()).getLimit());
        if (limits != null) {
            if (limits.getRowCount() instanceof LongValue) {
                limit = ((LongValue) limits.getRowCount()).getValue();
            }
            if (limits.getOffset() instanceof LongValue) {
                offset = ((LongValue) limits.getOffset()).getValue();
            }
//            limit = limits.getRowCount();
//            offset = limits.getOffset();
        }

        orderByElements = (((PlainSelect) ((Select) parsedQuery).getSelectBody()).getOrderByElements());

        AtomicInteger integer = new AtomicInteger(0);
        for (SelectItem selectItem : ((PlainSelect) stmt.getSelectBody()).getSelectItems()) {
            selectItem.accept(new SelectItemVisitorAdapter() {
                @Override
                public void visit(SelectExpressionItem item) {
                    Expression expr = item.getExpression();
                    Alias alias = item.getAlias();
                    if(alias == null){
                        if(item.getExpression() instanceof Column){
                            String colName = ((Column)item.getExpression()).getColumnName();
                            alias = new Alias(colName);
                        }else{
                            alias = new Alias(item.toString());
                        }
                    }

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
        } else if (cur instanceof InExpression) {
            throw new FirestoreJDBCException("Operation not supported : " + cur.toString());

        } else if (cur instanceof IsNullExpression) {
            throw new FirestoreJDBCException("Operation not supported : " + cur.toString());

        } else if (cur instanceof Between) {
            throw new FirestoreJDBCException("Operation not supported : " + cur.toString());

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
            } else {
                throw new FirestoreJDBCException("Operation not supported : " + cur.toString());
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
        if (orderByElements != null && !orderByElements.isEmpty()) {
            for (OrderByElement order : orderByElements) {
                Query.Direction direction = order.isAsc() ? Query.Direction.ASCENDING : Query.Direction.DESCENDING;
                order.getExpression();
                if (order.getExpression() instanceof Column) {
                    Column column = (Column) order.getExpression();
                    query = query.orderBy(column.getColumnName(), direction);
                }
            }
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

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        query = sql;
        originalQuery = sql;
        parseQuery();
        if (queryType == QueryType.SELECT) {
            return performSelectQuery();
        } else {
            int rowCount = executeWOResult(sql);
            HashMap<String, Object> data = new HashMap<>();
            data.put("rows", rowCount);
            firestoreJDBCResultSet = new FirestoreJDBCResultSet(aliasToColumnMap);
            firestoreJDBCResultSet.setQueryResult(List.of(new QuerySnapshotWrapper(null, data)));
            return firestoreJDBCResultSet;
        }
    }

    private int executeWOResult(String query) throws SQLException {
        return executeUpdate(query);
    }

    @Override
    public int executeUpdate(String s) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        setQuery(s);
        return executeUpdate();
    }

    @Override
    public int executeUpdate(String s, int i) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        setQuery(s);
        return executeUpdate();
    }

    @Override
    public int executeUpdate(String s, int[] ints) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        throw new FirestoreJDBCException("Unsupported operation.Please use PreparedStatement Instead");
    }

    @Override
    public int executeUpdate(String s, String[] strings) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        throw new FirestoreJDBCException("Unsupported operation.Please use PreparedStatement Instead");
    }

    @Override
    public boolean execute(String s, int i) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        throw new FirestoreJDBCException("Unsupported operation.Please use PreparedStatement Instead");
    }

    @Override
    public boolean execute(String s, int[] ints) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        throw new FirestoreJDBCException("Unsupported operation.Please use PreparedStatement Instead");
    }

    @Override
    public boolean execute(String s, String[] strings) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        throw new FirestoreJDBCException("Unsupported operation.Please use PreparedStatement Instead");
    }


    @Override
    public boolean execute(String s) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return executeWOResult(query) > 0;
    }


    @Override
    public int[] executeBatch() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        throw new FirestoreJDBCException("Unsupported operation.Please use PreparedStatement Instead");
    }


    @Override
    public ResultSet getResultSet() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return firestoreJDBCResultSet;
    }


    @Override
    public void close() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return 0;
    }

    @Override
    public void setMaxFieldSize(int i) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public int getMaxRows() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return (int)limit;
    }

    @Override
    public void setMaxRows(int i) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        limit = i;
    }

    @Override
    public void setEscapeProcessing(boolean b) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        throw new FirestoreJDBCException("Unsupported operation.");


    }

    @Override
    public int getQueryTimeout() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return 0;
    }

    @Override
    public void setQueryTimeout(int i) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void cancel() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        throw new FirestoreJDBCException("Unsupported operation.");


    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setCursorName(String s) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }


    @Override
    public int getUpdateCount() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return false;
    }

    @Override
    public void setFetchDirection(int i) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public int getFetchDirection() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return 0;
    }

    @Override
    public void setFetchSize(int i) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public int getFetchSize() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return 0;
    }

    @Override
    public void addBatch(String s) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void clearBatch() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public Connection getConnection() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return null;
    }

    @Override
    public boolean getMoreResults(int i) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return null;
    }


    @Override
    public int getResultSetHoldability() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return false;
    }

    @Override
    public void setPoolable(boolean b) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public boolean isPoolable() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return false;
    }


    ////////////////// PREPARED STATEMENT

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
        FirestoreJDBCResultSet.printCurrentMethod();

        return executeQuery(query);
    }

    private int performInsertQuery() throws FirestoreJDBCException {
        Insert insert = (Insert) parsedQuery;
        List<Column> cols = insert.getColumns();
        List<Expression> values = ((ExpressionList) insert.getItemsList()).getExpressions();
        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            Expression exp = values.get(i);
            Object value = "";
            if (exp instanceof Column) {
                value = ((Column) exp).getColumnName();
            } else if (exp instanceof StringValue) {
                value = ((StringValue) exp).getValue();
            } else if (exp instanceof net.sf.jsqlparser.expression.DoubleValue) {
                value = (((DoubleValue) exp).getValue());
            } else if (exp instanceof net.sf.jsqlparser.expression.DateValue) {
                value = ((DateValue) exp).getValue();
            } else if (exp instanceof net.sf.jsqlparser.expression.LongValue) {
                value = ((LongValue) exp).getValue();
            }
            data.put(cols.get(i).getColumnName(), value);

        }
        Object __id = randomUUID(10);
        if (data.containsKey("id") || data.containsKey("ID")) {
            __id = data.containsKey("id") ? data.get("id") : data.get("ID");
        }

        ApiFuture<WriteResult> future = db.collection(tableName).document(__id.toString()).set(data);

        try {
            future.get();
        } catch (Exception e) {
            throw new FirestoreJDBCException(e);
        }
        return 1;
    }


    private int performUpdateQuery() throws FirestoreJDBCException {
        Update update = (Update) parsedQuery;
        ArrayList<UpdateSet> updateSets = update.getUpdateSets();
        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < updateSets.size(); i++) {
            UpdateSet updateSet = updateSets.get(i);
            Column col = updateSet.getColumns().get(0);
            Object exp = updateSet.getExpressions().get(0);
            Object value = "";
            if (exp instanceof Column) {
                value = ((Column) exp).getColumnName();
            } else if (exp instanceof StringValue) {
                value = ((StringValue) exp).getValue();
            } else if (exp instanceof net.sf.jsqlparser.expression.DoubleValue) {
                value = (((DoubleValue) exp).getValue());
            } else if (exp instanceof net.sf.jsqlparser.expression.DateValue) {
                value = ((DateValue) exp).getValue();
            } else if (exp instanceof net.sf.jsqlparser.expression.LongValue) {
                value = ((LongValue) exp).getValue();
            }
            data.put(col.getColumnName(), value);

        }

        try {

            Query itemsQuery = getConditionalQuery();
            ApiFuture<QuerySnapshot> snapshot = itemsQuery.get();
            QuerySnapshot toDelete = snapshot.get();
            List<QueryDocumentSnapshot> dbList = toDelete.getDocuments();

            for (int i = 0; i < dbList.size(); i += 500) {
                List<QueryDocumentSnapshot> sub = dbList.subList(i, Math.min(dbList.size(), i + 500));
                WriteBatch batch = db.batch();

                sub.forEach(doc -> {
                    batch.update(doc.getReference(), data);
                });

                ApiFuture<List<WriteResult>> future = batch.commit();
                future.get();


            }

            return toDelete.size();
        } catch (Exception e) {
            throw new FirestoreJDBCException(e);
        }


    }

    public int performDelete() {
        Query itemsQuery = getConditionalQuery();
        ApiFuture<QuerySnapshot> snapshot = itemsQuery.get();
        try {
            QuerySnapshot toDelete = snapshot.get();
            List<QueryDocumentSnapshot> dbList = toDelete.getDocuments();

            for (int i = 0; i < dbList.size(); i += 500) {
                List<QueryDocumentSnapshot> sub = dbList.subList(i, Math.min(dbList.size(), i + 500));
                WriteBatch batch = db.batch();

                sub.forEach(doc -> {
                    batch.delete(doc.getReference());
                });

                ApiFuture<List<WriteResult>> future = batch.commit();
                future.get();

            }

            return toDelete.size();
        } catch (Exception e) {
            throw new FirestoreJDBCException(e);
        }
    }

    public int performDrop() {
        Query query = db.collection(tableName);
        setConditionalQuery(query);
        return performDelete();
    }

    public static String randomUUID(int l) {
        final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid.substring(0, Math.min(uuid.length(), l));
    }

    @Override
    public int executeUpdate() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        parseQuery();
        if (queryType == QueryType.INSERT) {
            return performInsertQuery();
        } else if (queryType == QueryType.UPDATE) {
            return performUpdateQuery();
        } else if (queryType == QueryType.DELETE) {
            return performDelete();
        } else if (queryType == QueryType.CREATE) {
            FJLogger.debug("CREATE operation not required for firestore. Collections are created on inserting document automatically.");
            return 1;
        } else if (queryType == QueryType.DROP) {
            return performDrop();
        } else {
            throw new FirestoreJDBCException("Unsupported operation.");
        }

    }

    @Override
    public void setNull(int i, int i1) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        switch (i1){
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.JAVA_OBJECT:
            case Types.NVARCHAR:
            case Types.NCHAR:
            case Types.BLOB:
            case Types.NULL:
            case Types.LONGNVARCHAR:
            case Types.NCLOB:
            case Types.OTHER:
            case Types.REF:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP_WITH_TIMEZONE:
            case Types.TIMESTAMP:
            case Types.DATE:
            case Types.TIME:
                insertParameter(query, paramPositionMap.get(i), "NULL");
                break;
            default:
                insertParameter(query, paramPositionMap.get(i), "0");
        }



    }

    @Override
    public void setBoolean(int i, boolean b) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + b);


    }

    @Override
    public void setByte(int i, byte b) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + b);


    }

    @Override
    public void setShort(int i, short i1) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + i1);


    }

    @Override
    public void setInt(int i, int i1) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + i1);

    }

    @Override
    public void setLong(int i, long l) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + l);


    }

    @Override
    public void setFloat(int i, float v) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + v);


    }

    @Override
    public void setDouble(int i, double v) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + v);


    }

    @Override
    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + bigDecimal);


    }

    @Override
    public void setString(int i, String s) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "'" + s + "'");
    }

    @Override
    public void setBytes(int i, byte[] bytes) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + new String(bytes));


    }

    @Override
    public void setDate(int i, Date date) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + date);


    }

    @Override
    public void setTime(int i, Time time) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + time);


    }

    @Override
    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + timestamp);


    }

    @Override
    public void setAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setUnicodeStream(int i, InputStream inputStream, int i1) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        try {
            insertParameter(query, paramPositionMap.get(i), "" + CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new SQLException(e);
        }

    }

    @Override
    public void clearParameters() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        query = originalQuery;


    }

    @Override
    public void setObject(int i, Object o, int i1) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + new Gson().toJson(o));


    }

    @Override
    public void setObject(int i, Object o) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        insertParameter(query, paramPositionMap.get(i), "" + new Gson().toJson(o));


    }

    @Override
    public boolean execute() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return false;
    }

    @Override
    public void addBatch() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setCharacterStream(int i, Reader reader, int i1) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setRef(int i, Ref ref) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setBlob(int i, Blob blob) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        try {
            insertParameter(query, paramPositionMap.get(i), new String(blob.getBinaryStream().readAllBytes()));
        } catch (IOException e) {
            throw new SQLException(e);
        }


    }

    @Override
    public void setClob(int i, Clob clob) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

    }

    @Override
    public void setArray(int i, Array array) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return null;
    }

    @Override
    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        setDate(i, date);

    }

    @Override
    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        setTime(i, time);

    }

    @Override
    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        setTimestamp(i, timestamp);

    }

    @Override
    public void setNull(int i, int i1, String s) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        setNull(i, i1);

    }

    @Override
    public void setURL(int i, URL url) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();
        setString(i, url.toString());

    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();

        return null;
    }

    @Override
    public void setRowId(int i, RowId rowId) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setNString(int i, String s) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setNCharacterStream(int i, Reader reader, long l) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setNClob(int i, NClob nClob) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setClob(int i, Reader reader, long l) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setBlob(int i, InputStream inputStream, long l) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setNClob(int i, Reader reader, long l) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setObject(int i, Object o, int i1, int i2) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setCharacterStream(int i, Reader reader, long l) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setAsciiStream(int i, InputStream inputStream) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setBinaryStream(int i, InputStream inputStream) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setCharacterStream(int i, Reader reader) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setNCharacterStream(int i, Reader reader) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setClob(int i, Reader reader) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setBlob(int i, InputStream inputStream) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


    }

    @Override
    public void setNClob(int i, Reader reader) throws SQLException {
        FirestoreJDBCResultSet.printCurrentMethod();


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
