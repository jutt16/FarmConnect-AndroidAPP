package com.example.farmconnect.Models;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoomModel {
    String chatRoomId;
    List<String> userIds;
    Timestamp lastMessageTimeStamp;
    String LastMessageSenderId;
    String lastMessage;

    public ChatRoomModel() {
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(Timestamp lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public String getLastMessageSenderId() {
        return LastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        LastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ChatRoomModel(String chatRoomId, List<String> userId, Timestamp lastMessageTimeStamp, String lastMessageSenderId) {
        this.chatRoomId = chatRoomId;
        this.userIds = userId;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.LastMessageSenderId = lastMessageSenderId;
    }
}
