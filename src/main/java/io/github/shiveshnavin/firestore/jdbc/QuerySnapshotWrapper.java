package io.github.shiveshnavin.firestore.jdbc;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Blob;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.Map;

public class QuerySnapshotWrapper {


    private final QueryDocumentSnapshot snapshot;
    private final Map<String, Object> data;

    public QuerySnapshotWrapper(QueryDocumentSnapshot snapshot, Map<String, Object> data) {
        this.snapshot = snapshot;
        this.data = data;
    }

    public QueryDocumentSnapshot getSnapshot() {
        return snapshot;
    }

    public Map<String, Object> getData() {
        return data;
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
        if (snapshot != null)
            return snapshot.get(field);
        else
            return data.get(field);

    }

    @Nullable
    public Boolean getBoolean(@Nonnull String field) {
        if(snapshot!=null){
            if(snapshot.get(field) instanceof Boolean){
                return snapshot.getBoolean(field);
            }
            else {
               return Boolean.valueOf(snapshot.get(field).toString());
            }
        }
        else {
            return Boolean.valueOf(getString(field));
        }

    }

    @Nullable
    public Double getDouble(@Nonnull String field) {
        if (snapshot != null)
            return Double.parseDouble(String.valueOf(snapshot.get(field)));
        else
            return Double.valueOf(getString(field));

    }

    @Nullable
    public String getString(@Nonnull String field) {
        if (snapshot != null)
            return String.valueOf(snapshot.get(field));
        else
            return String.valueOf(data.get(field));

    }

    @Nullable
    public Long getLong(@Nonnull String field) {
        if (snapshot != null)
            return Long.parseLong(String.valueOf(snapshot.get(field)));
        else
            return Long.valueOf(data.get(field).toString());

    }

    @Nullable
    public Date getDate(@Nonnull String field) {
        if (snapshot != null)
            return snapshot.getDate(field);
        else
            return (Date) data.get(field);

    }

    @Nullable
    public Timestamp getTimestamp(@Nonnull String field) {
        if (snapshot != null)
            return snapshot.getTimestamp(field);
        else
            return (Timestamp) data.get(field);

    }

    @Nullable
    public Blob getBlob(@Nonnull String field) {
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
}
