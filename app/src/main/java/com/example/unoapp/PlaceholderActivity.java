package com.example.unoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.unoapp.CardFiles.AllCards;
import com.example.unoapp.CardFiles.CardModel;

import java.util.Stack;

public class PlaceholderActivity extends AppCompatActivity {
    private static final String TAG = "PlaceholderActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);

        for (int i = 0; i < 7; i++) {
            Log.d(TAG, "onCreate: " + AllCards.getInstance().drawCard());
        }
    }
}