package com.example.chat.chatexample.util;

import org.json.simple.JSONObject;

public class JsonEncoder {

    public static final String ID = "id";
    public static final String USER_NAME = "userName";
    public static final String MESSAGE_TIMESTAMP = "timestamp";
    public static final String MESSAGE_CONTENT = "message";

    private JSONObject jo = new JSONObject();

    public JsonEncoder() {
    }

    public void setMessageContent(String content) {
        jo.put(MESSAGE_CONTENT, content);
    }

    public void setUserName(String userName) {
        jo.put(USER_NAME, userName);
    }

    public void setMessageTimestamp(Long timestamp) {
        jo.put(MESSAGE_TIMESTAMP, timestamp);
    }

    public void setId(String id) {
        jo.put(ID, id);
    }

    @Override
    public String toString() {
        return jo.toJSONString();
    }
}
