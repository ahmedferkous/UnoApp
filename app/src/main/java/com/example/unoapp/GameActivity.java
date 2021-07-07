package com.example.unoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unoapp.CardFiles.CardModel;
import com.example.unoapp.CardFiles.CardsAdapter;
import com.example.unoapp.CardFiles.Deck;
import com.example.unoapp.GameLogic.PlayersAdapter;
import com.example.unoapp.Networking.UnoClient;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "PlaceholderActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        RecyclerView revViewCards = findViewById(R.id.recViewCards);
        RecyclerView recViewPlayers = findViewById(R.id.recViewPlayers);
        //Button btnRedraw = findViewById(R.id.btnRedraw);

        revViewCards.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recViewPlayers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        CardsAdapter cardsAdapter = new CardsAdapter(this);
        PlayersAdapter playersAdapter = new PlayersAdapter(this);
        revViewCards.setAdapter(cardsAdapter);
        recViewPlayers.setAdapter(playersAdapter);

        ArrayList<UnoClient> players = new ArrayList<>();
        players.add(new UnoClient("123456789012", true));
        players.add(new UnoClient("123456789012", false));
        playersAdapter.setPlayers(players);

        ImageView imageRotation = findViewById(R.id.imageRotation);
        TextView txtRedraw = findViewById(R.id.txtRedraw);
        TextView txtReverse = findViewById(R.id.txtReverse);

        Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        Animation aniRotateAntiClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        imageRotation.setAnimation(aniRotateClk);

        txtRedraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CardModel> cards = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    CardModel drewCard = Deck.drawCard();
                    drewCard.setPlayable(true);
                    cards.add(drewCard);
                }
                cardsAdapter.setCards(cards);
            }
        });

        txtReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageRotation.getAnimation().equals(aniRotateClk)) {
                    imageRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.anticlockwise_red));
                    imageRotation.setAnimation(aniRotateAntiClk);
                } else {
                    imageRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.clockwise_red));
                    imageRotation.setAnimation(aniRotateClk);
                }
            }
        });


        /*
        btnRedraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CardModel> cards = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    CardModel drewCard = Deck.drawCard();
                    drewCard.setPlayable(true);
                    cards.add(drewCard);
                }
                cards.add(new CardModel(CardModel.TYPE_BACK));
                cardsAdapter.setCards(cards);
            }
        });


         */
    }
}