package com.example.unoapp.GameLogic;

import android.util.Log;

import com.example.unoapp.CardFiles.CardModel;
import com.example.unoapp.CardFiles.Deck;
import com.example.unoapp.Networking.ClientHolder;
import com.example.unoapp.Networking.Message;
import com.example.unoapp.Networking.NetworkWrapper;
import com.example.unoapp.Networking.ServerHolder;
import com.example.unoapp.Networking.UnoClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Stack;

public class PlayerInstance {
    private static final String TAG = "PlayerInstance";

    public interface onServerStatus {
        void onDisconnection(boolean isGameRunning);
    }

    private final ClientHolder client;
    private final onServerStatus onServerStatus;
    private ArrayList<Players> playersList;
    private Stack<CardModel> placedCards;
    public static Type cardType = new TypeToken<Stack<CardModel>>() {
    }.getType();
    public static Type playersType = new TypeToken<ArrayList<Players>>() {
    }.getType();
    private ServerHolder.LobbyNotification lobbyNotification;
    private ArrayList<CardModel> hand;
    private final Gson gson = new Gson();
    private boolean gameBegun = false;

    public PlayerInstance(ClientHolder client, ServerHolder.LobbyNotification lobbyNotification) {
        this.client = client;
        onServerStatus = client;
        this.lobbyNotification = lobbyNotification;
        playersList = new ArrayList<>();
        placedCards = new Stack<>();
        beginReceiving();
    }

    private ArrayList<String> getPlayers(ArrayList<Players> players) {
        ArrayList<String> playersArr = new ArrayList<>();
        for (Players player :
                players) {
            playersArr.add(player.getNickName());
        }
        return playersArr;
    }

    // Only IN!
    private void beginReceiving() {
        new Thread() {
            boolean serverConnection = true;

            // TODO: 8/07/2021 implement functionality with networkwrapper timings (send confirmation to server when players are all ready to begin)
            @Override
            public void run() {
                while (serverConnection) {
                    try {
                        Message receivedMessage = ServerHolder.decipherMessage(client.getIn());

                        switch (receivedMessage.getMessage_type()) {
                            case ServerHolder.CONNECTION_TEST:
                                Log.d(TAG, "run: Server tested connection");
                                ServerHolder.send(new Message(ServerHolder.CONNECTION_TEST, null), client.getOut());
                                break;
                            case ServerHolder.CONNECTION_OF_CLIENT:
                            case ServerHolder.DISCONNECTION_OF_CLIENT:
                                playersList = gson.fromJson(receivedMessage.getMessage(), playersType);
                                for (Players p : playersList) {
                                    if (p.getUser_id() == client.getUnique_id()) {
                                        playersList.remove(p);
                                    }
                                }
                                if (!gameBegun) {
                                    lobbyNotification.providedPlayerDetailsResult(getPlayers(playersList), false);
                                } else {
                                    NetworkWrapper.getManager().getUpdateCallback().setPlayers(playersList);
                                }
                                Log.d(TAG, "run: ???????");
                                break;
                            case GameInstance.UPDATE_PLAYERS:
                                NetworkWrapper.getManager().getUpdateCallback().reversalChange();
                                break;
                            case GameInstance.GAME_BEGIN:
                                hand = initHand();
                                placedCards = gson.fromJson(receivedMessage.getMessage(), cardType);
                                lobbyNotification.gameBegun(playersList, hand, placedCards.peek());
                                gameBegun = true;
                                Log.d(TAG, "run: Game begun, stack received: " + placedCards.peek());

                                break;
                            case GameInstance.UPDATE_STACK:
                                placedCards = gson.fromJson(receivedMessage.getMessage(), cardType);
                                NetworkWrapper.getManager().getUpdateCallback().setPlacedCard(placedCards.peek());
                                Log.d(TAG, "run: Received Stack: " + placedCards.peek());
                                break;
                            case GameInstance.COLOR_CHANGE_EVENT:
                                String color = receivedMessage.getMessage();
                                NetworkWrapper.getManager().getUpdateCallback().colorChange(color);
                                break;
                            case GameInstance.STACKED_RESULT:
                                break;
                            case GameInstance.PLAYER_TURN:
                                CardModel cardOnStack = placedCards.peek();

                                Log.d(TAG, "run: It's the players turn");

                                placedCards.push(new CardModel(CardModel.COLOR_BLUE, CardModel.TYPE_SKIP));
                                ServerHolder.send(new Message(GameInstance.TURN_FINISHED, gson.toJson(placedCards)), client.getOut());
                                NetworkWrapper.getManager().getUpdateCallback().setHand(hand);
                                break;
                            default:
                                break;
                        }
                    } catch (IOException e) {
                        serverConnection = false;
                        onServerStatus.onDisconnection(gameBegun);
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

    public static ArrayList<CardModel> initHand() {
        ArrayList<CardModel> newHand = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            newHand.add(Deck.drawCard());
        }
        return newHand;
    }


}
