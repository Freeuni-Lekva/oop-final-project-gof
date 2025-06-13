package data.chat;

import data.MySqlConnector;
import data.story.StoryDAO;
import model.chat.Message;

import java.sql.*;
import java.util.ArrayList;

/**
 * Handles all messages sent and received in a chat session.
 */
public class MessageDAO {
    private MySqlConnector connector;

    public MessageDAO(MySqlConnector connector) {
        this.connector = connector;
    }

    public String getMessage(int message_id) throws SQLException {
        String sql = "SELECT message FROM messages WHERE message_id = ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, message_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("message");
            }
        }

        return "";
    }

    public ArrayList<Message> getMessages(int chat_id) throws SQLException {
        String sql = "SELECT message_id, message, is_user FROM messages WHERE chat_id = ? ORDER BY message_id ASC";
        ArrayList<Message> messages = new ArrayList<>();

        StoryDAO storyDao = new StoryDAO(connector);
        String prompt = storyDao.getPrompt(chat_id);
        messages.add(new Message(prompt, false, 0,false));

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chat_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String msg = rs.getString("message");
                boolean isUser = rs.getBoolean("is_user");
                int id = rs.getInt("message_id");
                messages.add(new Message(msg, isUser, id,false));
            }
        }

        return messages;
    }

    public int addMessage(int chat_id, String msg, boolean isUser) throws SQLException {
        String sql = "INSERT INTO messages (message, chat_id, is_user) VALUES (?, ?, ?)";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, msg);
            stmt.setInt(2, chat_id);
            stmt.setBoolean(3, isUser);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return -1;
    }

    public boolean updateMessageContent(int message_id, String newMessageContent) throws SQLException {
        String sql = "UPDATE messages SET message = ? WHERE message_id = ?";
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newMessageContent);
            stmt.setInt(2, message_id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}



