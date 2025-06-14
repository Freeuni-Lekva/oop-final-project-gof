package model;

import data.media.CommentsDAO;

import java.time.LocalDateTime;


public class Post {
    private final int postId;
    private final int storyId;
    private String imageName;
    private final LocalDateTime createdAt;
    private int likeCount;
    private int commentCount;

    // waiting comments object to be created
   // private List<Comment> comments;

    public Post(int postId, int storyId, String imageName, LocalDateTime createdAt, int likeCount, int commentCount) {
        this.postId = postId;
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


    public void setStoryId(int storyId) {

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

    //Waiting for findCommentsByPostId method in commentsDao
//    public List<Comment> getComments() {
//        if (this.comments == null) {
//            CommentDAO commentDAO = new CommentDAO();
//            this.comments = commentDAO.findCommentsByPostId(this.postId);
//        }
//        return this.comments;
//    }

    //two post objects are same if they have same id
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Post other = (Post) obj;
        return postId == other.postId;
    }
    //hash them based on their id
    @Override
    public int hashCode() {
        return Integer.hashCode(postId);
    }


}