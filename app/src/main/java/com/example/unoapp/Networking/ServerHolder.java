package com.example.unoapp.Networking;

import android.content.Context;
import android.util.Log;

import com.example.unoapp.CardFiles.CardModel;
import com.example.unoapp.GameLogic.GameInstance;
import com.example.unoapp.GameLogic.PlayerInstance;
import com.example.unoapp.GameLogic.Players;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHolder implements Runnable, GameInstance.OnUserDisconnect, NetworkWrapper.GameStatus {
    public interface LobbyNotification {
        void providedPlayerDetailsResult(ArrayList<String> players, boolean isHoster);

        void gameBegun(ArrayList<Players> players, ArrayList<CardModel> hand, CardModel firstCard, PlayerInstance instance);

        void connectionRefused();
    }

    @Override
    public void beginGame() {
        listening = false;
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
            for (Players removingPlayer : players) {
                if (player.getUser_id() == removingPlayer.getUser_id()) {
                    players.remove(removingPlayer);
                }
            }
            if (listening) {
                lobbyNotification.providedPlayerDetailsResult(GameInstance.getListOfPlayers(clientList), true);
            } else {

            }
            Log.d(TAG, "onUserDisconnectResult: disconnection ");
            broadcast(new Message(DISCONNECTION_OF_CLIENT, gson.toJson(players)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = "ServerHolder";
    public static final String CONNECTION_TEST = "Testing Connection (IGNORE)...";
    public static final String MAX_CLIENTS_CONNECTED = "maximum_clients_connected";
    public static final String SUCCESS_CONNECTION = "successful_connection";
    public static final String DISCONNECTION_OF_CLIENT = "disconnected_client";
    public static final String CONNECTION_OF_CLIENT = "connected_client";
    public static final String UNO_APPLICATION_KEY = "_humid_uno_application_key_";
    public static Gson gson = new Gson();
    public static Type messageType = new TypeToken<Message>() {
    }.getType();
    public static int USER_UNIQUE_ID = 0;

    private final LobbyNotification lobbyNotification;
    private int server_id;
    private boolean listening = false;
    private final int port;
    private Context context;
    private Players serverPlayer;
    private String serverName;
    private GameInstance gameInstance;
    private ArrayList<Players> players = new ArrayList<>();
    private ArrayList<UnoClient> clientList;// TODO: 31/05/2021 add server hoster to client list

    public ServerHolder(int port, String serverName, Context context) {
        this.serverName = serverName;
        this.port = port;
        this.context = context;
        lobbyNotification = (LobbyNotification) context;
        server_id = USER_UNIQUE_ID;
        serverPlayer = new Players(serverName, server_id);
        players.add(serverPlayer);
        USER_UNIQUE_ID++;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
        players.remove(serverPlayer);
        serverPlayer = new Players(serverName, server_id);
        players.add(serverPlayer);
    }

    public ArrayList<UnoClient> getClientList() {
        return clientList;
    }

    public ArrayList<Players> getPlayers() {
        return players;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void testConnectivity() {
        while (listening) {
            for (UnoClient client : clientList) {
                try {
                    DataOutputStream outputStream = new DataOutputStream(client.getSocket().getOutputStream());
                    DataInputStream inputStream = new DataInputStream(client.getSocket().getInputStream());
                    send(new Message(CONNECTION_TEST, null), outputStream);
                    decipherMessage(inputStream);
                } catch (IOException e) {
                    onUserDisconnectResult(client);
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getServer_id() {
        return server_id;
    }

    public LobbyNotification getLobbyNotification() {
        return lobbyNotification;
    }

    private void listen() throws IOException {
        listening = true;
        ServerSocket serverSocket = new ServerSocket(port);
        gameInstance = new GameInstance(this);
        clientList = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                testConnectivity();
            }
        }.start();

        while (listening) {
            Socket newClient = serverSocket.accept();
            DataInputStream inputStream = new DataInputStream(newClient.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(newClient.getOutputStream());

            if (inputStream.readUTF().equals(UNO_APPLICATION_KEY)) {
                if (clientList.size() < 3) {
                    outputStream.writeUTF(gson.toJson(new Message(SUCCESS_CONNECTION, "Successfully connected to server on port: " + port)));
                    String nickName = inputStream.readUTF();
                    UnoClient newUnoClient = new UnoClient(newClient, nickName, USER_UNIQUE_ID);
                    clientList.add(newUnoClient);
                    players.add(new Players(nickName, USER_UNIQUE_ID));
                    outputStream.writeInt(USER_UNIQUE_ID);
                    USER_UNIQUE_ID++;
                    /*
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            lobbyNotification.providedPlayerDetailsResult(clientList);
                        }
                    });

                     */
                    lobbyNotification.providedPlayerDetailsResult(GameInstance.getListOfPlayers(clientList), true);
                    broadcast(new Message(CONNECTION_OF_CLIENT, gson.toJson(players)));
                    Log.d(TAG, "listen: connection of client " + nickName);
                } else {
                    outputStream.writeUTF(gson.toJson(new Message(MAX_CLIENTS_CONNECTED, "Sorry! Maximum players reached on " + serverName)));
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            listen();
        } catch (IOException e) {
            Log.d(TAG, "run: Error occured here");
            e.printStackTrace();
        }
    }


}
