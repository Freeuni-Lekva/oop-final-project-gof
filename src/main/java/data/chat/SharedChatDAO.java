package data.chat;

import data.MySqlConnector;
import model.chat.SharedChat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SharedChatDAO {

    public SharedChatDAO() {
    }

    public void shareChat(int chatId, int userId) throws SQLException {
        String sql = "REPLACE INTO shared_chats (chat_id, user_id, shared_at) VALUES (?, ?, NOW())";
        try (Connection connection = MySqlConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, chatId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void unshareChat(int chatId) throws SQLException {
        String sql = "DELETE FROM shared_chats WHERE chat_id = ?";
        try (Connection connection = MySqlConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, chatId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean isChatShared(int chatId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM shared_chats WHERE chat_id = ?";
        try (Connection connection = MySqlConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, chatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    public List<SharedChat> getSharedChatsFeedForUser(int loggedInUserId) throws SQLException {
        List<SharedChat> feed = new ArrayList<>();

        String sql = "SELECT sc.chat_id, sc.user_id, sc.shared_at, " +
                "u.username, u.image_name AS user_image, s.title AS story_title, s.story_id " +
                "FROM shared_chats sc " +
                "JOIN users u ON sc.user_id = u.user_id " +
                "JOIN chats c ON sc.chat_id = c.chat_id " +
                "JOIN stories s ON c.story_id = s.story_id " +
                "JOIN followers f ON sc.user_id = f.following_id " +
                "WHERE f.follower_id = ? " +
                "ORDER BY sc.shared_at DESC " +
                "LIMIT 20";

        try (Connection connection = MySqlConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, loggedInUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SharedChat sharedChat = new SharedChat();
                    sharedChat.setChatId(rs.getInt("chat_id"));
                    sharedChat.setUserId(rs.getInt("user_id"));
                    sharedChat.setUsername(rs.getString("username"));
                    sharedChat.setUserImage(rs.getString("user_image"));
                    sharedChat.setStoryTitle(rs.getString("story_title"));
                    sharedChat.setStoryId(rs.getInt("story_id"));
                    sharedChat.setSharedAt(rs.getTimestamp("shared_at"));
                    feed.add(sharedChat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return feed;
    }

    public List<SharedChat> getSharedChatsByUser(int userId) throws SQLException {
        List<SharedChat> sharedChats = new ArrayList<>();

        String sql = "SELECT sc.chat_id, sc.shared_at, s.title AS story_title, s.story_id " +
                "FROM shared_chats sc " +
                "JOIN chats c ON sc.chat_id = c.chat_id " +
                "JOIN stories s ON c.story_id = s.story_id " +
                "WHERE sc.user_id = ? " +
                "ORDER BY sc.shared_at DESC";

        try (Connection connection = MySqlConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SharedChat sharedChat = new SharedChat();
                    sharedChat.setChatId(rs.getInt("chat_id"));
                    sharedChat.setUserId(userId);
                    sharedChat.setStoryTitle(rs.getString("story_title"));
                    sharedChat.setStoryId(rs.getInt("story_id"));
                    sharedChat.setSharedAt(rs.getTimestamp("shared_at"));
                    sharedChats.add(sharedChat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return sharedChats;
    }

}