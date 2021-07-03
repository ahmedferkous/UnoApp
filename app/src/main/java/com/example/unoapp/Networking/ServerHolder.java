package com.example.unoapp.Networking;

import android.util.Log;
import com.example.unoapp.GameLogic.GameInstance;
import com.example.unoapp.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;

import javax.json.JsonValue;

public class ServerHolder implements Runnable, GameInstance.OnUserDisconnect, MainActivity.onGameStateChange {
    @Override
    public void beginGame() {
        gameInstance.beginGame();
    }

    @Override
    public void endGame() {
        gameInstance.endGame();
    }

    @Override
    public void onUserDisconnectResult(UnoClient player) {
        try {
            DataInputStream inputStream = new DataInputStream(player.getSocket().getInputStream());
            DataOutputStream outputStream = new DataOutputStream(player.getSocket().getOutputStream());

            inputStream.close();
            outputStream.close();
            player.getSocket().close();

            clientList.remove(player);

            broadcast(new Message(DISCONNECTION_OF_CLIENT, player.getNickname()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = "ServerHolder";
    public static final String MAX_CLIENTS_CONNECTED = "maximum_clients_connected";
    public static final String SUCCESS_CONNECTION = "successful_connection";
    public static final String DISCONNECTION_OF_CLIENT = "disconnected_client";
    public static final String CONNECTION_OF_CLIENT = "connected_client";
    public static final String UNO_APPLICATION_KEY = "_humid_uno_application_key_";
    public static Gson gson = new Gson();
    public static Type messageType = new TypeToken<Message>(){}.getType();

    private boolean listening = false;
    private final int port;
    private final String serverName;
    private GameInstance gameInstance;
    private ArrayList<UnoClient> clientList; // TODO: 31/05/2021 add server hoster to client list

    public ServerHolder(int port, String serverName) {
        this.serverName = serverName;
        this.port = port;
    }

    public ArrayList<UnoClient> getClientList() {
        return clientList;
    }

    public void broadcast(Message message) {
        for (UnoClient i : clientList) {
            try {
                DataOutputStream out = new DataOutputStream(i.getSocket().getOutputStream());
                out.writeUTF(gson.toJson(message));
            } catch (IOException e) {
                onUserDisconnectResult(i);
                e.printStackTrace();
            }
        }
    }

    public static Message decipherMessage(DataInputStream in) throws IOException {
        return gson.fromJson(in.readUTF(), messageType);
    }

    public static void send(Message message, DataOutputStream out) {
        try {
            out.writeUTF(gson.toJson(message));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() throws IOException {
        listening = true;
        ServerSocket serverSocket = new ServerSocket(port);
        gameInstance = new GameInstance(this);
        clientList = new ArrayList<>();

        while (listening) {
            Socket newClient = serverSocket.accept();
            DataInputStream inputStream = new DataInputStream(newClient.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(newClient.getOutputStream());

            if (inputStream.readUTF().equals(UNO_APPLICATION_KEY)) {
                if (clientList.size() < 3) {
                    outputStream.writeUTF(gson.toJson(new Message(SUCCESS_CONNECTION, "Successfully connected to server on port: " + port)));
                    String nickName = inputStream.readUTF();
                    // TODO: 21/06/2021 obtain image 
                    UnoClient newUnoClient = new UnoClient(newClient, nickName);
                    broadcast(new Message(CONNECTION_OF_CLIENT, nickName));
                    clientList.add(newUnoClient);
                    Log.d(TAG, "listen: connection of client " + nickName);
                } else {
                    outputStream.writeUTF(gson.toJson(new Message(MAX_CLIENTS_CONNECTED, "Sorry! Maximum players reached on " + serverName)));
                }
            }
        }
    }

    // TODO: 31/05/2021 Handle server "starting"?
    @Override
    public void run() {
        try {
            listen();
        } catch (IOException e) {
            // TODO: 31/05/2021 handle server disconnection, use callbacks?
            e.printStackTrace();
        }
    }


}
