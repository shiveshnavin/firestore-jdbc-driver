package io.github.shiveshnavin.firestore.jdbc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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