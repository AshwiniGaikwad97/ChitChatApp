package com.itkida.chitchatapp;

public class ChatMessage {
    private String messageText;
    private String senderId;
    private long timestamp;

    public ChatMessage() {
        // Empty constructor required for Firestore
    }

    public ChatMessage(String messageText, String senderId, long timestamp) {
        this.messageText = messageText;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

