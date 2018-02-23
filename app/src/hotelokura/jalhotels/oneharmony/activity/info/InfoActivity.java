package hotelokura.jalhotels.oneharmony.activity.info;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.ActivitySkeleton;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListActivity;
import hotelokura.jalhotels.oneharmony.activity.coverflow.CoverFlowActivity;
import hotelokura.jalhotels.oneharmony.activity.web.WebViewActivity;
import hotelokura.jalhotels.oneharmony.activity.webtop.WebTopActivity;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.util.DrawableUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

/**
 * インフォメーション画面
 */
public class InfoActivity extends ActivitySkeleton {
	static final String TAG = "InfoActivity";

	private String infoMessageTitle;
	private String infoMessageSub1;
	private String infoMessageSub2;
	private String infoLinkURL;
	private String appUrlLink;
	private String appCopyright;
	private boolean appCacheClearEnabled;
    private MainApplication mMain;
    private AppSetting mAppSetting;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

        mMain = MainApplication.getInstance();
		if (mMain == null || mMain.getAppSetting() == null) {
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_info);

		// 前画面から情報取得
		infoMessageTitle = getIntent().getStringExtra("InfoMessageTitle");
		infoMessageSub1 = getIntent().getStringExtra("InfoMessageSub1");
		infoMessageSub2 = getIntent().getStringExtra("InfoMessageSub2");
		infoLinkURL = getIntent().getStringExtra("InfoLinkURL");

		// appSettingから情報取得
        mAppSetting = mMain.getAppSetting();
		appUrlLink = mAppSetting.getBaricataLinkURL();
		appCopyright = mAppSetting.getCopyright();
		appCacheClearEnabled = mAppSetting.getCacheClearButtonEnabled();

		// ナビゲーションの設定
		InfoNavigationView navigationView = (InfoNavigationView) findViewById(R.id.navigationView);
		// 戻るボタン
		navigationView.setBackButtionListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// Infoタイトル
		TextView titleView = (TextView) findViewById(R.id.infoTitleView);
		titleView.setText(infoMessageTitle);
		// Infoサブ1
		TextView sub1View = (TextView) findViewById(R.id.infoMsg1View);
		SpannableStringBuilder spaText1 = textViewCenter(infoMessageSub1);
		sub1View.setText(spaText1);
		// Infoサブ2
		TextView sub2View = (TextView) findViewById(R.id.infoMsg2View);
		SpannableStringBuilder spaText2 = textViewCenter(infoMessageSub2);
		sub2View.setText(spaText2);
		// copyright
		TextView copyrightView = (TextView) findViewById(R.id.copyrightView);
		copyrightView.setText(appCopyright);
		// Version
		TextView verView = (TextView) findViewById(R.id.appVersion);
		verView.setText("Ver " + mMain.getVersionName());

		// コンテンツ詳細情報ボタン
		Button accessButton = (Button) findViewById(R.id.infoAccessButton);
		accessButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intentBrowser(infoLinkURL);
			}
		});

		// アプリケーション著作者画像
		ViewGroup infoAppBanner = (ViewGroup) findViewById(R.id.infoAppBanner);
		infoAppBanner.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intentBrowser(appUrlLink);
			}
		});

		// キャッシュ削除ボタン
		int cacheClearBackColor = Color.LTGRAY;
		Button cacheClearButton = (Button) findViewById(R.id.cacheClearButton);
        if(MainApplication.getInstance().isTabletDevice() == true){
            cacheClearButton.setTextSize(11.0f);
        }
        // ロゴ画像
        if (!mMain.isBrand()) {
            ImageView imageView = (ImageView) findViewById(R.id.logo_image);
            imageView.setImageResource(R.drawable.baricata_logo);
        }


		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			cacheClearButton.setBackgroundDrawable(DrawableUtil
					.makeNavigationButtonDrawable(cacheClearBackColor));
		} else {
			cacheClearButton.setBackground(DrawableUtil
					.makeNavigationButtonDrawable(cacheClearBackColor));
		}
		if (appCacheClearEnabled == true) {
			// 表示
			cacheClearButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// ダイアログで確認メッセージを表示する。
					viewCacheAlertDialog();
				}
			});
		} else {
			// 非表示
			cacheClearButton.setVisibility(View.GONE);
		}
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
	}

	/**
	 * キャッシュ削除時の確認ダイアログ
	 */
	public void viewCacheAlertDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(getString(R.string.delete_cache));
		dialog.setMessage(R.string.delete_cache_message);

		// イベント実装メソッド（肯定・否定）が、ボタン位置の関係上、逆になっています！
		// イベント：キャンセルボタン
		dialog.setPositiveButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		// イベント：削除ボタン
		dialog.setNeutralButton(getString(R.string.delete),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// キャッシュ削除
						cacheDelete();
						// 通知ダイアログ
						viewCacheDelAlertDialog();
					}
				});

		dialog.setCancelable(false);
		dialog.create().show();
	}

	/**
	 * キャッシュ削除完了の通知ダイアログ
	 */
	public void viewCacheDelAlertDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(getString(R.string.delete_cache));
		dialog.setMessage(R.string.delete_cache_complete);

		// イベント：了解ボタン
		dialog.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 遷移処理
						intentCatalogList();
					}
				});

		dialog.setCancelable(false);
		dialog.create().show();
	}

	/**
	 * TextViewにおいて複数行表示するとき、一行ごとに中央寄せを行う
	 *
	 * <pre>
	 * TextView.setText(textViewCenter(String));
	 * </pre>
	 *
	 * @see http://itpaparazzi.sblog.jp/?p=746
	 * @param String
	 *            文字列
	 * @return　SpannableStringBuilder 失敗時はnull
	 */
	private SpannableStringBuilder textViewCenter(String text) {
		if (text == null) {
			return null;
		}

		SpannableStringBuilder spannable = new SpannableStringBuilder();
		int start = spannable.length();
		spannable.append(text);
		int end = spannable.length();

		AlignmentSpan.Standard span = new AlignmentSpan.Standard(
				Layout.Alignment.ALIGN_CENTER);
		spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	/**
	 * キャッシュを削除する
	 */
	private void cacheDelete() {
		// DiskCacheをクリア
		MainApplication mainApplication = mMain;
		mainApplication.removeCache();
	}

	/**
	 * 内部ブラウザ画面へ遷移する
	 *
	 * @param url
	 */
	private void intentBrowser(String url) {
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra("StartUrl", url);
		startActivity(intent);
	}

	/**
	 * カタログ一覧画面へ遷移する<br>
	 * キャッシュ削除後の遷移
	 */
	public void intentCatalogList() {
        int flag = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK;
        if (mAppSetting.getUseWebTop()) {
            if (mMain.getWebTopUrl() == null) {
                WebTopActivity.startActivity(this, flag);
            } else {
                WebTopActivity.startActivity(this, mMain.getWebTopUrl(), mMain.getWebTopTitle(), flag);
            }
            return;
        }
        
		if (mMain.getCurrentCatalogListTags() == null) {
			Intent intent = new Intent(this, CoverFlowActivity.class);
			intent.setFlags(flag);
			startActivity(intent);
		} else {
            CatalogListActivity.startActivity(this,
					mMain.getCurrentCatalogListTags(),
					mMain.getCurrentCatalogListTitle(),
					mMain.getCurrentCatalogListId(),
					flag);
		}
	}
}
