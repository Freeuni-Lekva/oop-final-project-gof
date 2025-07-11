package media;

import data.MySqlConnector;
import data.media.CommentsDAO;
import data.media.LikesDAO;
import data.media.PostDAO;
import junit.framework.TestCase;
import model.media.Comment;
import model.media.Post;

import java.io.IOException;
import java.sql.*;
import java.util.List;



public class LikesDaoTest extends TestCase {
    private static LikesDAO likesDAO;

    private static final int TEST_USER_ID = 999;
    private static final int TEST_POST_ID = 888;
    private static final int TEST_COMMENT_ID = 777;
    private static final int ANOTHER_USER_ID = 998;
    private static final int TEST_STORY_ID = 111;

    @Override
    public void setUp() throws SQLException, IOException {
        likesDAO = new LikesDAO();
        MySqlConnector.setupSQL();
        setupInitialData();
    }

    private void setupInitialData() throws SQLException {
        try (Connection conn = MySqlConnector.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE " +
                    "INTO users (user_id, username, password_hash) VALUES (?, ?, ?)")) {
                stmt.setInt(1, TEST_USER_ID);
                stmt.setString(2, "testuser");
                stmt.setString(3, "password_hash_test");
                stmt.executeUpdate();

                stmt.setInt(1, ANOTHER_USER_ID);
                stmt.setString(2, "anotheruser");
                stmt.setString(3, "password_hash_another");
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO stories (story_id, creator_id, title, prompt) VALUES (?, ?, ?, ?)")) {
                stmt.setInt(1, TEST_STORY_ID);
                stmt.setInt(2, TEST_USER_ID);
                stmt.setString(3, "Test Story Title");
                stmt.setString(4, "Test Story Prompt");
                stmt.executeUpdate();
            }
        }
    }

    private void createTestPost(int postId, int storyId, int initialLikes) throws SQLException {
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO " +
                     "posts (post_id, story_id, image_name, like_count) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, postId);
            stmt.setInt(2, storyId);
            stmt.setString(3, "test_image_" + postId + ".jpg");
            stmt.setInt(4, initialLikes);
            stmt.executeUpdate();
        }
    }

    private void createTestComment(int commentId, int postId, int authorId, int initialLikes) throws SQLException {
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO " +
                     "comments (comment_id, post_id, author_id, comment, like_count) VALUES (?, ?, ?, ?, ?)")) { // Corrected columns
            stmt.setInt(1, commentId);
            stmt.setInt(2, postId);
            stmt.setInt(3, authorId);
            stmt.setString(4, "Test Comment Content for Comment " + commentId);
            stmt.setInt(5, initialLikes);
            stmt.executeUpdate();
        }
    }

    private int getPostLikeCountFromDb(int postId) throws SQLException {
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT like_count FROM posts WHERE post_id = ?")) {
            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("like_count");
                }
            }
        }
        return -1;
    }

    private int getCommentLikeCountFromDb(int commentId) throws SQLException {
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT like_count " +
                     "FROM comments WHERE comment_id = ?")) {
            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("like_count");
                }
            }
        }
        return -1;
    }

    private boolean checkPostLikeExistsInDb(int postId, int userId) throws SQLException {
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) " +
                     "FROM likes WHERE post_id = ? AND user_id = ?")) {
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean checkCommentLikeExistsInDb(int commentId, int userId) throws SQLException {
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) " +
                     "FROM likes WHERE comment_id = ? AND user_id = ?")) {
            stmt.setInt(1, commentId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void testAddLikeToPost() throws SQLException {
        createTestPost(TEST_POST_ID, TEST_STORY_ID, 0);
        int initialLikeCount = getPostLikeCountFromDb(TEST_POST_ID);
        assertEquals(0, initialLikeCount);
        assertFalse(checkPostLikeExistsInDb(TEST_POST_ID, TEST_USER_ID));

        likesDAO.addLikeToPost(TEST_POST_ID, TEST_USER_ID);

        assertEquals(initialLikeCount + 1, getPostLikeCountFromDb(TEST_POST_ID));
        assertTrue(checkPostLikeExistsInDb(TEST_POST_ID, TEST_USER_ID));

        likesDAO.addLikeToPost(TEST_POST_ID, TEST_USER_ID);

        assertEquals(initialLikeCount + 1, getPostLikeCountFromDb(TEST_POST_ID));
    }

    public void testAddLikeToComment() throws SQLException {
        createTestPost(TEST_POST_ID, TEST_STORY_ID, 0);
        createTestComment(TEST_COMMENT_ID, TEST_POST_ID, TEST_USER_ID, 0);
        int initialLikeCount = getCommentLikeCountFromDb(TEST_COMMENT_ID);
        assertEquals(0, initialLikeCount);
        assertFalse(checkCommentLikeExistsInDb(TEST_COMMENT_ID, TEST_USER_ID));

        likesDAO.addLikeToComment(TEST_COMMENT_ID, TEST_USER_ID);

        assertEquals(initialLikeCount + 1, getCommentLikeCountFromDb(TEST_COMMENT_ID));
        assertTrue(checkCommentLikeExistsInDb(TEST_COMMENT_ID, TEST_USER_ID));

        likesDAO.addLikeToComment(TEST_COMMENT_ID, TEST_USER_ID);

        assertEquals(initialLikeCount + 1, getCommentLikeCountFromDb(TEST_COMMENT_ID));
    }

    public void testRemoveLikePost() throws SQLException {
        createTestPost(TEST_POST_ID, TEST_STORY_ID, 1);
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO " +
                     "likes (user_id, post_id) VALUES (?, ?)")) {
            stmt.setInt(1, TEST_USER_ID);
            stmt.setInt(2, TEST_POST_ID);
            stmt.executeUpdate();
        }
        int initialLikeCount = getPostLikeCountFromDb(TEST_POST_ID);
        assertEquals(1, initialLikeCount);
        assertTrue(checkPostLikeExistsInDb(TEST_POST_ID, TEST_USER_ID));

        likesDAO.removeLikePost(TEST_POST_ID, TEST_USER_ID);

        assertEquals(initialLikeCount - 1, getPostLikeCountFromDb(TEST_POST_ID));
        assertFalse(checkPostLikeExistsInDb(TEST_POST_ID, TEST_USER_ID));

        likesDAO.removeLikePost(TEST_POST_ID, TEST_USER_ID);

        assertEquals(0, getPostLikeCountFromDb(TEST_POST_ID));
    }

    public void testRemoveLikeComment() throws SQLException {
        createTestPost(TEST_POST_ID, TEST_STORY_ID, 0);
        createTestComment(TEST_COMMENT_ID, TEST_POST_ID, TEST_USER_ID, 1);
        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO " +
                     "likes (user_id, comment_id) VALUES (?, ?)")) {
            stmt.setInt(1, TEST_USER_ID);
            stmt.setInt(2, TEST_COMMENT_ID);
            stmt.executeUpdate();
        }
        int initialLikeCount = getCommentLikeCountFromDb(TEST_COMMENT_ID);
        assertEquals(1, initialLikeCount);
        assertTrue(checkCommentLikeExistsInDb(TEST_COMMENT_ID, TEST_USER_ID));

        likesDAO.removeLikeComment(TEST_COMMENT_ID, TEST_USER_ID);

        assertEquals(initialLikeCount - 1, getCommentLikeCountFromDb(TEST_COMMENT_ID));
        assertFalse(checkCommentLikeExistsInDb(TEST_COMMENT_ID, TEST_USER_ID));

        likesDAO.removeLikeComment(TEST_COMMENT_ID, TEST_USER_ID);

        assertEquals(0, getCommentLikeCountFromDb(TEST_COMMENT_ID));
    }

    public void testPostLikeExists() throws SQLException {
        createTestPost(TEST_POST_ID, TEST_STORY_ID, 0);

        assertFalse(likesDAO.postLikeExists(TEST_POST_ID, TEST_USER_ID));

        likesDAO.addLikeToPost(TEST_POST_ID, TEST_USER_ID);

        assertTrue(likesDAO.postLikeExists(TEST_POST_ID, TEST_USER_ID));

        likesDAO.removeLikePost(TEST_POST_ID, TEST_USER_ID);

        assertFalse(likesDAO.postLikeExists(TEST_POST_ID, TEST_USER_ID));
    }

    public void testCommentLikeExists() throws SQLException {
        createTestPost(TEST_POST_ID, TEST_STORY_ID, 0);
        createTestComment(TEST_COMMENT_ID, TEST_POST_ID, TEST_USER_ID, 0);

        assertFalse(likesDAO.commentLikeExists(TEST_COMMENT_ID, TEST_USER_ID));

        likesDAO.addLikeToComment(TEST_COMMENT_ID, TEST_USER_ID);

        assertTrue(likesDAO.commentLikeExists(TEST_COMMENT_ID, TEST_USER_ID));

        likesDAO.removeLikeComment(TEST_COMMENT_ID, TEST_USER_ID);

        assertFalse(likesDAO.commentLikeExists(TEST_COMMENT_ID, TEST_USER_ID));
    }
}
