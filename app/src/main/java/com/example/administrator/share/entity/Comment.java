package com.example.administrator.share.entity;

public class Comment {

    private String nickname;

    private String face;

    private String authorname;

    private String replyUser;

    private String comment;

    private String commentTime;

    public String getFace(){return face;}

    public void setFace(String face){this.face = face;}

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
