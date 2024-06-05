package com.example.farmconnect.Models;

import java.util.Date;

 public class AllDislike {
     int id;
     int ad_id;
     int user_id;
     String type;
     Date created_at;
     Date updated_at;
     StoryUser user;

     public AllDislike() {
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

     public String getType() {
         return type;
     }

     public void setType(String type) {
         this.type = type;
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
