package data.chat;

import data.MySqlConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Manages chat sessions between users and story contexts.
 */
public class ChatDAO {
    private MySqlConnector connector;

    public ChatDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }

    public int getUserId(int chat_id) throws SQLException {
        String sql = "SELECT user_id FROM chats WHERE chat_id = ?";

        Connection conn = connector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, chat_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_id");
        }


        return -1;
    }

    public int getStoryId(int chat_id) throws SQLException {
        String sql = "SELECT story_id FROM chats WHERE chat_id = ?";

        Connection conn = connector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, chat_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("story_id");
        }


        return -1;
    }

    public int getChatId(int user_id, int story_id) throws SQLException {
        String sql = "SELECT chat_id FROM chats WHERE user_id = ? AND story_id = ?";

        Connection conn = connector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, user_id);
        stmt.setInt(2, story_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("chat_id");
        }

        return -1;
    }

    public int messageCount(int chat_id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM messages WHERE chat_id = ?";

        Connection conn = connector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, chat_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }

        return 0;
    }

    public int createChat(int user_id, int story_id) throws SQLException {
        String sql = "INSERT INTO chats (user_id, story_id) VALUES (?, ?)";

        Connection conn = connector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, user_id);
        stmt.setInt(2, story_id);
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }

        return -1;
    }

    public boolean deleteChat(int chat_id) throws SQLException {
        String sql = "DELETE FROM chats WHERE chat_id = ?";
        Connection conn = connector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, chat_id);
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
    }

}

