package com.example.farmconnect.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.Date;

public class UserModel implements Parcelable {
    private String userName;
    private String mobile;
    private String email;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;
    private String password;

    public UserModel() {
    }

    public UserModel(String userName, String mobile, String email, String password) {
        this.userName = userName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
    }

    public UserModel(String userName, String mobile, String email, Timestamp createdTimestamp, String userId, String fcmToken, String password) {
        this.userName = userName;
        this.mobile = mobile;
        this.email = email;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.password = password;
    }

    protected UserModel(Parcel in) {
        userName = in.readString();
        mobile = in.readString();
        email = in.readString();
        userId = in.readString();
        fcmToken = in.readString();
        password = in.readString();
        long timestamp = in.readLong();
        createdTimestamp = new Timestamp(new Date(timestamp));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(mobile);
        dest.writeString(email);
        dest.writeString(userId);
        dest.writeString(fcmToken);
        dest.writeString(password);
        dest.writeLong(createdTimestamp != null ? createdTimestamp.toDate().getTime() : -1L);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    // Getters and setters

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
