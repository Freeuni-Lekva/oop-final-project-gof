package model;

import data.UserDAO;
import model.story.Story;
import java.time.LocalDateTime;
import java.util.List;

public class User {
    private final int userId;
    private String username;
    private String passwordHash;
    private int age;
    private final LocalDateTime registerTime;
    private boolean isCreator;

    // Lists use lazy loading
    private List<Story> createdStories;
    private List<Story> bookmarkedStories;
    private List<User> followers;
    private List<User> following;

    public User(int userId, String username, String passwordHash, int age, LocalDateTime registerTime, boolean isCreator) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.age = age;
        this.registerTime = registerTime;
        this.isCreator = isCreator;
    }

    public void bookmark(Story story) {
        UserDAO userDAO = new UserDAO();
        userDAO.addBookmark(this.userId, story.getStoryId());
        this.getBookmarkedStories().add(story);
    }

    public void follow(User userToFollow) {
        UserDAO userDAO = new UserDAO();
        userDAO.addFollower(this.userId, userToFollow.getUserId());
        this.getFollowing().add(userToFollow);
    }

    // Getters
    public List<User> getFollowers() {
        if (this.followers == null) {
            UserDAO userDAO = new UserDAO();
            this.followers = userDAO.findFollowers(this.userId);
        }
        return this.followers;
    }

    public List<User> getFollowing() {
        if (this.following == null) {
            UserDAO userDAO = new UserDAO();
            this.following = userDAO.findFollowing(this.userId);
        }
        return this.following;
    }

    public List<Story> getCreatedStories() {
        if (this.createdStories == null) {
            UserDAO userDAO = new UserDAO();
            this.createdStories = userDAO.findCreatedStories(this.userId);
        }
        return this.createdStories;
    }

    public List<Story> getBookmarkedStories() {
        if (this.bookmarkedStories == null) {
            UserDAO userDAO = new UserDAO();
            this.bookmarkedStories = userDAO.findBookmarkedStories(this.userId);
        }
        return this.bookmarkedStories;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getAge() {
        return age;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public boolean isCreator() {
        return isCreator;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setCreatorStatus(boolean isCreator) {
        this.isCreator = isCreator;
    }

    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }
}
