package com.example.unoapp.GameLogic;

import android.graphics.Bitmap;

public class Players {
    private int handSize = 7;
    private boolean isTurn = false;
    private int user_id;
    private String nickName;

    public Players(String nickName, int user_id) {
        this.user_id = user_id;
        this.nickName = nickName;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getHandSize() {
        return handSize;
    }

    public void setHandSize(int handSize) {
        this.handSize = handSize;
    }

    public boolean isTurn() {
        return isTurn;
    }

    public void setTurn(boolean turn) {
        isTurn = turn;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
