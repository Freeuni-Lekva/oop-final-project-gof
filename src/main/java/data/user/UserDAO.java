package data.user;

import data.MySqlConnector;
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

    public User findUser(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;

        try (Connection conn = MySqlConnector.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = populateUser(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return user;
    }

    public User findUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = null;

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = populateUser(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return user;
    }

    public List<User> findFollowers(int userId) throws SQLException {
        List<User> followers = new ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN followers f ON u.user_id = f.follower_id WHERE f.following_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                followers.add(populateUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error finding followers: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return followers;
    }

    public List<User> findFollowing(int userId) throws SQLException {
        List<User> following = new ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN followers f ON u.user_id = f.following_id WHERE f.follower_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                following.add(populateUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error finding following: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return following;
    }

    public void saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, age, register_time, is_creator, image_name) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setInt(3, user.getAge());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setBoolean(5, user.isCreator());
            preparedStatement.setString(6, user.getImageName());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void addBookmark(int userId, Story story) throws SQLException {
        String sql = "INSERT IGNORE INTO bookmarks (user_id, story_id) VALUES (?, ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, story.getStoryId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding bookmark: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void followUser(int followerId, int followingId) throws SQLException {
        String sql = "INSERT IGNORE INTO followers (follower_id, following_id) VALUES (?, ?)";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, followerId);
            preparedStatement.setInt(2, followingId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error following user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void unfollowUser(int followerId, int followingId) throws SQLException {
        String sql = "DELETE FROM followers WHERE follower_id = ? AND following_id = ?";

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, followerId);
            preparedStatement.setInt(2, followingId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error unfollowing user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean isFollowing(int viewerId, int profileOwnerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM followers WHERE follower_id = ? AND following_id = ?";
        boolean isFollowing = false;

        try (Connection conn = MySqlConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, viewerId);
            preparedStatement.setInt(2, profileOwnerId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        isFollowing = true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking follow status: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return isFollowing;
    }


    private User populateUser(ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("user_id");
        String username = resultSet.getString("username");
        String passwordHash = resultSet.getString("password_hash");
        int age = resultSet.getInt("age");
        // Convert java.sql.Timestamp from database to java.time.LocalDateTime
        LocalDateTime registerTime = resultSet.getTimestamp("register_time").toLocalDateTime();
        boolean isCreator = resultSet.getBoolean("is_creator");
        String imageName = resultSet.getString("image_name");

        return new User(userId, username, passwordHash, age, registerTime, isCreator, imageName);
    }

}