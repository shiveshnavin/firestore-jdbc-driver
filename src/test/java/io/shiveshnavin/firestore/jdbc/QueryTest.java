package io.shiveshnavin.firestore.jdbc;

import io.shiveshnavin.firestore.FJLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryTest {

    static Connection connection;

    @BeforeAll
    public static void setupClass(){
        connection = TestHelper.getConnection();
    }

    @Test
    @Disabled
    public void testFirestoreJDBC() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeQuery("UPDATE message SET l=1 WHERE k=2");
        ResultSet resultSet = statement.getResultSet();
        int resultSize = resultSet.getFetchSize();

        assert resultSize > 0;

    }


    public static class NPUser{
        public  String email;
        public  String id;
        public  String name;
        public  String phone;

        @Override
        public String toString() {
            return "NPUser{" +
                    "email='" + email + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", phone='" + phone + '\'' +
                    '}';
        }
    }
    @Test
    public void testFirestoreJDBC_SELECT() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeQuery("SELECT * FROM npusers WHERE 1");
        ResultSet resultSet = statement.getResultSet();
        int resultSize = resultSet.getFetchSize();

        assert resultSize > 0;

        while (resultSet.next()){
            NPUser user = resultSet.unwrap(NPUser.class);
            FJLogger.debug(user.toString());
        }

    }

}
