package data.media;

import data.MySqlConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Manages likes on posts and comments.
 */
public class LikesDAO {

    public LikesDAO() {}

    public void addLikeToPost(int postId, int userId) throws SQLException {

        if (postLikeExists(postId, userId)) {
            return;
        }

        String insertLikeQuery = "INSERT IGNORE INTO likes (user_id, post_id) VALUES (?, ?)";
        String updateLikeCountQuery = "UPDATE posts " +
                "SET like_count = like_count + 1 WHERE post_id = ?";

        Connection conn = null;
        int rowsAffected = 0;
        try {
            conn = MySqlConnector.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement stmt = conn.prepareStatement(insertLikeQuery);
            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            rowsAffected = stmt.executeUpdate();
            stmt.close();

            if (rowsAffected > 0) {

                PreparedStatement updateStmt = conn.prepareStatement(updateLikeCountQuery);
                updateStmt.setInt(1, postId);
                updateStmt.executeUpdate();
                updateStmt.close();
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


    public void addLikeToComment(int commentId, int userId) throws SQLException {

        if (commentLikeExists(commentId, userId)) {
            return;
        }

        String insertLikeQuery = "INSERT IGNORE INTO likes (user_id, comment_id) VALUES (?, ?)";
        String updateLikeCountQuery = "UPDATE comments " +
                "SET like_count = like_count + 1 WHERE comment_id = ?";

        Connection conn = null;
        int rowsAffected = 0;
        try {
            conn = MySqlConnector.getConnection();
            conn.setAutoCommit(false);
                PreparedStatement stmt = conn.prepareStatement(insertLikeQuery);
                stmt.setInt(1, userId);
                stmt.setInt(2, commentId);
                rowsAffected = stmt.executeUpdate();
                stmt.close();

            if (rowsAffected > 0) {
                PreparedStatement updateStmt = conn.prepareStatement(updateLikeCountQuery);
                updateStmt.setInt(1, commentId);
                updateStmt.executeUpdate();
                updateStmt.close();
                conn.commit();
            }
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
        Connection conn = null;
        try {
            conn = MySqlConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try{
            conn.setAutoCommit(false);

            String removeQuery = "DELETE FROM likes WHERE user_id = ? AND post_id = ?";
            String decreaseLikeCount = "UPDATE posts SET like_count = GREATEST(like_count - 1, 0)" +
                    " WHERE post_id = ?";

            boolean removed = false;
            int rowsAffected = 0;
            try(PreparedStatement stmt = conn.prepareStatement(removeQuery);) {
                stmt.setInt(1, userId);
                stmt.setInt(2, postId);
                rowsAffected = stmt.executeUpdate();
                removed = true;
            }
            if (rowsAffected > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(decreaseLikeCount);) {
                    if (removed) {
                        stmt.setInt(1, postId);
                        stmt.executeUpdate();
                    }
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
            conn = MySqlConnector.getConnection();
            conn.setAutoCommit(false);

            String removeQuery = "DELETE FROM likes WHERE user_id = ? AND comment_id = ?";
            String decreaseLikeCount = "UPDATE comments SET like_count = GREATEST(like_count - 1, 0)" +
                    " WHERE comment_id = ?";

            boolean removed = false;
            int rowsAffected = 0;
            try(PreparedStatement stmt = conn.prepareStatement(removeQuery);) {
                stmt.setInt(1, userId);
                stmt.setInt(2, commentId);
                rowsAffected = stmt.executeUpdate();
                removed = true;
            }
            if (rowsAffected > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(decreaseLikeCount)) {
                    if (removed) {
                        stmt.setInt(1, commentId);
                        stmt.executeUpdate();
                    }
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

    public int countLikesMadeByUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM likes WHERE user_id = ?";
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
            System.out.println("Error fetching likes count by user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return 0;
    }

    public boolean hasUserLikedPost(int postId, int userId) throws SQLException {
        return postLikeExists(postId, userId);
    }

    // ---------- helpers ----------

    public boolean postLikeExists(int postId, int userId) throws SQLException {
        String sql = "SELECT EXISTS(SELECT 1 FROM likes WHERE post_id = ? AND user_id = ?)";


        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    public boolean commentLikeExists(int commentId, int userId) throws SQLException {
        String sql = "SELECT EXISTS(SELECT 1 FROM likes WHERE comment_id = ? AND user_id = ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, commentId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return false;
    }
}
