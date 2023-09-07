package com.danny.slaccess.db;

import com.danny.slaccess.HelloApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static void connect() {
        String dbURL = "jdbc:sqlite:accessedTable";
        Connection con = null;

        try {
            con = DriverManager.getConnection(dbURL);

            System.out.println("You have successfully connected to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void disconnect()
    {

    }
}
