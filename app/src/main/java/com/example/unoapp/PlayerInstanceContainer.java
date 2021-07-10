package com.example.unoapp;

import com.example.unoapp.GameLogic.PlayerInstance;

public class PlayerInstanceContainer {
    private static PlayerInstance instance;

    public static PlayerInstance getInstance() {
        return instance;
    }

    public static void setInstance(PlayerInstance instance) {
        PlayerInstanceContainer.instance = instance;
    }
}
