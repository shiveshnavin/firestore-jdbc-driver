package io.shiveshnavin.firestore.jdbc.metadata;


public class FirestoreColDefinition {
    private int index;
    private String columnName;
    private FirestoreColType colDataType;

    public FirestoreColDefinition(int index, String columnName, FirestoreColType colDataType) {
        this.index = index;
        this.columnName = columnName;
        this.colDataType = colDataType;
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

}
