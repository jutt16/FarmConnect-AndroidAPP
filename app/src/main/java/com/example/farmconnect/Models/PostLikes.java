package com.example.farmconnect.Models;

public class PostLikes {
    int id;
    int user_id;
    String user_profile_image_path;
    String user_name;

    public PostLikes(int id, int user_id, String user_profile_image_path, String user_name) {
        this.id = id;
        this.user_id = user_id;
        this.user_profile_image_path = user_profile_image_path;
        this.user_name = user_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
