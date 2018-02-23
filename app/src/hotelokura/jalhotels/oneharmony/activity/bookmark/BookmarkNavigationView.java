package hotelokura.jalhotels.oneharmony.activity.bookmark;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.DrawableUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class BookmarkNavigationView extends RelativeLayout {
	static final String TAG = "BookmarkNavigationView";

	public BookmarkNavigationView(Context context) {
		super(context);
		init(context);
	}

	public BookmarkNavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BookmarkNavigationView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_bookmark_navigation, this);

		int bgColor = getResources().getColor(R.color.bg_navi);
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
	 * 設定 イベント 戻るボタン
	 * 
	 * @param OnClickListener
	 *            コールバック
	 * @return
	 */
	public void setOnBackButtionListener(OnClickListener listener) {
		ImageButton button = (ImageButton) findViewById(R.id.backButton);
		button.setOnClickListener(listener);
	}

	public void setOnAppendButtionListener(OnClickListener listener) {
        ImageButton button = (ImageButton) findViewById(R.id.appendButton);
		button.setOnClickListener(listener);
	}
}
