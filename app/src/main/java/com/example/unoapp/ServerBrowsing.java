package com.example.unoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unoapp.GameLogic.LobbyAdapter;
import com.example.unoapp.GameLogic.PlayersAdapter;
import com.example.unoapp.Networking.ClientHolder;
import com.example.unoapp.Networking.NetworkWrapper;
import com.example.unoapp.Networking.ServerHolder;
import com.example.unoapp.Networking.UnoClient;
import com.example.unoapp.Networking.WifiDirectBroadcastReceiver;

import java.util.ArrayList;

public class ServerBrowsing extends AppCompatActivity implements ServerHolder.LobbyNotification, WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener, PeerAdapter.RequestConnectionToDevice {
    @Override
    public void gameBegun() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent gameIntent = new Intent(ServerBrowsing.this, GameActivity.class);
                startActivity(gameIntent);
            }
        });
    }

    @Override
    public void isServerHoster(boolean isHoster) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isHoster) {
                    btnBegin.setVisibility(View.VISIBLE);
                } else {
                    btnBegin.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void providedPlayerDetailsResult(ArrayList<String> players) {
        Log.d(TAG, "providedPlayerDetailsResult: " + players);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterLobby.setLobbyPlayers(players);
                txtNumberOfPlayers.setText(String.valueOf(players.size() + 1) + "/3");
                if (players.size() == 0) {
                    btnBegin.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onConnectionRequestResult(WifiP2pDevice device) {
        networkWrapper.requestConnectionToDevice(device);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        adapterNearbyPlayers.setDevices(networkWrapper.getDevices(peers));
        Log.d(TAG, "onPeersAvailable: " + networkWrapper.getDevices(peers));
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        networkWrapper.setPort(Integer.parseInt(edtTxtPort.getText().toString()));
        networkWrapper.setNickName(edtTxtNickname.getText().toString());
        networkWrapper.handleServerToClientCommunication(info, this);
    }
    private static final String TAG = "ServerBrowsing";

    private TextView txtNumberOfPlayers;
    private EditText edtTxtPort, edtTxtNickname;
    private Button btnBegin;
    private ImageView btnRefresh;
    private RecyclerView recViewNearbyPlayers, recViewLobbyPlayers;
    private PeerAdapter adapterNearbyPlayers;
    private LobbyAdapter adapterLobby;
    private NetworkWrapper networkWrapper;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiDirectBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_browsing);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        networkWrapper = new NetworkWrapper(manager, channel);
        broadcastReceiver = new WifiDirectBroadcastReceiver(this, networkWrapper);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        edtTxtPort = findViewById(R.id.edtTxtPort);
        edtTxtNickname = findViewById(R.id.edtTxtNickname);
        txtNumberOfPlayers = findViewById(R.id.txtNumberOfPlayers);
        btnBegin = findViewById(R.id.btnBegin);
        btnRefresh = findViewById(R.id.btnRefresh);
        recViewNearbyPlayers = findViewById(R.id.recViewNearbyPlayers);
        recViewLobbyPlayers = findViewById(R.id.recViewLobbyPlayers);
        adapterNearbyPlayers = new PeerAdapter(this);
        adapterLobby = new LobbyAdapter(this);

        recViewNearbyPlayers.setAdapter(adapterNearbyPlayers);
        recViewNearbyPlayers.setLayoutManager(new LinearLayoutManager(this));
        recViewLobbyPlayers.setAdapter(adapterLobby);
        recViewLobbyPlayers.setLayoutManager(new LinearLayoutManager(this));

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkWrapper.discover();
            }
        });
        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkWrapper.begin();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
        networkWrapper.discover();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        networkWrapper.stopDiscovery();
        adapterNearbyPlayers.setDevices(new ArrayList<>());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkWrapper.stopDiscovery();
        networkWrapper.disconnectFromGroup();
    }
}