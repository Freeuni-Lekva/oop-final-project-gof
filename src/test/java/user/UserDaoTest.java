package user;

import data.MySqlConnector;
import junit.framework.TestCase;
import model.User;
import data.user.UserDAO;
import model.story.Story;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserDaoTest extends TestCase {

    private Connection conn;
    private UserDAO userDao;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();
        userDao = new UserDAO();
    }

    public void testFindExistingUser() throws SQLException {
        User user = userDao.findUserById(1);

        assertNotNull(user);
        assertEquals("lsana", user.getUsername());
        assertEquals(1, user.getUserId());
        assertTrue(user.isCreator());
    }

    public void testSaveAndFindNewUser() throws SQLException {
        User newUser = new User(0, "newUser", "new_hash", 33, null, false);
        userDao.saveUser(newUser);

        User foundUser = userDao.findUserById(3);
        assertNotNull(foundUser);
        assertEquals("newUser", foundUser.getUsername());
        assertEquals(33, foundUser.getAge());
    }

    public void testFindFollowers() throws SQLException {
        createInitialFollowers();

        List<User> followersOfUser1 = userDao.findFollowers(1);

        assertNotNull(followersOfUser1);
        assertEquals(1, followersOfUser1.size());
        assertEquals("chichia", followersOfUser1.get(0).getUsername());

        List<User> followersOfUser2 = userDao.findFollowers(2);
        assertTrue(followersOfUser2.isEmpty());
    }

    public void testFindFollowing() throws SQLException {
        createInitialFollowers();

        List<User> user2IsFollowing = userDao.findFollowing(2);

        assertNotNull(user2IsFollowing);
        assertEquals(1, user2IsFollowing.size());
        assertEquals("lsana", user2IsFollowing.get(0).getUsername());

        List<User> user1IsFollowing = userDao.findFollowing(1);
        assertTrue(user1IsFollowing.isEmpty());
    }

    public void testAddFollower() throws SQLException {
        assertTrue(userDao.findFollowers(1).isEmpty());

        userDao.addFollower(1, 2);

        List<User> followers = userDao.findFollowers(1);
        assertEquals(1, followers.size());
        assertEquals(2, followers.get(0).getUserId());
    }

    public void testAddFollowing() throws SQLException {
        assertTrue(userDao.findFollowing(1).isEmpty());
        assertTrue(userDao.findFollowers(2).isEmpty());

        userDao.addFollowing(1, 2);

        List<User> followingList = userDao.findFollowing(1);
        assertEquals(1, followingList.size());
        assertEquals(2, followingList.get(0).getUserId());

        List<User> followersList = userDao.findFollowers(2);
        assertEquals(1, followersList.size());
        assertEquals(1, followersList.get(0).getUserId());
    }

    public void testAddBookmark() throws SQLException {
        data.story.StoryDAO storyDao = new data.story.StoryDAO();
        Story storyToBookmark = storyDao.getStory(2);
        assertNotNull(storyToBookmark);

        List<Story> initialBookmarks = storyDao.findBookmarkedStories(1);
        assertTrue(initialBookmarks.isEmpty());

        userDao.addBookmark(1, storyToBookmark);
        List<Story> updatedBookmarks = storyDao.findBookmarkedStories(1);

        assertEquals(1, updatedBookmarks.size());
        assertEquals(2, updatedBookmarks.get(0).getStoryId());

        userDao.addBookmark(1, storyToBookmark);
        List<Story> finalBookmarks = storyDao.findBookmarkedStories(1);

        assertEquals(1, finalBookmarks.size());
    }

    @Override
    public void tearDown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            MySqlConnector.close(conn);
        }
    }

    // --- Helper methods ---

    private void createInitialFollowers() throws SQLException {
        String sql = "INSERT INTO followers (follower_id, following_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, 2);
            ps.setInt(2, 1);
            ps.executeUpdate();
        }
    }
}