package data;

import model.User;
import model.story.Story;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class UserDAO {

    public User findUserById(int user_id) {
        // TODO: Implement method to get data from SQL database
        return null;
    }

    public User saveUser(User user) {
        // TODO: Implement method to save data to SQL database
        return user;
    }

    public List<User> findFollowers(int user_id) {
        // TODO: Implement method to get data from SQL database
        return null;
    }

    public List<User> findFollowing(int user_id) {
        // TODO: Implement method to get data from SQL database
        return null;
    }

    public List<Story> findCreatedStories(int user_id) {
        // TODO: Implement method to get data from SQL database
        return null;
    }

    public List<Story> findBookmarkedStories(int user_id) {
        // TODO: Implement method to get data from SQL database
        return null;
    }

    public void addBookmark(int user_id, Story story_id) {
        // TODO: Implement method to save data to SQL database
    }

    public void addFollower(int user_id, int follower_id) {
        // TODO: Implement method to save data to SQL database
    }
}