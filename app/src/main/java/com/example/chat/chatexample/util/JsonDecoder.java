package com.example.chat.chatexample.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.example.chat.chatexample.util.JsonEncoder.MESSAGE_CONTENT;
import static com.example.chat.chatexample.util.JsonEncoder.MESSAGE_TIMESTAMP;
import static com.example.chat.chatexample.util.JsonEncoder.USER_NAME;

public class JsonDecoder {

    JSONObject result;

    public JsonDecoder(String data) {
        JSONParser parser = new JSONParser();
        try {
            result = (JSONObject) parser.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        return (String) result.get(MESSAGE_CONTENT);
    }

    public String getUserName() {
        return (String) result.get(USER_NAME);
    }

    public Long getTimestamp() {
        return (Long) result.get(MESSAGE_TIMESTAMP);
    }
}
