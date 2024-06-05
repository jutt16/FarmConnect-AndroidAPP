package com.example.farmconnect.Models;

import java.util.Date;

public class PostComments {
    int id;
    String comment;
    int user_id;
    String user_profile_image_path;
    String user_name;
    Date created_at;

    public PostComments(int id, String comment, int user_id, String user_profile_image_path, String user_name, Date created_at) {
        this.id = id;
        this.comment = comment;
        this.user_id = user_id;
        this.user_profile_image_path = user_profile_image_path;
        this.user_name = user_name;
        this.created_at = created_at;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_profile_image_path() {
        return user_profile_image_path;
    }

    public void setUser_profile_image_path(String user_profile_image_path) {
        this.user_profile_image_path = user_profile_image_path;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
