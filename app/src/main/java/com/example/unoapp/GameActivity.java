package com.example.unoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unoapp.CardFiles.CardModel;
import com.example.unoapp.CardFiles.CardsAdapter;
import com.example.unoapp.CardFiles.Deck;
import com.example.unoapp.GameLogic.Players;
import com.example.unoapp.GameLogic.PlayersAdapter;
import com.example.unoapp.Networking.NetworkWrapper;
import com.example.unoapp.Networking.UnoClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements NetworkWrapper.UpdateCallback {
    public static final String ANTI = "anti";
    public static final String CLOCK = "clock";
    private int seconds;

    @Override
    public void setPlayers(ArrayList<Players> players) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playersAdapter.setPlayers(players);
            }
        });
    }

    @Override
    public void setHand(ArrayList<CardModel> hand) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cardsAdapter.setCards(hand);
            }
        });
    }

    @Override
    public void setPlacedCard(CardModel placedCard) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgViewPlacedCard.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, CardsAdapter.getImage(placedCard)));
            }
        });
    }

    // TODO: 8/07/2021 fix this ugly shit 
    @Override
    public void reversalChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] imageInfo = imgViewRotation.getTooltipText().toString().split(",");

                switch (imageInfo[0]) {
                    case ANTI:
                        switch (imageInfo[1]) {
                            case CardModel.COLOR_RED:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.clockwise_red));
                                imgViewRotation.setAnimation(aniRotateClk);
                                imgViewRotation.setTooltipText(CLOCK + "," + CardModel.COLOR_RED);
                                break;
                            case CardModel.COLOR_YELLOW:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.clockwise_yellow));
                                imgViewRotation.setAnimation(aniRotateClk);
                                imgViewRotation.setTooltipText(CLOCK + "," + CardModel.COLOR_YELLOW);
                                break;
                            case CardModel.COLOR_BLUE:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.clockwise_blue));
                                imgViewRotation.setAnimation(aniRotateClk);
                                imgViewRotation.setTooltipText(CLOCK + "," + CardModel.COLOR_BLUE);
                                break;
                            case CardModel.COLOR_GREEN:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.clockwise_green));
                                imgViewRotation.setAnimation(aniRotateClk);
                                imgViewRotation.setTooltipText(CLOCK + "," + CardModel.COLOR_GREEN);
                                break;
                            default:
                                break;
                        }
                        break;
                    case CLOCK:
                        switch (imageInfo[1]) {
                            case CardModel.COLOR_RED:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.anticlockwise_red));
                                imgViewRotation.setAnimation(aniRotateAntiClk);
                                imgViewRotation.setTooltipText(ANTI + "," + CardModel.COLOR_RED);
                                break;
                            case CardModel.COLOR_YELLOW:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.anticlockwise_yellow));
                                imgViewRotation.setAnimation(aniRotateAntiClk);
                                imgViewRotation.setTooltipText(ANTI + "," + CardModel.COLOR_YELLOW);
                                break;
                            case CardModel.COLOR_BLUE:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.anticlockwise_blue));
                                imgViewRotation.setAnimation(aniRotateAntiClk);
                                imgViewRotation.setTooltipText(ANTI + "," + CardModel.COLOR_BLUE);
                                break;
                            case CardModel.COLOR_GREEN:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.anticlockwise_green));
                                imgViewRotation.setAnimation(aniRotateAntiClk);
                                imgViewRotation.setTooltipText(ANTI + "," + CardModel.COLOR_GREEN);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void colorChange(String color) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] imageInfo = imgViewRotation.getTooltipText().toString().split(",");

                switch (imageInfo[0]) {
                    case ANTI:
                        switch (color) {
                            case CardModel.COLOR_RED:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.anticlockwise_red));
                                imgViewRotation.setAnimation(aniRotateAntiClk);
                                imgViewRotation.setTooltipText(ANTI + "," + CardModel.COLOR_RED);
                                break;
                            case CardModel.COLOR_YELLOW:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.anticlockwise_yellow));
                                imgViewRotation.setAnimation(aniRotateAntiClk);
                                imgViewRotation.setTooltipText(ANTI + "," + CardModel.COLOR_YELLOW);
                                break;
                            case CardModel.COLOR_BLUE:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.anticlockwise_blue));
                                imgViewRotation.setAnimation(aniRotateAntiClk);
                                imgViewRotation.setTooltipText(ANTI + "," + CardModel.COLOR_BLUE);
                                break;
                            case CardModel.COLOR_GREEN:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.anticlockwise_green));
                                imgViewRotation.setAnimation(aniRotateAntiClk);
                                imgViewRotation.setTooltipText(ANTI + "," + CardModel.COLOR_GREEN);
                                break;
                            default:
                                break;
                        }
                        break;
                    case CLOCK:
                        switch (color) {
                            case CardModel.COLOR_RED:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.clockwise_red));
                                imgViewRotation.setAnimation(aniRotateClk);
                                imgViewRotation.setTooltipText(CLOCK + "," + CardModel.COLOR_RED);
                                break;
                            case CardModel.COLOR_YELLOW:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.clockwise_yellow));
                                imgViewRotation.setAnimation(aniRotateClk);
                                imgViewRotation.setTooltipText(CLOCK + "," + CardModel.COLOR_YELLOW);
                                break;
                            case CardModel.COLOR_BLUE:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.clockwise_blue));
                                imgViewRotation.setAnimation(aniRotateClk);
                                imgViewRotation.setTooltipText(CLOCK + "," + CardModel.COLOR_BLUE);
                                break;
                            case CardModel.COLOR_GREEN:
                                imgViewRotation.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, R.drawable.clockwise_green));
                                imgViewRotation.setAnimation(aniRotateClk);
                                imgViewRotation.setTooltipText(CLOCK + "," + CardModel.COLOR_GREEN);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private static final String TAG = "PlaceholderActivity";
    private ImageView imgViewStackCards, imgViewPlacedCard, imgViewRotation;
    private RecyclerView recViewPlayers, recViewCards;
    private CardsAdapter cardsAdapter;
    private PlayersAdapter playersAdapter;
    private TextView txtTimeElapsed;
    private Animation aniRotateClk, aniRotateAntiClk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initViews();

        NetworkWrapper.getManager().setContext(this);

        Intent incomingIntent = getIntent();
        if (incomingIntent != null) {
            Gson gson = new Gson();
            ArrayList<Players> players = gson.fromJson(incomingIntent.getStringExtra(ServerBrowsing.PLAYERS), new TypeToken<ArrayList<Players>>() {
            }.getType());
            ArrayList<CardModel> hand = gson.fromJson(incomingIntent.getStringExtra(ServerBrowsing.HAND), new TypeToken<ArrayList<CardModel>>() {
            }.getType());
            CardModel firstCard = gson.fromJson(incomingIntent.getStringExtra(ServerBrowsing.FIRST_CARD), new TypeToken<CardModel>() {
            }.getType());
            Log.d(TAG, "onCreate: players " + players);
            Log.d(TAG, "onCreate: hand " + hand);
            Log.d(TAG, "onCreate: first card " + firstCard);
            if (players != null && hand != null && firstCard != null) {
                playersAdapter.setPlayers(players);
                cardsAdapter.setCards(hand);
                imgViewPlacedCard.setImageDrawable(AppCompatResources.getDrawable(GameActivity.this, CardsAdapter.getImage(firstCard)));
                colorChange(firstCard.getColor());
            }
        } else {
            Log.d(TAG, "onCreate: Nulled :(");
        }

        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
        }

        runTimer();
        

        /*
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

         */


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

    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs);

                txtTimeElapsed.setText("In-Game - " + time);
                seconds++;

                handler.postDelayed(this, 1000);
            }
        });
    }

    private void initViews() {
        imgViewStackCards = findViewById(R.id.imgViewStackCards);
        imgViewPlacedCard = findViewById(R.id.imgViewPlacedCard);
        imgViewRotation = findViewById(R.id.imgViewRotation);
        recViewPlayers = findViewById(R.id.recViewPlayers);
        recViewCards = findViewById(R.id.recViewCards);
        txtTimeElapsed = findViewById(R.id.txtTimeElapsed);
        aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        aniRotateAntiClk = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
        recViewCards.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recViewPlayers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        cardsAdapter = new CardsAdapter(this);
        playersAdapter = new PlayersAdapter(this);
        recViewCards.setAdapter(cardsAdapter);
        recViewPlayers.setAdapter(playersAdapter);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Exit Game?")
                .setMessage("Are you sure you would like to exit the game?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NetworkWrapper.getManager().disconnectFromGroup();
                        Intent serverBrowserIntent = new Intent(GameActivity.this, ServerBrowsing.class);
                        startActivity(serverBrowserIntent);
                    }
                });
        builder.create().show();
    }
}