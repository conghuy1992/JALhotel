package hotelokura.jalhotels.oneharmony.util;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;

/**
 * Created by barista5 on 2014/01/02.
 */
public class AdManager {
    static final String TAG = "AdManager";

    protected InterstitialAd interstitial;

    static public void startInterstitial(Activity activity) {
        AdManager adManager = new AdManager();
        adManager.initInterstitial(activity);
    }

    public boolean initInterstitial(Activity activity) {
        LogUtil.d(TAG, "initInterstitial");
        String className = activity.getClass().getSimpleName();
        LogUtil.d(TAG, "className : " + className);

        // 広告設定がOffの場合は表示しない
        if (!checkUseAdMob()) {
            LogUtil.d(TAG, "checkUseAdMob : false");
            return false;
        }

        // 表示条件を満たさない場合は表示しない
        if (!checkAdCanBeDisplayed(className)) {
            LogUtil.d(TAG, "checkAdCanBeDisplayed : false");
            return false;
        }

        String adId = getAdId(className);
        if (adId == null) {
            LogUtil.d(TAG, "getAdId : null");
            return false;
        }

        interstitial = new InterstitialAd(activity);
        interstitial.setAdUnitId(adId);

        // Create ad request
        AdRequest.Builder builder = new AdRequest.Builder();
//        builder.addTestDevice("CE7905B9217FC58128DC4CBA048657C0");
        builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        AdRequest adRequest = builder.build();

        // Set your AdListener
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                LogUtil.d(TAG, "onLeaveApplication");
                interstitial.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                String errorReason = "";
                switch(errorCode) {
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        errorReason = "Internal error";
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        errorReason = "Invalid request";
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        errorReason = "Network Error";
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        errorReason = "No fill";
                        break;
                }
                LogUtil.d(TAG, "onLeaveApplication : " + errorReason);
            }

            @Override
            public void onAdClosed() {
                LogUtil.d(TAG, "onLeaveApplication");
            }

            @Override
            public void onAdLeftApplication() {
                LogUtil.d(TAG, "onLeaveApplication");
            }

            @Override
            public void onAdOpened() {
                LogUtil.d(TAG, "onLeaveApplication");
            }
        });

        interstitial.loadAd(adRequest);

        return true;
    }

    public boolean checkUseAdMob() {
        AppSetting setting = MainApplication.getInstance().getAppSetting();
        if (setting == null) {
            return false;
        }
        return setting.getUseAdMob();
    }


    private boolean checkAdCanBeDisplayed(String className) {
        LogUtil.d(TAG, "checkAdCanBeDisplayed : " + className);

        boolean ret = false;
        AppSetting setting = MainApplication.getInstance().getAppSetting();

        // インターバル(=広告表示間隔)
        int adInterval = setting.getAdInterval(className);
        if (adInterval == -1) {
            return false;
        }

        // プリファレンス取得
        SharedPreferences sharedPreferences = MainApplication.getInstance().getSharedPreferences();
        String key = "ad-cnt-"+className;

        int count = (int) sharedPreferences.getLong(key, 0);
        LogUtil.d(TAG, "key before : " + count);

        // カウンタの初期値を表示条件とする
        if (count == 0) {
            ret = true;
        }

        // カウンタの更新
        if (count == adInterval) {
            // インターバル値までカウントアップしたら初期値に戻す => 次回表示される
            count = 0;
        } else {
            count++;
        }

        LogUtil.d(TAG, "key after : " + count);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, count);
        editor.commit();

        return ret;
    }

    public String getAdId(String className) {
        if (className.equals("CoverFlowActivity")) {
            return "ca-app-pub-2360795029520704/1819495876";
        }
        if (className.equals("CatalogListActivity")) {
            return "ca-app-pub-2360795029520704/4772962277";
        }
        if (className.equals("CatalogActivity")) {
            return "ca-app-pub-2360795029520704/3296229075";
        }
        return null;
    }

}
