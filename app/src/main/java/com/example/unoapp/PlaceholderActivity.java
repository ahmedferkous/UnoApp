package com.example.unoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.unoapp.CardFiles.Deck;

public class PlaceholderActivity extends AppCompatActivity {
    private static final String TAG = "PlaceholderActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);

        for (int i = 0; i < 7; i++) {
            Log.d(TAG, "onCreate: Card: " + Deck.drawCard());
        }
    }
}