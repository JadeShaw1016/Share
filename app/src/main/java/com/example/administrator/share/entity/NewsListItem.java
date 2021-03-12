package com.example.administrator.share.entity;

public class NewsListItem {

    private String title;

    private int newsId;
    //userId是发布圈子id为newsId的人的id
    private int userId;

    private String nickname;

    private String face;

    private String image;

    private String comment;

    private int status;

    private int commentId;

    private int favorId;

    private String commentTime;

    private String favorTime;

    private Integer clickTimes;

    public String getCommentTime() {
        return commentTime;
    }

    public String getFavorTime() {
        return favorTime;
    }

    public int getCommentId(){return commentId;}

    public int getFavorId(){return favorId;}

    public int getStatus(){return status;}

    public String getTitle() {
        return title;
    }

    public int getNewsId() { return newsId; }

    public String getNickname() {
        return nickname;
    }

    public int getUserId(){
        return userId;
    }

    public String getFace(){ return face; }

    public String getImage(){ return image; }

    public String getComment(){ return comment; }

    public Integer getClickTimes(){ return clickTimes; }
}
