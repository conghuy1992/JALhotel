package hotelokura.jalhotels.oneharmony.activity.news;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.analytics.CheckAction;
import hotelokura.jalhotels.oneharmony.brand_preview.PreviewManager;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.util.AdManager;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@SuppressLint("SetJavaScriptEnabled")
public class NewsActivity extends Activity {
    static final String TAG = "NewsActivity";

    public static final String INTENT_KEY_ACCOUNT = "cms_account";
    // ニュースリストURL
    public static final String INTENT_KEY_NEWS_LIST_URL = "NEWS_LIST_URL";
    // ニュース単独URL
    public static final String INTENT_KEY_NEWS_ONE_URL = "NEWS_ONE_URL";
    // start url
    public static final String INTENT_KEY_FIRST_URL = "NEWS_FIRST_URL";

    // ニュース単独ページか、その他かを示す(int 1 or 2)のキー
    public static final String INTENT_KEY_NEWS_DEPTH = "NEWS_DEPTH";
    //  ニュース単独ページを示す
    public static final int INTENT_VAL_NEWS_DEPTH_1 = 1;
    // ニュース単独ページ以外を示す
    public static final int INTENT_VAL_NEWS_DEPTH_2 = 2;

    private String mNewsListUrl = null;

    private String uuid;
    private String mNewsAccount = null;
    private Activity activity;
    private PullToRefreshWebView pullToRefreshWebView;
    private WebView webView;
    private ProgressDialog progressDialog;
    private Map<String, String> headers;
    private boolean pullToRefresh;
    private String newsHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        // String url = getIntent().getStringExtra(INTENT_KEY_NEWS_LIST_URL);
        AppSetting appSetting = MainApplication.getInstance().getAppSetting();
        String url = appSetting.getNewsURL();
        this.mNewsListUrl = url;
        this.mNewsAccount = getIntent().getStringExtra(INTENT_KEY_ACCOUNT);

        LogUtil.d(TAG, "url=" + url);
        LogUtil.d(TAG, "account=" + this.mNewsAccount);

        PreviewManager pm = PreviewManager.getInstance(this.getApplicationContext());
        if (!pm.isPreview()) {
            AdManager.startInterstitial(this);
        }

        try {
            URI uri = new URI(url);
            newsHost = uri.getHost();
        } catch (URISyntaxException e) {
            newsHost = "";
        }

        SharedPreferences preferences = getSharedPreferences("newsapp", MODE_PRIVATE);
        uuid = preferences.getString("uuid", null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            preferences.edit().putString("uuid", uuid).commit();
        }

        headers = new HashMap<String, String>();
        headers.put("X-CMS-Account", this.mNewsAccount);
        headers.put("X-App-UUID", uuid);

        webView = new WebView(this);
        setContentView(webView);

        pullToRefreshWebView = new PullToRefreshWebView(this);
        pullToRefreshWebView.setOnRefreshListener(new OnRefreshListener<WebView>() {
            @Override
            public void onRefresh(PullToRefreshBase<WebView> refreshView) {
                pullToRefresh = true;
                refreshView.getRefreshableView().reload();

            }
        });
//		webView = new WebView(this);
        setContentView(pullToRefreshWebView);
        webView = pullToRefreshWebView.getRefreshableView();
//		setContentView(webView);
        setupWebView(webView);

        webView.loadUrl(url, headers);

        // API LEVEL 11以上でアクションバーを変更する
        if (Build.VERSION.SDK_INT >= 11) {
            ActionBar acb = this.getActionBar();

            if (appSetting.getUseWebTop() && appSetting.getWebTopLayout().equals(3)) {
                // ニューステンプレートの場合は非表示
                if (acb != null) {
                    acb.hide();
                }
            }
            if (acb != null) {
                acb.setTitle("");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_close) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= 11) {
            webView.getSettings().setDisplayZoomControls(false);
        }
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (Throwable t) {
                // do nothing
            } finally {
                progressDialog = null;
            }
        }
    }

    class MyWebViewClient extends android.webkit.WebViewClient {
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            closeProgressDialog();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("WW", url);
            super.onPageFinished(view, url);
            closeProgressDialog();
            pullToRefreshWebView.onRefreshComplete();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (progressDialog == null && pullToRefresh == false) {
                progressDialog = ProgressDialog.show(activity, "",
                        "loading...", false);
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        public boolean transit(WebView view, String url) {
            if (url.startsWith("mailto:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("close:")) {
                finish();
                return true;
            } else if (url.startsWith("http://") || url.startsWith("https://")) {

                String[] split_item = url.split("/");
                String item_id = split_item[split_item.length - 1];
                MainApplication app = (MainApplication) NewsActivity.this.getApplication();
                CheckAction ca = app.getCheckAction();
                ca.sendAppTrackFromNews(null, item_id);

                Intent intent = new Intent(NewsActivity.this, NewsDetailActivity.class);
                intent.putExtra(NewsActivity.INTENT_KEY_ACCOUNT, NewsActivity.this.mNewsAccount);
                intent.putExtra(NewsActivity.INTENT_KEY_NEWS_LIST_URL, NewsActivity.this.mNewsListUrl);
                intent.putExtra(NewsActivity.INTENT_KEY_FIRST_URL, url);
                if (!url.endsWith("#style2")) {
                    intent.putExtra(NewsActivity.INTENT_KEY_NEWS_DEPTH, NewsActivity.INTENT_VAL_NEWS_DEPTH_2);
                } else {
                    intent.putExtra(NewsActivity.INTENT_KEY_NEWS_ONE_URL, url);
                    intent.putExtra(NewsActivity.INTENT_KEY_NEWS_DEPTH, NewsActivity.INTENT_VAL_NEWS_DEPTH_1);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.news_slide_right_enter, R.anim.news_slide_left_exit);

                return true;

            } else {
                return false;
            }

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean rtn = false;
            try {
                rtn = this.transit(view, url);
            } catch (ActivityNotFoundException anfException) {
                Log.e(TAG, anfException.getMessage(), anfException);
            }
            if (!rtn && url != null && url.startsWith("baricata://")) {
                // baricata スキーマを内部ブラウザで開こうとしている場合は何もしない
                LogUtil.e(TAG, "baricata:// を内部ブラウザで開こうとしたためURLのロードを中止します");
                return true;
            }
            return rtn;
        }

    }

    class MyWebChromeClient extends android.webkit.WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            activity.setProgress(progress * 1000);
        }
    }

    public static void startActivity(Activity activity, String url, String account) {
        startActivity(activity, url, account, -1);
    }

    public static void startActivity(Activity activity, String url, String account, int flag) {
        Intent intent = new Intent(activity, NewsActivity.class);
        intent.putExtra(INTENT_KEY_NEWS_LIST_URL, url);
        intent.putExtra(INTENT_KEY_ACCOUNT, account);
        if (flag > 0) intent.setFlags(flag);
        activity.startActivity(intent);
    }

}
