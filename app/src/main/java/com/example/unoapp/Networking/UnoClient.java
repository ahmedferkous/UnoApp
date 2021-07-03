package com.example.unoapp.Networking;

import android.graphics.Bitmap;

import com.example.unoapp.CardFiles.CardModel;

import java.net.Socket;
import java.util.ArrayList;

public class UnoClient {
    private Socket socket;
    private String nickname;
    private int handSize = 7;
    private Bitmap image;
    private boolean isTurn = false;

    public int getHandSize() {
        return handSize;
    }

    public void setHandSize(int handSize) {
        this.handSize = handSize;
    }

    public UnoClient(Socket socket, String nickname) {
        this.socket = socket;
        this.nickname = nickname;
    }

    public UnoClient(String nickname, boolean isTurn) {
        this.nickname = nickname;
        this.isTurn = isTurn;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isTurn() {
        return isTurn;
    }

    public void setTurn(boolean turn) {
        isTurn = turn;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getNickname() {
        return nickname;
    }


}
