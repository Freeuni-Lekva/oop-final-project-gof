package data.story;

import data.MySqlConnector;
import model.story.Story;
import model.story.Tags;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

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


    public List<Integer> findStoryIdsByMultipleTags(List<String> tagNames) throws SQLException {

        Set<String> uniqueUpperTags = cleanedTags(tagNames);

        if (uniqueUpperTags.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> storyIds = new ArrayList<>();
        int numberOfUniqueTags = uniqueUpperTags.size();

        String inClausePlaceholders = String.join(",", Collections.nCopies(numberOfUniqueTags, "?"));
        String sql = "SELECT st.story_id FROM story_tags st " +
                "JOIN tags t ON st.tag_id = t.tag_id " +
                "WHERE UPPER(t.name) IN (" + inClausePlaceholders + ") " +
                "GROUP BY st.story_id " +
                "HAVING COUNT(DISTINCT UPPER(t.name)) = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            int index = 1;
            for (String upperCaseTag : uniqueUpperTags) {
                preparedStatement.setString(index++, upperCaseTag);
            }

            preparedStatement.setInt(index, numberOfUniqueTags);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                storyIds.add(resultSet.getInt("story_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error finding story IDs by multiple tags: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return storyIds;
    }


    private Set<String> cleanedTags(List<String> tagNames) {
        Set<String> uniqueUpperTags = new HashSet<>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    uniqueUpperTags.add(tagName.trim().toUpperCase());
                }
            }
        }
        return uniqueUpperTags;
    }

}
