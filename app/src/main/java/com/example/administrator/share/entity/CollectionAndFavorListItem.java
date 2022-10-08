package com.example.administrator.share.entity;

public class CollectionAndFavorListItem {

    private int newsId;

    /**
     *  userId是圈子作者的id
     */
    private int userId;

    private String image;

    private String title;

    private String nickname;

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

    public String getImage(){ return image; }
}
