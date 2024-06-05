package com.example.farmconnect.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.farmconnect.utils.DateUtils;

import java.util.Date;

public class AllReview implements Parcelable {
    int id;
    int ad_id;
    int user_id;
    String comment;
    Date created_at;
    Date updated_at;
    StoryUser user;

    protected AllReview(Parcel in) {
        id = in.readInt();
        ad_id = in.readInt();
        user_id = in.readInt();
        comment = in.readString();
        created_at = DateUtils.parseDate(in.readString());
        updated_at = DateUtils.parseDate(in.readString());
        user = in.readParcelable(StoryUser.class.getClassLoader());
    }

    public static final Creator<AllReview> CREATOR = new Creator<AllReview>() {
        @Override
        public AllReview createFromParcel(Parcel in) {
            return new AllReview(in);
        }

        @Override
        public AllReview[] newArray(int size) {
            return new AllReview[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(ad_id);
        dest.writeInt(user_id);
        dest.writeString(comment);
        dest.writeString(DateUtils.formatDate(created_at));
        dest.writeString(DateUtils.formatDate(updated_at));
        dest.writeParcelable(user, flags);
    }

    public AllReview() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAd_id() {
        return ad_id;
    }

    public void setAd_id(int ad_id) {
        this.ad_id = ad_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public StoryUser getUser() {
        return user;
    }

    public void setUser(StoryUser user) {
        this.user = user;
    }
}
