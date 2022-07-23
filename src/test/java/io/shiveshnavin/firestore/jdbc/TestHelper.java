package io.shiveshnavin.firestore.jdbc;

import io.shiveshnavin.firestore.FirestoreHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestHelper {

    public static Connection getConnection() throws RuntimeException{
        try {
            DriverManager.registerDriver(new FirestoreJDBCDriver());
            String serviceAccountJsonPath = FirestoreHelper.class.getClassLoader().getResource("keys/test-a0930.json").getFile();
            Connection con= DriverManager.getConnection(serviceAccountJsonPath);
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
