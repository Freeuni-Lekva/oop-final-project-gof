package model;

import java.time.LocalDateTime;

public class User {
    private final int userId;
    private String username;
    private String passwordHash;
    private int age;
    private final LocalDateTime registerTime;
    private boolean isCreator;

    public User(int userId, String username, String passwordHash, int age, LocalDateTime registerTime, boolean isCreator) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.age = age;
        this.registerTime = registerTime;
        this.isCreator = isCreator;
    }

    // Getters
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