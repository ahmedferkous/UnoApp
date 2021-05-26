package com.example.unoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiDirectBroadcastRece";
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;

    public WifiDirectBroadcastReceiver() {
    }

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action);
        switch(action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                //check to see if Wi-Fi is enabled and notify appropriate activity.
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                switch(state) {
                    case WifiP2pManager.WIFI_P2P_STATE_ENABLED:
                        Log.d(TAG, "onReceive: P2P Enabled");
                        break;
                    default:
                        Log.d(TAG, "onReceive: P2P Disabled");
                        break;
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                // Call WifiP2pManager.requestPeers() to get a list of current peers.
                if (manager != null) {
                    manager.requestPeers(channel, activity);
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                // respond to new connections or disconnections/
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                // respond to device's wifi state changing.
                break;
            default:
                break;
        }
    }
}
