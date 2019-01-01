package com.example.ivan.mafia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayCheck {
    Boolean isLaunched;
    Integer phaseNumber;
    String myRole;
    Boolean sheriffIsRight;
    String winners;
    HashMap<String, String> players = new HashMap<>();
    ArrayList<String> playersWithMyRole = new ArrayList<>();

    @Override
    public String toString() {
        String resp = "isLaunched: " + isLaunched + ", phaseNumber: " + phaseNumber + ", myRole: " + myRole +
                ", sheriffIsRight: " + sheriffIsRight + ", winners: " + winners + "; Players: ";
        for (Map.Entry<String, String> entry : players.entrySet())
            resp += entry.getKey() + ": " + entry.getValue() + ", ";
        resp += "; Players with my role: ";
        for (String s : playersWithMyRole)
            resp += s + ", ";
        return resp;
    }
}