package com.example.unoapp.CardFiles;

import android.util.Log;

import java.util.Collections;
import java.util.Stack;

// TODO: 25/05/2021 Maybe not static for other games? Explore this. Perhaps add a clear deck to fix? 
// TODO: 25/05/2021 Using p2p, sync two players deck cards? 
public class AllCards {
    private static final String TAG = "Deck";
    private static AllCards instance;
    private static Stack<CardModel> cardsDeck;
    private static Stack<CardModel> placedCards;

    private AllCards() {
        initCards();
    }

    public static AllCards getInstance() {
        if (null == instance) {
            instance = new AllCards();
        }
        return instance;
    }

    private void initCards() {
        cardsDeck = new Stack<>();

        for (int i = 0; i <=9; i++) {
            String str_i = String.valueOf(i);
            cardsDeck.push(new CardModel(CardModel.COLOR_GREEN, str_i, CardModel.TYPE_NUMBER));
            cardsDeck.push(new CardModel(CardModel.COLOR_RED, str_i, CardModel.TYPE_NUMBER));
            cardsDeck.push(new CardModel(CardModel.COLOR_YELLOW, str_i, CardModel.TYPE_NUMBER));
            cardsDeck.push(new CardModel(CardModel.COLOR_BLUE, str_i, CardModel.TYPE_NUMBER));
            if (i != 0) {
                cardsDeck.push(new CardModel(CardModel.COLOR_GREEN, str_i, CardModel.TYPE_NUMBER));
                cardsDeck.push(new CardModel(CardModel.COLOR_RED, str_i, CardModel.TYPE_NUMBER));
                cardsDeck.push(new CardModel(CardModel.COLOR_YELLOW, str_i, CardModel.TYPE_NUMBER));
                cardsDeck.push(new CardModel(CardModel.COLOR_BLUE, str_i, CardModel.TYPE_NUMBER));
            }
        }

        for (int i = 0; i < 2; i++) {
            cardsDeck.push(new CardModel(CardModel.COLOR_RED, CardModel.TYPE_PLUS_TWO));
            cardsDeck.push(new CardModel(CardModel.COLOR_YELLOW, CardModel.TYPE_PLUS_TWO));
            cardsDeck.push(new CardModel(CardModel.COLOR_BLUE, CardModel.TYPE_PLUS_TWO));
            cardsDeck.push(new CardModel(CardModel.COLOR_GREEN, CardModel.TYPE_PLUS_TWO));

            cardsDeck.push(new CardModel(CardModel.COLOR_RED, CardModel.TYPE_REVERSE));
            cardsDeck.push(new CardModel(CardModel.COLOR_YELLOW, CardModel.TYPE_REVERSE));
            cardsDeck.push(new CardModel(CardModel.COLOR_BLUE, CardModel.TYPE_REVERSE));
            cardsDeck.push(new CardModel(CardModel.COLOR_GREEN, CardModel.TYPE_REVERSE));

            cardsDeck.push(new CardModel(CardModel.COLOR_RED, CardModel.TYPE_SKIP));
            cardsDeck.push(new CardModel(CardModel.COLOR_YELLOW, CardModel.TYPE_SKIP));
            cardsDeck.push(new CardModel(CardModel.COLOR_BLUE, CardModel.TYPE_SKIP));
            cardsDeck.push(new CardModel(CardModel.COLOR_GREEN, CardModel.TYPE_SKIP));
        }

        for (int i = 0; i < 4; i++) {
            cardsDeck.push(new CardModel(CardModel.TYPE_COLOR_SWITCH));
            cardsDeck.push(new CardModel(CardModel.TYPE_PLUS_FOUR));
        }
       shuffleDeck();
    }

    public CardModel drawCard() {
        if (cardsDeck.size() == 0) {
            cardsDeck = placedCards;
            placedCards = new Stack<>();
            shuffleDeck();
        }
        return cardsDeck.pop();
    }

    // TODO: 25/05/2021 outside check to ensure it is a legal move
    public void placeCard(CardModel card) {
        placedCards.push(card);
    }

    public void shuffleDeck() {
        Collections.shuffle(cardsDeck);
    }

    /*4 debugging purposes only
    public String toString() {
        String b = "";
        for (CardModel c: cardsDeck) {
            Log.d(TAG, "toString: " + c.toString());
        }
        return b;
    }

     */

    public int size() {
        return cardsDeck.size();
    }

}
