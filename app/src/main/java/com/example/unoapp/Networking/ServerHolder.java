package com.example.unoapp.Networking;

import android.util.Log;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHolder implements Runnable {
    public interface onServerDisconnection {
        void onServerCloseResult(String info);
    }
    private static final String TAG = "ServerHolder";
    public static final String MAX_CLIENTS_CONNECTED = "maximum_clients_connected";
    public static final String SUCCESS_CONNECTION = "successful_connection";
    public static final String DISCONNECTION_OF_CLIENT = "disconnected_client";
    public static final String CONNECTION_OF_CLIENT = "connected_client";
    public static final String UNO_APPLICATION_KEY = "humid_uno_application_key_";

    private onServerDisconnection onServerDisconnection;

    private int port;
    private String serverName;
    private ServerSocket serverSocket;
    private ArrayList<UnoClients> clientList = new ArrayList<>(); // TODO: 31/05/2021 add server hoster to client list
    private Gson gson = new Gson();

    public ServerHolder(int port, String serverName) {
        this.serverName = serverName;
        this.port = port;
    }

    // run on a thread
    public void handle(UnoClients client) {
        Log.d(TAG, "handle: new client: size: " + clientList);
        while(true) {
            try {
               DataInputStream in = new DataInputStream(client.getSocket().getInputStream());
               DataOutputStream out = new DataOutputStream(client.getSocket().getOutputStream());
                // TODO: 31/05/2021 Handle client communications to server, main game logic (perhaps use another class?)
            } catch(IOException e) {
                try {
                    DataInputStream in = new DataInputStream(client.getSocket().getInputStream());
                    DataOutputStream out = new DataOutputStream(client.getSocket().getOutputStream());

                    clientList.remove(clientList.indexOf(client));
                    client.getSocket().close();
                    in.close();
                    out.close();

                    broadcast(new Message(DISCONNECTION_OF_CLIENT, client.getNickname() + " has disconnected!"));
                    Log.d(TAG, "handle: Disconnection of client " + client.getNickname() + " size: " + clientList.size());
                } catch(IOException e1) {
                    e1.printStackTrace();
                }
                break;
            }
        }
    }

    private void broadcast(Message message) {
        for (UnoClients i : clientList) {
            try {
                DataOutputStream out = new DataOutputStream(i.getSocket().getOutputStream());
                out.writeUTF(gson.toJson(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listen() throws IOException {
        serverSocket = new ServerSocket(port);
        Log.d(TAG, "listen: Server is listening for peers...");
        while (true) {
            Socket newClient = serverSocket.accept();
            DataInputStream inputStream = new DataInputStream(newClient.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(newClient.getOutputStream());

            if (inputStream.readUTF().equals(UNO_APPLICATION_KEY)) {
                Log.d(TAG, "listen: socket validated");
                if (clientList.size() < 3) {
                    outputStream.writeUTF(gson.toJson(new Message(SUCCESS_CONNECTION, "Successfully connected to server on port: " + port)));
                    Log.d(TAG, "listen: Successful Connection " + "Successfully connected to server on port: " + port);
                    String nickName = inputStream.readUTF();
                    Log.d(TAG, "listen: Successful Connection: Nickname received " + nickName);
                    UnoClients newUnoClient = new UnoClients(newClient, nickName);
                    clientList.add(newUnoClient);
                    broadcast(new Message(CONNECTION_OF_CLIENT, nickName + " has connected to the server!"));
                    new HandleClient(newUnoClient).start();
                } else {
                    outputStream.writeUTF(gson.toJson(new Message(MAX_CLIENTS_CONNECTED, "Sorry! Maximum players reached on " + serverName)));
                    Log.d(TAG, "listen: Unsuccessful, max players " + "Sorry! Maximum players reached on " + serverName);
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

    private class HandleClient extends Thread {
        private UnoClients client;

        public HandleClient(UnoClients client) {
            this.client = client;
        }

        @Override
        public void run() {
            handle(client);
        }
    }

}
