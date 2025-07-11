package media;

import data.MySqlConnector;
import data.media.PostDAO;
import data.story.StoryDAO;
import data.user.UserDAO;
import junit.framework.TestCase;
import model.User;
import model.media.Post;
import model.story.Story;

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

        List<Post> postsForLsana = postDao.getPostsByCreatorId(lsanaId);
        List<Post> postsForChichia = postDao.getPostsByCreatorId(chichiaId);

        assertNotNull("List for 'lsana' should not be null", postsForLsana);
        assertEquals("Lsana should have 3 posts (1 original + 2 new)", 3, postsForLsana.size());

        assertNotNull("List for 'chichia' should not be null", postsForChichia);
        assertEquals("Chichia's post count should remain unchanged at 1", 1, postsForChichia.size());
    }



    public void testDeletePost() throws SQLException {
        initPosts();

        Post postBeforeDelete = postDao.getPostById(3);
        assertNotNull("Post with ID 3 should exist before deletion", postBeforeDelete);
        int story_id = postBeforeDelete.getStoryId();
        assertNotNull("Story for a post exists",story_id);

        postDao.deletePost(3);
        Story story = storyDao.getStory(story_id);

        Post postAfterDelete = postDao.getPostById(3);
        assertNull("Post with ID 3 should be null after deletion", postAfterDelete);
        assertNull("Story also got deleted",story);

        Post otherPost = postDao.getPostById(4); // The second post from initPosts()
        assertNotNull("Post with ID 4 should still exist", otherPost);
    }

    @Override
    public void tearDown() {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", true);
    }

    // --------- Helper method ---------
    private void initPosts() throws SQLException {
        postDao.addPost(new Post(3, 1, "src/main/webapp/images/posts/image1.jpg", LocalDateTime.now(), 0, 0));
        postDao.addPost(new Post(4, 2, "src/main/webapp/images/posts/image2.jpg", LocalDateTime.now(), 0, 0));
    }

    private int addPostsForUsers() throws SQLException {
        User lsana = userDao.findUser("lsana");
        if (lsana == null) {
            throw new IllegalStateException("Test setup failed: User 'lsana' not found. Please ensure setup.sql has been run.");
        }
        int lsanaId = lsana.getUserId();

        int newStoryId1 = storyDao.createStoryAndGetId("A New Adventure for Lsana",
                "A new prompt.", "A new description.", lsanaId);
        int newStoryId2 = storyDao.createStoryAndGetId("Another Tale by Lsana",
                "Another prompt.", "Another description.", lsanaId);

        postDao.addPost(new Post(0, newStoryId1, "image1.jpg", LocalDateTime.now(), 0, 0));
        postDao.addPost(new Post(0, newStoryId2, "image2.jpg", LocalDateTime.now(), 0, 0));

        return lsanaId;
    }

    public void testGetTotalPostCount() throws SQLException {
        int initialCount = postDao.getTotalPostCount();
        assertEquals("Initial post count should be 2 from setup.sql", 2, initialCount);

        postDao.addPost(new Post(0, 1, "a_new_image.jpg", LocalDateTime.now(), 0, 0));

        int newCount = postDao.getTotalPostCount();
        assertEquals("Count should be 3 after adding one post", 3, newCount);
    }
}
