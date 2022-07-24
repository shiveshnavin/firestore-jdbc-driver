package io.github.shiveshnavin.firestore.jdbc;

import io.github.shiveshnavin.firestore.FirestoreHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;
@Disabled
class FirestoreJDBCDriverTest {

    @BeforeEach
    void setUp() {

    }

    @Disabled
    @Test
    void connect() throws Exception {
        DriverManager.registerDriver(new FirestoreJDBCDriver());
        String serviceAccountJsonPath = FirestoreHelper.class.getClassLoader().getResource("keys/test-a0930.json").getFile();
        Connection con= DriverManager.getConnection(serviceAccountJsonPath);
        assertNotNull(con);
    }
}