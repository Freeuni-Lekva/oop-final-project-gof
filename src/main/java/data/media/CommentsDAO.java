package data.media;

import data.MySqlConnector;
import model.media.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles comment operations on posts, including likes.
 */
public class CommentsDAO {

    public CommentsDAO() { }

    public void addComment(String commentContent, int authorId, int postId) throws SQLException {
        String insertQuery = "INSERT INTO comments (comment, author_id, post_id) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE posts SET comment_count = comment_count + 1 WHERE post_id = ?";

        Connection conn = null;
        try {
            conn = MySqlConnector.getConnection();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, commentContent);
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
            conn.rollback();
            throw new SQLException("rolled back", e);
        } finally {
            if(conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void deleteComment(int commentId) throws SQLException {
        int postId = getPostId(commentId);
        if (postId == -1) {
            return;
        }

        String deleteQuery = "DELETE FROM comments WHERE comment_id = ?";
        String updateQuery = "UPDATE posts SET comment_count = GREATEST(comment_count - 1, 0) WHERE post_id = ?";

        try (Connection conn = MySqlConnector.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, commentId);
                int rowsAffected = deleteStmt.executeUpdate();

                if (rowsAffected > 0) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, postId);
                        updateStmt.executeUpdate();
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            throw new SQLException("Transaction failed for deleteComment.", e);
        }
    }

    public List<Comment> getCommentsForPost(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT comment_id, post_id, author_id, comment, like_count" +
                " FROM comments  WHERE post_id = ? ORDER BY comment_id DESC";

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

    public int getTotalCommentCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM comments";
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return 0;
    }

    public List<Comment> getRecentCommentsWithUsername(int limit) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.comment_id, c.post_id, c.author_id, c.comment, c.like_count, u.username " +
                "FROM comments c " +
                "JOIN users u ON c.author_id = u.user_id " +
                "ORDER BY c.comment_id DESC " +
                "LIMIT ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment com = new Comment(
                            rs.getInt("comment_id"),
                            rs.getInt("post_id"),
                            rs.getInt("author_id"),
                            rs.getString("comment"),
                            rs.getInt("like_count"),
                            rs.getString("username")
                    );
                    comments.add(com);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return comments;
    }

    public int deleteUnengagedComments() throws SQLException {
        String sql = "DELETE FROM comments WHERE created_at < DATE_SUB(NOW(), INTERVAL 3 MONTH) AND like_count = 0";
        int rowsAffected = 0;

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return rowsAffected;
    }

    public Comment getCommentById(int commentId) throws SQLException {
        String sql = "SELECT * FROM comments WHERE comment_id = ?";
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Comment(
                            rs.getInt("comment_id"),
                            rs.getInt("post_id"),
                            rs.getInt("author_id"),
                            rs.getString("comment"),
                            rs.getInt("like_count")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public Map<String, Long> getCommentStatsByUser(int userId, String period) throws SQLException {
        Map<String, Long> stats = new LinkedHashMap<>();

        String dateFunction;
        switch (period.toLowerCase()) {
            case "weekly":
                dateFunction = "DATE(created_at - INTERVAL(WEEKDAY(created_at)) DAY)";
                break;
            case "monthly":
                dateFunction = "DATE_FORMAT(created_at, '%Y-%m-01')";
                break;
            case "daily":
            default:
                dateFunction = "DATE(created_at)";
                break;
        }

        String sql = "SELECT " + dateFunction + " as period_start, COUNT(*) as period_count " +
                "FROM comments WHERE author_id = ? " +
                "GROUP BY period_start ORDER BY period_start ASC";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                stats.put(resultSet.getString("period_start"), resultSet.getLong("period_count"));
            }
        }
        catch (SQLException e) {
            System.out.println("Error getting comment stats by user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return stats;
    }
}