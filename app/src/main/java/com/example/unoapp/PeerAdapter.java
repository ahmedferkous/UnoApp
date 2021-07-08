package com.example.unoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.net.InetAddress;
import java.util.ArrayList;

public class PeerAdapter extends RecyclerView.Adapter<PeerAdapter.ViewHolder> {
    public interface RequestConnectionToDevice {
        void onConnectionRequestResult(WifiP2pDevice device);
    }

    private RequestConnectionToDevice requestConnectionToDevice;
    private ArrayList<WifiP2pDevice> devices = new ArrayList<>();
    private Context context;

    public PeerAdapter(Context context) {
        this.context = context;
    }

    public void setDevices(ArrayList<WifiP2pDevice> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.peer_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WifiP2pDevice boundDevice = devices.get(position);
        holder.txtHostName.setText(boundDevice.deviceName);
        holder.txtAddressName.setText(boundDevice.deviceAddress);

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Connecting...")
                        .setMessage("Are you sure you would like to connect to this device?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    requestConnectionToDevice = (RequestConnectionToDevice) context;
                                    requestConnectionToDevice.onConnectionRequestResult(boundDevice);
                                } catch (ClassCastException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtHostName, txtAddressName;
        private MaterialCardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtHostName = itemView.findViewById(R.id.txtHostName);
            txtAddressName = itemView.findViewById(R.id.txtAddressName);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
