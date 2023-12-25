package com.bms.pathogold_bms.model.chat;

import java.util.Objects;

public class Messages {

    String message;
    String senderId;
    long timestamp;
    String currenttime;

    public Messages() {
    }

    public Messages(String message, String senderId, long timestamp, String currenttime) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.currenttime = currenttime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Messages)) return false;
        Messages messages = (Messages) o;
        return timestamp == messages.timestamp && Objects.equals(message, messages.message) && Objects.equals(senderId, messages.senderId) && Objects.equals(currenttime, messages.currenttime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, senderId, timestamp, currenttime);
    }
}
