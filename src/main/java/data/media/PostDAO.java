package data.media;

import model.media.Comment;
import model.media.Post;
import data.MySqlConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Manages post creation within a story, including image handling.
 * */
public class PostDAO {


    public PostDAO() {}

    public void addPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (story_id, image_name) VALUES (?, ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, post.getStoryId());
            stmt.setString(2, post.getImageName());

            stmt.executeUpdate();

        }catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void incrementLikeCount(int postId) throws SQLException {
        String sql = "UPDATE posts SET like_count = like_count + 1 WHERE post_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void incrementCommentCount(int postId) throws SQLException {
        String sql = "UPDATE posts SET comment_count = comment_count + 1 WHERE post_id = ?";
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public Post getPostsByStoryId(int storyId) throws SQLException {
        String sql = "SELECT post_id, story_id, image_name, created_at, like_count, comment_count FROM posts WHERE story_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, storyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Post post = new Post(
                        rs.getInt("post_id"),
                        rs.getInt("story_id"),
                        rs.getString("image_name"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("like_count"),
                        rs.getInt("comment_count")
                );
                return post;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return null;
    }

    public Post getFirstPostForStory(int storyId) throws SQLException {
        String sql = "SELECT * FROM posts WHERE story_id = ? ORDER BY created_at ASC LIMIT 1";
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, storyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Post(
                        rs.getInt("post_id"),
                        rs.getInt("story_id"),
                        rs.getString("image_name"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("like_count"),
                        rs.getInt("comment_count")
                );
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public Post getPostById(int postId) throws SQLException {
        String sql = "SELECT post_id, story_id, image_name, created_at, like_count, comment_count FROM posts WHERE post_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Post(
                        rs.getInt("post_id"),
                        rs.getInt("story_id"),
                        rs.getString("image_name"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("like_count"),
                        rs.getInt("comment_count")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return null;
    }

}
