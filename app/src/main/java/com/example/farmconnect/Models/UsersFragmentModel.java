package com.example.farmconnect.Models;

import java.util.List;

public class UsersFragmentModel {
    private int id;
    private String userId;
    private String name;
    private String address;
    private List<String> roles;
    private String profileImagePath;

    public UsersFragmentModel() {
    }

    public UsersFragmentModel(int id, String userId, String name, String address, List<String> roles, String profileImagePath) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.roles = roles;
        this.profileImagePath = profileImagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}
