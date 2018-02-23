package hotelokura.jalhotels.oneharmony.activity.splash;

import java.io.InputStream;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyConnectFlag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.ActivitySkeleton;
import hotelokura.jalhotels.oneharmony.activity.CatalogDickCachUtil;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListActivity;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListData;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListItemData;
import hotelokura.jalhotels.oneharmony.activity.coverflow.CoverFlowActivity;
import hotelokura.jalhotels.oneharmony.activity.coverflow.CoverFlowData;
import hotelokura.jalhotels.oneharmony.activity.news.NewsActivity;
import hotelokura.jalhotels.oneharmony.activity.webtop.WebTopActivity;
import hotelokura.jalhotels.oneharmony.brand_preview.PreviewLoginActivity;
import hotelokura.jalhotels.oneharmony.brand_preview.PreviewManager;
import hotelokura.jalhotels.oneharmony.brand_preview.PreviewMenuActivity;
import hotelokura.jalhotels.oneharmony.cache.DiskCache;
import hotelokura.jalhotels.oneharmony.net.AsyncCallback;
import hotelokura.jalhotels.oneharmony.net.AsyncResult;
import hotelokura.jalhotels.oneharmony.net.DownloadManager;
import hotelokura.jalhotels.oneharmony.setting.AppNetSetting;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.setting.PlistDict;
import hotelokura.jalhotels.oneharmony.util.BitmapUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.view.ProgressIndicatorDialog;

/**
 * スプラッシュ画面
 */
public class SplashActivity extends ActivitySkeleton {
    static final String TAG = "SplashActivity";

    public static final int MODE_NOMAL = 0;
    public static final int MODE_DL = 1;

    private static final String INTENT_KEY_MODE = "mode";
    private static final int DISP_TIME = 1 * 1000;

    private final Handler mMyHandler = new Handler();

    private int mMode = MODE_DL;

    private ProgressIndicatorDialog progress;

    private String appNetUrl = null;
    private AlertDialog downloadAlertDialog;
    private boolean isRequiredUpdate = false;
    private boolean downloadedSetting;
    private boolean downloadedSettingError;
    private AsyncResult<PlistDict> downloadedSettingErrorResult;
    private boolean isFinish = false;

    private PreviewManager mPrevManager;

    private CatalogListData listData = null;
    private CatalogListItemData itemData = null;

