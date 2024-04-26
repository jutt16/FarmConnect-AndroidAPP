package com.example.farmconnect.Models;

public class FriendRequestModel {
    private int id;
    private int sender;
    private int reciever;
    private String created_at;
    private String updated_at;
    private int sender_id;
    private String sender_name;
    private String sender_mobile;
    private String sender_address = null;
    private String sender_profile_image_path;


    // Getter Methods

    public int getId() {
        return id;
    }

    public int getSender() {
        return sender;
    }

    public int getReciever() {
        return reciever;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public int getSender_id() {
        return sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getSender_mobile() {
        return sender_mobile;
    }

    public String getSender_address() {
        return sender_address;
    }

    public String getSender_profile_image_path() {
        return sender_profile_image_path;
    }

    // Setter Methods

    public void setId(int id) {
        this.id = id;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public void setReciever(int reciever) {
        this.reciever = reciever;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setSender_mobile(String sender_mobile) {
        this.sender_mobile = sender_mobile;
    }

    public void setSender_address(String sender_address) {
        this.sender_address = sender_address;
    }

    public void setSender_profile_image_path(String sender_profile_image_path) {
        this.sender_profile_image_path = sender_profile_image_path;
    }
}
