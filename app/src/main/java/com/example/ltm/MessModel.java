package com.example.ltm;

public class MessModel {
    static String SENT_BY_ME = "me";
    static String SENT_BY_BOT = "bot";

    String mess;
    String sentBy;

    public MessModel(String mess, String sentBy) {
        this.mess = mess;
        this.sentBy = sentBy;
    }

    public String getMess() {
        return mess;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setMess(String mess){
        this.mess = mess;
    }

}
