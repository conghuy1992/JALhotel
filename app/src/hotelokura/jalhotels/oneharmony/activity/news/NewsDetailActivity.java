package hotelokura.jalhotels.oneharmony.activity.news;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.ActivitySkeleton;
import hotelokura.jalhotels.oneharmony.brand_preview.PreviewManager;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.util.AdManager;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.view.ProgressIndicatorDialog;

import java.util.HashMap;
import java.util.UUID;

/**
 * 単一ニュース画面
 */
public class NewsDetailActivity extends ActivitySkeleton implements View.OnTouchListener, ProgressIndicatorDialog.EnableStopLoading {
	static final String TAG = "NewsDetailActivity";

	private ProgressIndicatorDialog progress;
	private WebView webView;
    private ImageButton mBtnGoBack = null;
    private ImageButton mBtnGoForward = null;
    private Button mBtnReload = null;
    private Button mBtnOpenApp = null;
    private ImageButton mBtnBackToNewsList = null;
    private int mHideBrowserOperationBarCount = 0;

    private HashMap<String, String> mHttpHeader = null;

    private String mNewsAccount = null;
    private String mNewsListUrl = null;
    private String mNewsOneUrl  = null;
	private String mStartUrl    = null;
    private int mNewsDepth      = NewsActivity.INTENT_VAL_NEWS_DEPTH_1;
    private String mUuid        = null;
    private boolean isFinishing = false;
    private boolean mRequiredBrowserOperationBar = false;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

        MainApplication main = MainApplication.getInstance();
        AppSetting appSetting = main.getAppSetting();
        if (main == null || appSetting == null) {
			finish();
			return;
		}

        PreviewManager pm = PreviewManager.getInstance(this.getApplicationContext());
        if(!pm.isPreview() && appSetting.getUseAdMob()) {
            AdManager.startInterstitial(this);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news_detail);

        FrameLayout fl = (FrameLayout)this.findViewById(R.id.frame_overray);
        fl.bringToFront();
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.linear_overray);
        ll.bringToFront();
        View v = this.findViewById(R.id.view_overray);
        v.bringToFront();
        v.setOnTouchListener(this);

		// 前画面から情報取得
        this.mNewsAccount = getIntent().getStringExtra(NewsActivity.INTENT_KEY_ACCOUNT);
        this.mNewsListUrl = getIntent().getStringExtra(NewsActivity.INTENT_KEY_NEWS_LIST_URL);
        this.mNewsOneUrl  = getIntent().getStringExtra(NewsActivity.INTENT_KEY_NEWS_ONE_URL);
        this.mStartUrl    = getIntent().getStringExtra(NewsActivity.INTENT_KEY_FIRST_URL);
        this.mNewsDepth   = getIntent().getIntExtra(NewsActivity.INTENT_KEY_NEWS_DEPTH, NewsActivity.INTENT_VAL_NEWS_DEPTH_1);
        if(this.mStartUrl.equals(this.mNewsOneUrl)) {
            this.mRequiredBrowserOperationBar = false;
        } else {
            this.mRequiredBrowserOperationBar = true;
        }

