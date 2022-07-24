package io.shiveshnavin.firestore.jdbc.metadata;


import java.util.List;

public class FirestoreColDefinition {
    private String columnName;
    private FirestoreColType colDataType;

    public FirestoreColDefinition(String columnName, FirestoreColType colDataType) {
        this.columnName = columnName;
        this.colDataType = colDataType;
    }


    public static String getColNameFromQualified(String columnName) {
        if (columnName.contains(".")) {
            String[] split = columnName.split("\\.");
            return split[split.length - 1];
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

}
