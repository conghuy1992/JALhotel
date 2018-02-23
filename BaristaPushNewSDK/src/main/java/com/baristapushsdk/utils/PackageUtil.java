package com.baristapushsdk.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.baristapushsdk.BaseActivity;
import com.baristapushsdk.R;

public class PackageUtil extends BaseActivity {


    public static String getVersionName() {
        PackageInfo info = getPackageInfo();
        return info != null ? info.versionName : BaseActivity.getAppContext().getString(R.string.version_not_found);
    }

    public static int getVersionCode() {
        PackageInfo info = getPackageInfo();
        return info != null ? info.versionCode : 0;
    }

    private static PackageInfo getPackageInfo() {
        PackageManager pm = BaseActivity.getAppContext().getPackageManager();
        try {
            return pm.getPackageInfo(BaseActivity.getAppContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            Log.e("", "not get package info", e);
        }
        return null;
    }
}