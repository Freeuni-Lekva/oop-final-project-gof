package data.story;

import data.MySqlConnector;
import model.story.Story;
import model.story.Tags;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Manages creation and retrieval of tags used to label stories.
 * Handles relationships between stories and their associated tags.
 */
public class TagsDAO {

    public TagsDAO() {}

    public List<String> getStoryTags(String storyId) throws SQLException {
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }


    public List<Story> getStories(List<String> storyTags) throws SQLException {
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

}
