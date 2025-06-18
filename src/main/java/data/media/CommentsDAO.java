package data.media;

import data.MySqlConnector;
import model.media.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles comment operations on posts, including likes.
 */
public class CommentsDAO {
    private Connection conn;

    public CommentsDAO(MySqlConnector sqlConnector) throws SQLException {
    }

    public void addComment(String commmentContent, int authorId, int postId)
    throws SQLException {
        String insertQuery = "INSERT INTO comments (comment, author_id, post_id) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE posts SET comment_count = comment_count + 1 WHERE post_id = ?";

        conn = null;
        try {
            conn = MySqlConnector.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, commmentContent);
                stmt.setInt(2, authorId);
                stmt.setInt(3, postId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setInt(1, postId);
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if(conn != null) {
                conn.rollback();
            };
            throw new SQLException("rolled back");
        } finally {
            if(conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Comment> getCommentsForPost(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String query =  "SELECT comment_id, post_id, author_id, comment, like_count" +
                " FROM comments  WHERE post_id = ? ORDER BY comment_id";
        conn = null;
        try(Connection conn = MySqlConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, postId);

            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    Comment com = new Comment(
                            rs.getInt("comment_id"),
                            rs.getInt("post_id"),
                            rs.getInt("author_id"),
                            rs.getString("comment"),
                            rs.getInt("like_count")
                    );
                    comments.add(com);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return comments;
    }

    public int getAuthorId(int commentId) throws SQLException {
        String query =  "SELECT author_id FROM comments WHERE comment_id = ?";
        conn = null;
        try (Connection conn = MySqlConnector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    return rs.getInt("author_id");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //if not found
        return -1;
    }

    public int getPostId(int commentId) throws SQLException {
        String query =  "SELECT post_id FROM comments WHERE comment_id = ?";
        conn = null;
        try(Connection conn = MySqlConnector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt("post_id");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        //if not found
        return -1;
    }

    public int getLikeCount(int commentId) throws SQLException {
        String query =  "SELECT like_count FROM comments WHERE comment_id = ?";
        conn = null;
        try (Connection conn = MySqlConnector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()){

                if(rs.next()){
                    return rs.getInt("like_count");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        //if not found
        return -1;
    }

}
