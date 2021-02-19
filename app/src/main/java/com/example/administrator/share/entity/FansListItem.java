package com.example.administrator.share.entity;

public class FansListItem {
    private String username;
    private int fansId;
    private int userId;
    private String face;
    private String signature;

    public String getUserName() {
        return username;
    }

    public int getFansId() {
        return fansId;
    }

    public int getUserId() {
        return userId;
    }

    public String getFace(){return face;}

    public String getSignature(){return signature;}
}
