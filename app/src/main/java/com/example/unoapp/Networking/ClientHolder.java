package com.example.unoapp.Networking;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.unoapp.GameLogic.PlayerInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import static com.example.unoapp.Networking.ServerHolder.MAX_CLIENTS_CONNECTED;
import static com.example.unoapp.Networking.ServerHolder.SUCCESS_CONNECTION;
import static com.example.unoapp.Networking.ServerHolder.UNO_APPLICATION_KEY;
import static com.example.unoapp.Networking.ServerHolder.decipherMessage;

public class ClientHolder implements Runnable, PlayerInstance.onServerStatus {
    @Override
    public void onDisconnection(boolean isGameRunning) {
        if (!isGameRunning) {
            lobbyNotification.providedPlayerDetailsResult(new ArrayList<>());
        } else {
            // TODO: 7/07/2021 show notification of disconnection 
        }
    }

    private static final String TAG = "ClientHolder";
    private ServerHolder.LobbyNotification lobbyNotification;
    private DataInputStream in;
    private DataOutputStream out;
    private final InetAddress inetAddress;
    private Context context;
    private final String nickName;
    private File image;
    private final int port;

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public ClientHolder(InetAddress inetAddress, String nickName, int port, Context context) {
        this.inetAddress = inetAddress;
        this.nickName = nickName;
        this.context = context;
        lobbyNotification = (ServerHolder.LobbyNotification) context;
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
            switch (receivedMessage.getMessage_type()) {
                case SUCCESS_CONNECTION:
                    out.writeUTF(nickName);
                    new PlayerInstance(this, lobbyNotification);
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
