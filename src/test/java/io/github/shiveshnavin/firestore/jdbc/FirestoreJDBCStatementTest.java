package io.github.shiveshnavin.firestore.jdbc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


@Disabled
class FirestoreJDBCStatementTest {

    public static FirestoreJDBCConnection conn;

    @BeforeAll
    public static void setup() {
        conn = TestHelper.getConnection();
    }

    //    @Disabled
    @Test
    void executeQuery_1() throws Exception {

        String sql = "UPDATE clips set text = 'zlipaaa', filename = 'blehsas.txt' where timeStamp = 1638040289511 and text = 'zlip'";
        FirestoreJDBCStatement statement = new FirestoreJDBCStatement(conn.getFirestore());
        sql = "SELECT id from clips where timeStamp = 1638040289511  ;";
        statement.setQuery(sql);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        while (--count >= 0) {
            System.out.println(metaData.getColumnTypeName(count) + ":" + metaData.getColumnName(count) + "=" + resultSet.getObject(count));
        }
        System.out.println(resultSet);
    }

}