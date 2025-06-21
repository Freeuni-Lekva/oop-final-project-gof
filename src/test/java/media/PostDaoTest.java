package media;

import data.MySqlConnector;
import data.media.PostDAO;
import data.story.StoryDAO;
import data.user.UserDAO;
import junit.framework.TestCase;
import model.User;
import model.media.Post;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;

public class PostDaoTest extends TestCase {


    private Connection conn;
    private PostDAO postDao;
    private UserDAO userDao;
    private StoryDAO storyDao;
    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();

        postDao = new PostDAO();
        userDao = new UserDAO();
        storyDao = new StoryDAO();
    }

    public void testAddAndGetPostsByStoryId() throws SQLException {
        initPosts();

        Post posts = postDao.getPostsByStoryId(1);
        assertEquals("image1.jpg", posts.getImageName());
    }

    public void testGetPostById() throws SQLException {
        initPosts();

        Post post = postDao.getPostById(3);
        assertNotNull(post);
        assertEquals(1, post.getStoryId());
        assertEquals("src/main/webapp/images/posts/image1.jpg", post.getImageName());
    }

    public void testIncrementLikeCount() throws SQLException {
        initPosts();

        postDao.incrementLikeCount(1);
        postDao.incrementLikeCount(1);

        Post post = postDao.getPostById(1);
        assertEquals(4, post.getLikeCount());
    }

    public void testIncrementCommentCount() throws SQLException {
        initPosts();

        postDao.incrementCommentCount(1);
        postDao.incrementCommentCount(1);
        postDao.incrementCommentCount(1);

        Post post = postDao.getPostById(1);
        assertEquals(4, post.getCommentCount());
    }

    public void testGetPostsByCreatorId() throws SQLException {
        int lsanaId = addPostsForUsers();


        User chichia = userDao.findUser("chichia");
        assertNotNull("User 'chichia' should exist from setup.sql", chichia);
        int chichiaId = chichia.getUserId();

        // ACT: Call the method we want to test for both users.
        List<Post> postsForLsana = postDao.getPostsByCreatorId(lsanaId);
        List<Post> postsForChichia = postDao.getPostsByCreatorId(chichiaId);


        assertNotNull("List for 'lsana' should not be null", postsForLsana);
        assertEquals("Lsana should have 3 posts (1 original + 2 new)", 3, postsForLsana.size());

        assertNotNull("List for 'chichia' should not be null", postsForChichia);
        assertEquals("Chichia's post count should remain unchanged at 1", 1, postsForChichia.size());
    }

    @Override
    public void tearDown() {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", true);
    }

    // --------- Helper method ---------
    private void initPosts() throws SQLException {
        // Use dummy postId values; actual DB will auto-increment if set up that way
        postDao.addPost(new Post(3, 1, "src/main/webapp/images/posts/image1.jpg", LocalDateTime.now(), 0, 0));
        postDao.addPost(new Post(4, 2, "src/main/webapp/images/posts/image2.jpg", LocalDateTime.now(), 0, 0));
    }

    private int addPostsForUsers() throws SQLException {
        // --- 1. Find the existing user from setup.sql ---
        User lsana = userDao.findUser("lsana");
        if (lsana == null) {
            throw new IllegalStateException("Test setup failed: User 'lsana' not found. Please ensure setup.sql has been run.");
        }
        int lsanaId = lsana.getUserId();

        // --- 2. Create new stories for 'lsana' using StoryDAO ---
        int newStoryId1 = storyDao.createStoryAndGetId("A New Adventure for Lsana", "A new prompt.", lsanaId);
        int newStoryId2 = storyDao.createStoryAndGetId("Another Tale by Lsana", "Another prompt.", lsanaId);

        // --- 3. Create new posts using the correct Post constructor ---
        // We provide dummy values (0, now(), 0, 0) for fields that are not used by the addPost method.
        // This matches the style in your initPosts() helper.
        postDao.addPost(new Post(0, newStoryId1, "image1.jpg", LocalDateTime.now(), 0, 0));
        postDao.addPost(new Post(0, newStoryId2, "image2.jpg", LocalDateTime.now(), 0, 0));

        return lsanaId;
    }
}
