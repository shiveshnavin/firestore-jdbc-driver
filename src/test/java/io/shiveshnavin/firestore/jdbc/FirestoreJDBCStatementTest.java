package io.shiveshnavin.firestore.jdbc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
@Disabled
class FirestoreJDBCStatementTest {

    public static FirestoreJDBCConnection conn;

    @BeforeAll
    public static void setup(){
        conn = TestHelper.getConnection();
    }

    @Disabled
    @Test
    void executeQuery_1() throws Exception {

        String sql = "select message0_.id as id1_0_ from Message message0_";
        FirestoreJDBCStatement statement = new FirestoreJDBCStatement(conn.getFirestore());
        statement.setQuery(sql);
        statement.execute();

    }

}