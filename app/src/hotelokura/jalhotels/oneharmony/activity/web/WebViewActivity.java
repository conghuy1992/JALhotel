package hotelokura.jalhotels.oneharmony.activity.web;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import hotelokura.jalhotels.oneharmony.JSONParser;
import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.ActivitySkeleton;
import hotelokura.jalhotels.oneharmony.util.ButtonUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.view.ProgressIndicatorDialog;
import hotelokura.jalhotels.oneharmony.activity.catalog.CatalogActivity;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;

import java.io.IOException;


/**
 * 内部ブラウザ画面
 */
public class WebViewActivity extends ActivitySkeleton {
    static final String TAG = "WebViewActivity";
    private ProgressIndicatorDialog progress;
    public static WebView webView;

    private String startUrl;
    private String mTitle;
    private boolean isFinishing = false;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_NEWS1 = 1;
    public static final int MODE_NEWS2 = 2;
    private int mMode = WebViewActivity.MODE_NORMAL;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        MainApplication main = MainApplication.getInstance();
        if (main == null || main.getAppSetting() == null) {
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web);

        // 前画面から情報取得
        startUrl = getIntent().getStringExtra("StartUrl");
        mTitle = getIntent().getStringExtra("title");
        this.mMode = this.getIntent().getIntExtra("mode", WebViewActivity.MODE_NORMAL);

