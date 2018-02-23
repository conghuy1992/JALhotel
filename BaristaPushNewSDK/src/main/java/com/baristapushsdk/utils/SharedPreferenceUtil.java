package com.baristapushsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.baristapushsdk.BaseActivity;
/**
 * Created by Trung Kien on 10/6/2015.
 */
public class SharedPreferenceUtil extends BaseActivity{
    private static final String SHARED_PREFERENCES = "preferences";
    public final static String KEY_REG_ID = "reg_id";
    public final static String KEY_APP_VERSION = "app_version";
    public final static String KEY_REGISTER_STATUS = "register_status";
    public final static String KEY_REGISTER_ACCEPT_PUSH = "register_accpet_push";
    public final static String KEY_RETRIEVE_ACCEPT_PUSH = "retrieve_status_push";
    public final static String KEY_BADGE = "badge";
    public final static String KEY_ACCEPT_MESSAGE_POPUP = "message_popup";
    public final static String KEY_BUNDLE_ID = "bundle_id";
    public final static String KEY_APP_NAME = "app_name";
    public final static String KEY_APP_ICON = "app_icon";
    public final static String KEY_DIALOG = "dialog_show";
    public final static String KEY_DIALOG_HOME = "dialog_show_home";
    public final static String KEY_LIMIT = "limit_message";
    //public final static String KEY_IS_SYNC = "is_sync";
    public final static String KEY_CLASS_NAME = "className";
    public final static String KEY_CLICK_NOTIFICATION = "click_notification";
    public final static String KEY_CLICK_NOTIFICATION_DETAIL = "click_notification_detail";
    public final static String KEY_EDIT = "is_edit";
    public final static String KEY_BACK_HOME = "back_home";
    private static Context context;

    public static void setContextApp(Context context_app){
        context = context_app;
    }
    private static SharedPreferences getPrefs() {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static final String getString(final String key) {
        return getPrefs().getString(key, "");
    }

    public static final int getInt(final String key, final int defaultValue) {
        return getPrefs().getInt(key, defaultValue);
    }

    public static final Long getLong(final String key, final Long defaultValue) {
        return getPrefs().getLong(key, defaultValue);
    }

    public static final void putLong(final String key, final Long value) {
        getPrefs().edit().putLong(key, value).commit();
    }

    public static final void putInt(final String key, final int value) {
        getPrefs().edit().putInt(key, value).commit();
    }

    public static final void putString(final String key, final String value) {
        getPrefs().edit().putString(key, value).commit();
    }

    public static final void removeKey(final String key) {
        getPrefs().edit().remove(key).commit();
    }

    public static final void putBoolean(final String key, final Boolean value) {
        getPrefs().edit().putBoolean(key, value).commit();
    }

    public static final Boolean getBoolean(final String key, final Boolean defaultValue) {
        return getPrefs().getBoolean(key, defaultValue);
    }

    public static final Boolean containsKey(final String key) {
        return getPrefs().contains(key);
    }
}
