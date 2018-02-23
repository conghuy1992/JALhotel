package hotelokura.jalhotels.oneharmony.activity.info;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.DrawableUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class InfoToolbarView extends LinearLayout {
	static final String TAG = "InfoToolbarView";

	public InfoToolbarView(Context context) {
		super(context);
		init(context);
	}

	public InfoToolbarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_info_toolbar, this);

		int bgColor = getResources().getColor(R.color.bg_navi);
		ViewGroup layout = (ViewGroup) findViewById(R.id.toolbarLayout);
		Button infoAccessButton = (Button) findViewById(R.id.infoAccessButton);

		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			layout.setBackgroundDrawable(DrawableUtil
					.makeNavigationDrawable(bgColor));
			infoAccessButton.setBackgroundDrawable(DrawableUtil
					.makeNavigationButtonDrawable(bgColor));
		} else {
			layout.setBackground(DrawableUtil.makeNavigationDrawable(bgColor));
			infoAccessButton.setBackground(DrawableUtil
					.makeNavigationButtonDrawable(bgColor));
		}
	}
}
