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


    public List<Story> getStories(List<String> storyTags) throws SQLException {
        if (storyTags == null || storyTags.isEmpty()) {
            return Collections.emptyList();
        }

        int counter = 0;
        List<String> StoryTagsCopy = new ArrayList<>();

        for (String storyTag : storyTags) {
            if (Tags.isValidTag(storyTag)) {
                StoryTagsCopy.add(storyTag);
            }
            else {
                counter++;
            }
        }

        if (counter == storyTags.size()) {
            return Collections.emptyList();
        }

        String placeholders = String.join(", ", Collections.nCopies(StoryTagsCopy.size(), "?"));
        String sql = "SELECT s.story_id, s.creator_id, s.title, s.prompt, s.created_at " +
                "FROM stories s " +
                "JOIN story_tags st ON s.story_id = st.story_id " +
                "JOIN tags t ON st.tag_id = t.tag_id " +
                "WHERE t.name IN (" + placeholders + ") " +
                "GROUP BY s.story_id";

        List<Story> stories = new ArrayList<>();

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < StoryTagsCopy.size(); i++) {
                stmt.setString(i + 1, StoryTagsCopy.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int storyId = rs.getInt("story_id");
                int creatorId = rs.getInt("creator_id");
                String title = rs.getString("title");
                String prompt = rs.getString("prompt");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                stories.add(new Story(title, prompt, creatorId, storyId, createdAt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return stories;
    }

}
