package com.example.unoapp.CardFiles;

public class CardModel {
    public static final String COLOR_RED = "color_red";
    public static final String COLOR_YELLOW = "color_yellow";
    public static final String COLOR_BLUE = "color_blue";
    public static final String COLOR_GREEN = "color_green";

    public static final String TYPE_NUMBER = "type_number";
    public static final String TYPE_PLUS_TWO = "type_plus_two";
    public static final String TYPE_REVERSE = "type_reverse";
    public static final String TYPE_SKIP = "type_skip";
    public static final String TYPE_COLOR_SWITCH = "type_color_switch";
    public static final String TYPE_PLUS_FOUR = "type_plus_four";
    public static final String TYPE_BACK = "back_card";
    public static final String TYPE_REVEAL = "type_reveal";
    public static final String TYPE_LIFO = "type_last_in_first_out";
    public static final String TYPE_DEFLECT = "type_deflect";
    public static final String TYPE_DISCARD_COLOR = "type_discard_color";
    public static final String TYPE_REDRAW = "type_redraw";
    public static final String TYPE_TRADE_HANDS = "type_trade_hands";
    public static final String TYPE_CANCELLED = "type_cancelled";

    public static final String NUMBER_ZERO = "0";
    public static final String NUMBER_ONE = "1";
    public static final String NUMBER_TWO = "2";
    public static final String NUMBER_THREE = "3";
    public static final String NUMBER_FOUR = "4";
    public static final String NUMBER_FIVE = "5";
    public static final String NUMBER_SIX = "6";
    public static final String NUMBER_SEVEN = "7";
    public static final String NUMBER_EIGHT = "8";
    public static final String NUMBER_NINE = "9";

    private String color;
    private String number;
    private String type;
    private boolean playable = false;

    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    public CardModel() {
    }

    public CardModel(String color, String number, String type) {
        this.color = color;
        this.number = number;
        this.type = type;
    }

    public CardModel(String color, String type) {
        this.color = color;
        this.type = type;
    }

    public CardModel(String type) {
        this.type = type;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CardModel{" +
                "color='" + color + '\'' +
                ", number=" + number +
                ", type='" + type + '\'' +
                '}';
    }
}

