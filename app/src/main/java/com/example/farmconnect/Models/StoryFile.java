package com.example.farmconnect.Models;

import java.util.Date;

public class StoryFile {
    public int id;
    public int story_id;
    public String file_path;
    public String file_type;
    public Date created_at;
    public Date updated_at;

    public StoryFile() {
    }

    public StoryFile(int id, int story_id, String file_path, String file_type, Date created_at, Date updated_at) {
        this.id = id;
        this.story_id = story_id;
        this.file_path = file_path;
        this.file_type = file_type;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStory_id() {
        return story_id;
    }

    public void setStory_id(int story_id) {
        this.story_id = story_id;
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
