package com.example.unoapp.CardFiles;

import java.util.Collections;
import java.util.Stack;

// TODO: 25/05/2021 Maybe not static for other games? Explore this. Perhaps add a clear deck to fix? 
public class Deck {
    private static Deck instance;
    private static Stack<CardModel> cardsDeck;

    private Deck() {
        initCards();
    }

    public static Deck getInstance() {
        if (null == instance) {
            instance = new Deck();
        }
        return instance;
    }

    // TODO: 25/05/2021 ensure all cards are accounted 4 
    private void initCards() {
        cardsDeck = new Stack<>();

        for (int i = 0; i <=9; i++) {
            cardsDeck.push(new CardModel(CardModel.COLOR_GREEN, i, CardModel.TYPE_NUMBER));
            cardsDeck.push(new CardModel(CardModel.COLOR_RED, i, CardModel.TYPE_NUMBER));
            cardsDeck.push(new CardModel(CardModel.COLOR_YELLOW, i, CardModel.TYPE_NUMBER));
            cardsDeck.push(new CardModel(CardModel.COLOR_BLUE, i, CardModel.TYPE_NUMBER));
            if (i != 0) {
                cardsDeck.push(new CardModel(CardModel.COLOR_GREEN, i, CardModel.TYPE_NUMBER));
                cardsDeck.push(new CardModel(CardModel.COLOR_RED, i, CardModel.TYPE_NUMBER));
                cardsDeck.push(new CardModel(CardModel.COLOR_YELLOW, i, CardModel.TYPE_NUMBER));
                cardsDeck.push(new CardModel(CardModel.COLOR_BLUE, i, CardModel.TYPE_NUMBER));
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

    // TODO: 25/05/2021 Handle case when stack is empty
    public CardModel drawCard() {
        CardModel retrievedCard = cardsDeck.peek();
        cardsDeck.pop();
        return retrievedCard;
    }

    public void shuffleDeck() {
        Collections.shuffle(cardsDeck);
    }

    //4 debugging purposes only
    public String toString() {
        String b = "";
        for (CardModel c: cardsDeck) {
            b += " " + c.toString() + "\n\n\n";
        }
        return b;
    }

}
