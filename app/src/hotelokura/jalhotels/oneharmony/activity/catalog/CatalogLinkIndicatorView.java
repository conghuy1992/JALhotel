package hotelokura.jalhotels.oneharmony.activity.catalog;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

/**
 * ペーリリンクインジケータ
 * 
 * <pre>
 * setup()メソッドで起動する。
 * 通常非表示だが、以下の条件で点滅表示される。点滅終了後は再び非表示となる。
 * 点滅中にクリックすると内部ブラウザ遷移を行う。
 * 1.ID_settings.plist - UsePageLinkが「Yes」の時だけ、表示して点滅させる。　
 * 2.ID_settings.plist - PageLinkBlinkTimes（点滅回数）が1以上。
 * </pre>
 */
public class CatalogLinkIndicatorView extends LinearLayout {
	static final String TAG = "CatalogLinkIndicatorView";

	private int flashingNum; // 点滅回数
	private long duration = 1000; // 点滅時間

	private AlphaAnimation animation = null;

	public CatalogLinkIndicatorView(Context context) {
		super(context);
		init(context);
	}

	public CatalogLinkIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_catalog_link_indicator, this);
	}

	public void start(int flashingNum) {
		stop(); // 点滅中なら一旦停止させる
		this.flashingNum = flashingNum;
		if (flashingNum > 0) {
			startAnima();
		}
	}

	public void stop() {
		if (animation != null) {
			animation.setAnimationListener(null);
			animation = null;
		}
		clearAnimation();
	}

	@Override
	public void setVisibility(int visibility) {
		stop();
		super.setVisibility(visibility);
	}

	/**
	 * 点滅開始
	 */
	public void startAnima() {
		animation = new AlphaAnimation(0.0f, 1.0f);

		// ----------------------------------------
		// これらはsetVisibilityと競合するので、設定不可です。
		// alphg.setFillEnabled(true);
		// alphg.setFillBefore(false);
		// alphg.setFillAfter(true);
		// ----------------------------------------

		// 変化時間
		animation.setDuration(duration);

		// 繰り返し回数
		int repCount;
		if (flashingNum >= 1) {
			repCount = flashingNum;
			repCount = repCount * 2; // フェードイン・アウト調節
			repCount--; // 0スタート調節
			animation.setRepeatCount(repCount);
		}

		// リピート設定　フェードイン・アウト交互
		animation.setRepeatMode(Animation.REVERSE);

		// イベント
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(GONE); // 点滅終了で非表示にする。
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});

		// スタート
		startAnimation(animation);
	}
}
