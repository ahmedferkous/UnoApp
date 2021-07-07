package com.example.unoapp.GameLogic;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unoapp.Networking.UnoClient;
import com.example.unoapp.R;

import java.util.ArrayList;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.ViewHolder> {
    private ArrayList<UnoClient> players = new ArrayList<>();
    private Context context;


    public PlayersAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.player_item, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UnoClient boundPlayer = players.get(position);

        holder.txtPlayerName.setText(boundPlayer.getNickname());
        String handSize = "x" + boundPlayer.getHandSize() + " Cards";
        holder.txtNumOfCards.setText(handSize);

    /*
    Glide.with(context)
            .asBitmap()
            .load(boundPlayer.getImage())
            .into(holder.playerImage);

     */

        if (boundPlayer.isTurn()) {
            holder.imagePlayerTurn.setVisibility(View.VISIBLE);
        } else {
            holder.imagePlayerTurn.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public void setPlayers(ArrayList<UnoClient> players) {
        this.players = players;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtPlayerName, txtNumOfCards;
        private ImageView imagePlayerTurn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPlayerName = itemView.findViewById(R.id.txtPlayerName);
            txtNumOfCards = itemView.findViewById(R.id.txtNumOfCards);
            imagePlayerTurn = itemView.findViewById(R.id.imagePlayerTurn);
        }
    }
}
