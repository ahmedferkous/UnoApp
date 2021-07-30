package com.example.unoapp;

import com.example.unoapp.GameLogic.GameInstance;
import com.example.unoapp.GameLogic.PlayerInstance;

public class InstanceContainers {
    private static PlayerInstance playerInstance;
    private static GameInstance gameInstance;

    public static PlayerInstance getPlayerInstance() {
        return playerInstance;
    }

    public static void setPlayerInstance(PlayerInstance instance) {
        InstanceContainers.playerInstance = instance;
    }
}
