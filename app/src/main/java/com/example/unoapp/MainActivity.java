package com.example.unoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

// TODO: 26/05/2021 Permissions! 
public class MainActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener {
    private static final String TAG = "MainActivity";
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiDirectBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    // TODO: 26/05/2021 Get group ID and fix this mess up lmao
    // TODO: 26/05/2021 Here: https://androiddevsimplified.wordpress.com/2016/09/13/local-networking-in-android-wifi-direct/
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        for (WifiP2pDevice device : peers.getDeviceList()) {
            Log.d(TAG, "onPeersAvailable: Device: " + device.deviceName + " Address: " + device.deviceAddress);
        }
        WifiP2pDevice sendingDevice = peers.get("4e:66:41:0e:4f:a6"); // other phone

        if (sendingDevice != null) { // debugging purposes
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = sendingDevice.deviceAddress;
            new ReceiveDataTask(MainActivity.this).execute(); // receiving data from s7edge
            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int reason) {
                }
            });
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

        TextView txtReceive = findViewById(R.id.txtReceive);

        Button btnSendData = findViewById(R.id.btnSendData);
        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendDataTask().execute(txtReceive.getText().toString(), "0e:2f:b0:c7:e0:c2"); // sending data from s7 edge to the samsung a20 phone
            }
        });
        
    }

    public void discover() {
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
        discover();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        manager.stopPeerDiscovery(channel, null);
    }

    public static class ReceiveDataTask extends AsyncTask<Void, Void, String> {
        private WeakReference<MainActivity> activityReference;

        public ReceiveDataTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {
                ServerSocket serverSocket = new ServerSocket(6869); // server hoster specifies port
                Socket client = serverSocket.accept();

                DataInputStream in = new DataInputStream(client.getInputStream());
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

    public static class SendDataTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                Socket client = new Socket();
                client.bind(null);
                client.connect(new InetSocketAddress(strings[1], 6869), 500);

                DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
                outputStream.writeUTF(strings[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}