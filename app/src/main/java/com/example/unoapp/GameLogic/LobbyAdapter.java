package com.example.unoapp.GameLogic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unoapp.R;

import java.util.ArrayList;

public class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.ViewHolder> {
    private ArrayList<String> lobbyPlayers = new ArrayList<>();
    private Context context;

    public LobbyAdapter(Context context) {
        this.context = context;
    }

    public void setLobbyPlayers(ArrayList<String> lobbyPlayers) {
        this.lobbyPlayers = lobbyPlayers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.player_lobby_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtPlayerName.setText(lobbyPlayers.get(position));
    }

    @Override
    public int getItemCount() {
        return lobbyPlayers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtPlayerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPlayerName = itemView.findViewById(R.id.txtPlayerName);
        }
    }
}
