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
        User newUser = new User(0, "newUser", "new_hash", 33, null, false, "image1.jpg");
        userDao.saveUser(newUser);

        User foundUser = userDao.findUserById(4);
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

    public void testFollowUser() throws SQLException {
        assertTrue(userDao.findFollowing(1).isEmpty());
        assertTrue(userDao.findFollowers(2).isEmpty());

        userDao.followUser(1, 2);

        List<User> followingList = userDao.findFollowing(1);
        assertEquals(1, followingList.size());
        assertEquals(2, followingList.get(0).getUserId()); // User 1 is following User 2

        List<User> followersList = userDao.findFollowers(2);
        assertEquals(1, followersList.size());
        assertEquals(1, followersList.get(0).getUserId()); // User 2 is followed by User 1
    }


    public void testUnfollowUser() throws SQLException {
        createInitialFollowers();

        assertEquals(1, userDao.findFollowing(2).size());

        userDao.unfollowUser(2, 1);

        assertTrue(userDao.findFollowing(2).isEmpty());
        assertTrue(userDao.findFollowers(1).isEmpty());
    }


    public void testIsFollowing() throws SQLException {
        assertFalse("User 2 should not be following User 1 initially", userDao.isFollowing(2, 1));
        assertFalse("User 1 should not be following User 2 initially", userDao.isFollowing(1, 2));

        createInitialFollowers();

        assertTrue("User 2 should now be following User 1", userDao.isFollowing(2, 1));
        assertFalse("Directionality check: User 1 should still not be following User 2", userDao.isFollowing(1, 2));
    }


    public void testUpdateUsername() throws SQLException {

        User initialUser = userDao.findUserById(1);
        assertEquals("Initial username should be 'lsana'", "lsana", initialUser.getUsername());

        String newUsername = "luka";

        userDao.updateUsername(1, newUsername);
        User updatedUser = userDao.findUserById(1);
        assertEquals("Username should have been updated", newUsername, updatedUser.getUsername());
        assertEquals("Password hash should remain unchanged", initialUser.getPasswordHash(), updatedUser.getPasswordHash());
    }


    public void testUpdateUserPassword() throws SQLException {
        User initialUser = userDao.findUserById(1);
        String initialHash = initialUser.getPasswordHash();

        String newHashedPassword = "abcdefg12345";
        userDao.updateUserPassword(1, newHashedPassword);

        User updatedUser = userDao.findUserById(1);

        assertEquals("Password hash should have been updated", newHashedPassword, updatedUser.getPasswordHash());
        assertNotSame("The new hash should be different from the old one", initialHash, updatedUser.getPasswordHash());
        assertEquals("Username should remain unchanged", "lsana", updatedUser.getUsername());
    }


    public void testUpdateUserImage() throws SQLException {
        User initialUser = userDao.findUserById(1);
        assertEquals("Initial image name should be 'image1.jpg'", "image1.jpg", initialUser.getImageName());

        String newImageName = "coolPic.jpg";

        userDao.updateUserImage(1, newImageName);

        User updatedUser = userDao.findUserById(1);

        assertEquals("Image name should have been updated", newImageName, updatedUser.getImageName());
        assertEquals("Username should remain unchanged", "lsana", updatedUser.getUsername());
    }

    public void testIsCreator() throws SQLException {
        User initialUser = userDao.findUserById(3);
        assertFalse(initialUser.isCreator());

        userDao.SetCreator(initialUser.getUserId());
        assertTrue(userDao.findUserById(3).isCreator());
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