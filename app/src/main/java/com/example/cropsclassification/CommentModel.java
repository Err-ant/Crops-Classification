package com.example.cropsclassification;

public class CommentModel {

    String commentDate, commentTime, commentUserid, commentUserImage, commentText, commentUserName;

    public CommentModel() {
    }

    public CommentModel(String commentDate, String commentTime, String commentUserid, String commentUserImage, String commentText, String commentUserName) {
        this.commentDate = commentDate;
        this.commentTime = commentTime;
        this.commentUserid = commentUserid;
        this.commentUserImage = commentUserImage;
        this.commentText = commentText;
        this.commentUserName = commentUserName;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentUserid() {
        return commentUserid;
    }

    public void setCommentUserid(String commentUserid) {
        this.commentUserid = commentUserid;
    }

    public String getCommentUserImage() {
        return commentUserImage;
    }

    public void setCommentUserImage(String commentUserImage) {
        this.commentUserImage = commentUserImage;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentUserName() {
        return commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }
}
