package data;

import model.User;
import model.story.Story;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations related to the User entity.
 */
public class UserDAO {

    public UserDAO() { }

    public User findUser(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = null;

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = populateUser(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

    public List<User> findFollowers(int userId) {
        List<User> followers = new ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN followers f ON u.user_id = f.follower_id WHERE f.following_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    followers.add(populateUser(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding followers: " + e.getMessage());
            e.printStackTrace();
        }
        return followers;
    }

    public List<User> findFollowing(int userId) {
        List<User> following = new ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN followers f ON u.user_id = f.following_id WHERE f.follower_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    following.add(populateUser(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding following: " + e.getMessage());
            e.printStackTrace();
        }
        return following;
    }

    public void saveUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, age, register_time, is_creator) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setInt(3, user.getAge());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setBoolean(5, user.isCreator());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addBookmark(int userId, Story story) {
        // INSERT IGNORE prevents an error if the bookmark already exists.
        String sql = "INSERT IGNORE INTO bookmarks (user_id, story_id) VALUES (?, ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, story.getStoryId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding bookmark: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addFollower(int userId, int followerId) {
        // INSERT IGNORE prevents an error if the relationship already exists.
        String sql = "INSERT IGNORE INTO followers (follower_id, following_id) VALUES (?, ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, followerId); // The user doing the following
            preparedStatement.setInt(2, userId);     // The user being followed
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding follower: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addFollowing(int userId, int followingId) {
        String sql = "INSERT IGNORE INTO followers (follower_id, following_id) VALUES (?, ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, followingId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error following user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to create a User object from a ResultSet row.
     */
    private User populateUser(ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("user_id");
        String username = resultSet.getString("username");
        String passwordHash = resultSet.getString("password_hash");
        int age = resultSet.getInt("age");
        // Convert java.sql.Timestamp from database to java.time.LocalDateTime
        LocalDateTime registerTime = resultSet.getTimestamp("register_time").toLocalDateTime();
        boolean isCreator = resultSet.getBoolean("is_creator");

        return new User(userId, username, passwordHash, age, registerTime, isCreator);
    }

}