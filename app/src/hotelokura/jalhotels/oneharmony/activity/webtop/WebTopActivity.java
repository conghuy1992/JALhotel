package hotelokura.jalhotels.oneharmony.activity.webtop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.baristapushsdk.Push;
import com.baristapushsdk.entity.PushMessage;
import com.baristapushsdk.ui.MessageBaseActivity;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.ActivityLogin;
import hotelokura.jalhotels.oneharmony.activity.ActivityReservation;
import hotelokura.jalhotels.oneharmony.activity.CatalogDickCachUtil;
import hotelokura.jalhotels.oneharmony.activity.catalog.CatalogActivity;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListActivity;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListData;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListNavigationView;
import hotelokura.jalhotels.oneharmony.activity.coverflow.CoverFlowData;
import hotelokura.jalhotels.oneharmony.activity.map.MapActivity;
import hotelokura.jalhotels.oneharmony.activity.news.NewsActivity;
import hotelokura.jalhotels.oneharmony.activity.web.WebViewActivity;
import hotelokura.jalhotels.oneharmony.analytics.CheckAction;
import hotelokura.jalhotels.oneharmony.net.DownloadManager;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.setting.CatalogListSetting;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.view.ProgressIndicatorDialog;

/**
 * 内部ブラウザ画面
 */
public class WebTopActivity extends MessageBaseActivity {
    static final String TAG = "WebTopActivity";

    public static final String TOP_SCREEN = "top";
    protected String mGaScreenName = "";
    private ProgressIndicatorDialog mProgress;
    private WebView mWebView;
    private String mStartUrl;
    private MainApplication mMain;
    private AppSetting mAppSetting;
    private boolean mChild = false;
    private CatalogListData listData;

    private int mMode = 0;
    private String mParam;

    private String mCurrentTitle;
    //
//    private static final String BUNDLEID = "55b0902d866b3d36658b45c6";
    private static final String BUNDLEID = "56553cb8703a198a578b456e";
    private static final String CONST_SENDER_ID = "599192067986";

//    private static final String BUNDLEID = "5636c6b7831b7e98725c5be3";
//    private static final String CONST_SENDER_ID = "987959125347";

    private ActivityInfo info;
    private Button btnLogin;
    public static boolean check_Login = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mMain = MainApplication.getInstance();
        mAppSetting = mMain.getAppSetting();
        if (mMain == null || mAppSetting == null) {
            LogUtil.e(TAG, "getAppSetting failed.");
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webtop);

        this.mGaScreenName = WebTopActivity.TOP_SCREEN;

        // 前画面から情報取得
        mCurrentTitle = getIntent().getStringExtra("title");
        mStartUrl = getIntent().getStringExtra("StartUrl");
        mMain.setWebTopUrl(mStartUrl);
        mMain.setWebTopTitle(mCurrentTitle);

        if (mStartUrl == null || mStartUrl.equals("")) {
            mStartUrl = mAppSetting.getWebTopURL();
        } else {
            // 前画面からURLが渡されたらセカンドメニューとみなす
            mChild = true;
        }

        // タイトルバー初期化
        initNavigationView();

        // WebView初期化
        initWebView();

        // リストデータの初期化
        initListData();

