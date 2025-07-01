package data.admin;

import data.MySqlConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminDAO {

    public void SetIsAdmin(int userId) throws SQLException {
        String sql = "UPDATE users SET is_admin = ? WHERE user_id = ?";
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1,1);
            preparedStatement.setInt(2,userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Couldn't set isAdmin for userId " + userId + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
