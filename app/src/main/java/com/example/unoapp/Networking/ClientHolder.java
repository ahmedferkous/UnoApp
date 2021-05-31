package com.example.unoapp.Networking;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import static com.example.unoapp.Networking.ServerHolder.MAX_CLIENTS_CONNECTED;
import static com.example.unoapp.Networking.ServerHolder.SUCCESS_CONNECTION;
import static com.example.unoapp.Networking.ServerHolder.UNO_APPLICATION_KEY;

public class ClientHolder implements Runnable{
    private static final String TAG = "ClientHolder";
    private DataOutputStream out;
    private DataInputStream in;
    private InetAddress inetAddress;
    private String nickName;
    private int port;
    private Socket client;
    private Gson gson = new Gson();
    private Type messageType = new TypeToken<Message>(){}.getType();
    
    public ClientHolder(InetAddress inetAddress, String nickName, int port) {
        this.inetAddress = inetAddress;
        this.nickName = nickName;
        this.port = port;
    }

    private void receive() throws IOException {
        Log.d(TAG, "receive: Receiving...");
        while(true) {
            Message receivedMessage = gson.fromJson(in.readUTF(), messageType);
            switch(receivedMessage.getMessage_type()) {
                // TODO: 31/05/2021 Handle cards, i.e main game logic here (perhaps use another class?)
            }
        }
    }
    
    private void write(Message message) throws IOException {
        try {
            out.writeUTF(gson.toJson(message));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: 31/05/2021 callback interfaces for successful/unsuccessful connections? (messages from Message class)
    private void connect() throws IOException {
        client = new Socket();
        client.connect(new InetSocketAddress(inetAddress, port));

        in = new DataInputStream(client.getInputStream());
        out = new DataOutputStream(client.getOutputStream());

        out.writeUTF(UNO_APPLICATION_KEY);

        Message receivedMessage = gson.fromJson(in.readUTF(), messageType);
        switch(receivedMessage.getMessage_type()) {
            case SUCCESS_CONNECTION:
                out.writeUTF(nickName);
                new ReceiveThread().start();
                Log.d(TAG, "connect: Successful connection");
                break;
            case MAX_CLIENTS_CONNECTED:
                Log.d(TAG, "connect: Unsuccessful connection");
                break;
            default:
                // TODO: 31/05/2021 Handle server-side problem?
                break;
        }
    }

    // TODO: 31/05/2021 Create class extending IOException, to send message? 
    @Override
    public void run() {
        try {
            connect();
        } catch (IOException e) {
            Log.d(TAG, "run: Exception");
            e.printStackTrace();
        }
    }
    
    class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                receive();
            } catch (IOException e) {
                Log.d(TAG, "run: Exception");
                e.printStackTrace();
            }
        }
    }
}
