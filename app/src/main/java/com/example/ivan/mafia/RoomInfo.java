package com.example.ivan.mafia;

import android.support.annotation.NonNull;

public class RoomInfo {

    private String roomName;
    private Boolean isLaunched;
    private String roomMaker;
    private Boolean password;

    String getRoomName() {
        return roomName;
    }

    Boolean getLaunched() {
        return isLaunched;
    }

    String getRoomMaker() {
        return roomMaker;
    }

    Boolean getPassword() {
        return password;
    }

    @NonNull
    @Override
    public String toString() {
        return "roomName: " + roomName + ", isLaunched: " + isLaunched + ", roomMaker: " + roomMaker + ", password: " + password;
    }

}
