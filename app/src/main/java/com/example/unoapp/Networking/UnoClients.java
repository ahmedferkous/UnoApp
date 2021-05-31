package com.example.unoapp.Networking;

import java.net.Socket;

public class UnoClients {
    private Socket socket;
    private String nickname;

    public UnoClients(Socket socket, String nickname) {
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
