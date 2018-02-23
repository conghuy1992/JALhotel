package hotelokura.jalhotels.oneharmony.activity.cataloglist;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.DrawableUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CatalogListNavigationView extends RelativeLayout {
	static final String TAG = "CatalogListNavigationView";

	private boolean titleString = false;
	private boolean titleBitmap = false;
	private boolean titleBitmapSet = false;

	public CatalogListNavigationView(Context context) {
		super(context);
		init(context);
	}

	public CatalogListNavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CatalogListNavigationView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_cataloglist_navigation, this);

		int bgColor = getResources().getColor(R.color.bg_navi);
		setBGColor(bgColor);

		changeTitleVisibility();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		LogUtil.d(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);

		changeTitleVisibility();
	}

	/**
	 * タイトルの表示/非表示
	 */
	private void changeTitleVisibility() {
		ImageView titleImageView = (ImageView) findViewById(R.id.titleImageView);
		TextView titleView = (TextView) findViewById(R.id.titleView);
		if (!titleBitmapSet) {
			titleImageView.setVisibility(View.GONE);
			titleView.setVisibility(View.GONE);
			return;
		}
		titleImageView.setVisibility(View.VISIBLE);

		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			if (!titleString || titleBitmap) {
				titleView.setVisibility(View.GONE);
			} else {
				titleView.setVisibility(View.VISIBLE);
			}
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			titleView.setVisibility(View.VISIBLE);
			break;
		}
	}

	/**
	 * 設定 背景色
	 * 
	 * @param int レイアウトの背景色
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public void setBGColor(int bgColor) {
		ViewGroup layout = (ViewGroup) findViewById(R.id.navigationLayout);

		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			layout.setBackgroundDrawable(DrawableUtil
					.makeNavigationDrawable(bgColor));
		} else {
			layout.setBackground(DrawableUtil.makeNavigationDrawable(bgColor));
		}
	}

	/**
	 * 設定 タイトル 文言
	 * 
	 * @param String
	 *            タイトル文言
	 * @return
	 */
	public void setTitle(String title) {
		TextView titleView = (TextView) findViewById(R.id.titleView);
		titleView.setText(title);

		titleString = (title != null && !title.equals(""));
		changeTitleVisibility();
	}

	/**
	 * 設定 タイトル 文字色
	 * 
	 * @param String
	 *            タイトル文字色
	 * @return
	 */
	public void setTitleColor(int color) {
		TextView titleView = (TextView) findViewById(R.id.titleView);
		titleView.setTextColor(color);
	}

	/**
	 * 設定 タイトルロゴ
	 * 
	 * @param Bitmap
	 *            タイトルロゴ
	 * @return
	 */
	public void setTitleImageLogo(Bitmap bm) {
		ImageView titleImageView = (ImageView) findViewById(R.id.titleImageView);
		titleImageView.setImageBitmap(bm);

		titleBitmapSet = true;
		titleBitmap = (bm != null);
		changeTitleVisibility();
	}

	/**
	 * 設定 イベント 戻るボタン
	 * 
	 * @param OnClickListener
	 *            コールバック
	 * @return
	 */
	public void setBackButtionListener(OnClickListener listener) {
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
		backButton.setOnClickListener(listener);
	}

	public void setBackButtionVisibility(int visibility) {
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
		backButton.setVisibility(visibility);
	}

	public void clearImages() {
		ImageView titleImageView = (ImageView) findViewById(R.id.titleImageView);
		titleImageView.setImageDrawable(null);

		titleBitmapSet = true;
		titleBitmap = false;
	}
}
