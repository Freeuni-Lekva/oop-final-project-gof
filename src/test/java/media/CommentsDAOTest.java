package media;

import data.MySqlConnector;
import data.media.CommentsDAO;
import data.media.PostDAO;
import junit.framework.TestCase;
import model.media.Comment;
import model.media.Post;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class CommentsDAOTest extends TestCase {

    private static CommentsDAO commentsDAO;
    private static PostDAO postDAO;

    @Override
    public void setUp() throws SQLException, IOException {
        commentsDAO = new CommentsDAO();
        postDAO = new PostDAO();
        MySqlConnector.setupSQL();
    }

    public void testAddComment() throws SQLException {
        int postId = 2;
        int userId = 2;

        Post post1 = postDAO.getPostById(postId);
        assertNotNull(post1);
        assertEquals(1, post1.getCommentCount());

        commentsDAO.addComment("This is a test comment", userId, postId);

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "SELECT c.comment, c.author_id FROM comments c WHERE c.post_id = ? ORDER BY c.comment_id DESC LIMIT 1")) {

            statement.setInt(1, postId);
            ResultSet rs = statement.executeQuery();

            assertTrue(rs.next());
            assertEquals("This is a test comment", rs.getString("comment"));
            assertEquals(userId, rs.getInt("author_id"));
        }

        Post post = postDAO.getPostById(postId);
        assertNotNull(post);
        assertEquals(2, post.getCommentCount());
    }

    public void testDeleteComment() throws SQLException {
        int postId = 1;
        int commentId = 1;

        commentsDAO.deleteComment(commentId);

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM comments WHERE comment_id = ?")) {

            statement.setInt(1, commentId);
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        }

        Post post = postDAO.getPostById(postId);
        assertNotNull(post);
        assertEquals(0, post.getCommentCount());
    }

    public void testGetCommentsForPost() throws SQLException {

        commentsDAO.addComment("This is test comment", 2, 2);

        int postId = 2;
        List<Comment> comments = commentsDAO.getCommentsForPost(postId);

        assertNotNull(comments);
        assertEquals(2, comments.size());

        Comment comment = comments.get(0);
        assertEquals(2, comment.getCommentId());
        assertEquals("Great insights on the topic!", comment.getCommentContents());
        assertEquals(1, comment.getLikesCount());

        comment = comments.get(1);
        assertEquals(3, comment.getCommentId());
        assertEquals("This is test comment", comment.getCommentContents());
        assertEquals(0, comment.getLikesCount());
    }

    public void testGetAuthorId() throws SQLException {
        int commentId = 1;
        int authorId = commentsDAO.getAuthorId(commentId);
        assertEquals(2, authorId);

        commentId = 2;
        authorId = commentsDAO.getAuthorId(commentId);
        assertEquals(1, authorId);
    }

    public void testGetPostId() throws SQLException {
        int commentId = 1;
        int postId = commentsDAO.getPostId(commentId);
        assertEquals(1, postId);

        commentId = 2;
        postId = commentsDAO.getPostId(commentId);
        assertEquals(2, postId);
    }

    public void testGetLikeCount() throws SQLException {
        int commentId = 1;
        int likeCount = commentsDAO.getLikeCount(commentId);
        assertEquals(0, likeCount);

        commentId = 2;
        likeCount = commentsDAO.getLikeCount(commentId);
        assertEquals(1, likeCount);
    }

    public void testGetAuthorIdNotFound() throws SQLException {
        int authorId = commentsDAO.getAuthorId(1024);
        assertEquals(-1, authorId);
    }

    public void testGetPostIdNotFound() throws SQLException {
        int postId = commentsDAO.getPostId(256);
        assertEquals(-1, postId);
    }

    public void testGetLikeCountNotFound() throws SQLException {
        int likeCount = commentsDAO.getLikeCount(32768);
        assertEquals(-1, likeCount);
    }

    public void testGetTotalCommentCount() throws SQLException {
        assertEquals("Initial count should be 2", 2, commentsDAO.getTotalCommentCount());

        commentsDAO.addComment("A third comment for counting", 1, 1);
        assertEquals("Count should be 3 after adding one more", 3, commentsDAO.getTotalCommentCount());
    }

    public void testGetCommentById() throws SQLException {
        Comment comment = commentsDAO.getCommentById(1);
        assertNotNull("Comment with ID 1 should be found", comment);
        assertEquals("Comment content should match fixture data", "Amazing post!", comment.getCommentContents());

        Comment nonExistentComment = commentsDAO.getCommentById(999);
        assertNull("A non-existent comment ID should return null", nonExistentComment);
    }

    public void testGetRecentCommentsWithUsername() throws SQLException {
        List<Comment> comments = commentsDAO.getRecentCommentsWithUsername(5);

        Comment newestComment = comments.get(0);
        assertEquals("Newest comment ID should be 2", 2, newestComment.getCommentId());
        assertEquals("Author of newest comment should be lsana", "lsana", newestComment.getAuthorUsername());

        Comment olderComment = comments.get(1);
        assertEquals("Older comment ID should be 1", 1, olderComment.getCommentId());
        assertEquals("Author of older comment should be chichia", "chichia", olderComment.getAuthorUsername());
    }

    public void testDeleteUnengagedComments() throws SQLException {
        addCommentWithTimestamp("Old and unliked", 0, "2022-01-01 20:00:00");
        addCommentWithTimestamp("Old but liked", 5, "2022-01-01 20:00:00");
        commentsDAO.addComment("New and unliked", 1, 1);

        assertEquals("Total comments should be 5 before cleanup", 5, commentsDAO.getTotalCommentCount());

        int deletedCount = commentsDAO.deleteUnengagedComments();

        assertEquals("Exactly 1 unengaged comment should have been deleted", 1, deletedCount);
        assertEquals("Total comments should be 4 after cleanup", 4, commentsDAO.getTotalCommentCount());
    }

    private void addCommentWithTimestamp(String text, int likes, String timestamp) throws SQLException {
        String sql = "INSERT INTO comments (author_id, post_id, comment, like_count, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setInt(2, 1);
            stmt.setString(3, text);
            stmt.setInt(4, likes);
            stmt.setString(5, timestamp);
            stmt.executeUpdate();
        }
    }
}
