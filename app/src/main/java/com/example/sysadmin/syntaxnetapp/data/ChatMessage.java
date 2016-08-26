package com.example.sysadmin.syntaxnetapp.data;

/**
 * Created by sysadmin on 26/7/16.
 */
public class ChatMessage {
    private String msg;
    private boolean isMe;
    public void setMsg(String msg){
        this.msg=msg;
    }
    public void setIsMe(boolean isMe){
        this.isMe=isMe;
    }
    public String getMsg(){
        return msg;
    }
    public boolean isItMe(){
        return isMe;
    }
}
