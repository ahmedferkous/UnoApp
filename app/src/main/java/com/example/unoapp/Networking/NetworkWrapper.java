package com.example.unoapp.Networking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.unoapp.CardFiles.CardModel;
import com.example.unoapp.GameLogic.PlayerInstance;
import com.example.unoapp.GameLogic.Players;
import com.example.unoapp.ServerBrowsingActivity;

import java.util.ArrayList;

public class NetworkWrapper {
    public interface GameStatus {
        void beginGame();

        void endGame();
    }

    public interface UpdateCallback {
        void setPlayers(ArrayList<Players> players);

        void setHand(ArrayList<CardModel> hand, boolean playerTurn);

        void setPlacedCard(CardModel placedCard);

        void reversalChange();

        void colorChange(String color);

        void disconnection();
    }

    private static final String TAG = "ManagerWrapper";
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private GameStatus gameStatus;
    private ServerHolder serverHolder;
    private ClientHolder clientHolder;
    private boolean serverExists = false;
    private int port;
    private String nickName;

    public NetworkWrapper(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }

    public void begin() {
        if (gameStatus != null) {
            gameStatus.beginGame();
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    @SuppressLint("MissingPermission")
    public void discover() {
        stopDiscovery();

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: Success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure: Failure " + reason);
            }
        });
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void stopDiscovery() {
        manager.stopPeerDiscovery(channel, null);
    }

    @SuppressLint("MissingPermission")
    public void requestConnectionToDevice(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.groupOwnerIntent = 0;
        Log.d(TAG, "onConnectionRequestResult: Requested!");
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: Connected!");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure: failed to connect " + reason);
            }
        });
    }

    public ArrayList<WifiP2pDevice> getDevices(WifiP2pDeviceList peers) {
        ArrayList<WifiP2pDevice> devices = new ArrayList<>(peers.getDeviceList());
        return devices;
    }

    // TODO: 30/07/2021 Fix EADDRINUSE (address already in use)
    // TODO: 30/07/2021 Fix unnecessary starting of new server sockets + client sockets
    public void handleServerToClientCommunication(WifiP2pInfo info, Context context) {
        if (null != info) {
            Log.d(TAG, "onConnectionInfoAvailable: " + info);
            if (info.groupOwnerAddress != null) {
                if (info.isGroupOwner) {
                    if (!serverExists) {
                        clientHolder = null;
                        serverExists = true;
                        serverHolder = new ServerHolder(port, nickName, context);
                        new Thread(serverHolder).start();
                        gameStatus = serverHolder;
                    } else {
                        serverHolder.setServerName(nickName);
                    }
                } else {
                    serverHolder = null;
                    serverExists = false;
                    clientHolder = new ClientHolder(info.groupOwnerAddress, nickName, port, context);
                    new Thread(clientHolder).start();
                }
            }
        } else {
            Log.d(TAG, "onConnectionInfoAvailable: Nulled");
        }
    }


    @SuppressLint("MissingPermission")
    public void requestPeers(ServerBrowsingActivity activity) {
        manager.requestPeers(channel, activity);
    }

    public void requestConnectionInfo(ServerBrowsingActivity activity) {
        manager.requestConnectionInfo(channel, activity);
    }

    public void disconnectFromGroup() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

}
