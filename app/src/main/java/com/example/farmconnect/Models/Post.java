package com.example.farmconnect.Models;

import java.util.Date;

public class Post {
    private int id;
    private int user_id;
    private Object content;
    private String file_path;
    private String file_type;
    private int likes;
    private int comments;
    private String discription;
    private Date created_at;
    private String user_profile_image_path;
    private String user_name;

    public Post(int id, int user_id, Object content, String file_path, String file_type, int likes, int comments, String discription, Date created_at, String user_profile_image_path, String user_name) {
        this.id = id;
        this.user_id = user_id;
        this.content = content;
        this.file_path = file_path;
        this.file_type = file_type;
        this.likes = likes;
        this.comments = comments;
        this.discription = discription;
        this.created_at = created_at;
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

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
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
