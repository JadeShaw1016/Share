package com.example.administrator.share.entity;

/**
 * Created by djzhao on 17/05/05.
 */

public class NewsListItem {

    private String title;

    private int newsId;

    private String username;

    private String image;

    private String comment;

    private int status;

    private int commentId;

    private String commentTime;

    public String getCommentTime() {
        return commentTime;
    }

    public int getCommentId(){return commentId;}

    public int getStatus(){return status;}

    public String getTitle() {
        return title;
    }

    public int getNewsId() { return newsId; }

    public String getUsername() {
        return username;
    }

    public String getImage(){ return image; }

    public String getComment(){ return comment; }
}
