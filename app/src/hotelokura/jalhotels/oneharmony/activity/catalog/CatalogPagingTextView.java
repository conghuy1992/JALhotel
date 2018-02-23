package hotelokura.jalhotels.oneharmony.activity.catalog;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CatalogPagingTextView extends LinearLayout {
	static final String TAG = "CatalogPagingTextView";

    private Context mContext;

	private int maxPage;
	private int textColor = Color.WHITE;
	private int contentsHeight = 0;

	public CatalogPagingTextView(Context context) {
		super(context);
		init(context);
	}

	public CatalogPagingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LogUtil.d(TAG, "init");

        mContext = context;

		View.inflate(context, R.layout.view_catalog_paging_text, this);

		TextView tabTextView = (TextView) findViewById(R.id.tabTextView);
        tabTextView.setText(context.getString(R.string.text_tab_down));

		WebView webView = (WebView) findViewById(R.id.webView);
		webView.setBackgroundColor(Color.TRANSPARENT);
	}

	public void setTabOnClickListener(OnClickListener listener) {
		ViewGroup tabLayout = (ViewGroup) findViewById(R.id.tabLayout);
		tabLayout.setOnClickListener(listener);
	}

	public void setTitle(String title) {
		// TextView view = (TextView) findViewById(R.id.titleView);
		// view.setText(title);
	}

	public void setStatus(String status) {
		TextView view = (TextView) findViewById(R.id.statusView);
		view.setText(status);
	}

	public void setText(String text) {
		WebView webView = (WebView) findViewById(R.id.webView);
		if (text == null || text.equals("")) {
			webView.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
		} else {
			webView.loadDataWithBaseURL(null, text, "text/html", "UTF-8", null);
		}
	}

	public void setTextColor(int color) {
		textColor = color;

        TextView tabTextView = (TextView) findViewById(R.id.tabTextView);
        tabTextView.setTextColor(textColor);

		TextView statusView = (TextView) findViewById(R.id.statusView);
		statusView.setTextColor(textColor);
	}

	public void setBGColor(int color) {
		ViewGroup tabLayout = (ViewGroup) findViewById(R.id.tabLayout);
		GradientDrawable drawable = (GradientDrawable) tabLayout
				.getBackground();
		drawable.setColor(color);

		TextView statusView = (TextView) findViewById(R.id.statusView);
		statusView.setBackgroundColor(color);

		ViewGroup webViewLayout = (ViewGroup) findViewById(R.id.webViewLayout);
		webViewLayout.setBackgroundColor(color);
	}

	public void setMaxPage(int page) {
		maxPage = page;
	}

	public void setCurrentPage(int orientation, int page1, String page1Text,
			int page2, String page2Text) {
		setCurrentPageStatus(page1, page2);
		setCurrentPageText(orientation, page1, page1Text, page2, page2Text);
	}

	private void setCurrentPageStatus(int page1, int page2) {
		StringBuffer sb = new StringBuffer();
		if (page1 >= 0) {
			sb.append(page1 + 1);
			sb.append(" ");
		}
		if (page2 >= 0) {
			if (sb.length() > 0) {
				sb.append("- ");
			}
			sb.append(page2 + 1);
			sb.append(" ");
		}
		if (maxPage >= 0) {
			sb.append("/ ");
			sb.append(maxPage);
			sb.append(" ページ");
		}
		setStatus(sb.toString());
	}

	@SuppressWarnings("unused")
	private void setCurrentPageTitle(String page1Title, String page2Title) {
		StringBuffer sb = new StringBuffer();
		if (page1Title != null && !page1Title.equals("")) {
			sb.append(page1Title);
			sb.append(" ");
		}
		if (page2Title != null && !page2Title.equals("")) {
			if (sb.length() > 0) {
				sb.append("/ ");
			}
			sb.append(page2Title);
			sb.append(" ");
		}
		setTitle(sb.toString());
	}

	private void setCurrentPageText(int orientation, int page1,
			String page1Text, int page2, String page2Text) {
		StringBuffer text = null;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横画面のとき
			if ((page1Text == null || page1Text.equals(""))
					&& (page2Text == null || page2Text.equals(""))) {
				text = null;
			} else if (page2Text == null || page2Text.equals("")) {
				text = new StringBuffer();
				text.append("<div align='right'><small>");
				text.append(page1 + 1);
				text.append(" ページ");
				text.append("</small></div>");
				text.append(page1Text);
			} else if (page1Text == null || page1Text.equals("")) {
				text = new StringBuffer();
				text.append("<div align='right'><small>");
				text.append(page2 + 1);
				text.append(" ページ");
				text.append("</small></div>");
				text.append(page2Text);
			} else {
				text = new StringBuffer();
				text.append("<div align='right'><small>");
				text.append(page1 + 1);
				text.append(" ページ");
				text.append("</small></div>");
				text.append(page1Text);
				text.append("<hr>");
				text.append("<div align='right'><small>");
				text.append(page2 + 1);
				text.append(" ページ");
				text.append("</small></div>");
				text.append(page2Text);
			}
		} else {
			// 縦画面のとき
			if (page1Text != null) {
				text = new StringBuffer();
				text.append(page1Text);
			} else if (page2Text != null) {
				text = new StringBuffer();
				text.append(page2Text);
			}
		}
		if (text == null) {
			setText(null);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("<font style='word-wrap: break-word;' color='#");
			sb.append(String.format("%06x", (textColor & 0xffffff)));
			sb.append("'>");
			sb.append(text);
			sb.append("</font>");
			setText(sb.toString());
		}
	}

	public void toggleItemVisibility() {
		final ViewGroup layout = (ViewGroup) findViewById(R.id.contentsLayout);
		if (layout.getVisibility() == View.VISIBLE) {
			setItemVisibility(View.GONE);
		} else {
			setItemVisibility(View.VISIBLE);
		}
	}

	public void setItemVisibility(final int visibility) {
		this.setVisibility(View.VISIBLE);

		final ViewGroup layout = (ViewGroup) findViewById(R.id.contentsLayout);
		if (layout.getVisibility() == visibility) {
			return;
		}
		if (layout.getHeight() > 0) {
			contentsHeight = layout.getHeight();
		}
		if (contentsHeight <= 0) {
			return;
		}

		final TextView tabTextView = (TextView) findViewById(R.id.tabTextView);

		TranslateAnimation translateAnimation;
		if (visibility == View.GONE) {
			translateAnimation = new TranslateAnimation(0, 0, 0, contentsHeight);
		} else {
			layout.setVisibility(visibility);
			translateAnimation = new TranslateAnimation(0, 0, contentsHeight, 0);
		}
		translateAnimation.setDuration(200);
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				if (visibility == View.GONE) {
                    tabTextView.setText(mContext.getString(R.string.text_tab_up));
				} else {
                    tabTextView.setText(mContext.getString(R.string.text_tab_down));
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				layout.setVisibility(visibility);
				// アニメーション後にチラつかない様にする
				CatalogPagingTextView.this.setAnimation(null);
			}
		});
		startAnimation(translateAnimation);
	}
}
