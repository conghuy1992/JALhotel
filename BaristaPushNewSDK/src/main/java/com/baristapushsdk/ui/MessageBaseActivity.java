package com.baristapushsdk.ui;

import android.app.Activity;

import com.baristapushsdk.entity.PushMessage;

/**
 * Created by Trung Kien on 10/21/2015.
 */
public abstract class MessageBaseActivity extends Activity {

    private static MessageBaseActivity singleton;

    public static MessageBaseActivity getInstance() {
        return singleton;
    }

    public MessageBaseActivity() {
        super();
        singleton = this;
    }

    /**
     * @param pushMessage
     */

    public abstract void onReceivedMessageList(PushMessage pushMessage);

    public abstract void onLoadMessageList();

}
