package com.example.unoapp.Networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.unoapp.MainActivity;
import com.example.unoapp.Networking.ManagerWrapper;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiDirectBroadcastRece";
    private ManagerWrapper managerWrapper;
    private MainActivity activity;

    public WifiDirectBroadcastReceiver() {

    }

    public WifiDirectBroadcastReceiver(MainActivity activity, ManagerWrapper managerWrapper) {
        super();
        this.managerWrapper = managerWrapper;
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
                managerWrapper.requestPeers(activity);
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                // respond to new connections or disconnections/
                managerWrapper.requestConnectionInfo(activity);
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                // respond to device's wifi state changing.
                managerWrapper.discover();
                break;
            default:
                break;
        }
    }
}
