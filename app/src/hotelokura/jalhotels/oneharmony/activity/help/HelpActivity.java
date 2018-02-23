package hotelokura.jalhotels.oneharmony.activity.help;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.ActivitySkeleton;
import hotelokura.jalhotels.oneharmony.activity.info.InfoActivity;
import hotelokura.jalhotels.oneharmony.setting.CatalogSetting;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

/**
 * ヘルプ画面
 */
public class HelpActivity extends ActivitySkeleton {
	static final String TAG = "HelpActivity";

	private String infoMessageTitle;
	private String infoMessageSub1;
	private String infoMessageSub2;
	private String infoLinkURL;

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
		setContentView(R.layout.activity_help);

		// 前画面から情報取得
		infoMessageTitle = getIntent().getStringExtra("InfoMessageTitle");
		infoMessageSub1 = getIntent().getStringExtra("InfoMessageSub1");
		infoMessageSub2 = getIntent().getStringExtra("InfoMessageSub2");
		infoLinkURL = getIntent().getStringExtra("InfoLinkURL");

		// ナビゲーションの設定
		HelpNavigationView navi = (HelpNavigationView) findViewById(R.id.navigationView);
		// 戻るボタン
		navi.setBackButtionListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// インフォメーションボタン
		navi.setInfoButtionListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intentInfoButton();
			}
		});

        // ツールバーボタンの表示非表示
        CatalogSetting setting = MainApplication.getInstance().getCurrentCatalogSetting();

        // 目次
        RelativeLayout rl_content = (RelativeLayout) findViewById(R.id.content);
        rl_content.setVisibility(View.GONE);

        // サムネイル
        RelativeLayout rl_thumbnail = (RelativeLayout) findViewById(R.id.thumbnail);
        rl_thumbnail.setVisibility(View.GONE);

        // しおり
        RelativeLayout rl_bookmark = (RelativeLayout) findViewById(R.id.bookmark);
        if (!setting.getUseBookmark()) rl_bookmark.setVisibility(View.GONE);

        // SNS
        RelativeLayout rl_sns = (RelativeLayout) findViewById(R.id.sns);
        rl_sns.setVisibility(View.GONE);

        // 動画
        RelativeLayout rl_movie = (RelativeLayout) findViewById(R.id.movie);
        rl_movie.setVisibility(View.GONE);

        // 問い合わせ
        RelativeLayout rl_mail = (RelativeLayout) findViewById(R.id.mail);
        rl_mail.setVisibility(View.GONE);

        // カート
        RelativeLayout rl_cart = (RelativeLayout) findViewById(R.id.cart);
        if (!setting.getUseCartLink()) rl_cart.setVisibility(View.GONE);

        // マップ
        RelativeLayout rl_map = (RelativeLayout) findViewById(R.id.map);
        if (!setting.getUseMapLink()) rl_map.setVisibility(View.GONE);

        // 外部リンク
        RelativeLayout rl_ext_link = (RelativeLayout) findViewById(R.id.ext_link);
        if (!setting.getUseExternalLink()) rl_ext_link.setVisibility(View.GONE);

        // ページリンク
        RelativeLayout rl_page_link = (RelativeLayout) findViewById(R.id.page_link);
        if (!setting.getUsePageLink()) rl_page_link.setVisibility(View.GONE);
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
	 * インフォメーション画面に遷移する
	 *
	 * @return
	 */
	private void intentInfoButton() {
		Intent intent = new Intent();
		intent.setClass(this, InfoActivity.class);
		intent.putExtra("InfoMessageTitle", infoMessageTitle);
		intent.putExtra("InfoMessageSub1", infoMessageSub1);
		intent.putExtra("InfoMessageSub2", infoMessageSub2);
		intent.putExtra("InfoLinkURL", infoLinkURL);
		startActivity(intent);
	}

}
