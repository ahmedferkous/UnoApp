package com.example.unoapp.GameLogic;

import android.util.Log;

import com.example.unoapp.CardFiles.CardModel;
import com.example.unoapp.CardFiles.Deck;
import com.example.unoapp.Networking.ClientHolder;
import com.example.unoapp.Networking.Message;
import com.example.unoapp.Networking.ServerHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Stack;

public class PlayerInstance {
    private static final String TAG = "PlayerInstance";

    public interface onServerDisconnection {
        void onDisconnection();
    }

    private final ClientHolder client;
    private final onServerDisconnection onServerDisconnection;
    private ArrayList<String> playersList;
    private Stack<CardModel> placedCards;
    public static Type cardType = new TypeToken<Stack<CardModel>>() {}.getType();
    private ArrayList<CardModel> hand;
    private final Gson gson = new Gson();

    public PlayerInstance(ClientHolder client) {
        this.client = client;
        onServerDisconnection = client;
        playersList = new ArrayList<>();
        placedCards = new Stack<>();
        beginReceiving();
    }

    // Only IN!
    private void beginReceiving() {
        new Thread() {
            boolean serverConnection = true;

            @Override
            public void run() {
                while (serverConnection) {
                    try {
                        Message receivedMessage = ServerHolder.decipherMessage(client.getIn());

                        switch (receivedMessage.getMessage_type()) {
                            case ServerHolder.CONNECTION_OF_CLIENT:
                                playersList.add(receivedMessage.getMessage());
                                break;
                            case ServerHolder.DISCONNECTION_OF_CLIENT:
                                playersList.remove(receivedMessage.getMessage());
                                break;
                            case GameInstance.UPDATE_PLAYERS:
                                // TODO: 21/06/2021 update of reversal
                                break;
                            case GameInstance.GAME_BEGIN:
                                placedCards = gson.fromJson(receivedMessage.getMessage(), cardType);
                                Log.d(TAG, "run: Game begun, stack received: " + placedCards.peek());
                                initHand();
                                break;
                            case GameInstance.UPDATE_STACK:
                                placedCards = gson.fromJson(receivedMessage.getMessage(), cardType);
                                Log.d(TAG, "run: Received Stack: " + placedCards.peek());
                                break;
                            case GameInstance.COLOR_CHANGE_EVENT:
                                String color = receivedMessage.getMessage();
                                // TODO: 26/06/2021 Update color 
                                break;
                            case GameInstance.STACKED_RESULT:
                                break;
                            case GameInstance.PLAYER_TURN:
                                CardModel cardOnStack = placedCards.peek();

                                Log.d(TAG, "run: It's the players turn");

                                placedCards.push(new CardModel(CardModel.COLOR_BLUE, CardModel.TYPE_SKIP));
                                ServerHolder.send(new Message(GameInstance.TURN_FINISHED, gson.toJson(placedCards)), client.getOut());
                                break;
                            default:
                                break;
                        }
                    } catch (IOException e) {
                        serverConnection = false;
                        onServerDisconnection.onDisconnection();
                    }
                }
            }
        }.start();
    }

    private int cardsStacked(int number) {
        int numberOfCardsToDraw = 0;
        Stack<CardModel> cards = placedCards;

        for (int i = 0; i < number; i++) {
            CardModel peekedCard = cards.pop();
            if (peekedCard.getType().equals(CardModel.TYPE_PLUS_TWO)) {
                numberOfCardsToDraw += 2;
            } else if (peekedCard.getType().equals(CardModel.TYPE_PLUS_FOUR)) {
                numberOfCardsToDraw += 4;
            }
        }
        return numberOfCardsToDraw;

    }

    private boolean allLegal(CardModel stackedCard) {
        for (CardModel c : hand) {
            if (isLegal(stackedCard, c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLegal(CardModel stackedCard, CardModel cardToPlace) {
        if (cardToPlace.getType().equals(CardModel.TYPE_PLUS_FOUR) || cardToPlace.getType().equals(CardModel.TYPE_COLOR_SWITCH)) {
            return true;
        }
        if (cardToPlace.getColor().equals(stackedCard.getColor())) {
            return true;
        }
        if (cardToPlace.getNumber().equals(stackedCard.getNumber())) {
            return true;
        }
        return false;
    }

    private void initHand() {
        hand = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            hand.add(Deck.drawCard());
        }
    }


}
