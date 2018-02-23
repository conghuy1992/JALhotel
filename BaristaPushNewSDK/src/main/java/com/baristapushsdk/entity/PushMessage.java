package com.baristapushsdk.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Trung Kien on 10/6/2015.
 */

public class PushMessage implements Serializable {

    public String pushMesageId;

    public String pushMessageTitle;

    public String pushMessageContent;

    public String pushMesageDate;

    public String pushMesageImageUrl;

    public String pushMesageWebUrl;

    public boolean pushMesageIsRead;

    /*@Override
    public String toString() {
        return "PushMessage{" +
                "id"+ getId()+"\'"+
                "pushMesageId='" + pushMesageId + '\'' +
                "pushMessageTitle='" + pushMessageTitle + '\'' +
                ", pushMesageImageUrl=" + pushMesageImageUrl +
                ", pushMesageDate=" + pushMesageDate +
                ", pushMesageWebUrl='" + pushMesageWebUrl + '\'' +
                ", pushMessageContent='" + pushMessageContent + '\'' +
                ", pushMesageIsRead=" + pushMesageIsRead +
                '}';
    }*/

}
