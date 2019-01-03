package com.example.ivan.mafia;

import java.util.HashMap;
import java.util.Map;

public class PlayCheck {
    Boolean isLaunched;
    Integer phaseNumber;
    String myRole;
    Boolean sheriffIsRight;
    String winners;
    Integer voteCount;
    HashMap<String, String> players = new HashMap<>();
    HashMap<String, String> playerRoles = new HashMap<>();

    @Override
    public String toString() {
        String resp = "isLaunched: " + isLaunched + ", phaseNumber: " + phaseNumber + ", myRole: " + myRole +
                ", sheriffIsRight: " + sheriffIsRight + ", winners: " + winners + "; Players: ";
        for (Map.Entry<String, String> entry : players.entrySet())
            resp += entry.getKey() + ": " + entry.getValue() + ", ";
        resp += "; Players with my role: ";
        for (Map.Entry<String, String> entry : playerRoles.entrySet())
            resp += entry.getKey() + ": " + entry.getValue() + ", ";
        return resp;
    }
}