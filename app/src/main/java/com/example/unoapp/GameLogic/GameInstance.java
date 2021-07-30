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
    public static final String STACKED_NUMBER = "stacked_number";
    public static final String TURN_FINISHED = "finished_turn";
    public static final String HAND_SIZE = "hand_size";

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
        CardModel retrievedCard = Deck.drawCard();
        while (!retrievedCard.getType().equals(CardModel.TYPE_NUMBER)) {
            retrievedCard = Deck.drawCard();
        }
        return retrievedCard;
    }

    public void beginGame() {
        cardStack = new Stack<>();
        cardStack.push(drawOrdinaryCard());
        gameRunning = true;
        players = server.getClientList();
        new LiveGame().start();
    }

    public static ArrayList<String> getListOfPlayers(ArrayList<UnoClient> players) {
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
            void onCompletedTurn(Stack<CardModel> cards, boolean stackedEvent, int numberOfStackedCards);
            void onUncompletedTurn();
        }

        private final UnoClient player;
        private final onCompleteTurn onCompleteTurn;
        private Message message;

        public LivePlayer(UnoClient player, LiveGame liveGame) {
            this.player = player;
            onCompleteTurn = liveGame;
        }

        public LivePlayer(UnoClient player, LiveGame liveGame, Message message) {
            this.player = player;
            this.message = message;
            onCompleteTurn = liveGame;
        }

        @Override
        public void run() {
            try {
                DataInputStream in = new DataInputStream(player.getSocket().getInputStream());
                DataOutputStream out = new DataOutputStream(player.getSocket().getOutputStream());

                if (message != null) {
                    ServerHolder.send(message, out);
                } else {
                    ServerHolder.send(new Message(PLAYER_TURN, null), out);
                }

                Message receivedMessage = ServerHolder.decipherMessage(in);
                Stack<CardModel> cards = gson.fromJson(receivedMessage.getMessage(), PlayerInstance.cardType);

                switch (receivedMessage.getMessage_type()) {
                    case TURN_FINISHED:
                        onCompleteTurn.onCompletedTurn(cards, false, 0);
                        break;
                    case STACKED_RESULT:
                        Message additionalMessage = ServerHolder.decipherMessage(in);
                        if (additionalMessage.getMessage_type().equals(STACKED_NUMBER)) {
                            onCompleteTurn.onCompletedTurn(cards, true, Integer.parseInt(additionalMessage.getMessage()));
                        }
                    case CLAIMED_WINNER:

                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                onUserDisconnect.onUserDisconnectResult(player);
                onCompleteTurn.onUncompletedTurn();
            }
        }
    }

    private class LiveGame extends Thread implements LivePlayer.onCompleteTurn {
        private int playerIndexTurn = 0;

        @Override
        public void onUncompletedTurn() {
            if (gameRunning) {
                players = server.getClientList();
                if (players.size() > 1) {
                    if (playerIndexTurn >= players.size() - 1) {
                        playerIndexTurn = 0;
                    } else {
                        playerIndexTurn++;
                    }
                    new LivePlayer(players.get(playerIndexTurn), this).start();
                } else {
                    endGame();
                }
            }
        }

        @Override
        public void onCompletedTurn(Stack<CardModel> cards, boolean stackedEvent, int numberOfStackedCards) {
            // TODO: 14/06/2021 Signal player drew a card if stack size didn't change (2 other players)
            // TODO: 14/06/2021 Come up with solutions for dealing with 'stacking' as well as color-switches
            if (gameRunning) {
                if (!(cardStack.size() == cards.size())) {
                    cardStack = cards;
                    server.broadcast(new Message(UPDATE_STACK, gson.toJson(cards)));

                    CardModel cardOnTop = cardStack.peek();

                    switch (cardOnTop.getType()) {
                        case CardModel.TYPE_SKIP:
                            if (playerIndexTurn == players.size() - 1) {
                                playerIndexTurn = 1;
                            } else if (playerIndexTurn == players.size() - 2) {
                                playerIndexTurn = 0;
                            } else {
                                playerIndexTurn += 2;
                            }
                            break;
                        case CardModel.TYPE_REVERSE:
                            Collections.reverse(players);
                            server.broadcast(new Message(UPDATE_PLAYERS, null));
                        default:
                            if (playerIndexTurn == players.size() - 1) {
                                playerIndexTurn = 0;
                            } else {
                                playerIndexTurn++;
                            }
                            break;
                    }
                }

                if (stackedEvent) {
                    new LivePlayer(players.get(playerIndexTurn), this, new Message(STACKED_RESULT, String.valueOf(numberOfStackedCards))).start();
                } else {
                    new LivePlayer(players.get(playerIndexTurn), this).start();
                }
            } else {
                Log.d(TAG, "onCompletedTurn: Game ended");
            }
        }

        @Override
        public void run() {
            server.broadcast(new Message(GAME_BEGIN, gson.toJson(cardStack)));
            ArrayList<CardModel> hand = PlayerInstance.initHand();

            ArrayList<Players> playersCopy = new ArrayList<>(server.getPlayers().size());
            for (Players p : server.getPlayers()) {
                if (!(p.getUser_id() == server.getServer_id())) {
                    playersCopy.add(p);
                }
            }
            server.getLobbyNotification().gameBegun(playersCopy, hand, cardStack.peek(), null);

            UnoClient player = null;
            for (int i = 0; i < players.size(); i++) {
                try {
                    player = players.get(i);
                    DataInputStream in = new DataInputStream(player.getSocket().getInputStream());
                    ServerHolder.decipherMessage(in);
                } catch (IOException e) {
                    onUserDisconnect.onUserDisconnectResult(player);
                    players = server.getClientList();
                    e.printStackTrace();
                }
            }

            new LivePlayer(players.get(playerIndexTurn), this).start();

            //}
        }

    }
}
