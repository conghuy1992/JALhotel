package com.baristapushsdk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.baristapushsdk.utils.NetworkUtil;

/**
 * Created by Tran on 12/22/2015.
 */
public class BaseActivity extends Activity {
    private static Context singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = this;
        NetworkUtil.initNetworkConfig();
    }

    public static Context getAppContext() {
        return singleton;
    }

    public static Context setAppContext(Context context) {
        singleton = context;
        return singleton;
    }
}
