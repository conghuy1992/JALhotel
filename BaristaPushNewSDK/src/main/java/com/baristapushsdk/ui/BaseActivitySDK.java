package com.baristapushsdk.ui;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;

import com.baristapushsdk.entity.PushMessage;

/**
 * Created by Trung Kien on 10/6/2015.
 */
public abstract class BaseActivitySDK extends Activity {

    private static BaseActivitySDK singleton;

    public static BaseActivitySDK getInstance() {
        return singleton;
    }

    public BaseActivitySDK() {
        super();
        singleton = this;
    }

    /**
     * @param pushMessage
     */
    public abstract void onReceivedMessage(PushMessage pushMessage);

}
