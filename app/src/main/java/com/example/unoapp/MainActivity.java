package com.example.unoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

// TODO: 26/05/2021 Permissions! 
public class MainActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener, PeerAdapter.RequestConnectionToDevice {
    private static final String TAG = "MainActivity";
    public static final String UNO_APPLICATION_KEY = "uno_application_key_405454";
    public static final int PORT = 7889;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiDirectBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private TextView txtReceive;
    private Button btnRefresh;
    private RecyclerView recView;
    private PeerAdapter adapter;

    @Override
    public void onConnectionRequestResult(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
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

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        ArrayList<WifiP2pDevice> devices = new ArrayList<>();
        devices.addAll(peers.getDeviceList());
        Log.d(TAG, "onPeersAvailable: " + devices);
        adapter.setDevices(devices);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if (null != info) {
            Log.d(TAG, "onConnectionInfoAvailable: " + info);
            if (info.groupOwnerAddress != null) {
                if (info.isGroupOwner) {
                    new ReceiveDataTask().execute();
                } else {
                    new SendDataTask(this).execute(info.groupOwnerAddress);
                }
            }
        } else {
            Log.d(TAG, "onConnectionInfoAvailable: Nulled");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        broadcastReceiver = new WifiDirectBroadcastReceiver(manager, channel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        txtReceive = findViewById(R.id.txtReceive);
        recView = findViewById(R.id.recView);
        btnRefresh = findViewById(R.id.btnRefresh);
        adapter = new PeerAdapter(this);

        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discover(manager, channel);
            }
        });
    }

    /*
    private void handlePermission() {
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED)) {
            showSnackBar();
        }
    }

    private void showSnackBar() {
        Snackbar.make(parent, "This app requires location permissions", Snackbar.LENGTH_INDEFINITE)
                .setAction("Grant Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
    }

     */

    public static void discover(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        manager.stopPeerDiscovery(channel, null);

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

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
        discover(manager, channel);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        manager.stopPeerDiscovery(channel, null);
        adapter.setDevices(new ArrayList<>());
    }

    public static class ReceiveDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT); // server hoster specifies port
                Socket client = serverSocket.accept();

                DataInputStream in = new DataInputStream(client.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());

                if (in.readUTF().equals(UNO_APPLICATION_KEY)) {
                    outputStream.writeUTF("Welcome!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class SendDataTask extends AsyncTask<InetAddress, Void, String> {
        private WeakReference<MainActivity> activityReference;

        public SendDataTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected String doInBackground(InetAddress... inetAddresses) {
            try {
                Socket client = new Socket();
                client.connect(new InetSocketAddress(inetAddresses[0], PORT));

                DataInputStream in = new DataInputStream(client.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
                outputStream.writeUTF(UNO_APPLICATION_KEY);
                return in.readUTF();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: String: " + s);
            if (s != null) {
                TextView txtReceive = activityReference.get().findViewById(R.id.txtReceive);
                txtReceive.setText(s);
            }
        }
    }
}