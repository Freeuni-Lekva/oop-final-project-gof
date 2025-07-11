// Recommended package: model.chat
package model.chat;

import java.sql.Timestamp;

public class SharedChat {

    private int chatId;
    private int storyId;
    private int userId;

    private String username;
    private String userImage;
    private String storyTitle;

    private Timestamp sharedAt;

    public SharedChat() {

    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public Timestamp getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(Timestamp sharedAt) {
        this.sharedAt = sharedAt;
    }

    @Override
    public String toString() {
        return "SharedChat{" +
                "chatId=" + chatId +
                ", storyId=" + storyId +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", userImage='" + userImage + '\'' +
                ", storyTitle='" + storyTitle + '\'' +
                ", sharedAt=" + sharedAt +
                '}';
    }
}