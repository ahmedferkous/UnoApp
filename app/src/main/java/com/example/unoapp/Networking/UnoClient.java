package com.example.unoapp.Networking;

import com.example.unoapp.CardFiles.CardModel;

import java.net.Socket;
import java.util.ArrayList;

public class UnoClient {
    private Socket socket;
    private String nickname;

    public UnoClient(Socket socket, String nickname) {
        this.socket = socket;
        this.nickname = nickname;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getNickname() {
        return nickname;
    }


}
