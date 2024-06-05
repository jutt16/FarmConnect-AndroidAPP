package com.example.farmconnect.Models;

import java.util.ArrayList;

public class AdsModel {
    int id;
    int seller_id;
    int reviews;
    ArrayList<AllReview> allReviews;
    String quantity;
    String price;
    String discription;
    ArrayList<AdsFile> files;
    int likes;
    ArrayList<AllLike> allLikes;
    ArrayList<AllDislike> allDisikes;
    int dislikes;
    Object current_user_has_reaction;
    String created_at;
    String user_profile_image_path;
    String user_name;

    public AdsModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(int seller_id) {
        this.seller_id = seller_id;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public ArrayList<AllReview> getAllReviews() {
        return allReviews;
    }

    public void setAllReviews(ArrayList<AllReview> allReviews) {
        this.allReviews = allReviews;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public ArrayList<AdsFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<AdsFile> files) {
        this.files = files;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public ArrayList<AllLike> getAllLikes() {
        return allLikes;
    }

    public void setAllLikes(ArrayList<AllLike> allLikes) {
        this.allLikes = allLikes;
    }

    public ArrayList<AllDislike> getAllDisikes() {
        return allDisikes;
    }

    public void setAllDisikes(ArrayList<AllDislike> allDisikes) {
        this.allDisikes = allDisikes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public Object getCurrent_user_has_reaction() {
        return current_user_has_reaction;
    }

    public void setCurrent_user_has_reaction(Object current_user_has_reaction) {
        this.current_user_has_reaction = current_user_has_reaction;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
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
