package model.media;

import java.time.LocalDateTime;


public class Post {
    private int postId;
    private final int storyId;
    private String imageName;
    private final LocalDateTime createdAt;
    private int likeCount;
    private int commentCount;

    public Post(int postId, int storyId, String imageName, LocalDateTime createdAt, int likeCount, int commentCount) {
        this.postId = postId;
        this.storyId = storyId;
        this.imageName = imageName;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    private Post(int storyId, String imageName, LocalDateTime createdAt, int likeCount, int commentCount) {
        this.storyId = storyId;
        this.imageName = imageName;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public int getPostId() {
        return postId;
    }

    public int getStoryId() {
        return storyId;
    }

    public String getImageName() {
        return imageName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }


    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }



}