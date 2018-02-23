package com.baristapushsdk.entity;

import java.util.List;

/**
 * Created by Trung Kien on 10/6/2015.
 */
public class ListMessage {
    private String error;
    private List<Message> list_message;

    public List<Message> getListMessage() {
        return list_message;
    }

    public void setListMessage(List<Message> listMessage) {
        this.list_message = listMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
