package io.shiveshnavin.firestore.jdbc;


import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class FirestoreJDBCDriver implements Driver {


    @Override
    public Connection connect(String serviceAccountFilePath, Properties properties) {
        FirestoreJDBCConnection connection = new FirestoreJDBCConnection(serviceAccountFilePath);
        return connection;
    }

    @Override
    public boolean acceptsURL(String s) throws SQLException {
        return true;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
