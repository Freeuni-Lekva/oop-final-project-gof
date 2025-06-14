package data;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String DATABASE_NAME = dotenv.get("DB_NAME", "story-ai-db");
    private static final String USER = dotenv.get("DB_USER", "root");
    private static final String PASS = dotenv.get("DB_PASS", "root");

    private static final String URL =
            "jdbc:mysql://localhost:3306/" + DATABASE_NAME +
                    "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static Connection getConnection() throws SQLException {        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Database connection failed in webapp");
            e.printStackTrace();
            throw e;
        }
    }

    public static void close(Connection c) {
        if (c != null) {
            try { c.close(); }
            catch (SQLException ignored) {}
        }
    }

    public static void setupSQL() throws SQLException, IOException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        String path = "setup.sql";
        BufferedReader br = new BufferedReader(new FileReader(path));

        StringBuilder currentStatement = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            String trimmedLine = line.trim();

            if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                continue;
            }

            currentStatement.append(trimmedLine);

            if (trimmedLine.endsWith(";")) {
                String sqlCommand = currentStatement.substring(0, currentStatement.length() - 1).trim();

                if (!sqlCommand.isEmpty()) {
                    stmt.execute(sqlCommand);
                }
                currentStatement.setLength(0);
            } else {
                currentStatement.append(" ");
            }
        }

        String finalStatement = currentStatement.toString().trim();
        if (!finalStatement.isEmpty()) {
            System.out.println("Executing final: " + finalStatement);
            stmt.execute(finalStatement);
        }

    }


}
