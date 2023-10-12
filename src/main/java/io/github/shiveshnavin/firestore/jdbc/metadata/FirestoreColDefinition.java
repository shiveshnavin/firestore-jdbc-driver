package io.github.shiveshnavin.firestore.jdbc.metadata;


import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.List;
import java.util.Map;

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

    public <T> T executeExpression(T value) {
        if(this.getExpression() == null)
            return value;
        if(!isSupportedExpression(this.getExpression()))
            return value;
        Expression expression = this.getExpression();
        if(expression instanceof Function){
            String funcName = ((Function) expression).getName();
            if(funcName.equals("concat")){

            }

        }

        return value;
    }

    public static Map.Entry<String, FirestoreColDefinition> getColDefinitionByAliasOrName(String name, Map<String, FirestoreColDefinition> aliasToColumnMap) {
        for (Map.Entry<String, FirestoreColDefinition> col : aliasToColumnMap.entrySet()) {
            if (col.getKey().equals(name) || col.getValue().getColumnName().equals(name)) {
                return col;
            }
        }
        return null;
    }

    public static boolean isSupportedExpression(Expression expression) {
        if(expression instanceof Function){
            String funcName = ((Function) expression).getName();
            return List.of("concat").contains(funcName);
        }
        return false;
    }


}
