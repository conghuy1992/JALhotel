package com.baristapushsdk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.baristapushsdk.badge.ShortcutBadger;
import com.baristapushsdk.ui.BaseActivitySDK;
import com.baristapushsdk.ui.MessageBaseActivity;
import com.baristapushsdk.ui.adapter.NotificationAdapter;
import com.baristapushsdk.utils.NetworkUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.baristapushsdk.api.ApiManager;
import com.baristapushsdk.entity.PushMessage;
import com.baristapushsdk.ui.MessageListSDK;
import com.baristapushsdk.utils.PackageUtil;
import com.baristapushsdk.utils.SharedPreferenceUtil;

/**
 * Created by Trung Kien on 10/6/2015.
 */
public class Push extends  BaseActivity{

    // TAG
    private final static String TAG = "PushMessage";
    private static String errorMessage = "";
    private final static String FOLDER_PATH = "/backups/barista";
    private final static String FILE_NAME = "/registration_id_";
    private static List<PushMessage> listMessage;
    private static ProgressDialog progress;

    /**
     * Check is register
     *
     * @return
     */
    public static final boolean isRegister(Context context) {
        BaseActivity.setAppContext(context);
        SharedPreferenceUtil.setContextApp(context);
        NetworkUtil.initNetworkConfig();
        if (NetworkUtil.hasNetwork()) {
            if (SharedPreferenceUtil.getBoolean(SharedPreferenceUtil.KEY_REGISTER_STATUS, false)) {
                if (Push.getUnLimitedMessage().equals("1")) {
                    countBadge("0");
                }else {
                    countBadge("1");
                }
            }

        }else {
            Toast.makeText(MessageBaseActivity.getInstance(),
                    MessageBaseActivity.getInstance().getResources().getString(R.string.network_error)
                    , Toast.LENGTH_SHORT).show();
        }
        return SharedPreferenceUtil.getBoolean(SharedPreferenceUtil.KEY_REGISTER_STATUS, false);
    }

