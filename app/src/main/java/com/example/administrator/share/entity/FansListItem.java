package com.example.administrator.share.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class FansListItem implements Parcelable {
    private String username;
    private int fansId;
    private int userId;
    private String face;
    private String signature;

    protected FansListItem(Parcel in) {
        username = in.readString();
        fansId = in.readInt();
        userId = in.readInt();
        face = in.readString();
        signature = in.readString();
    }

    public static final Creator<FansListItem> CREATOR = new Creator<FansListItem>() {
        @Override
        public FansListItem createFromParcel(Parcel in) {
            return new FansListItem(in);
        }

        @Override
        public FansListItem[] newArray(int size) {
            return new FansListItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeInt(fansId);
        parcel.writeInt(userId);
        parcel.writeString(face);
        parcel.writeString(signature);
    }
}