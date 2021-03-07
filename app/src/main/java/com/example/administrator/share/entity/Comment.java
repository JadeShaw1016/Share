package com.example.administrator.share.entity;

public class Comment {

    private String username;

    private String face;

    private String authorname;

    private String replyUser;

    private String comment;

    private String commentTime;

    public String getFace(){return face;}

    public void setFace(String face){this.face = face;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public String getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(String replyUser) {
        this.replyUser = replyUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}
