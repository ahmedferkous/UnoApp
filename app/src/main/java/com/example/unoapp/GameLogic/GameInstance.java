package com.example.unoapp.GameLogic;

import android.util.Log;

import com.example.unoapp.CardFiles.CardModel;
import com.example.unoapp.CardFiles.Deck;
import com.example.unoapp.Networking.ClientHolder;
import com.example.unoapp.Networking.Message;
import com.example.unoapp.Networking.ServerHolder;
import com.example.unoapp.Networking.UnoClient;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class GameInstance {
    private static final String TAG = "GameInstance";
    public interface OnUserDisconnect {
        void onUserDisconnectResult(UnoClient player);
    }
    public static final String GAME_BEGIN = "beginning_game";
    public static final String UPDATE_STACK = "update_card_stack";
    public static final String UPDATE_PLAYERS = "update_players_list";
    public static final String PLAYER_TURN = "players_turn";
    public static final String CLAIMED_WINNER = "winner_of_game";
    public static final String STACKED_RESULT = "player_stacked_plus";
    public static final String TURN_FINISHED = "finished_turn";

    private final ServerHolder server;
    private static OnUserDisconnect onUserDisconnect;
    private static Stack<CardModel> cardStack;
    private ArrayList<UnoClient> players;
    private boolean gameRunning;
    private static Gson gson = new Gson();

    public GameInstance(ServerHolder server) {
        this.server = server;
        onUserDisconnect = this.server;
    }


    private CardModel drawOrdinaryCard() {
        /*
        CardModel retrievedCard = Deck.drawCard();
        while (!retrievedCard.getType().equals(CardModel.TYPE_NUMBER)) {
            retrievedCard = Deck.drawCard();
        }
        return retrievedCard;

         */
        return new CardModel(CardModel.COLOR_RED, CardModel.NUMBER_ONE, CardModel.TYPE_NUMBER);
    }

    public void beginGame() {
        cardStack = new Stack<>();
        cardStack.push(drawOrdinaryCard());
        gameRunning = true;
        players = server.getClientList();
        new LiveGame().start();
    }

    private ArrayList<String> getListOfPlayers() {
        ArrayList<String> listOfPlayers = new ArrayList<>();

        for (UnoClient clients : players) {
            listOfPlayers.add(clients.getNickname());
        }

        return listOfPlayers;
    }

    public void endGame() {
        gameRunning = false;
    }

    private static class LivePlayer extends Thread {
        public interface onCompleteTurn {
            void onCompletedTurn(Stack<CardModel> cards, Message recMessage);
        }
        private final UnoClient player;
        private final onCompleteTurn onCompleteTurn;
        private String type;

        public LivePlayer(UnoClient player, LiveGame liveGame) {
            this.player = player;
            onCompleteTurn = liveGame;
        }

        public LivePlayer(UnoClient player, LiveGame liveGame, String type) {
            this.player = player;
            this.type = type;
            onCompleteTurn = liveGame;
        }

        @Override
        public void run() {
            try {
                DataInputStream in = new DataInputStream(player.getSocket().getInputStream());
                DataOutputStream out = new DataOutputStream(player.getSocket().getOutputStream());
                ServerHolder.send(new Message(PLAYER_TURN, gson.toJson(cardStack)), out);

                Message receivedMessage = ServerHolder.decipherMessage(in);
                switch (receivedMessage.getMessage_type()) {
                    case TURN_FINISHED:
                        Stack<CardModel> cards = gson.fromJson(receivedMessage.getMessage(), PlayerInstance.cardType);
                        onCompleteTurn.onCompletedTurn(cards, receivedMessage);
                        break;
                    case CLAIMED_WINNER:

                        break;
                    default:
                        break;
                }
            } catch(IOException e) {
                onUserDisconnect.onUserDisconnectResult(player);
            }
        }
    }

    private class LiveGame extends Thread implements LivePlayer.onCompleteTurn {
        private int playerIndexTurn = 0;
        @Override
        public void onCompletedTurn(Stack<CardModel> cards, Message recMessage) {
            // TODO: 14/06/2021 Signal player drew a card if stack size didn't change
            // TODO: 14/06/2021 Come up with solutions for dealing with 'stacking' as well as color-switches
            if (!(cardStack.size() == cards.size())) {
                cardStack = cards;
                server.broadcast(new Message(UPDATE_STACK, gson.toJson(cards)));

                CardModel cardOnTop = cardStack.peek();

                switch(cardOnTop.getType()) {
                    case CardModel.TYPE_REVERSE:
                        Collections.reverse(players);
                        if (playerIndexTurn == players.size()-1) {
                            playerIndexTurn = 0;
                        } else {
                            playerIndexTurn++;
                        }
                        server.broadcast(new Message(UPDATE_PLAYERS, gson.toJson(getListOfPlayers())));
                        break;
                    case CardModel.TYPE_SKIP:
                        if (playerIndexTurn == players.size()-1) {
                            playerIndexTurn = 1;
                        } else if (playerIndexTurn == players.size()-2) {
                            playerIndexTurn = 0;
                        } else {
                            playerIndexTurn += 2;
                        }
                        break;
                    default:
                        if (playerIndexTurn == players.size()-1) {
                            playerIndexTurn = 0;
                        } else {
                            playerIndexTurn++;
                        }
                        break;
                }
                Log.d(TAG, "onCompletedTurn: Server received card: " + cardOnTop);

            }
            new LivePlayer(players.get(playerIndexTurn), this).start();

        }
        @Override
        public void run() {
            Collections.shuffle(players);
            server.broadcast(new Message(UPDATE_PLAYERS, gson.toJson(getListOfPlayers())));
            server.broadcast(new Message(GAME_BEGIN, gson.toJson(cardStack)));

            // TODO: 14/06/2021 Fix
            //while(gameRunning) {
            new LivePlayer(players.get(playerIndexTurn), this).start();
            //}
        }

    }
}
