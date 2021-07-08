package com.example.unoapp.Networking;

import android.graphics.Bitmap;

import com.example.unoapp.CardFiles.CardModel;

import java.net.Socket;
import java.util.ArrayList;

public class UnoClient {
    private Socket socket;
    private String nickname;
    private int user_id;

    public UnoClient(Socket socket, String nickname, int user_id) {
        this.socket = socket;
        this.nickname = nickname;
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getNickname() {
        return nickname;
    }


}
