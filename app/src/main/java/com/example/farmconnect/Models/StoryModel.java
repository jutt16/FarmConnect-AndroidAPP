package com.example.farmconnect.Models;

import java.util.ArrayList;
import java.util.Date;

public class StoryModel{
    public int id;
    public int user_id;
    public Object content;
    public String discription;
    public int likes;
    public Date created_at;
    public Date updated_at;
    public ArrayList<StoryFile> files;
    public StoryUser user;

    public StoryModel() {
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

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
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

    public ArrayList<StoryFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<StoryFile> files) {
        this.files = files;
    }

    public StoryUser getUser() {
        return user;
    }

    public void setUser(StoryUser user) {
        this.user = user;
    }
}





