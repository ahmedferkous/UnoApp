package com.example.unoapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unoapp.Networking.NetworkWrapper;
import com.example.unoapp.Networking.WifiDirectBroadcastReceiver;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button btnServerBrowser, btnHowToPlay;
    private RelativeLayout parentRelLayout;
    ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    int counter = 0;
                    for (String permission : result.keySet()) {
                        if (result.get(permission)) {
                            counter++;
                        }
                    }
                    if (counter == 2) {
                        navigateToBrowser();
                    }
                }
            }
    );

    public static final int LOCATION_PERMISSION = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnServerBrowser = findViewById(R.id.btnServerBrowser);
        btnHowToPlay = findViewById(R.id.btnHowToPlay);
        parentRelLayout = findViewById(R.id.parentRelLayout);

        btnServerBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePermission();
            }
        });
    }

    private void handlePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            navigateToBrowser();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showSnackbar();
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            }
        }
    }

    private void navigateToBrowser() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please enable your location.")
                    .setCancelable(false)
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                Intent serverBrowserIntent = new Intent(MainActivity.this, ServerBrowsingActivity.class);
                                startActivity(serverBrowserIntent);
                            }
                        }
                    });
            builder.create().show();
        } else {
            Intent serverBrowserIntent = new Intent(this, ServerBrowsingActivity.class);
            startActivity(serverBrowserIntent);
        }
    }

    private void showSnackbar() {
        Snackbar.make(parentRelLayout, "Without this permission, the app cannot detect nearby devices.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Grant Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityResultLauncher.launch(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
                    }
                }).show();
    }
}