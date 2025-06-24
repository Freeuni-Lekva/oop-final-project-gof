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

    public StoryDAO() {
    }

    public void createStory(String title, String prompt, String description, int creatorId) throws SQLException {
        String sql = "INSERT INTO stories (creator_id, title, prompt, description, created_at) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, creatorId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, prompt);
            preparedStatement.setString(4, description);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error creating story: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public int createStoryAndGetId(String title, String prompt, String description, int creatorId) throws SQLException {
        String sql = "INSERT INTO stories (creator_id, title, prompt, description, created_at) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, creatorId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, prompt);
            preparedStatement.setString(4, description);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating story: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return -1;
    }

    public Story getStory(int storyId) throws SQLException {
        String sql = "SELECT * FROM stories WHERE story_id = ?";
        Story story = null;

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, storyId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                story = populateStory(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching story: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return story;
    }

    public List<Story> searchStoriesByTitle(String query) throws SQLException {
        List<Story> stories = new ArrayList<>();

        String sql = "SELECT * FROM stories WHERE title LIKE ? ORDER BY created_at DESC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, "%" + query + "%");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                stories.add(populateStory(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error searching stories by title: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return stories;
    }

    public List<Story> searchStoriesByCreatorName(String query) throws SQLException {
        List<Story> stories = new ArrayList<>();

        String sql = "SELECT s.* FROM stories s JOIN users u ON s.creator_id = u.user_id " +
                "WHERE u.username LIKE ? ORDER BY s.created_at DESC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, "%" + query + "%");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                stories.add(populateStory(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error searching stories by creator: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return stories;
    }

    public String getPrompt(int storyId) throws SQLException {
        String sql = "SELECT prompt FROM stories WHERE story_id = ?";
        String prompt = null;

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, storyId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                prompt = resultSet.getString("prompt");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching prompt: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return prompt;
    }

    public List<Story> getStoriesList(int creatorId) throws SQLException {
        List<Story> stories = new ArrayList<>();
        String sql = "SELECT * FROM stories WHERE creator_id = ? ORDER BY created_at DESC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, creatorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                stories.add(populateStory(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error finding stories: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return stories;
    }

    public List<Story> getAllStories() throws SQLException {
        List<Story> stories = new ArrayList<>();
        String sql = "SELECT * FROM stories ORDER BY created_at DESC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                stories.add(populateStory(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all stories: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return stories;
    }

    public void linkTagsToStory(int storyId, List<String> tagNames) throws SQLException {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        String getTagIdSql = "SELECT tag_id FROM tags WHERE name = ?";
        String linkTagSql = "INSERT INTO story_tags (story_id, tag_id) VALUES (?, ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement getTagIdStatement = conn.prepareStatement(getTagIdSql);
             PreparedStatement linkTagStatement = conn.prepareStatement(linkTagSql)) {

            for (String tagName : tagNames) {
                getTagIdStatement.setString(1, tagName);
                int tagId = -1;
                ResultSet resultSet = getTagIdStatement.executeQuery();
                if (resultSet.next()) {
                    tagId = resultSet.getInt("tag_id");
                }

                if (tagId != -1) {
                    linkTagStatement.setInt(1, storyId);
                    linkTagStatement.setInt(2, tagId);
                    linkTagStatement.addBatch();
                }
            }
            linkTagStatement.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error linking tags to story: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public List<Story> getStoriesByIds(List<Integer> storyIds) throws SQLException {
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

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                stories.add(populateStory(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stories by IDs: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return stories;
    }

    public List<Story> findBookmarkedStories(int userId) throws SQLException {
        List<Story> stories = new ArrayList<>();
        String sql = "SELECT s.* FROM stories s JOIN bookmarks b ON s.story_id = b.story_id WHERE b.user_id = ? ORDER BY s.created_at DESC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                stories.add(populateStory(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error finding bookmarked stories: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return stories;
    }


    public void removeBookmark(int userId, int storyId) throws SQLException {
        String sql = "DELETE FROM bookmarks WHERE user_id = ? AND story_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, storyId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error removing bookmark: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void addBookmark(int userId, int storyId) throws SQLException {
        /* used insert ignore; if the pair already exists, it will do nothing
         * instead of throwing error */
        String sql = "INSERT IGNORE INTO bookmarks (user_id, story_id) VALUES (?, ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, storyId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding bookmark: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public List<Story> findReadHistory(int userId) throws SQLException {
        List<Story> stories = new ArrayList<>();
        String sql = "SELECT s.* FROM stories s " +
                "JOIN read_history rh ON s.story_id = rh.story_id " +
                "WHERE rh.user_id = ? " +
                "ORDER BY rh.last_read_at DESC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                stories.add(populateStory(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error finding reading history stories: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return stories;
    }

    public void addReadHistory(int userId, int storyId) throws SQLException {
        String sql = "INSERT INTO read_history (user_id, story_id, last_read_at) VALUES (?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE last_read_at = NOW()";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, storyId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding story: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void removeReadHistory(int userId, int storyId) throws SQLException {
        String sql = "DELETE FROM read_history WHERE user_id = ? AND story_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, storyId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error removing history: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void updateStory(Story story) throws SQLException {
        String sql = "UPDATE stories SET title = ?, prompt = ?, description = ? WHERE story_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, story.getTitle());
            preparedStatement.setString(2, story.getPrompt());
            preparedStatement.setString(3, story.getDescription());
            preparedStatement.setInt(4, story.getStoryId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating story: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteStory(int storyId) throws SQLException {
        String sql = "DELETE FROM stories WHERE story_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, storyId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting story: " + e.getMessage());
            e.printStackTrace();
            throw e;
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
        String description = resultSet.getString("description");
        LocalDateTime creationDate = resultSet.getTimestamp("created_at").toLocalDateTime();

        return new Story(title, prompt, description, creatorId, storyId, creationDate);
    }
}
