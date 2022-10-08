package com.example.administrator.share.entity;

public class FavorMsgListItem {

    private int newsId;

    /**
     *  userId是点赞者的id
     */
    private int userId;

    private String face;

    private String title;

    private String nickname;

    private String image;

    private int isVisited;

    private int favorId;

    private String favorTime;

    public String getFavorTime() {
        return favorTime;
    }

    public int getFavorId(){return favorId;}

    public int getIsVisited(){return isVisited;}

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
}
