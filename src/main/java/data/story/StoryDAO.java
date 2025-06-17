package data.story;

import data.MySqlConnector;
import model.story.Story;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles database operations related to the Story entity.
 */
public class StoryDAO {

    public StoryDAO() { }

    public void createStory(String title, String prompt, int creatorId) {
        String sql = "INSERT INTO stories (creator_id, title, prompt, created_at) VALUES (?, ?, ?, NOW())";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, creatorId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, prompt);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error creating story: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Story getStory(int storyId) {
        String sql = "SELECT * FROM stories WHERE story_id = ?";
        Story story = null;

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, storyId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    story = populateStory(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching story: " + e.getMessage());
            e.printStackTrace();
        }
        return story;
    }

    public String getPrompt(int storyId) {
        String sql = "SELECT prompt FROM stories WHERE story_id = ?";
        String prompt = null;

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, storyId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    prompt = resultSet.getString("prompt");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching prompt: " + e.getMessage());
            e.printStackTrace();
        }
        return prompt;
    }

    public List<Story> getStoriesList(int creatorId) {
        List<Story> stories = new ArrayList<>();
        String sql = "SELECT * FROM stories WHERE creator_id = ? ORDER BY created_at DESC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, creatorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    stories.add(populateStory(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding stories: " + e.getMessage());
            e.printStackTrace();
        }
        return stories;
    }

    public List<Story> getStoriesByIds(List<Integer> storyIds) {
        List<Story> stories = new ArrayList<>();
        if (storyIds == null || storyIds.isEmpty()) {
            return stories;
        }

        String placeholders = String.join(",", Collections.nCopies(storyIds.size(), "?"));
        String sql = "SELECT * FROM stories WHERE story_id IN (" + placeholders + ")";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            for (int i = 0; i < storyIds.size(); i++) {
                preparedStatement.setInt(i + 1, storyIds.get(i));
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    stories.add(populateStory(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stories by IDs: " + e.getMessage());
            e.printStackTrace();
        }
        return stories;
    }

    public List<Story> findBookmarkedStories(int userId) {
        List<Story> stories = new ArrayList<>();
        String sql = "SELECT s.* FROM stories s JOIN bookmarks b ON s.story_id = b.story_id WHERE b.user_id = ? ORDER BY s.created_at DESC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    stories.add(populateStory(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bookmarked stories: " + e.getMessage());
            e.printStackTrace();
        }
        return stories;
    }

    public void updateStory(Story story) {
        String sql = "UPDATE stories SET title = ?, prompt = ? WHERE story_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, story.getTitle());
            preparedStatement.setString(2, story.getPrompt());
            preparedStatement.setInt(3, story.getStoryId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating story: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteStory(int storyId) {
        String sql = "DELETE FROM stories WHERE story_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, storyId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting story: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to create a Story object from a ResultSet row.
     */
    private Story populateStory(ResultSet resultSet) throws SQLException {
        int storyId = resultSet.getInt("story_id");
        int creatorId = resultSet.getInt("creator_id");
        String title = resultSet.getString("title");
        String prompt = resultSet.getString("prompt");
        // Convert java.sql.Timestamp from database to java.time.LocalDateTime
        LocalDateTime creationDate = resultSet.getTimestamp("created_at").toLocalDateTime();

        return new Story(title, prompt, creatorId, storyId, creationDate);
    }
}
