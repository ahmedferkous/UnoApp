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

    private String color;
    private int number;
    private String type;

    public CardModel() {
    }

    public CardModel(String color, int number, String type) {
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

    public void setNumber(int number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public int getNumber() {
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

