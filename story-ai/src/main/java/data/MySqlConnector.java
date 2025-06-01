package data;

import java.sql.*;

public class MySqlConnector {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver registered");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not register MySQL driver");
            e.printStackTrace();
        }
    }


    // Update these for your own setup
    private static final String DATABASE_NAME = "kitkat";
    private static final String USER = "root";
    private static final String PASS = "root";

    private static final String URL =
            "jdbc:mysql://localhost:3306/" + DATABASE_NAME +
                    "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Database connection failed in webapp");
            e.printStackTrace();
            return null;
        }
    }

    public static void close(Connection c) {
        if (c != null) {
            try { c.close(); }
            catch (SQLException ignored) {}
        }
    }


}
