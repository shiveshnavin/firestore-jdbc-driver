package io.shiveshnavin.firestore.jdbc;

import io.shiveshnavin.firestore.FJLogger;
import io.shiveshnavin.firestore.exceptions.FirestoreJDBCException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
@Disabled
public class QueryTest {

    static Connection connection;

    @BeforeAll
    public static void setupClass(){
        connection = TestHelper.getConnection();
    }

    @Test
    public void testCount() throws Exception{
        String sql = "select count(user0_.id) as col_0_0_ from product user0_";
        Statement statement = connection.createStatement();
        statement.executeQuery(sql);
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()){
            System.out.println(resultSet.getInt("col_0_0_"));
        }
    }




    @Test
    public void testGE() throws Exception{
        String sql = "select (user0_.id) as col_0_0_ from product user0_ where amount >= 100";
        Statement statement = connection.createStatement();
        statement.executeQuery(sql);
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()){
            System.out.println(resultSet.getString("col_0_0_"));
        }
    }



    @Test
    public void testBW() throws Exception{
        String sql = "select (user0_.id) as col_0_0_ from product user0_ where amount between 190 and 200";
        Statement statement = connection.createStatement();
        Assertions.assertThrows(FirestoreJDBCException.class,()->{
            statement.executeQuery(sql);
        });
    }


    @Test
    public void testDrop() throws Exception{
        String sql = "Drop TABLE of";
        Statement statement = connection.createStatement();
        statement.executeQuery(sql);

    }

}
