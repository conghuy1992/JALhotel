package hotelokura.jalhotels.oneharmony.activity;

import android.app.Activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.activity.splash.SplashActivity;
import hotelokura.jalhotels.oneharmony.brand_preview.PreviewManager;
import hotelokura.jalhotels.oneharmony.setting.AppNetSetting;
import hotelokura.jalhotels.oneharmony.setting.PlistDict;
import hotelokura.jalhotels.oneharmony.setting.PlistReader;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * スプラッシュ画面
 */
public class MainActivity extends Activity {
	static final String TAG = "MainActivity";

    private PreviewManager mPrevManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

//        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
//            LogUtil.d(TAG, "FLAG_ACTIVITY_BROUGHT_TO_FRONT");
//            finish();
//            return;
//        }

        mPrevManager = PreviewManager.getInstance(getApplicationContext());
        downloadAppNetSetting();

        final Bundle extras = this.getIntent().getExtras();
        SplashActivity.startActivity(this,
                mPrevManager.isPreview() ? SplashActivity.MODE_NOMAL : SplashActivity.MODE_DL,
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK,
                extras);
        finish();
	}

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * AppNet.pistの読み込み
     */
    private void downloadAppNetSetting() {
        LogUtil.d(TAG, "downloadAppNetSetting");
        // Assetsから取得
        AssetManager asset = getResources().getAssets();
        PlistDict dict = null;
        try {
            InputStream is = asset.open("AppNet.plist");
            dict = PlistReader.read(is);
            is.close();
        } catch (IOException e) {
            LogUtil.d(TAG, "Not Found: AppNet.plist");
        }
        if (dict != null) {
            MainApplication.getInstance().setAppNetSetting(
                    new AppNetSetting(dict));
        } else {
            MainApplication.getInstance().setAppNetSetting(null);
        }
    }
}
