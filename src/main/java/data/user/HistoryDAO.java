package data.user;

import data.MySqlConnector;
import data.chat.ChatDAO;
import model.story.Story;
import data.story.StoryDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

/**
 * Handles database operations for the user's read history.
 */
public class HistoryDAO {

    public HistoryDAO() { }

    public void addReadHistory(int userId, int storyId) {
        String sql = "INSERT INTO read_history (user_id, story_id, last_read_at) VALUES (?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE last_read_at = NOW()";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, storyId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding to read history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Story> getReadHistoryForUser(int userId) throws SQLException {
        List<Integer> orderedStoryIds = new ArrayList<>();
        StoryDAO storyDao = new StoryDAO();
        String sql = "SELECT story_id FROM read_history WHERE user_id = ? ORDER BY last_read_at DESC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderedStoryIds.add(resultSet.getInt("story_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching read history story IDs: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        if (orderedStoryIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Story> unorderedStories = storyDao.getStoriesByIds(orderedStoryIds);
        Map<Integer, Story> storyMap = unorderedStories.stream()
                .collect(Collectors.toMap(Story::getStoryId, story -> story));

        List<Story> orderedStories = new ArrayList<>();
        for (Integer storyId : orderedStoryIds) {
            if (storyMap.containsKey(storyId)) {
                orderedStories.add(storyMap.get(storyId));
            }
        }

        return orderedStories;
    }

    public void deleteReadHistory(int userId, int storyId) throws SQLException {
        String getChatIdSql = "SELECT chat_id FROM chats WHERE user_id = ? AND story_id = ?";
        String deleteHistorySql = "DELETE FROM read_history WHERE user_id = ? AND story_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement getChatStmt = conn.prepareStatement(getChatIdSql);
             PreparedStatement deleteHistoryStmt = conn.prepareStatement(deleteHistorySql)) {

            getChatStmt.setInt(1, userId);
            getChatStmt.setInt(2, storyId);

            ResultSet rs = getChatStmt.executeQuery();
            if (rs.next()) {
                int chatId = rs.getInt("chat_id");

                ChatDAO chatDao = new ChatDAO();
                chatDao.deleteChat(chatId);
            }

            deleteHistoryStmt.setInt(1, userId);
            deleteHistoryStmt.setInt(2, storyId);
            deleteHistoryStmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting read history and associated chat: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}