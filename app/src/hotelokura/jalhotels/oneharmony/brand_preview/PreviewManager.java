package hotelokura.jalhotels.oneharmony.brand_preview;

import android.content.Context;
import android.content.SharedPreferences;

import hotelokura.jalhotels.oneharmony.MainApplication;

/**
 * Created by barista5 on 2013/12/12.
 */
public class PreviewManager {
    static final String TAG = "PreviewManager";

    public static final String PREFERENCES_FILE_NAME = "preference";
    public static final Boolean USE_PREF = true;

    public static final String SPLASH_NAME_P = "40";
    public static final String SPLASH_NAME_L = "41";

    private static PreviewManager mInstance;
    private Context mContext;

    private Boolean mLogin = false;
    private String mUrl = "";
    private String mName = "";
    private String mSplashUrl = "";
    private String mAppId = "";

    PreviewManager(Context context) {
        mContext = context;
    }

    public static PreviewManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PreviewManager(context);
        }
        return mInstance;
    }

    public boolean isPreview() {
        return false;
    }

    // ログイン処理
    public void login(){
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("logged-in", 1);
            editor.commit();
        } else {
            mLogin = true;
        }
    }

    // ログアウト処理
    public void logout(){
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("logged-in", 0);
            editor.commit();
        } else {
            mLogin = false;
        }
    }

    // ログイン判定
    public Boolean isLogin(){
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            if(settings == null) return false;
            int login = (int) settings.getLong("logged-in", 0);
            if(login == 1) return true;
            else return false;
        } else {
            return mLogin;
        }
    }

    public void setUrl(String url) {
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("net_url", url);
            editor.commit();
        } else {
            mUrl = url;
        }

    }

    public String getUrl() {
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            if (settings == null) {
                return null;
            }
            return settings.getString("net_url", null);
        } else {
            return mUrl;
        }
    }

    public void setName(String name) {
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("login_name", name);
            editor.commit();
        } else {
            mName = name;
        }
    }

    public String getName() {
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            if (settings == null) {
                return null;
            }
            return settings.getString("login_name", "");
        } else {
            return mName;
        }
    }

    public void setSplashUrl(String url) {
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("splash_url", url);
            editor.commit();
        } else {
            mSplashUrl = url;
        }
    }

    public String getSplashUrl() {
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            if (settings == null) {
                return null;
            }
            return settings.getString("splash_url", "");
        } else {
            return mSplashUrl;
        }
    }

    public void setAppId(String appId) {
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("app_id", appId);
            editor.commit();
        } else {
            mAppId = appId;
        }
    }

    public String getAppId() {
        if (USE_PREF) {
            SharedPreferences settings = getSharedPreferences();
            if (settings == null) {
                return null;
            }
            return settings.getString("app_id", "");
        } else {
            return mAppId;
        }
    }
    
    private SharedPreferences getSharedPreferences() {
        return MainApplication.getInstance().getSharedPreferences();
    }
}
