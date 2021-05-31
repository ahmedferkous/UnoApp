package com.example.unoapp.Networking;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.unoapp.MainActivity;

import java.util.ArrayList;

public class ManagerWrapper {
    private static final String TAG = "ManagerWrapper";
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private int port;
    private boolean serverExists = false;

    public ManagerWrapper(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }

    public void setPort(int port) {
        this.port = port;
    }

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

    public void stopDiscovery() {
        manager.stopPeerDiscovery(channel, null);
    }

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
            }
        });
    }

    public ArrayList<WifiP2pDevice> getDevices(WifiP2pDeviceList peers) {
        ArrayList<WifiP2pDevice> devices = new ArrayList<>();
        devices.addAll(peers.getDeviceList());
        return devices;
    }

    public void handleServerToClientCommunication(WifiP2pInfo info) {
        if (null != info) {
            Log.d(TAG, "onConnectionInfoAvailable: " + info);
            if (info.groupOwnerAddress != null) {
                if (info.isGroupOwner) {
                    if (!serverExists) {
                        new Thread(new ServerHolder(port, "humid")).start();
                        serverExists = true;
                    }
                } else {
                    new Thread(new ClientHolder(info.groupOwnerAddress, "Ahmed", port)).start();
                }
            }
        } else {
            Log.d(TAG, "onConnectionInfoAvailable: Nulled");
        }
    }

    public void requestPeers(MainActivity activity) {
        manager.requestPeers(channel, activity);
    }

    public void requestConnectionInfo(MainActivity activity) {
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