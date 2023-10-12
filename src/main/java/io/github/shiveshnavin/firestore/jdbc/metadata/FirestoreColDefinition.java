package io.github.shiveshnavin.firestore.jdbc.metadata;


import com.sun.jdi.IntegerValue;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.schema.Column;

import java.util.*;
import java.util.stream.Collectors;

public class FirestoreColDefinition {
    private int index;
    private String columnName;
    private FirestoreColType colDataType;

    private Expression expression;

    public FirestoreColDefinition(int index,
                                  String columnName,
                                  FirestoreColType colDataType) {
        this.index = index;
        this.columnName = columnName;
        this.colDataType = colDataType;
    }

    public FirestoreColDefinition(int index,
                                  String columnName,
                                  FirestoreColType colDataType,
                                  Expression expression) {
        this.index = index;
        this.columnName = columnName;
        this.colDataType = colDataType;
        this.expression = expression;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public static String getColNameFromQualified(String columnName) {
        if (columnName.contains(".")) {
            String[] split = columnName.split("\\.");
            String colName = split[split.length - 1];
            colName = colName.replaceAll("[^a-zA-Z0-9.-_]", "");
            return colName;
        }
        return columnName;
    }


    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public FirestoreColType getColDataType() {
        return colDataType;
    }

    public void setColDataType(FirestoreColType colDataType) {
        this.colDataType = colDataType;
    }


    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public static Set<String> getReferencedColsFromExpressions(Expression expr) {
        Set<String> colList = new HashSet<>();
        if (expr instanceof Function) {
            Function func = ((Function) expr);
            String funcName = func.getName();
            if (funcName.equals("concat")) {
                colList = func.getParameters()
                        .getExpressions()
                        .stream().filter(e -> e instanceof Column)
                        .map(e -> ((Column) e).getColumnName())
                        .collect(Collectors.toSet());
                return colList;
            }
        }
        if (expr instanceof Column)
            colList.add(((Column) expr).getColumnName());
        if (expr instanceof Addition) {
            Expression leftExpression = ((Addition) expr).getLeftExpression();
            Expression rightExpression = ((Addition) expr).getRightExpression();
            colList.addAll(getReferencedColsFromExpressions(leftExpression));
            colList.addAll(getReferencedColsFromExpressions(rightExpression));
        }
        return colList;
    }

    public Object executeExpression(Object value, Map<String, Object> data) {
        try{
            Expression expr = this.getExpression();
            return executeExpression(expr, value, data);
        }catch (Exception e){
            e.printStackTrace();
            return value;
        }
    }

    public Object executeExpression(Expression expr, Object value, Map<String, Object> data) {
        if (expr == null)
            return value;
        if (expr instanceof Column)
            return data.get(((Column) expr).getColumnName());
        if (expr instanceof StringValue)
            return ((StringValue) expr).getValue();
        if (expr instanceof LongValue)
            return String.valueOf(((LongValue) expr).getValue());
        if (expr instanceof DoubleValue)
            return String.valueOf(((DoubleValue) expr).getValue());
        if (!isSupportedExpression(expr))
            return value;
        if (expr instanceof Function) {
            Function func = ((Function) expr);
            List<Expression> expressions = func.getParameters().getExpressions();
            String funcName = func.getName();
            if (funcName.equals("concat")) {
                StringBuilder conc = new StringBuilder("");
                for (Expression e : expressions) {
                    if (e instanceof Column) {
                        conc.append(data.get(((Column) e).getColumnName()));
                    } else if (e instanceof TimeKeyExpression) {
                        conc.append(System.currentTimeMillis());
                    } else if (e instanceof StringValue) {
                        conc.append(((StringValue) e).getValue());
                    } else {
                        conc.append(e.toString());
                    }
                }
                value = conc.toString();
            }
        }
        if (expr instanceof Addition) {
            Expression leftExpression = ((Addition) expr).getLeftExpression();
            Expression rightExpression = ((Addition) expr).getRightExpression();
            String lVal = executeExpression(leftExpression, 0, data).toString();
            String rVal = executeExpression(rightExpression, 0, data).toString();
            return (Long) ((Long.parseLong(lVal) + (Long.parseLong(rVal))));

        }
        return value;
    }

    public static Map.Entry<String, FirestoreColDefinition> getColDefinitionByAliasOrName(String name, Map<String, FirestoreColDefinition> aliasToColumnMap) {
        if(aliasToColumnMap == null)
            return null;
        Map.Entry<String, FirestoreColDefinition> colDefinitionByName = getColDefinitionByName(name, aliasToColumnMap);
        if (colDefinitionByName != null)
            return colDefinitionByName;
        return getColDefinitionByAlias(name, aliasToColumnMap);
    }

    public static Map.Entry<String, FirestoreColDefinition> getColDefinitionByName(String name, Map<String, FirestoreColDefinition> aliasToColumnMap) {
        for (Map.Entry<String, FirestoreColDefinition> col : aliasToColumnMap.entrySet()) {
            if (col.getValue().getColumnName().equals(name)) {
                return col;
            }
        }
        return null;
    }


    public static Map.Entry<String, FirestoreColDefinition> getColDefinitionByAlias(String alias, Map<String, FirestoreColDefinition> aliasToColumnMap) {
        for (Map.Entry<String, FirestoreColDefinition> col : aliasToColumnMap.entrySet()) {
            if (col.getKey().equals(alias)) {
                return col;
            }
        }
        return null;
    }

    public static boolean isSupportedExpression(Expression expression) {
        if (expression instanceof Function) {
            String funcName = ((Function) expression).getName();
            return List.of("concat").contains(funcName);
        }
        if (expression instanceof Addition)
            return true;
        return false;
    }


}
