package com.example.unoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.unoapp.Networking.ManagerWrapper;
import com.example.unoapp.Networking.WifiDirectBroadcastReceiver;

import java.util.ArrayList;

// TODO: 26/05/2021 Permissions! 
public class MainActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener, PeerAdapter.RequestConnectionToDevice {
    public interface onGameStateChange {
        void beginGame();
        void endGame();
    }
    private static final String TAG = "MainActivity";
    private ManagerWrapper managerWrapper;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiDirectBroadcastReceiver broadcastReceiver;
    private TextView edtTxtPort;
    private IntentFilter intentFilter;
    private Button btnRefresh, btnBegin;
    private RecyclerView recView;
    private PeerAdapter adapter;

    @Override
    public void onConnectionRequestResult(WifiP2pDevice device) {
        managerWrapper.requestConnectionToDevice(device);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        adapter.setDevices(managerWrapper.getDevices(peers));
        Log.d(TAG, "onPeersAvailable: " + managerWrapper.getDevices(peers));
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        managerWrapper.setPort(Integer.parseInt(edtTxtPort.getText().toString()));
        managerWrapper.handleServerToClientCommunication(info);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        managerWrapper = new ManagerWrapper(manager, channel);
        broadcastReceiver = new WifiDirectBroadcastReceiver(this, managerWrapper);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        edtTxtPort = findViewById(R.id.edtTxtPort);
        recView = findViewById(R.id.recView);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnBegin = findViewById(R.id.btnBegin);
        adapter = new PeerAdapter(this);

        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managerWrapper.discover();
            }
        });

        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managerWrapper.begin();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
        managerWrapper.discover();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        managerWrapper.stopDiscovery();
        adapter.setDevices(new ArrayList<>());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        managerWrapper.disconnectFromGroup();
    }
}