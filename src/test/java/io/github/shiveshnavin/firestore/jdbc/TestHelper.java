package io.github.shiveshnavin.firestore.jdbc;

import io.github.shiveshnavin.firestore.FirestoreHelper;

import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestHelper {

    public static FirestoreJDBCConnection getConnection() throws RuntimeException{
        try {
            DriverManager.registerDriver(new FirestoreJDBCDriver());
            String serviceAccountJsonPath = "D:\\code\\node_projects\\xpasteit\\creds.json";
            FirestoreJDBCConnection con= (FirestoreJDBCConnection) DriverManager.getConnection(serviceAccountJsonPath);
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
