package com.example.ivan.mafia;

import android.support.annotation.NonNull;

public class RoomList {

    private Integer roomId;
    private String roomName;
    private String roomPassword;
    private Boolean isLaunched;
    private String roomMaker;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public Boolean getLaunched() {
        return isLaunched;
    }

    public void setLaunched(Boolean launched) {
        isLaunched = launched;
    }

    public String getRoomMaker() {
        return roomMaker;
    }

    public void setRoomMaker(String roomMaker) {
        this.roomMaker = roomMaker;
    }

    @NonNull
    @Override
    public String toString() {
        return "roomId: " + roomId + ", roomName: " + roomName + ", roomPassword: " + roomPassword + ", isLaunched: " + isLaunched + ", roomMaker: " + roomMaker;
    }

}
