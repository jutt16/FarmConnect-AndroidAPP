package com.example.farmconnect.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    private String userName;
    private String mobile;
    private String email;
    private String password;
    private Uri profileImageUri;

    public UserModel(String userName, String mobile, String email, String password, Uri profileImageUri) {
        this.userName = userName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.profileImageUri = profileImageUri;
    }

    protected UserModel(Parcel in) {
        userName = in.readString();
        mobile = in.readString();
        email = in.readString();
        password = in.readString();
        profileImageUri = in.readParcelable(Uri.class.getClassLoader());
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Uri getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(Uri profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(mobile);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeParcelable(profileImageUri, flags);
    }
}