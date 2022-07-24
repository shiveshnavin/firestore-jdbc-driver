package io.github.shiveshnavin.firestore;

import com.google.api.client.util.StringUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.shiveshnavin.firestore.exceptions.FirestoreJDBCException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {

    private Firestore defaultDatabase;
    private Map<String, FirebaseApp> firebaseApps = new HashMap<>();

    public FirestoreHelper(InputStream resourceAsStream) {
        try {
            String serviceAccountJson = StringUtils.newStringUtf8(resourceAsStream.readAllBytes());
            init(serviceAccountJson);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FirestoreJDBCException("Unable to read service account file.");
        }
    }

    public FirestoreHelper(String path) {
        try {
            InputStream resourceAsStream = new FileInputStream(path);
            String serviceAccountJson = StringUtils.newStringUtf8(resourceAsStream.readAllBytes());
            init(serviceAccountJson);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FirestoreJDBCException("Unable to find service account file in path : " + path);
        }
    }

    public FirebaseApp init(String serviceAccountJson) throws FirestoreJDBCException {

        JsonParser jsonParser = new JsonParser();
        JsonObject serviceAccount = jsonParser.parse(serviceAccountJson).getAsJsonObject();
        String projectId = serviceAccount.get("project_id").getAsString();

        if (FirebaseApp.getApps().stream().anyMatch(app -> app.getName().equals(projectId))) {
            if (!firebaseApps.containsKey(projectId)) {
                firebaseApps.put(projectId, FirebaseApp.getInstance(projectId));
            }
            if (defaultDatabase == null) {
                defaultDatabase = FirestoreClient.getFirestore(FirebaseApp.getInstance(projectId));
            }
            return FirebaseApp.getInstance(projectId);
        }

        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8))))
                    .setDatabaseUrl("https://" + projectId + ".firebaseio.com/")
                    .build();

            FirebaseApp firebaseApp = FirebaseApp.initializeApp(options, projectId);

            Firestore db = FirestoreClient.getFirestore(firebaseApp);
            if (defaultDatabase == null) {
                defaultDatabase = db;
            }
            firebaseApps.put(projectId, firebaseApp);
            return firebaseApp;

        } catch (IOException e) {
            e.printStackTrace();
            throw new FirestoreJDBCException("Parsing service account json failed.");
        }
    }

    public Firestore getDefaultDatabase() {
        return defaultDatabase;
    }

    public FirebaseDatabase getFirebaseDatabase(String projectId) {
        return FirebaseDatabase.getInstance(firebaseApps.get(projectId));
    }

}
