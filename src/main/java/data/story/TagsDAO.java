package data.story;

import data.MySqlConnector;
import model.story.Story;
import model.story.Tags;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages creation and retrieval of tags used to label stories.
 * Handles relationships between stories and their associated tags.
 */
public class TagsDAO {

    public TagsDAO() {}

    public List<String> getStoryTags(int storyId) throws SQLException {
        String sql = "SELECT t.name FROM tags t JOIN story_tags st ON t.tag_id = st.tag_id WHERE st.story_id = ?";
        List<String> tags = new ArrayList<>();

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, storyId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tags.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return tags;
    }

    public List<Integer> findStoryIdsByTag(String query) throws SQLException {
        List<Integer> storyIds = new ArrayList<>();

        String sql = "SELECT s.story_id FROM stories s " +
                "JOIN story_tags st ON s.story_id = st.story_id " +
                "JOIN tags t ON st.tag_id = t.tag_id " +
                "WHERE t.name LIKE ? " +
                "ORDER BY s.created_at DESC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, "%" + query + "%");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                storyIds.add(resultSet.getInt("story_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error finding story IDs by tag: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return storyIds;
    }

}
