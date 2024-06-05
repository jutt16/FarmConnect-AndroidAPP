package com.example.farmconnect.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.farmconnect.utils.DateUtils;

import java.util.Date;

public class StoryUser implements Parcelable {
    public int id;
    public String user_id;
    public String name;
    public String email;
    public int status;
    public String mobile;
    public String address;
    public String profile_image_path;
    public String email_verified_at;
    public Date created_at;
    public Date updated_at;

    public StoryUser() {
    }

    protected StoryUser(Parcel in) {
        id = in.readInt();
        user_id = in.readString();
        name = in.readString();
        email = in.readString();
        status = in.readInt();
        mobile = in.readString();
        address = in.readString();
        profile_image_path = in.readString();
        email_verified_at = in.readString();
        created_at = DateUtils.parseDate(in.readString());
        updated_at = DateUtils.parseDate(in.readString());
    }

    public static final Creator<StoryUser> CREATOR = new Creator<StoryUser>() {
        @Override
        public StoryUser createFromParcel(Parcel in) {
            return new StoryUser(in);
        }

        @Override
        public StoryUser[] newArray(int size) {
            return new StoryUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(user_id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeInt(status);
        dest.writeString(mobile);
        dest.writeString(address);
        dest.writeString(profile_image_path);
        dest.writeString(email_verified_at);
        dest.writeString(DateUtils.formatDate(created_at));
        dest.writeString(DateUtils.formatDate(updated_at));
    }

    public StoryUser(int id, String user_id, String name, String email, int status, String mobile, String address, String profile_image_path, String email_verified_at, Date created_at, Date updated_at) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.mobile = mobile;
        this.address = address;
        this.profile_image_path = profile_image_path;
        this.email_verified_at = email_verified_at;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfile_image_path() {
        return profile_image_path;
    }

    public void setProfile_image_path(String profile_image_path) {
        this.profile_image_path = profile_image_path;
    }

    public String getEmail_verified_at() {
        return email_verified_at;
    }

    public void setEmail_verified_at(String email_verified_at) {
        this.email_verified_at = email_verified_at;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
