package hotelokura.jalhotels.oneharmony.activity.catalog;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.DrawableUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.view.FunctionButtonLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CatalogNavigationView extends RelativeLayout {
	static final String TAG = "CatalogNavigationView";

	public CatalogNavigationView(Context context) {
		super(context);
		init(context);
	}

	public CatalogNavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CatalogNavigationView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_catalog_navigation, this);

		int bgColor = getResources().getColor(R.color.bg_catalog_navi);
		ViewGroup layout = (ViewGroup) findViewById(R.id.navigationLayout);

		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			layout.setBackgroundDrawable(DrawableUtil
					.makeNavigationDrawable(bgColor));
		} else {
			layout.setBackground(DrawableUtil.makeNavigationDrawable(bgColor));
		}

        boolean dispText = MainApplication.getInstance().getAppSetting().getCatalogToolbarButtonText();
        if (!dispText) {
            FunctionButtonLayout homeButton = (FunctionButtonLayout) findViewById(R.id.homeButton);
            FunctionButtonLayout questionButton = (FunctionButtonLayout) findViewById(R.id.questionButton);
            homeButton.setButtonTextGone();
            questionButton.setButtonTextGone();
        }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	/**
	 * 設定 タイトル 文言
	 * 
	 * @param String
	 *            タイトル文言
	 * @return
	 */
	public void setTitle(String title) {
		TextView view = (TextView) findViewById(R.id.titleView);
		view.setText(title);
	}

	/**
	 * 設定 タイトル 文字色
	 * 
	 * @param String
	 *            タイトル文字色
	 * @return
	 */
	public void setTitleColor(int color) {
		TextView view = (TextView) findViewById(R.id.titleView);
		view.setTextColor(color);
	}

	/**
	 * 設定 イベント ホームボタン
	 * 
	 * @param OnClickListener
	 *            コールバック
	 * @return
	 */
	public void setHomeButtionListener(OnClickListener listener) {
		FunctionButtonLayout btn = (FunctionButtonLayout) findViewById(R.id.homeButton);
		btn.setButtonOnClickListener(listener);
	}

	/**
	 * 設定 イベント ホームボタン
	 * 
	 * @param OnClickListener
	 *            コールバック
	 * @return
	 */
	public void setHelpButtionListener(OnClickListener listener) {
		FunctionButtonLayout btn = (FunctionButtonLayout) findViewById(R.id.questionButton);
		btn.setButtonOnClickListener(listener);
	}
}
