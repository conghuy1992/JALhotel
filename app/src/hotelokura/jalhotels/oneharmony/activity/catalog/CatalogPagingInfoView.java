package hotelokura.jalhotels.oneharmony.activity.catalog;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CatalogPagingInfoView extends LinearLayout {
	static final String TAG = "CatalogPagingInfoView";

	private int maxPage;

	public CatalogPagingInfoView(Context context) {
		super(context);
		init(context);
	}

	public CatalogPagingInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_catalog_paging_info, this);
	}

	public void setTitle(String title) {
		TextView view = (TextView) findViewById(R.id.titleView);
		view.setText(title);
	}

	public void setStatus(String status) {
		TextView view = (TextView) findViewById(R.id.statusView);
		view.setText(status);
	}

	public void setMaxPage(int page) {
		maxPage = page;
	}

	public void setCurrentPage(int page1, String page1Title, int page2,
			String page2Title) {
		setCurrentPageStatus(page1, page2);
		setCurrentPageTitle(page1Title, page2Title);
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
			sb.append(getContext().getString(R.string.text_page));
		}
		setStatus(sb.toString());
	}

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
}
