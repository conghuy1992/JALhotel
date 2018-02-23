package com.baristapushsdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.baristapushsdk.BaseActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

public class NetworkUtil extends BaseActivity{

    private static String CONTS_X_APP_BUNDLE_ID = "x-app-bundle-id";
    private static String CONTS_X_DEVICE_ID = "x-device-id";
    private static String CONTS_X_PUSH_TOKEN = "x-push-token";
    private static String CONTS_X_OS_TYPE = "x-os-type";
    private static String CONTS_X_OS_VERSION = "x-os-version";

    public static boolean hasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) BaseActivity.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return true;
        }
        return false;
    }

    //
    /**
     * The client.
     */
    private static AsyncHttpClient client;

    public static void initNetworkConfig() {
        client = new AsyncHttpClient();
        client.setTimeout(30000);
    }


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        String requsetUrl = url + "?" + params.toString();

        //DebugLog.e("params: " + params.toString() + "  requestUrl: " + requsetUrl);

        Log.e("NetworkUtil", "requsetUrl:" + requsetUrl);
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.get(requsetUrl, responseHandler);

    }

    /**
     * post data API
     *
     * @param params
     * @param responseHandler
     */
    public static void post(String url, RequestParams params, String bundle_id, String device_id,
                            String push_token, String os_type, String os_vesion,
                            AsyncHttpResponseHandler responseHandler) {
        //DebugLog.e("params: " + params.toString() + "  requestUrl: " + url);
        client.addHeader(CONTS_X_APP_BUNDLE_ID,bundle_id);
        client.addHeader(CONTS_X_DEVICE_ID,device_id);
        client.addHeader(CONTS_X_PUSH_TOKEN,push_token);
        client.addHeader(CONTS_X_OS_TYPE,os_type);
        client.addHeader(CONTS_X_OS_VERSION,os_vesion);
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.post(url, params, responseHandler);
    }
}
