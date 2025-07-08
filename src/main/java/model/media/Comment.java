package model.media;

public class Comment {
    private int commentId;
    private int postId;
    private int authorId;
    private String commentContents;
    private int likesCount;
    private String authorUsername;

    public Comment(int commentId, int postId, int authorId, String commentContents, int likesCount) {
        this.commentId = commentId;
        this.postId = postId;
        this.authorId = authorId;
        this.commentContents = commentContents;
        this.likesCount = likesCount;
    }

    public Comment(int commentId, int postId, int authorId, String comment, int likeCount, String authorUsername) {
        this(commentId, postId, authorId, comment, likeCount); // Calls the other constructor
        this.authorUsername = authorUsername;
    }


    public int getCommentId() {
        return commentId;
    }

    public int getPostId() {
        return postId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getCommentContents() {
        return commentContents;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public String getAuthorUsername() { return authorUsername; }

    //setters:

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setCommentContents(String commentContents) {
        this.commentContents = commentContents;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }


    //toString:
    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", postId=" + postId +
                ", authorId=" + authorId +
                ", commentContents='" + commentContents + '\'' +
                ", likesCount=" + likesCount +
                '}';
    }


}
