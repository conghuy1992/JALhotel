package hotelokura.jalhotels.oneharmony.activity.web;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.DrawableUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class WebToolbarView extends LinearLayout {
	static final String TAG = "WebToolbarView";

	public WebToolbarView(Context context) {
		super(context);
		init(context);
	}

	public WebToolbarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_web_toolbar, this);

		int bgColor = getResources().getColor(R.color.bg_navi);
		ViewGroup layout = (ViewGroup) findViewById(R.id.toolbarLayout);

		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			layout.setBackgroundDrawable(DrawableUtil
					.makeNavigationDrawable(bgColor));
		} else {
			layout.setBackground(DrawableUtil.makeNavigationDrawable(bgColor));
		}
	}
}