        // ナビゲーションの設定
        // 戻るボタン
        WebNavigationView navigationView = (WebNavigationView) findViewById(R.id.navigationView);
        navigationView.setBackButtionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 外部で開くボタン
        navigationView.setOpenButtionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 次画面起動 外部アプリ起動
                Uri uri = Uri.parse(webView.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        // ツールバーの設定
        // ニュースの場合は非表示、その他は表示
        WebToolbarView wtv = (WebToolbarView) this.findViewById(R.id.toolbarView);
        if (this.mMode == WebViewActivity.MODE_NEWS1) {
            wtv.setVisibility(View.GONE);
        } else {
            wtv.setVisibility(View.VISIBLE);
        }

        // 戻るボタン
        final ImageButton webBackButton = (ImageButton) findViewById(R.id.webBackButton);
        webBackButton.setEnabled(false);
        webBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.goBack();
            }
        });

        // 進むボタン
        final ImageButton webForwardButton = (ImageButton) findViewById(R.id.webForwardButton);
        webForwardButton.setEnabled(false);
        webForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.goForward();
            }
        });

        //タイトル
        if (mTitle != null && mTitle.length() > 0) {
            navigationView.setTitle(mTitle);
        }

        // 中止ボタン
        ImageButton webStopButton = (ImageButton) findViewById(R.id.webStopButton);
        webStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.stopLoading();
            }
        });

        // 更新ボタン
        ImageButton webReloadButton = (ImageButton) findViewById(R.id.webReloadButton);
        webReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });

        // ボタン状態
        ButtonUtil.drawableEnabled(webBackButton, false);
        ButtonUtil.drawableEnabled(webForwardButton, false);

        // WebViewの設定
        webView = (WebView) findViewById(R.id.browserView);
        // JavaScriptを有効
        webView.getSettings().setJavaScriptEnabled(true);

        //Web Storageを有効
        webView.getSettings().setDomStorageEnabled(true);
        // ズームを有効
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        // ズーム時に崩れるのを抑制
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        // 余白(スクロールバー領域)を消して被せる
        webView.setVerticalScrollbarOverlay(true);
        webView.setHorizontalScrollbarOverlay(true);
        // イベント
        webView.setWebViewClient(new WebViewClient() {
            public boolean transit(WebView webView, String url) {
                if (WebViewActivity.this.isFinishing) {
                    LogUtil.d(TAG, "THIS WEBVIEW IS FINISHED");
                    return true;
                }
                if ((url.startsWith("http:") || url.startsWith("https:"))
                        && !url.startsWith("https://play.google.com/")) {
                    return false;
                }

                boolean transitChallenge = false;
                boolean requiredFinish = false;
                try {
                    Uri uri = Uri.parse(url);
                    // http,https以外(mailtoとか)は別Activityに投げる
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    LogUtil.d(TAG, "This Intent:" + intent);
                    WebViewActivity.this.isFinishing = true;
                    startActivity(intent);
                    transitChallenge = true;
                    requiredFinish = true;
                } catch (android.content.ActivityNotFoundException e) {
                    Log.e(TAG, "", e);
                }

                // 失敗したら現在のURLでintent発行
                if (!transitChallenge) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webView.getUrl()));
                        startActivity(intent);
                        requiredFinish = true;
                    } catch (android.content.ActivityNotFoundException e) {
                        Log.e(TAG, "", e);
                    }
                }

                if (requiredFinish) {
                    WebViewActivity.this.finish();
                    return true;
                } else {
                    return false;
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                LogUtil.d(TAG, "WEBVIEW URL:" + url);
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

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setProgressBarIndeterminateVisibility(true);

                // ボタン状態
                ButtonUtil.drawableEnabled(webBackButton, webView.canGoBack());
                ButtonUtil.drawableEnabled(webForwardButton,
                        webView.canGoForward());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setProgressBarIndeterminateVisibility(false);

                if (progress != null) {
                    progress.dismiss();
                    progress = null;
                }

                if (!url.equals(WebViewActivity.this.startUrl)
                        && WebViewActivity.this.mMode == WebViewActivity.MODE_NEWS1) {
                    // ニュース個別 から read more へ移動したとき
                    WebViewActivity.this.mMode = WebViewActivity.MODE_NEWS2;

                } else if (url.equals(WebViewActivity.this.startUrl)) {
                    // ニュース個別を開いたときや、
                    // read more などから ニュース個別 に戻ったとき
                    WebViewActivity.this.mMode = WebViewActivity.MODE_NEWS1;

                }

                // ニュース個別の場合は非表示、その他は表示
                WebToolbarView wtv = (WebToolbarView) WebViewActivity.this.findViewById(R.id.toolbarView);
                if (WebViewActivity.this.mMode == WebViewActivity.MODE_NEWS1) {
                    wtv.setVisibility(View.GONE);
                } else {
                    wtv.setVisibility(View.VISIBLE);
                }

                // ボタン状態
                ButtonUtil.drawableEnabled(webBackButton, webView.canGoBack());
                ButtonUtil.drawableEnabled(webForwardButton,
                        webView.canGoForward());

                //Start JavaScript after page finished loading
                if (CatalogActivity.catalogSetting != null && CatalogActivity.catalogSetting.getUseEndPageScript()) {
                    view.getSettings().setJavaScriptEnabled(true);
                    //view.evaluateJavascript("$('#catalog-set-'+(page.cr+1)).scrollTop(0);",null);

                    new endPageScriptAsync().execute();
                    //view.addJavascriptInterface(new JsClass(view.getContext()), "Android");
                    //view.setWebChromeClient(new WebChromeClient());
                    //view.loadUrl(CatalogActivity.catalogSetting.getUseEndPageScriptURL());
                }
            }
        });

        // Webページ表示

        if (startUrl.equals("loginfromnative")) {
            String user = getIntent().getStringExtra("user");
            String pw = getIntent().getStringExtra("pw");
            String postData = "UserName=" + user + "&Password=" + pw + "&RememberMe=false";
            webView.postUrl(JSONParser.API_LOGIN, EncodingUtils.getBytes(postData, "base64"));
        } else {
            webView.loadUrl(startUrl);
        }

        // ロード中インジケータを表示
        progress = new ProgressIndicatorDialog(this);
        progress.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (progress != null) {
                    progress.dismiss();
                    progress = null;
                }
                finish();
            }
        });
        progress.show();
    }

    @Override
    protected void onStart() {
        LogUtil.d(TAG, "onStart");
        super.onStart();
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

        if (webView != null) {
            ViewGroup layout = (ViewGroup) findViewById(R.id.layout);
            layout.removeView(webView);
            webView.stopLoading();
            webView.setWebViewClient(null);
            webView.setWebChromeClient(null);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
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

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.news_slide_left_enter, R.anim.news_slide_right_exit);
    }

    class endPageScriptAsync extends AsyncTask<Void, Integer, String> {
        protected void onPreExecute() {
            Log.d("PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            Log.d("DoINBackGround", "On doInBackground...");
            String response_str = "";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(CatalogActivity.catalogSetting.getUseEndPageScriptURL());
                // Get the response
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    response_str = client.execute(request, responseHandler);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                System.out.println("Some error occured.");
            }

            return response_str;
        }

        protected void onPostExecute(String result) {
            webView.loadUrl("javascript:" + result);
        }
    }

}