    private boolean isTablet;
    private Bitmap splashImage = null;
    private Bitmap splashPortraitImage = null;
    private Bitmap splashLandscapeImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        /* 以下のコメントアウトを外してTapjoyを有効にする */
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        //端末固有IDを取得しない
        connectFlags.put(TapjoyConnectFlag.DISABLE_PERSISTENT_IDS, "true");
        // TODO TapjoyConnectNotifier()を追加してバリスタのサーバでTapjoyの通知を確認する
//        TapjoyConnect.requestTapjoyConnect(getApplicationContext(), "0f74a989-77e0-475d-823c-53b4dc0bae0c", "xIQ5ASoQa4kzIpZULvwG", connectFlags);
        TapjoyConnect.requestTapjoyConnect(getApplicationContext(), "f8dce476-943f-490f-b80a-1f744895b816", "bquhgYmFR1DNpNQWEiI1", connectFlags);
        isTablet = MainApplication.getInstance().isTabletDevice();
        if (!isTablet) {
            // スマホの時は縦固定
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        mPrevManager = PreviewManager.getInstance(getApplicationContext());

        mMode = getIntent().getIntExtra(INTENT_KEY_MODE, MODE_DL);
        if (MODE_NOMAL == mMode) {
            mMyHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    moveNextActivity();
                }
            }, DISP_TIME);
            return;
        }

        // App.plistの読み込み
        downloadAppSetting();

        // 表示時間か、ダウンロード完了を待つ為のスレッドを作成
        long viewTime = (new Date()).getTime() + (DISP_TIME); // 表示時間
        final Date viewDate = new Date(viewTime);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isLoop = true;
                while (isLoop && !isFinish) {
                    try {
                        Thread.sleep(500);
                        if (isRequiredUpdate) {
                            continue;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 表示時間の確認
                    Date now = new Date();
                    if (now.compareTo(viewDate) > 0) {
                        if (downloadedSetting) {
                            isLoop = false;
                        } else {
                            // まだダウンロードが終わっていないならロード中インジケータを表示する
                            mMyHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // 画像非表示
                                    recycleImages();
                                    // ロード中インジケータを表示
                                    if (progress == null && !isRequiredUpdate) {
                                        progress = new ProgressIndicatorDialog(
                                                SplashActivity.this);
                                        progress.setOnCancelListener(new OnCancelListener() {
                                            @Override
                                            public void onCancel(
                                                    DialogInterface dialog) {
                                                if (progress != null) {
                                                    progress.dismiss();
                                                    progress = null;
                                                }
                                                finish();
                                            }
                                        });
                                        progress.show();
                                    }
                                }
                            });
                        }
                        if (downloadedSettingError) {
                            // ダウンロードエラー発生中
                            downloadedSettingError = false;
                            mMyHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // エラーダイアログ表示
                                    showDownloadAlertDialog(downloadedSettingErrorResult);
                                }
                            });
                        }
                    }
                }

                if (isFinish) {
                    // アプリ終了
                    mMyHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                } else if (isRequiredUpdate) {

                } else {
                    // 次の画面に遷移
                    mMyHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listData != null) {
                                listData.clearDownloadOffer();
                            }
                            if (itemData != null) {
                                itemData.clearDownloadOffer();
                            }

                            moveNextActivity();
                        }
                    });
                }
            }
        }).start();
    }

    private String mPushBaseUrl;
    private static String kGCMSenderId = null;

    private String getGcmSenderId() {
        if (SplashActivity.kGCMSenderId == null) {
            SplashActivity.kGCMSenderId = getString(R.string.gcm_sender_id);
        }
        return SplashActivity.kGCMSenderId;
    }

    @Override
    protected void onStart() {
        LogUtil.d(TAG, "onStart");
        super.onStart();

        downloadImages();
        changeImage();
    }

    @Override
    protected void onResume() {
        LogUtil.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtil.d(TAG, "onStop");
        super.onStop();

        recycleImages();
        if (downloadAlertDialog != null) {
            downloadAlertDialog.dismiss();
            downloadAlertDialog = null;
        }
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    @Override
    protected void onRestart() {
        LogUtil.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        LogUtil.d(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);

        changeImage();
    }

    /**
     * App.plistのダウンロード
     */
    private void downloadAppSetting() {
        AppNetSetting appNetSetting = MainApplication.getInstance()
                .getAppNetSetting();
        if (appNetSetting != null) {
            // AppNet.plistがあるならそのURLから取得する
            appNetUrl = appNetSetting.getNetAppURL();
        }
        if (MainApplication.getInstance().isBrand()) {
            if (mPrevManager.isPreview()) {
                appNetUrl = mPrevManager.getUrl();
            } else {
                appNetUrl = appNetUrl + "sp/content/app/" + getString(R.string.app_id) + '/';
            }
        } else if (MainApplication.getInstance().isEcApp()) {
            appNetUrl = null;
        }

        DownloadManager.getInstance().offerPlist("splash", 0, appNetUrl,
                "App.plist", new AsyncCallback<PlistDict>() {
                    @Override
                    public void onSuccess(AsyncResult<PlistDict> result) {
                        PlistDict dict = result.getContent();

                        if (dict == null) {
                            downloadedSettingError = true;
                            downloadedSettingErrorResult = result;
                            return;
                        }

                        String appLastMod = result.getLastModified();

                        // キャッシュクリアの確認
                        CatalogDickCachUtil.chackAppSetting(appNetUrl,
                                appLastMod);

                        MainApplication app = MainApplication.getInstance();
                        app.setAppSetting(new AppSetting(dict));
                        app.initCheckAction();

                        // バージョンチェック
                        // バージョンチェックを行うか
                        AppSetting appSetting = MainApplication.getInstance().getAppSetting();
                        boolean isPromptsUpdate = appSetting.isPromptsUpdate();
                        if (!isPromptsUpdate) {
                            // バージョンチェックしない
                            downloadedSetting = true;
                            downloadNextSetting();
                            return;
                        }

                        // 最新バージョン
                        int latestVersionInt = appSetting.getLatestCompatVersion();
                        // このアプリのバージョン
                        AppNetSetting appNetSetting = MainApplication.getInstance().getAppNetSetting();
                        int versionInt = appNetSetting.getAppCompatVersion();
                        if (versionInt < latestVersionInt) {
                            isRequiredUpdate = true;
                            if (downloadAlertDialog != null) {
                                downloadAlertDialog.setOnCancelListener(null);
                                downloadAlertDialog.dismiss();
                                downloadAlertDialog = null;
                            }
                            if (progress != null) {
                                progress.setOnDismissListener(null);
                                progress.setOnCancelListener(null);
                                progress.dismiss();
                                progress = null;
                            }
                            final String msg = appSetting.getNewAppMessage(getString(R.string.required_update_msg_key));
                            final String pButtonText = appSetting.getNewAppPositive(getString(R.string.update_positive_button_key));
                            final String nButtonText = appSetting.getNewAppNegative(getString(R.string.update_negative_button_key));
                            final String googlePlayUrl = appSetting.getGooglePlayURL();
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                            alertDialogBuilder.setMessage(msg);
                            alertDialogBuilder.setCancelable(false);
                            alertDialogBuilder.setPositiveButton(pButtonText,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            try {
                                                isRequiredUpdate = false;
                                                downloadedSetting = true;
                                                downloadNextSetting();
                                                Uri uri = Uri.parse(googlePlayUrl);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            } catch (Exception innerException) {
                                                Log.e(TAG, innerException.getMessage());
                                            }
                                        }
                                    });
                            alertDialogBuilder.setNegativeButton(nButtonText,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            isRequiredUpdate = false;
                                            downloadedSetting = true;
                                            downloadNextSetting();
                                        }
                                    });
                            alertDialogBuilder.show();
                        } else {
                            isRequiredUpdate = false;
                            downloadedSetting = true;
                            downloadNextSetting();
                        }
                    }

                    @Override
                    public void onFailed(AsyncResult<PlistDict> result) {
                        MainApplication.getInstance().setAppSetting(null);
                        downloadedSettingError = true;
                        downloadedSettingErrorResult = result;
                    }
                });
    }

    /**
     * 次画面の設定を先行ダウンロード
     */
    private void downloadNextSetting() {
        String catalogUrl = null;

        final AppSetting appSetting = MainApplication.getInstance().getAppSetting();
        catalogUrl = appSetting.getNetCataloglistURL();
        if (appSetting.getCoverFlowUse() || appSetting.getUseWebTop()) {
            listData = new CoverFlowData();
        } else {
            listData = new CatalogListData();
        }
        MainApplication.getInstance().setListData(listData);

        listData.setCatalogUrl(catalogUrl);
        listData.setCallback(new CatalogListData.Callback() {
            @Override
            public void downloadNaviLogoImageCompleted(Bitmap bmp) {
            }

            @Override
            public void downloadCompleted() {
                if (!appSetting.getUseWebTop()) {
                    downloadNextItemImage();
                }
            }

            @Override
            public void downloadFailed() {
            }
        });

        if (appSetting.getUseWebTop()) {
            listData.downloadSettingForWebTop();
        } else {
            listData.downloadSetting();
        }
    }

    private void downloadNextItemImage() {
        String catalogUrl = null;

        AppSetting appSetting = MainApplication.getInstance().getAppSetting();
        catalogUrl = appSetting.getNetCataloglistURL();
        itemData = new CatalogListItemData(listData.settingName());
        MainApplication.getInstance().setItemData(itemData);

        itemData.setCatalogUrl(catalogUrl);
        itemData.setCallback(new CatalogListItemData.Callback() {
            @Override
            public void downloadCompleted(String filename) {
            }
        });

        itemData.downloadItemImages(listData.getCatalogListCsvArray());
    }

    /**
     * ダウンロード失敗時のエラーダイアログ表示
     *
     * @param result
     */
    private void showDownloadAlertDialog(AsyncResult<?> result) {
        if (isRequiredUpdate) {
            return;
        }
        if (downloadAlertDialog != null) {
            downloadAlertDialog.dismiss();
            downloadAlertDialog = null;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.err_title);
        alertDialogBuilder.setMessage(result.getMessage());
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(R.string.err_button_retry,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 再ダウンロード
                        dialog.dismiss();
                        downloadAlertDialog = null;
                        downloadAppSetting();
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.err_button_finish,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // キャンセル実行
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // 終了
                downloadAlertDialog = null;
                isFinish = true;
            }
        });
        downloadAlertDialog = alertDialogBuilder.create();
        downloadAlertDialog.show();
    }

    /**
     * 背景画像の読み込み
     */
    private void downloadImages() {
        MainApplication mainApp = MainApplication.getInstance();
        if (!isTablet) {
            if (mainApp.isHDpi()) {
                splashImage = getBitmap("Default-568h@2x.png");
            } else if (mainApp.isMDpi()) {
                splashImage = getBitmap("Default@2x.png");
            } else {
                splashImage = getBitmap("Default.png");
            }
        } else {
            if (mainApp.isMDpi()) {
                splashPortraitImage = getBitmap("Default-Portrait@2x~ipad.png");
                splashLandscapeImage = getBitmap("Default-Landscape@2x~ipad.png");
            } else {
                splashPortraitImage = getBitmap("Default-Portrait~ipad.png");
                splashLandscapeImage = getBitmap("Default-Landscape~ipad.png");
            }
        }
    }

    /**
     * 画像の取得
     */
    private Bitmap getBitmap(String filename) {
        Bitmap res = null;
        InputStream is = null;

//        if (mPrevManager.isPreview()) {
        if (false) {
            DiskCache disk = MainApplication.getInstance().getDickCache();
            is = disk.get(mPrevManager.getUrl(), filename);
        } else {
            AssetManager asset = getResources().getAssets();
            try {
                is = asset.open(filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (is != null) {
            try {
                res = BitmapUtil.getInstance().decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 画像のメモリ解放
     */
    private void recycleImages() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageDrawable(null);
        imageView.invalidate();

        if (splashImage != null) {
            splashImage.recycle();
            splashImage = null;
        }
        if (splashPortraitImage != null) {
            splashPortraitImage.recycle();
            splashPortraitImage = null;
        }
        if (splashLandscapeImage != null) {
            splashLandscapeImage.recycle();
            splashLandscapeImage = null;
        }
        System.gc();
    }

    /**
     * 背景画像の表示
     */
    private void changeImage() {
        // 画面の向きで表示する画像を変更する
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if (MODE_NOMAL == mMode) {
            // 背景色
            LinearLayout rootView = (LinearLayout) findViewById(R.id.root);
            rootView.setBackgroundColor(Color.WHITE);

            // ロゴ画像
            int displayWidth = getResources().getDisplayMetrics().widthPixels;
            float displayRate = 0.6f;
            if (isTablet) {
                switch (getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        displayRate = 0.4f;
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        displayRate = 0.25f;
                        break;
                }
            }
            imageView.getLayoutParams().width = (int) (displayWidth * displayRate);
            imageView.setImageResource(R.drawable.brand_logo_info);
        } else {
            if (!isTablet) {
                // スマホのとき
                imageView.setImageBitmap(splashImage);
            } else {
                // タブレットのとき
                switch (getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        imageView.setImageBitmap(splashPortraitImage);
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        imageView.setImageBitmap(splashLandscapeImage);
                        break;
                }
            }
        }
    }

    /**
     * 次の画面へ遷移する
     */
    private void moveNextActivity() {
        // ステータスバー表示
        recycleImages();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent;
        int flag = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK;

        AppSetting appSetting = MainApplication.getInstance().getAppSetting();
        if (MODE_NOMAL == mMode) {
            if (mPrevManager.isLogin()) {
                intent = new Intent(this, PreviewMenuActivity.class);
            } else {
                intent = new Intent(this, PreviewLoginActivity.class);
            }
            intent.setFlags(flag);
            startActivity(intent);

        } else if (appSetting.getUseWebTop() && appSetting.getWebTopLayout().equals(3)) {
            NewsActivity.startActivity(this, appSetting.getNewsURL(), MainApplication.getInstance().getAppId());

        } else if (appSetting.getUseWebTop()) {
            WebTopActivity.startActivity(this, flag);
        } else if (appSetting.getCoverFlowUse()) {
            intent = new Intent(this, CoverFlowActivity.class);
            intent.setFlags(flag);
            startActivity(intent);
        } else {
            CatalogListActivity.startActivity(this, null, null, null, flag);
        }
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public static void startActivity(Activity activity, int mode) {
        startActivity(activity, mode, -1, null);
    }

    /**
     * @param activity
     * @param mode
     * @param flag
     * @param extras   SplashActivityに送るプッシュの情報
     *                 プッシュ受け取り時の動作はSplashActivity#onCreate() でないと処理できない
     *                 理由は以下の2点
     *                 ・プッシュは u_app_idに依存する
     *                 ・そのu_app_idはSplashActivity#onCreateで取得できる
     */
    public static void startActivity(Activity activity, int mode, int flag, Bundle extras) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.putExtra(INTENT_KEY_MODE, mode);
        if (extras != null) {
            String customStr = extras.getString("pn_custom");
            if (customStr == null) {
                customStr = "null";
            }
            JSONObject custom = null;
            try {
                custom = new JSONObject(customStr);
            } catch (JSONException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            } catch (NullPointerException nullPointerException) {
                Log.e(TAG, nullPointerException.getMessage(), nullPointerException);
            }
            if (custom != null) {
                intent.putExtra("pn_custom", customStr);
            }
        }
        if (flag > 0) intent.setFlags(flag);
        activity.startActivity(intent);
    }
}
