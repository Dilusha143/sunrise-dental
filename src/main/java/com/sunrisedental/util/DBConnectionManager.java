package com.sunrisedental.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnectionManager {

    private static DBConnectionManager instance;

    private final String url;
    private final String user;
    private final String password;

    private DBConnectionManager() {
       
        this.url = "jdbc:mysql://localhost:3306/sunrise_dental?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        this.user = "root";
        this.password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found on classpath.", e);
        }
    }

    public static synchronized DBConnectionManager getInstance() {
        if (instance == null) {
            instance = new DBConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
