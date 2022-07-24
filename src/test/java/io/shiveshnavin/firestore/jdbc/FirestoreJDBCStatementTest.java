package io.shiveshnavin.firestore.jdbc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class FirestoreJDBCStatementTest {

    public static FirestoreJDBCConnection conn;

    @BeforeAll
    public static void setup(){
        conn = TestHelper.getConnection();
    }

    @Test
    void executeQuery_1() throws Exception {

        String sql = "select message0_.id as id1_0_ from Message message0_";
        FirestoreJDBCStatement statement = new FirestoreJDBCStatement(conn.getFirestore());
        statement.setQuery(sql);
        statement.execute();

    }

    @Test
    void executeUpdate() {
    }

    @Test
    void testExecuteUpdate() {
    }

    @Test
    void testExecuteUpdate1() {
    }

    @Test
    void testExecuteUpdate2() {
    }

    @Test
    void execute() {
    }

    @Test
    void testExecute() {
    }

    @Test
    void testExecute1() {
    }

    @Test
    void testExecute2() {
    }
}