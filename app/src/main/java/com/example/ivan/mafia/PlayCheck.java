package com.example.ivan.mafia;

import java.util.HashMap;

class PlayCheck {
    Boolean isLaunched;
    Integer phaseNumber;
    String myRole;
    Boolean sheriffIsRight;
    String winners;
    Integer voteCount;
    Boolean voted;
    HashMap<String, String> players = new HashMap<>();
    HashMap<String, String> playerRoles = new HashMap<>();
}