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
        clearDatabase();
        setupInitialData();
    }

    private void clearDatabase() throws SQLException {
        try (Connection conn = MySqlConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS messages");
            stmt.executeUpdate("DROP TABLE IF EXISTS chats");
            stmt.executeUpdate("DROP TABLE IF EXISTS story_tags");
            stmt.executeUpdate("DROP TABLE IF EXISTS tags");
            stmt.executeUpdate("DROP TABLE IF EXISTS read_history");
            stmt.executeUpdate("DROP TABLE IF EXISTS bookmarks");
            stmt.executeUpdate("DROP TABLE IF EXISTS likes");
            stmt.executeUpdate("DROP TABLE IF EXISTS comments");
            stmt.executeUpdate("DROP TABLE IF EXISTS posts");
            stmt.executeUpdate("DROP TABLE IF EXISTS stories");
            stmt.executeUpdate("DROP TABLE IF EXISTS followers");
            stmt.executeUpdate("DROP TABLE IF EXISTS users");

            stmt.executeUpdate("CREATE TABLE users (user_id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, password_hash VARCHAR(256) NOT NULL, age INT, register_time DATETIME DEFAULT CURRENT_TIMESTAMP, active BOOLEAN DEFAULT TRUE, last_login DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, is_creator BOOLEAN DEFAULT FALSE, is_admin BOOLEAN DEFAULT FALSE, image_name VARCHAR(256))");
            stmt.executeUpdate("CREATE TABLE followers (follower_id INT, following_id INT, PRIMARY KEY (follower_id, following_id), FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE, FOREIGN KEY (following_id) REFERENCES users(user_id) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE stories (story_id INT AUTO_INCREMENT PRIMARY KEY, creator_id INT NOT NULL, title VARCHAR(200) NOT NULL, prompt TEXT, description TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (creator_id) REFERENCES users(user_id) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE posts (post_id INT AUTO_INCREMENT PRIMARY KEY, story_id INT NOT NULL, image_name VARCHAR(256), created_at DATETIME DEFAULT CURRENT_TIMESTAMP, like_count INT DEFAULT 0, comment_count INT DEFAULT 0, FOREIGN KEY (story_id) REFERENCES stories(story_id) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE comments (comment_id INT AUTO_INCREMENT PRIMARY KEY, author_id INT NOT NULL, post_id INT NOT NULL, comment TEXT NOT NULL, like_count INT DEFAULT 0, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE, FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE likes (like_id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, post_id INT, comment_id INT, FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE, FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE, FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE, CHECK ((post_id IS NOT NULL AND comment_id IS NULL) OR (post_id IS NULL AND comment_id IS NOT NULL)))");
            stmt.executeUpdate("CREATE TABLE bookmarks (user_id INT, story_id INT, PRIMARY KEY (user_id, story_id), FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE, FOREIGN KEY (story_id) REFERENCES stories(story_id) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE read_history (user_id INT, story_id INT, last_read_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY (user_id, story_id), FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE, FOREIGN KEY (story_id) REFERENCES stories(story_id) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE tags (tag_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50) NOT NULL UNIQUE)");
            stmt.executeUpdate("CREATE TABLE story_tags (story_id INT, tag_id INT, PRIMARY KEY (story_id, tag_id), FOREIGN KEY (story_id) REFERENCES stories(story_id) ON DELETE CASCADE, FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE chats (chat_id INT AUTO_INCREMENT PRIMARY KEY, story_id INT NOT NULL, user_id INT NOT NULL, FOREIGN KEY (story_id) REFERENCES stories(story_id) ON DELETE CASCADE, FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE messages (message_id INT AUTO_INCREMENT PRIMARY KEY, message TEXT NOT NULL, chat_id INT NOT NULL, is_user BOOLEAN DEFAULT TRUE, FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE)");
        }
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