    public static void showPopupNewIfNewMessage(){
        SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG,false);
        if (getBadgeCount() != ""){
            if (Integer.parseInt(getBadgeCount()) > 0) {
                if (!SharedPreferenceUtil.getBoolean(SharedPreferenceUtil.KEY_DIALOG_HOME, false)) {
                    showPopupNewMessage(MessageBaseActivity.getInstance());
                }
            }
        }
    }

    private static void countBadge(String limit_badge){
        progress = new ProgressDialog(MessageBaseActivity.getInstance());
        progress.setMessage(MessageBaseActivity.getInstance().getResources().getString(R.string.sync));
        progress.setCancelable(true);
        progress.show();
        countBadgeAPI(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), limit_badge);
    }

    private static void countBadgeSwitch(String limit_badge){
        progress = new ProgressDialog(MessageBaseActivity.getInstance());
        progress.setMessage(MessageBaseActivity.getInstance().getResources().getString(R.string.sync));
        progress.setCancelable(true);
        progress.show();
        countBadgeSwitch(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), limit_badge);
    }

    /**
     * Show list messages screen
     */
    public static final void showListMessages() {
        final Context context = BaseActivity.getAppContext();
        Intent intent = new Intent(context, MessageListSDK.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        MessageBaseActivity.getInstance().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    /**
     * Register
     *
     * @param senderId : sender id
     */
    public static final void register(final String senderId, final String bundleId) {
        // Get context
        final Context context = BaseActivity.getAppContext();
        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_BADGE, "0");
        setUnLimitedMessageFirst("0");
        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_BACK_HOME, "0");
        if (checkPlayServices(context)) {
            errorMessage = "";
            // Get Registration Id
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                    try {
                        String regId = readRegistrationIdKey();
                        if (regId == null || regId.equals("")) {
                            regId = gcm.register(senderId);
                        } else {
                            gcm.register(senderId);
                        }
                        Log.i(TAG, "Device registered, registration ID=" + regId);

                        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_REG_ID, regId);
                        SharedPreferenceUtil.putInt(SharedPreferenceUtil.KEY_APP_VERSION, PackageUtil.getVersionCode());
                    } catch (IOException ex) {
                        Log.e(TAG, "Error :" + ex.getMessage());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    final String regId = getRegistrationId(context);
                    String android_id = Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    String strVersion = String.valueOf(android.os.Build.VERSION.RELEASE);
                    String[] version = strVersion.split("\\.");
                    String osVersion = version[0] + "." + version[1];

                    if (!TextUtils.isEmpty(regId)) {
                        ApiManager.registerPush(bundleId, android_id ,regId, "Android", osVersion, new ApiManager.OnRegisStatus() {
                            @Override
                            public void onRegis(boolean isSuccess) {
                                if (isSuccess) {
                                    writeRegistrationIdKey(regId);
                                    registerAccpetPush(bundleId, "1");
                                    SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_REGISTER_STATUS, isSuccess);
                                    SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_BUNDLE_ID, bundleId);
                                    if (Push.getUnLimitedMessage().equals("1")) {
                                        countBadge("0");
                                    }else {
                                        countBadge("1");
                                    }
                                }
                            }
                        });
                    }
                }
            }.execute();

        }else {
            errorMessage = context.getResources().getString(R.string.device_not_support);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    /**
     * write RegistrationId key
     *
     * @param string
     */
    private static void writeRegistrationIdKey(String string) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_PATH);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String app_name = SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_APP_NAME).replace(" ","_");
            String path = Environment.getExternalStorageDirectory() +  FOLDER_PATH + FILE_NAME + app_name;
            File myFile = new File(path);
            myFile.createNewFile();
            FileOutputStream fos =  new FileOutputStream(path) ;
            fos.write(string.getBytes());
            fos.close();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * read RegistrationId key
     */
    private static String readRegistrationIdKey() {
        String registrationId = "";
        try {
            String app_name = SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_APP_NAME).replace(" ","_");
            String path = Environment.getExternalStorageDirectory() +  FOLDER_PATH + FILE_NAME + app_name;
            FileInputStream in = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            registrationId = sb.toString();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return registrationId;
    }

    /**
     * Get Registration Id
     *
     * @param context
     * @return
     */
    public static String getRegistrationId(Context context) {
        String registrationId = SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_REG_ID);
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = SharedPreferenceUtil.getInt(SharedPreferenceUtil.KEY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = PackageUtil.getVersionCode();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Register accept push
     * @param bundleId
     * @param accept_push
     */
    public static final void registerAccpetPush(final String bundleId, final String accept_push) {
        // Get context
        final Context context = BaseActivity.getAppContext();

        final String regId = getRegistrationId(context);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String strVersion = String.valueOf(android.os.Build.VERSION.RELEASE);
        String[] version = strVersion.split("\\.");
        String osVersion = version[0] + "." + version[1];

        if (!TextUtils.isEmpty(regId)) {
            ApiManager.registerAcceptPush(bundleId, android_id ,regId, "Android", osVersion, accept_push, new ApiManager.OnRegisAcceptPush() {
                @Override
                public void onAccept(boolean isSuccess) {
                    if (isSuccess) {
                        SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_REGISTER_ACCEPT_PUSH, isSuccess);
                        retrieveStatusPush(bundleId);
                    }
                }
            });
        }
    }

    /**
     * Retrieve Status Push
     * @param bundleId
     */
    public static final void retrieveStatusPush(final String bundleId) {
        // Get context
        final Context context = BaseActivity.getAppContext();

        final String regId = getRegistrationId(context);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String strVersion = String.valueOf(android.os.Build.VERSION.RELEASE);
        String[] version = strVersion.split("\\.");
        String osVersion = version[0] + "." + version[1];

        if (!TextUtils.isEmpty(regId)) {
            ApiManager.retrieveStatusPush(bundleId, android_id ,regId, "Android", osVersion, new ApiManager.OnRetrieveStatusPush() {
                @Override
                public void onRetrieveStatus(boolean isSuccess, String status) {
                    if (isSuccess) {
                        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_RETRIEVE_ACCEPT_PUSH, status);
                    }
                }
            });
        }
    }

    public static final void countBadgeAPI(final String bundleId, String limit_badge) {
        // Get context
        final Context context = BaseActivity.getAppContext();

        final String regId = getRegistrationId(context);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String strVersion = String.valueOf(android.os.Build.VERSION.RELEASE);
        String[] version = strVersion.split("\\.");
        String osVersion = version[0] + "." + version[1];

        if (!TextUtils.isEmpty(regId)) {
            ApiManager.countBadge(bundleId, android_id ,regId, "Android", osVersion, limit_badge, new ApiManager.OnGetBadgeCount() {
                @Override
                public void onGetBadgeCount(boolean isSuccess, String count) {
                    if (isSuccess) {
                        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_BADGE, count);
                        showPopupNewIfNewMessage();
                        Push.setBadgeCount(Integer.parseInt(count));
                        MessageBaseActivity.getInstance().onLoadMessageList();
                    }
                    progress.dismiss();
                }
            });
        }
    }

    public static final void countBadgeSwitch(final String bundleId, String limit_badge) {
        // Get context
        final Context context = BaseActivity.getAppContext();

        final String regId = getRegistrationId(context);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String strVersion = String.valueOf(android.os.Build.VERSION.RELEASE);
        String[] version = strVersion.split("\\.");
        String osVersion = version[0] + "." + version[1];

        if (!TextUtils.isEmpty(regId)) {
            ApiManager.countBadge(bundleId, android_id ,regId, "Android", osVersion, limit_badge, new ApiManager.OnGetBadgeCount() {
                @Override
                public void onGetBadgeCount(boolean isSuccess, String count) {
                    if (isSuccess) {
                        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_BADGE, count);
                        Push.setBadgeCount(Integer.parseInt(count));
                        MessageBaseActivity.getInstance().onLoadMessageList();
                    }
                    progress.dismiss();
                }
            });
        }
    }


    /**
     * set badge count
     *
     * @param badgeCount
     */
    public static final void setBadgeCount(int badgeCount) {
        Context context = BaseActivity.getAppContext();
        try {
            if(badgeCount <= 0) {
                ShortcutBadger.with(context).remove();
            } else {
                ShortcutBadger.with(context).count(badgeCount);
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }

    public static final String getBadgeCount(){
        return SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BADGE);
    }

    public static final void setStatusPush(){
        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_RETRIEVE_ACCEPT_PUSH,"1");
    }

    public static final String getStatusPush(){
        return SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_RETRIEVE_ACCEPT_PUSH);
    }

    public static final void setAccpetMessagePopup(String value){
        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_ACCEPT_MESSAGE_POPUP, value);
    }

    public static final String getAccpetMessagePopup(){
        return SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_ACCEPT_MESSAGE_POPUP);
    }

    public static final void setUnLimitedMessage(String value){
        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_LIMIT, value);
        if (value.equals("1")) {
            countBadgeSwitch("0");
        }else {
            countBadgeSwitch("1");
        }
    }

    public static final void setUnLimitedMessageFirst(String value){
        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_LIMIT, value);
    }

    public static final void setKeyBackHome(String value){
        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_BACK_HOME, value);
    }

    public static final String getKeyBackHome(){
        return SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BACK_HOME);
    }

    public static final String getUnLimitedMessage(){
        return SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_LIMIT);
    }


    public static  final  void showPopupNewMessage(Context context){

        if (getAccpetMessagePopup().equals("1") && context != null
                && !SharedPreferenceUtil.getBoolean(SharedPreferenceUtil.KEY_DIALOG, false)) {
            // custom dialog
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            dialog.setContentView(R.layout.dialog_new);
            dialog.setCancelable(false);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_view);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG,false);
                    dialog.dismiss();
                    showListMessages();
                }
            });
            Button closeButton = (Button) dialog.findViewById(R.id.btn_close);
            // if button is clicked, close the custom dialog
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG,false);
                    dialog.dismiss();
                }
            });
            try {
                if(!dialog.isShowing()){
                    dialog.show();
                    SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG,true);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static  final  void destroySDK(){
        SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG_HOME,false);
    }

    public static  final  void registerNotification(String appName, Bitmap iconApp, String className){
        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_APP_NAME, appName);
        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_CLASS_NAME, className);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        iconApp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_APP_ICON, encodedImage);
    }
}

