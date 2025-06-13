package data.story;

import data.MySqlConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles story creation, retrieval, and listing by creator.
 */
public class StoryDAO {
    private MySqlConnector connector;

    public StoryDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }

    public String getPrompt(int story_id) throws SQLException {
        String sql = "SELECT prompt FROM stories WHERE story_id = ?";
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, story_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("prompt");
            }
        }
        return null;
    }


}
