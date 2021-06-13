package com.example.unoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.example.unoapp.CardFiles.CardModel;
import com.example.unoapp.CardFiles.CardsAdapter;
import com.example.unoapp.CardFiles.Deck;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PlaceholderActivity extends AppCompatActivity {
    private static final String TAG = "PlaceholderActivity";
    public CardsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);

        RecyclerView recView = findViewById(R.id.recView);
        recView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        adapter = new CardsAdapter(this);
        recView.setAdapter(adapter);

        ArrayList<CardModel> cards = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            CardModel drewCard = Deck.drawCard();
            drewCard.setPlayable(true);
            cards.add(drewCard);
        }
        cards.add(new CardModel(CardModel.TYPE_BACK));
        adapter.setCards(cards);


    }
}