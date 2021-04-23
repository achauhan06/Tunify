package edu.neu.madcourse.numadsp21finalproject.chat;

public class Chat {

    private String sender;
    private String receiver;
    private String sticker;

    public Chat(String sender, String receiver, String body) {
        this.sender = sender;
        this.receiver = receiver;
        this.sticker = body;

    }
    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSticker() {
        return sticker;
    }
}
