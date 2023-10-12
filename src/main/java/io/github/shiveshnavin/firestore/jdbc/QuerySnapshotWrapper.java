package io.github.shiveshnavin.firestore.jdbc;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Blob;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import io.github.shiveshnavin.firestore.jdbc.metadata.FirestoreColDefinition;
import net.sf.jsqlparser.expression.Expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class QuerySnapshotWrapper {


    private final QueryDocumentSnapshot snapshot;
    private final Map<String, Object> data;
    private Map<String, FirestoreColDefinition> aliasToColumnMap;

    public QuerySnapshotWrapper(QueryDocumentSnapshot snapshot,
                                Map<String, Object> data,
                                Map<String, FirestoreColDefinition> aliasToColumnMap) {
        this.snapshot = snapshot;
        this.aliasToColumnMap = aliasToColumnMap;
        this.data = data;
    }

    private Map<String, Object> postProcessExpressions(Map<String, Object> data) {
        if (this.aliasToColumnMap == null || this.aliasToColumnMap.isEmpty()) {
            return data;
        }

        Map<String, Object> processProcessedData = new HashMap<>();
        data.forEach((key, value) -> {
            Map.Entry<String, FirestoreColDefinition> definitionEntry = FirestoreColDefinition.getColDefinitionByAliasOrName(key, this.aliasToColumnMap);
            if (definitionEntry == null || definitionEntry.getValue() == null) {
                processProcessedData.put(key, value);
            } else if (definitionEntry.getValue().getExpression() == null) {
                processProcessedData.put(key, value);
            }else{
                FirestoreColDefinition firestoreColDefinition = definitionEntry.getValue();
                value = firestoreColDefinition.executeExpression(value);
                processProcessedData.put(key, value);
            }
        });

        return processProcessedData;
    }

    public QueryDocumentSnapshot getSnapshot() {
        return snapshot;
    }

    public Map<String, Object> getData() {
        return postProcessExpressions(data);
    }

    @Nonnull
    public String getId() {
        if (snapshot != null)
            return snapshot.getId();
        else
            return (String) data.get("id");

    }

    @Nullable
    public Timestamp getUpdateTime() {
        if (snapshot != null)
            return snapshot.getUpdateTime();
        else
            return Timestamp.of(new Date(System.currentTimeMillis()));

    }

    @Nullable
    public Timestamp getCreateTime() {

        if (snapshot != null)
            return snapshot.getCreateTime();
        else
            return Timestamp.of(new Date(System.currentTimeMillis()));

    }

    public boolean contains(@Nonnull String field) {
        if (snapshot != null)
            return snapshot.contains(field);
        else
            return data.containsKey(field);

    }

    @Nullable
    public Object get(@Nonnull String field) {
        return wrapWithExpression(field, _get(field));
    }

    @Nullable
    private Object _get(@Nonnull String field) {
        if (snapshot != null)
            return snapshot.get(field);
        else
            return data.get(field);

    }

    @Nullable
    public Boolean getBoolean(@Nonnull String field) {
        return wrapWithExpression(field, _getBoolean(field));
    }

    @Nullable
    private Boolean _getBoolean(@Nonnull String field) {
        if (snapshot != null) {
            if (snapshot.get(field) instanceof Boolean) {
                return snapshot.getBoolean(field);
            } else {
                return Boolean.valueOf(snapshot.get(field).toString());
            }
        } else {
            return Boolean.valueOf(getString(field));
        }

    }

    @Nullable
    public Double getDouble(@Nonnull String field) {
        return wrapWithExpression(field, _getDouble(field));
    }

    @Nullable
    private Double _getDouble(@Nonnull String field) {
        if (snapshot != null)
            return Double.parseDouble(String.valueOf(snapshot.get(field)));
        else
            return Double.valueOf(getString(field));

    }

    @Nullable
    public String getString(@Nonnull String field) {
        return wrapWithExpression(field, _getString(field));
    }

    @Nullable
    private String _getString(@Nonnull String field) {
        if (snapshot != null)
            return String.valueOf(snapshot.get(field));
        else
            return String.valueOf(data.get(field));

    }


    @Nullable
    public Long getLong(@Nonnull String field) {
        return wrapWithExpression(field, _getLong(field));
    }

    @Nullable
    private Long _getLong(@Nonnull String field) {
        if (snapshot != null)
            return Long.parseLong(String.valueOf(snapshot.get(field)));
        else
            return Long.valueOf(data.get(field).toString());

    }

    @Nullable
    public Date getDate(@Nonnull String field) {
        return wrapWithExpression(field, _getDate(field));
    }

    @Nullable
    private Date _getDate(@Nonnull String field) {
        if (snapshot != null)
            return snapshot.getDate(field);
        else
            return (Date) data.get(field);

    }


    @Nullable
    public Timestamp getTimestamp(@Nonnull String field) {
        return wrapWithExpression(field, _getTimestamp(field));
    }

    @Nullable
    private Timestamp _getTimestamp(@Nonnull String field) {
        if (snapshot != null)
            return snapshot.getTimestamp(field);
        else
            return (Timestamp) data.get(field);

    }

    @Nullable
    public Blob getBlob(String field) {
        return wrapWithExpression(field, _getBlob((field)));
    }

    @Nullable
    private Blob _getBlob(@Nonnull String field) {
        if (snapshot != null)
            return snapshot.getBlob(field);
        else
            return (Blob) data.get(field);

    }

    @Nonnull
    public DocumentReference getReference() {
        if (snapshot != null)
            return snapshot.getReference();
        else
            return null;

    }

    public <T> T toObject(Class<T> aClass) {
        if (snapshot != null)
            return snapshot.toObject(aClass);
        else
            return new Gson().fromJson(new Gson().toJson(data), aClass);

    }

    private <T> T wrapWithExpression(String field, T v) {
        Map.Entry<String, FirestoreColDefinition> definitionEntry =
                FirestoreColDefinition.getColDefinitionByAliasOrName(field, this.aliasToColumnMap);
        if(definitionEntry != null){
            return definitionEntry.getValue().executeExpression(v);
        }
        return v;
    }
}
