package com.example.unoapp.GameLogic;

import android.graphics.Bitmap;

public class Players {
    private int handSize = 7;
    private Bitmap image;
    private boolean isTurn = false;
    private String nickName;

    public Players(Bitmap image, String nickName) {
        this.image = image;
        this.nickName = nickName;
    }
}
