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
}
