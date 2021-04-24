package edu.neu.madcourse.numadsp21finalproject.chat;

import com.google.firebase.Timestamp;

public class ChatItem {

    private String chatId;
    private String senderId;
    private String senderName;
    private String receiverName;
    private String receiverId;
    private String message;
    private Timestamp time;

    public ChatItem(String senderId, String senderName,
                    String receiverId, String receiverName,
                    String body, Timestamp time) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.message = body;
        this.time = time;

    }
    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getTimeAsString() {
        return time.toDate().toString();
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId(String chatId) {
        return chatId;
    }
}
