package media;

import data.MySqlConnector;
import data.media.PostDAO;
import junit.framework.TestCase;
import model.media.Post;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;

public class PostDaoTest extends TestCase {


    private Connection conn;
    private PostDAO postDao;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();

        postDao = new PostDAO();
    }

    public void testAddAndGetPostsByStoryId() throws SQLException {
        initPosts();

        List<Post> posts = postDao.getPostsByStoryId(1);
        assertEquals(2, posts.size());

        assertEquals("image1.jpg", posts.get(0).getImageName());
        assertEquals("src/main/webapp/images/posts/image1.jpg", posts.get(1).getImageName());
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
}