//        LinearLayout ll = (LinearLayout) this.findViewById(R.id.browser_operation);
//        if(this.mNewsDepth == NewsActivity.INTENT_VAL_NEWS_DEPTH_1) {
//            ll.setVisibility(View.GONE);
//        } else {
//            ll.setVisibility(View.VISIBLE);
//        }

        // httpヘッダー
        this.mHttpHeader = new HashMap<String, String>();
        this.mHttpHeader.put("X-CMS-Account", this.mNewsAccount);
        this.mHttpHeader.put("X-App-UUID", this.mUuid);

        this.mBtnGoBack         = (ImageButton) this.findViewById(R.id.btnGoBack);
        this.mBtnGoForward      = (ImageButton) this.findViewById(R.id.btnGoForward);
        this.mBtnReload         = (Button) this.findViewById(R.id.btnReload);
        this.mBtnOpenApp        = (Button) this.findViewById(R.id.btnOpenApp);
        this.mBtnBackToNewsList = (ImageButton) this.findViewById(R.id.btnBacktoList);

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

                if (NewsDetailActivity.this.isFinishing) {
                    LogUtil.d(TAG, "THIS WEBVIEW IS FINISHED");
                    return true;
                }
                // ニュース記事の読込
                if (url.equals(NewsDetailActivity.this.mNewsOneUrl)) {
                    NewsDetailActivity.this.mRequiredBrowserOperationBar = false;
                    NewsDetailActivity.this.hideBrowserOperationBar();
                    webView.loadUrl(url, NewsDetailActivity.this.getHttpHeader());
                    return false;
                } else {
                    NewsDetailActivity.this.mRequiredBrowserOperationBar = true;
                }

                // GooglePlay のは別途intent発行するので
                // それを除いたhttpリクエストならそのまま遷移
                if ((url.startsWith("http:") || url.startsWith("https:"))
                        && !url.startsWith("https://play.google.com/") ) {
                    return false;
                }

                boolean transitChallenge = false;
                boolean requiredFinish = false;
                try {
                    Uri uri = Uri.parse(url);
                    // http,https以外(mailtoとか)は別Activityに投げる
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    LogUtil.d(TAG, "This Intent:" + intent);
                    NewsDetailActivity.this.isFinishing = true;
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
                    NewsDetailActivity.this.finish();
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
                } catch(ActivityNotFoundException anfException) {
                    Log.e(TAG, anfException.getMessage(), anfException);
                } catch(Exception exception) {
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
                if (progress == null) {
                    progress = new ProgressIndicatorDialog(NewsDetailActivity.this);
                }
                progress.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setProgressBarIndeterminateVisibility(false);

                if(url.equals(NewsDetailActivity.this.mNewsOneUrl)) {
                    NewsDetailActivity.this.hideBrowserOperationBar();
                }
                if (progress != null) {
                    progress.dismiss();
                    progress = null;
                }
            }

        });

		// Webページ表示
        webView.loadUrl(mStartUrl, this.mHttpHeader);

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
        this.showBrowserOperationBar();
	}

    private HashMap<String, String> getHttpHeader() {
        if(this.mHttpHeader == null) {
            this.mHttpHeader = new HashMap<String, String>();
        }
        String ac = this.mHttpHeader.get("X-CMS-Account");
        if(ac == null || ac.isEmpty()) {
            if(this.mNewsAccount == null || this.mNewsAccount.isEmpty()) {
                this.mNewsAccount = getIntent().getStringExtra(NewsActivity.INTENT_KEY_ACCOUNT);
            }
            this.mHttpHeader.put("X-CMS-Account", this.mNewsAccount);
        }
        String uid = this.mHttpHeader.get("X-App-UUID");
        if(uid == null || uid.isEmpty()) {
            if(this.mUuid == null || this.mUuid.isEmpty()) {
                this.mUuid = this.getUuId();
            }
            this.mHttpHeader.put("X-App-UUID", this.mUuid);
        }
        return this.mHttpHeader;
    }

    private void setEnabled(Button btn, boolean enabled) {
        btn.setEnabled(enabled);
        if(enabled) {
            btn.setTextColor(0xffffffff);
        } else {
            btn.setTextColor(0x66666666);
        }
    }

    private String getUuId() {

        SharedPreferences preferences = getSharedPreferences("newsapp", MODE_PRIVATE);
        this.mUuid = preferences.getString("uuid", null);
        if (this.mUuid == null) {
            this.mUuid = UUID.randomUUID().toString();
            preferences.edit().putString("uuid", this.mUuid).commit();
        }

        return this.mUuid;
    }

    public void onClickBackToList(View v) {
        this.isFinishing = true;
        this.finish();
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
        FrameLayout fl = (FrameLayout) this.findViewById(R.id.layout);
        if(fl != null) {
            fl.removeAllViews();
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

    public void onClickGoBack(View v) {
        if(this.webView.canGoBack()) {
            WebBackForwardList mWebBackForwardList = this.webView.copyBackForwardList();
            String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()-1).getUrl();
            if(historyUrl.equals(this.mNewsOneUrl)) {
                this.mRequiredBrowserOperationBar = false;
            } else {
                this.mRequiredBrowserOperationBar = true;
            }
            this.webView.goBack();
        }
    }

    public void onClickGoForward(View v) {
        if(this.webView.canGoForward()) {
            this.webView.goForward();
        }
    }

    public void onClickOpenApp(View v) {
        Uri uri = Uri.parse(webView.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void onClickReload(View v) {
        this.webView.reload();
    }

    void showBrowserOperationBar() {

        if(!this.mRequiredBrowserOperationBar) {
            return;
        }
        this.mHideBrowserOperationBarCount++;
        final LinearLayout ll = (LinearLayout) this.findViewById(R.id.browser_operation);
        ll.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NewsDetailActivity.this.hideBrowserOperationBar();
            }
        }, 2000l);
    }

    void hideBrowserOperationBar() {
        if(this.mHideBrowserOperationBarCount > 1) {
            this.mHideBrowserOperationBarCount--;
        } else {
            this.mHideBrowserOperationBarCount = 0;
            final LinearLayout ll = (LinearLayout) this.findViewById(R.id.browser_operation);
            if(ll != null) {
                ll.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        this.showBrowserOperationBar();
        return false;
    }

    @Override
    public void stopLoading() {
//        if(this.webView != null) {
//            this.webView.stopLoading();
//        }
    }
}