        //
        initPushNotification();
        // create button login
        initbtnLogin();
    }


    @Override
    protected void onStart() {
        LogUtil.d(TAG, "onStart");
        super.onStart();

        if (!mChild) {
            mMain.setCurrentCatalogListTags(null);
            mMain.setWebTopUrl(null);
            mMain.setWebTopTitle(null);
        }

        // ロード中インジケータを表示
        initLoading();

        // イベントの受付を再開
        mMode = 0;

        // 設定ファイルをダウンロードするかの判定
        boolean isDownload = false;
        if (listData.getCatalogListCsvArray() == null
                || CatalogDickCachUtil.chackCatalogListDownload(mAppSetting.getNetCataloglistURL(),
                listData.settingActionMode())) {
            // 初期表示、またはダウンロードから10分たっていた
            isDownload = true;
            LogUtil.d(TAG, "For the first time or it had passed 10 minutes.");
        }

        if (isDownload) {
            // 設定ダウンロード
            startLoading();
            mWebView.clearCache(true);
            listData.downloadSettingForWebTop();
        } else {
            if (mChild) {
                CatalogListNavigationView navigationView = (CatalogListNavigationView) findViewById(R.id.navigationView);
                navigationView.setTitleImageLogo(listData.getNavigationLogoImage());
            }
        }

        // URLからの画面ロード バージョンによって動作を変更
        int process_branch_ver = 19; // Build.VERSION_CODES.KITKAT

        if (Build.VERSION.SDK_INT < process_branch_ver && isDownload) {
            // KitKat 以前の場合はダウンロードが必要なときだけロード
            showStartPage();

        } else if (Build.VERSION.SDK_INT >= process_branch_ver) {
            // KitKat 以降の場合は必ずロードする（しないと真っ白な状態のWebViewになる
            showStartPage();
        }
    }

    @Override
    protected void onResume() {
        LogUtil.d(TAG, "onResume");
        super.onResume();
        //ボタンのリンクを有効にする
        mMode = 0;
        if (!check_Login) {
            btnLogin.setText(getResources().getString(R.string.login));
        } else {
            btnLogin.setText(getResources().getString(R.string.logout));
        }
    }

    @Override
    protected void onPause() {
        LogUtil.d(TAG, "onPause");
        Push.setKeyBackHome("0");
        super.onPause();
        Push.destroySDK();
    }

    @Override
    protected void onStop() {
        LogUtil.d(TAG, "onStop");
        super.onStop();
        stopLoading();
        DownloadManager.getInstance().clear("cataloglist.csv");
    }

    @Override
    protected void onRestart() {
        LogUtil.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        Push.setKeyBackHome("0");
        super.onDestroy();
        Push.destroySDK();
        finishWebView();
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
    }

    private void initNavigationView() {
        // ナビゲーションの設定
        CatalogListNavigationView navigationView = (CatalogListNavigationView) findViewById(R.id.navigationView);
        if (mChild) {
            // 可視化
            navigationView.setVisibility(View.VISIBLE);

            // 戻るボタン
            navigationView.setBackButtionListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        AppSetting appSetting = mMain.getAppSetting();
        // 背景色
        navigationView.setBGColor(appSetting.getCataloglistTitleBGColor());
        // タイトル
        navigationView.setTitle(appSetting.getCataloglistTitle());
        if (mCurrentTitle != null && !mCurrentTitle.equals("")) {
            // カバーフローからの値を優先する
            navigationView.setTitle(mCurrentTitle);
        }
        // タイトル文字色
        navigationView.setTitleColor(appSetting.getCataloglistTitleTextColor());
    }

    private void finishWebView() {
        if (mWebView != null) {
            ViewGroup layout = (ViewGroup) findViewById(R.id.layout);
            layout.removeView(mWebView);
            mWebView.stopLoading();
            mWebView.setWebViewClient(null);
            mWebView.setWebChromeClient(null);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    private void initLoading() {
        mProgress = new ProgressIndicatorDialog(this);
        mProgress.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stopLoading();
                finish();
            }
        });
    }

    private void startLoading() {
        if (mProgress != null && !mProgress.isShowing()) {
            mProgress.show();
        }
    }

    private void stopLoading() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }

    private void initListData() {
        listData = settingListData();
//        listData.setCatalogUrl("http://baricata.bari-lab.com/takashimayaan/");
        listData.setCatalogUrl(mAppSetting.getNetCataloglistURL());
        listData.setParentActivity(this);
        listData.setCallback(new CatalogListData.Callback() {
            @Override
            public void downloadNaviLogoImageCompleted(Bitmap bmp) {
                CatalogListNavigationView navigationView = (CatalogListNavigationView) findViewById(R.id.navigationView);
                navigationView.setTitleImageLogo(bmp);
            }

            @Override
            public void downloadCompleted() {
                showStartPage();
                stopLoading();
            }

            @Override
            public void downloadFailed() {
                stopLoading();
                showErrorPage();
            }
        });
    }

    private CatalogListData settingListData() {
        mMain.setListData(null);
        return mChild ? new CatalogListData() : new CoverFlowData();
    }

    public static void startActivity(Activity activity, String url, String title) {
        startActivity(activity, url, title, -1);
    }

    public static void startActivity(Activity activity, int flag) {
        startActivity(activity, null, null, flag);
    }

    public static void startActivity(Activity activity, String url, String title, int flag) {
        Intent intent = new Intent(activity, WebTopActivity.class);
        if (url != null) intent.putExtra("StartUrl", url);
        if (title != null) intent.putExtra("title", title);
        if (flag > 0) intent.setFlags(flag);
        activity.startActivity(intent);
    }

    private void showStartPage() {
        LogUtil.d(TAG, "loadUrl url=" + mStartUrl);
        mWebView.loadUrl(mStartUrl);
    }

    private void showErrorPage() {
        mWebView.loadUrl("file:///android_asset/html/error.html");
    }

    private String settingName() {
        if (mChild) {
            return "cataloglist.csv";
        } else {
            return "cataloglist.csv";
        }
    }

    private void initWebView() {
        // WebViewの設定
        mWebView = (WebView) findViewById(R.id.browserView);
        // JavaScriptを有効
        mWebView.getSettings().setJavaScriptEnabled(true);
        //Web Storageを有効
        mWebView.getSettings().setDomStorageEnabled(true);
        // ズームを有効
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setSupportZoom(false);
        // ズーム時に崩れるのを抑制
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        // 余白(スクロールバー領域)を消して被せる
        mWebView.setVerticalScrollbarOverlay(true);
        mWebView.setHorizontalScrollbarOverlay(true);
        mWebView.addJavascriptInterface(new JsClass(), "android");
        // イベント
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                LogUtil.d(TAG, consoleMessage.message() + "--From line " + consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return true;
            }
        });
        // イベント
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtil.d(TAG, "WebViewClient: onPageStarted");
                super.onPageStarted(view, url, favicon);
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                LogUtil.d(TAG, "WebViewClient: onReceivedError");
                super.onReceivedError(view, errorCode, description, failingUrl);
                showErrorPage();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtil.d(TAG, "WebViewClient: onPageFinished");
                stopLoading();
                super.onPageFinished(view, url);
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            public boolean transit(WebView webView, String url) {
                if (url.startsWith("reload://")) {
//                    showStartPage();
                    listData.downloadSettingForWebTop();
                    return true;
                }

                if (url.startsWith("baricata://")) {
                    if (mMode != 0) {
                        // 連打対策、0以外の場合は既に処理を受け付けているので何もしない
                        return true;
                    }

                    String params = url.substring("baricata://".length());
                    String[] paramArray = params.split("::");

                    if (paramArray.length < 1 || paramArray[0] == null) {
                        LogUtil.e(TAG, "Invalid Param! in shouldOverrideUrlLoading.");
                        return true;
                    }

                    mMode = Integer.valueOf(paramArray[0]);
                    LogUtil.d(TAG, "shouldOverrideUrlLoading mode=" + mMode);

                    MainApplication app = (MainApplication) WebTopActivity.this.getApplication();
                    CheckAction ca = app.getCheckAction();

                    switch (mMode) {
                        case CatalogListSetting.MODE_RESERVATION: {
                            String filterId;
                            if (paramArray.length < 2 || paramArray[1] == null) {
                                LogUtil.e(TAG, "Invalid Param! in shouldOverrideUrlLoading mode=" + mMode);
                                filterId = "";
                            } else {
                                filterId = paramArray[1];
                            }
                            Intent intent = new Intent(WebTopActivity.this, ActivityReservation.class);
                            intent.putExtra("filter_id", filterId);
                            startActivity(intent);
                            break;
                        }
                        case CatalogListSetting.MODE_URL_SCHEMA: {
                            // 次画面起動 外部アプリ起動
                            if (paramArray.length < 2 || paramArray[1] == null) {
                                LogUtil.e(TAG, "Invalid Param! in shouldOverrideUrlLoading mode=" + mMode);
                                break;
                            }

                            ca.sendAppTrackFromWebtop(WebTopActivity.this.mGaScreenName, mMode, null, paramArray[1], null);
                            Uri uri = Uri.parse(paramArray[1]);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            break;
                        }
                        case CatalogListSetting.MODE_URL: {
                            // 次画面起動 内部ブラウザ起動
                            if (paramArray.length < 2 || paramArray[1] == null) {
                                LogUtil.e(TAG, "Invalid Param! in shouldOverrideUrlLoading mode=" + mMode);
                                break;
                            }
                            if ((paramArray[1].startsWith("http:") || paramArray[1].startsWith("https:"))
                                    && !paramArray[1].startsWith("https://play.google.com/")) {
                                mParam = paramArray[1];
                                ca.sendAppTrackFromWebtop(WebTopActivity.this.mGaScreenName, mMode, null, paramArray[1], null);
                                webView.loadUrl("javascript:getTitle()");
                                return true;
                            } else {
                                //Google Play は外部ブラウザで表示
                                ca.sendAppTrackFromWebtop(WebTopActivity.this.mGaScreenName, mMode, null, paramArray[1], null);
                                Uri uri = Uri.parse(paramArray[1]);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                break;
                            }
                        }
                        case CatalogListSetting.MODE_CATALOG: {
                            // 次画面起動 カタログ画面遷移
                            if (paramArray.length < 3 || paramArray[1] == null || paramArray[2] == null) {
                                LogUtil.e(TAG, "Invalid Param! in shouldOverrideUrlLoading mode=" + mMode);
                                break;
                            }
                            ca.sendAppTrackFromWebtop(WebTopActivity.this.mGaScreenName, mMode, paramArray[1], null, null);

                            CatalogActivity.startActivity(WebTopActivity.this, paramArray[1], paramArray[2], 0);
                            break;
                        }
                        case CatalogListSetting.MODE_MAP: {
                            // 次画面起動 地図
                            String filterId;
                            if (paramArray.length < 2 || paramArray[1] == null) {
                                LogUtil.e(TAG, "Invalid Param! in shouldOverrideUrlLoading mode=" + mMode);
                                filterId = "";
                            } else {
                                filterId = paramArray[1];
                            }
                            ca.sendAppTrackFromWebtop(WebTopActivity.this.mGaScreenName, mMode, paramArray[1], null, null);
                            MapActivity.startActivity(WebTopActivity.this, filterId, false);
                            break;
                        }
                        case CatalogListSetting.MODE_MAP_LIST: {
                            // 次画面起動 地図
                            if (paramArray.length < 2 || paramArray[1] == null) {
                                LogUtil.e(TAG, "Invalid Param! in shouldOverrideUrlLoading mode=" + mMode);
                                break;
                            }
                            ca.sendAppTrackFromWebtop(WebTopActivity.this.mGaScreenName, mMode, paramArray[1], null, null);
                            MapActivity.startActivity(WebTopActivity.this, paramArray[1], true);
                            break;
                        }
                        case CatalogListSetting.MODE_CATALOG_LIST: {
                            // 次画面起動 カタログ一覧
                            if (paramArray.length < 2 || paramArray[1] == null) {
                                mParam = "";
                            } else {
                                mParam = paramArray[1];
                            }
                            ca.sendAppTrackFromWebtop(WebTopActivity.this.mGaScreenName, mMode, mParam, null, null);
                            webView.loadUrl("javascript:getTitle()");
                            return true;
                        }
                        case CatalogListSetting.MODE_CATALOG_LIST_WEB: {
                            // 次画面起動 カタログ画面遷移
                            if (paramArray.length < 2 || paramArray[1] == null) {
                                LogUtil.e(TAG, "Invalid Param! in shouldOverrideUrlLoading mode=" + mMode);
                                break;
                            }
                            mParam = paramArray[1];
                            ca.sendAppTrackFromWebtop(WebTopActivity.this.mGaScreenName, mMode, paramArray[1], null, null);
                            webView.loadUrl("javascript:getTitle()");
                            return true;
                        }
                        case CatalogListSetting.MODE_NEWS: {
                            // 次画面起動 ニュース起動
                            if (paramArray.length < 2 || paramArray[1] == null) {
                                LogUtil.e(TAG, "Invalid Param! in shouldOverrideUrlLoading mode=" + mMode);
                                break;
                            }
                            ca.sendAppTrackFromWebtop(WebTopActivity.this.mGaScreenName, mMode, paramArray[1], null, null);
                            NewsActivity.startActivity(WebTopActivity.this, paramArray[1], MainApplication.getInstance().getAppId());
                            break;
                        }
                        case CatalogListSetting.MODE_NEWS_MENU: {
                            Push.showListMessages();
                            break;
                        }
                        default:
                            break;
                    }
                    mMode = 0;

                }
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                LogUtil.d(TAG, "shouldOverrideUrlLoading url=" + url);
                boolean rtn = false;
                try {
                    rtn = this.transit(webView, url);
                } catch (ActivityNotFoundException anfException) {
                    Log.e(TAG, anfException.getMessage(), anfException);
                } catch (Exception exception) {
                    Log.e(TAG, exception.getMessage(), exception);
                }
                if (!rtn && url != null && url.startsWith("baricata://")) {
                    // baricata スキーマを内部ブラウザで開こうとしている場合は何もしない
                    LogUtil.e(TAG, "baricata:// を内部ブラウザで開こうとしたためURLのロードを中止します");
                    return true;
                }
                return rtn;
            }
        });
    }

    /**
     * JavaScript用メソッド定義インナークラス
     */
    public class JsClass {
        /**
         * JSへのデータ受け渡し処理
         *
         * @return
         */
        @JavascriptInterface
        public void setTitle(String title) {
            LogUtil.d(TAG, "setTitle tile=" + title);

            switch (mMode) {
                case CatalogListSetting.MODE_CATALOG_LIST: {
                    String[] tags = null;
                    if (mParam == null || mParam.isEmpty()) {
                        mParam = null;
                    } else {
                        tags = new String[]{mParam};
                    }
                    CatalogListActivity.startActivity(WebTopActivity.this, tags, title, null);
                    break;
                }
                case CatalogListSetting.MODE_CATALOG_LIST_WEB: {
                    startActivity(WebTopActivity.this, mParam, title);
                    break;
                }
                case CatalogListSetting.MODE_URL: {
                    Intent intent = new Intent(WebTopActivity.this, WebViewActivity.class);
                    intent.putExtra("StartUrl", mParam);
                    if (!title.equals("undefined")) {
                        intent.putExtra("title", title);
                    }
                    LogUtil.d(TAG, "MODE_URL");
                    startActivity(intent);
                    break;
                }
                case CatalogListSetting.MODE_NEWS_MENU: {
                    Push.showListMessages();
                    break;
                }
                default:
                    break;
            }
        }

    }

    private void initPushNotification() {
        PackageManager packageManager = this.getPackageManager();
        try {
            info = packageManager.getActivityInfo(this.getComponentName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //regsiter token and accept push
        if (!Push.isRegister(this)) {
            // register app
            Push.register(CONST_SENDER_ID, BUNDLEID);
            Push.setStatusPush();
            Push.registerAccpetPush(BUNDLEID, "1");
            Push.setAccpetMessagePopup("1");
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            Push.registerNotification(getResources().getString(R.string.app_name), icon, info.name);
        }
    }

    @Override
    public void onReceivedMessageList(PushMessage pushMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //set number badge into button
                showBadgeAndPopup();
            }
        });
    }

    @Override
    public void onLoadMessageList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //set number badge into button
                showBadge();
            }
        });
    }

    private void showBadge() {
        if (Integer.parseInt(Push.getBadgeCount()) > 0) {
            //show content badge if number message unread > 0
//            content_badge_number.setVisibility(View.VISIBLE);
            //display number message unread
//            badge_num.setText(Push.getBadgeCount());
        } else {
            //hide content badge if number message unread <= 0
//            content_badge_number.setVisibility(View.GONE);
        }
    }

    private void showBadgeAndPopup() {
        //show content badge
//        content_badge_number.setVisibility(View.VISIBLE);
        //display number message unread
//        badge_num.setText(Push.getBadgeCount());
        //show popup new
        Push.showPopupNewMessage(WebTopActivity.this);
    }

    public void initbtnLogin() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check_Login) {
                    startActivity(new Intent(WebTopActivity.this, ActivityLogin.class));
                } else {
                    check_Login = false;
                    startActivity(new Intent(WebTopActivity.this, ActivityLogin.class));
                }
            }
        });
    }
}
