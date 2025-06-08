package data;

import io.github.cdimascio.dotenv.Dotenv;

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

    private static final Dotenv dotenv = Dotenv.load();
    private static final String DATABASE_NAME = dotenv.get("DB_NAME", "story-ai-db");
    private static final String USER = dotenv.get("DB_USER", "root");
    private static final String PASS = dotenv.get("DB_PASS", "root");

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
