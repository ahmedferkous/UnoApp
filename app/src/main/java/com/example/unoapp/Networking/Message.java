package com.example.unoapp.Networking;

public class Message {
    private String message_type;
    private String message;

    public Message(String message_type, String message) {
        this.message_type = message_type;
        this.message = message;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
