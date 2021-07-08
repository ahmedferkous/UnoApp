package com.example.unoapp.CardFiles;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
    private static Random rand = new Random();

    //function to get a random number from 0, to num
    private static int randomNum(int num) {
        return rand.nextInt(num);
    }

    // based on a random number from 0 to 4 (not including 4), get the color
    private static String getColor() {
        switch (randomNum(4)) {
            case 0:
                return CardModel.COLOR_RED;
            case 1:
                return CardModel.COLOR_YELLOW;
            case 2:
                return CardModel.COLOR_BLUE;
            case 3:
                return CardModel.COLOR_GREEN;
            default:
                break;
        }
        return null;
    }

    private static String getNumber() {
        switch (randomNum(10)) {
            case 0:
                return CardModel.NUMBER_ZERO;
            case 1:
                return CardModel.NUMBER_ONE;
            case 2:
                return CardModel.NUMBER_TWO;
            case 3:
                return CardModel.NUMBER_THREE;
            case 4:
                return CardModel.NUMBER_FOUR;
            case 5:
                return CardModel.NUMBER_FIVE;
            case 6:
                return CardModel.NUMBER_SIX;
            case 7:
                return CardModel.NUMBER_SEVEN;
            case 8:
                return CardModel.NUMBER_EIGHT;
            case 9:
                return CardModel.NUMBER_NINE;
            default:
                break;
        }
        return null;
    }

    private static String getSpecialType() {
        switch (randomNum(3)) {
            case 0:
                return CardModel.TYPE_PLUS_TWO;
            case 1:
                return CardModel.TYPE_REVERSE;
            case 2:
                return CardModel.TYPE_SKIP;
            default:
                break;
        }
        return null;
    }

    private static String getWildCardType() {
        switch (randomNum(2)) {
            case 0:
                return CardModel.TYPE_COLOR_SWITCH;
            case 1:
                return CardModel.TYPE_PLUS_FOUR;
            default:
                break;
        }
        return null;
    }

    public static CardModel drawCard() {
        int randomNumber = randomNum(108);
        if (randomNumber >= 32 && randomNumber <= 108) {
            return new CardModel(getColor(), getNumber(), CardModel.TYPE_NUMBER);

        } else if (randomNumber >= 9 && randomNumber <= 31) {
            return new CardModel(getColor(), getSpecialType());

        } else if (randomNumber >= 0 && randomNumber <= 8) {
            return new CardModel(getWildCardType());
        }
        return null;
    }


}
