package com.example.administrator.share.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class FollowsListItem implements Parcelable {
    private String nickname;
    private int fansId;
    private int userId;
    private String face;
    private String signature;
    private String followTime;

    protected FollowsListItem(Parcel in) {
        nickname = in.readString();
        fansId = in.readInt();
        userId = in.readInt();
        face = in.readString();
        signature = in.readString();
    }

    public static final Creator<FollowsListItem> CREATOR = new Creator<FollowsListItem>() {
        @Override
        public FollowsListItem createFromParcel(Parcel in) {
            return new FollowsListItem(in);
        }

        @Override
        public FollowsListItem[] newArray(int size) {
            return new FollowsListItem[size];
        }
    };

    public String getNickName() {
        return nickname;
    }

    public int getFansId() {
        return fansId;
    }

    public int getUserId() {
        return userId;
    }

    public String getFace(){return face;}

    public String getSignature(){return signature;}

    public String getFollowTime(){return followTime;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nickname);
        parcel.writeInt(fansId);
        parcel.writeInt(userId);
        parcel.writeString(face);
        parcel.writeString(signature);
    }
}