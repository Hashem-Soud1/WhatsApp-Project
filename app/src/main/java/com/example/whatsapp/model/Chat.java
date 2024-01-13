package com.example.whatsapp.model;

import android.util.Log;

public class Chat {

    public String msg;
    private String sender;
    private String receiver;




    public Chat (String msg,String receiver,String sender) {
        this.msg = msg;
        this.receiver = receiver;
        this.sender = sender;
    }

    public Chat() {}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }
}
