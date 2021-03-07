package com.example.administrator.share.entity;

import java.util.List;

public class CircleDetail {

    private String username;

    private String releaseTime;

    private String face;

    private String title;

    private String image;

    private String content;

    private List<Comment> comments;

    private int clickTimes;

    private int collectTimes;

    private int commentTimes;

    public String getFace(){return face;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public int getClickTimes(){return clickTimes;}

    public int getCollectTimes(){return collectTimes;}

    public int getCommentTimes(){return commentTimes;}
}
