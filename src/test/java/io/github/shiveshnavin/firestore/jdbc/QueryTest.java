package io.github.shiveshnavin.firestore.jdbc;

import io.github.shiveshnavin.firestore.FirestoreHelper;
import io.github.shiveshnavin.firestore.exceptions.FirestoreJDBCException;
import io.github.shiveshnavin.firestore.jdbc.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Map;

//@Disabled
public class QueryTest {

    static Connection connection;

    @BeforeAll
    public static void setupClass(){
        try {
            connection = TestHelper.getConnection();
        } catch (Exception e) {

        }
    }


    @Disabled
    @Test
    public void testProjectionSelect() throws Exception {
        if(connection == null)
            return;
        String sql = "INSERT INTO sample2 (id,name,marks) values (?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,1234234);
        statement.setString(2, "Hello");
        statement.setInt(3, 10);
        statement.executeUpdate();


        statement = connection.prepareStatement("SELECT name, id as mid FROM sample2");
        statement.executeQuery();
        ResultSet resultSet = statement.getResultSet();
        int size = resultSet.getFetchSize();
        assert size > 0;
    }



    @Test
//    @Disabled
    public void testInsertIntoSelect() throws Exception {
        if(connection == null)
            return;
        connection.prepareStatement("DELETE FROM sample").executeUpdate();
        connection.prepareStatement("DELETE FROM sample2").executeUpdate();
        String sql = "INSERT INTO sample (id,name,marks) values (?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,1234234);
        statement.setString(2, "Hello");
        statement.setInt(3, 10);
        statement.executeUpdate();
        statement = connection.prepareStatement(sql);
        statement.setInt(1,1234235);
        statement.setString(2, "Hello2");
        statement.setInt(3, 11);
        statement.executeUpdate();

//        sql = "INSERT INTO sample2" +
//                " SELECT concat('myname', name) as name2, marks as id, marks as marks FROM sample" +
//                " WHERE marks > 10;";
//
//        FirestoreHelper.setEnableAutoGenIds(true);
//        statement = connection.prepareStatement(sql);
//        statement.executeQuery();
        statement = connection.prepareStatement("SELECT concat('hello',id) as mid, name2 as name3 FROM sample where marks > 10");
        statement.executeQuery();
        FirestoreJDBCResultSet resultSet =(FirestoreJDBCResultSet) statement.getResultSet();
        int size = resultSet.getFetchSize();
        Map<String, Object> data = resultSet.getQueryResult().get(0).getData();
        assert size > 0;
    }


    @Test
    @Disabled
    public void testNullInsert() throws Exception{
        if(connection == null)
            return;
        String sql = "INSERT INTO sample (id,name,marks) values (?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,1234234);
        statement.setNull(2, Types.VARCHAR);
        statement.setNull(3,Types.NUMERIC);
        statement.executeUpdate();

        statement = connection.prepareStatement("SELECT * FROM sample WHERE id = 1234234");
        statement.executeQuery();
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()){
            System.out.println(resultSet.getString("id")
                    + " : " + resultSet.getString("name")
                    + " : " + resultSet.getString("marks")
            );
        }

    }

//    @Test
    @Disabled
    public void testCount() throws Exception{
        String sql = "select count(user0_.id) as col_0_0_ from product user0_";
        Statement statement = connection.createStatement();
        statement.executeQuery(sql);
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()){
            System.out.println(resultSet.getInt("col_0_0_"));
        }
    }




//    @Test
    @Disabled
    public void testGE() throws Exception{
        String sql = "select (user0_.id) as col_0_0_ from product user0_ where amount >= 100";
        Statement statement = connection.createStatement();
        statement.executeQuery(sql);
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()){
            System.out.println(resultSet.getString("col_0_0_"));
        }
    }



//    @Test
    @Disabled
    public void testBW() throws Exception{
        String sql = "select (user0_.id) as col_0_0_ from product user0_ where amount between 190 and 200";
        Statement statement = connection.createStatement();
        Assertions.assertThrows(FirestoreJDBCException.class,()->{
            statement.executeQuery(sql);
        });
    }


//    @Test
    @Disabled
    public void testDrop() throws Exception{
        String sql = "Drop TABLE of";
        Statement statement = connection.createStatement();
        statement.executeQuery(sql);

    }

}
