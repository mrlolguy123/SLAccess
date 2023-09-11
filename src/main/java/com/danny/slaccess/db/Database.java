package com.danny.slaccess.db;

import com.danny.slaccess.HelloApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLOutput;

public class Database {
    Connection conn = null;
    public Connection connect() {
        String dbURL = "jdbc:sqlite:accessedTable";

        try {
            conn = DriverManager.getConnection(dbURL);
            System.out.println("You have successfully connected to the database.");
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void disconnect() {
        try{
            conn.close();
            System.out.println("Database is now disconnected.");
        }
        catch (SQLException e){
            System.out.println("Database was not connected.");
        }
    }
}
