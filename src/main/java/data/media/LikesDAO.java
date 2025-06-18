package data.media;

import data.MySqlConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Manages likes on posts and comments.
 */
public class LikesDAO {
    private MySqlConnector connector;

    public LikesDAO(MySqlConnector sqlConnector) {
        this.connector = sqlConnector;
    }

    public void addLikeToPost(int postId, int userId) throws SQLException {
        String insertLikeQuery = "INSERT INTO likes (user_id, post_id) VALUES (?, ?)";
        String updateLikeCountQuery = "UPDATE posts " +
                "SET like_count = like_count + 1 WHERE post_id = ?";

        Connection conn = null;
        try {
            conn = connector.getConnection();
            conn.setAutoCommit(false);
            try {
                PreparedStatement stmt = conn.prepareStatement(insertLikeQuery);
                stmt.setInt(1, userId);
                stmt.setInt(2, postId);
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
            try {
                PreparedStatement stmt = conn.prepareStatement(updateLikeCountQuery);
                stmt.setInt(1, postId);
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
            conn.commit();
        } catch (SQLException e) {
            if(conn != null) conn.rollback();
            throw new SQLException("rolled back");
        } finally {
            if(conn != null){conn.setAutoCommit(true);
                conn.close();
            }
        }
    }


    public void addLikeToComment(int commentId, int userId) throws SQLException {
        String insertLikeQuery = "INSERT INTO likes (user_id, comment_id) VALUES (?, ?)";
        String updateLikeCountQuery = "UPDATE comments " +
                "SET like_count = like_count + 1 WHERE comment_id = ?";

        Connection conn = null;
        try {
            conn = connector.getConnection();
            conn.setAutoCommit(false);
            try {
                PreparedStatement stmt = conn.prepareStatement(insertLikeQuery);
                stmt.setInt(1, userId);
                stmt.setInt(2, commentId);
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
            try {
                PreparedStatement stmt = conn.prepareStatement(updateLikeCountQuery);
                stmt.setInt(1, commentId);
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
            conn.commit();
        } catch (SQLException e) {
            if(conn != null) conn.rollback();
            throw new SQLException("rolled back");
        } finally {
            if(conn != null){
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void removeLikePost(int postId, int userId) throws SQLException {
        Connection conn = connector.getConnection();
        try{
            conn.setAutoCommit(false);

            String removeQuery = "DELETE FROM likes WHERE user_id = ? AND post_id = ?";
            String decreaseLikeCount = "UPDATE posts SET like_count = GREATEST(like_count - 1, 0)" +
                    " WHERE post_id = ?";

            boolean removed = false;
            try(PreparedStatement stmt = conn.prepareStatement(removeQuery);) {
                stmt.setInt(1, userId);
                stmt.setInt(2, postId);
                stmt.executeUpdate();
                removed = true;
            }
            try(PreparedStatement stmt = conn.prepareStatement(decreaseLikeCount);) {
                if(removed) {
                    stmt.setInt(1, postId);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if(conn != null) conn.rollback();
            throw new SQLException("rolled back");
        } finally {
            if(conn != null){
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void removeLikeComment(int commentId, int userId) throws SQLException {
        Connection conn = null;
        try{
            conn = connector.getConnection();
            conn.setAutoCommit(false);

            String removeQuery = "DELETE FROM likes WHERE user_id = ? AND comment_id = ?";
            String decreaseLikeCount = "UPDATE comments SET like_count = GREATEST(like_count - 1, 0)" +
                    " WHERE comment_id = ?";

            boolean removed = false;
            try(PreparedStatement stmt = conn.prepareStatement(removeQuery);) {
                stmt.setInt(1, userId);
                stmt.setInt(2, commentId);
                stmt.executeUpdate();
                removed = true;
            }
            try(PreparedStatement stmt = conn.prepareStatement(decreaseLikeCount)) {
                if(removed) {
                    stmt.setInt(1, commentId);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if(conn != null) conn.rollback();
            throw new SQLException("rolled back");
        } finally {
            if(conn != null){
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }


}
