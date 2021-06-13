package com.example.unoapp.Networking;

import android.util.Log;

import com.example.unoapp.GameLogic.PlayerInstance;
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
import static com.example.unoapp.Networking.ServerHolder.decipherMessage;

public class ClientHolder implements Runnable, PlayerInstance.onServerDisconnection{
    @Override
    public void onDisconnection() {
        // TODO: 2/06/2021 Notify user of disconnection
    }

    private static final String TAG = "ClientHolder";
    private DataInputStream in;
    private DataOutputStream out;
    private final InetAddress inetAddress;
    private final String nickName;
    private final int port;


    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public ClientHolder(InetAddress inetAddress, String nickName, int port) {
        this.inetAddress = inetAddress;
        this.nickName = nickName;
        this.port = port;
    }


    private void connect() throws IOException {
        Socket client = new Socket();
        client.connect(new InetSocketAddress(inetAddress, port));

        in = new DataInputStream(client.getInputStream());
        out = new DataOutputStream(client.getOutputStream());

        out.writeUTF(UNO_APPLICATION_KEY);

        Message receivedMessage = decipherMessage(in);
        if (receivedMessage != null) {
            switch(receivedMessage.getMessage_type()) {
                case SUCCESS_CONNECTION:
                    out.writeUTF(nickName);
                    new PlayerInstance(this);
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
    

}
