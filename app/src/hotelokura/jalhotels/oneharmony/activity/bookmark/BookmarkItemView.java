package hotelokura.jalhotels.oneharmony.activity.bookmark;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.DrawableUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BookmarkItemView extends RelativeLayout {
	static final String TAG = "BookmarkItemView";

	private BookmarkAdapter parentAdapter;

	public BookmarkItemView(Context context) {
		super(context);
		init(context);
	}

	public BookmarkItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BookmarkItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_bookmark_item, this);

		int bgColor = getResources().getColor(R.color.delete_bg_color);
		Button button = (Button) findViewById(R.id.deleteButton);

		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			button.setBackgroundDrawable(DrawableUtil
					.makeNavigationButtonDrawable(bgColor));
		} else {
			button.setBackground(DrawableUtil
					.makeNavigationButtonDrawable(bgColor));
		}
	}

	public void setParentAdapter(BookmarkAdapter parentAdapter) {
		this.parentAdapter = parentAdapter;
	}

	public void showDeleteSwitchButton(final int position) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.deleteSwitchButtonLayout);
		layout.setVisibility(View.VISIBLE);
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageButton view = (ImageButton) findViewById(R.id.deleteSwitchButton);
				if (view.isEnabled()) {
					view.setPressed(true);
					view.invalidate();
					view.performClick();
				}
			}
		});

		ImageButton button = (ImageButton) findViewById(R.id.deleteSwitchButton);
		if (position == parentAdapter.getDeletePosition()) {
			button.setImageResource(R.drawable.toggle_del_on);
		} else {
			button.setImageResource(R.drawable.toggle_del_off);
		}
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 削除ボタンの表示/非表示
				if (position == parentAdapter.getDeletePosition()) {
					parentAdapter.setDeletePosition(-1);
				} else {
					parentAdapter.setDeletePosition(position);
				}
				parentAdapter.notifyDataSetChanged();
			}
		});
	}

	public void deleteMode(boolean mode) {
		Button button = (Button) findViewById(R.id.deleteButton);
		if (mode) {
			button.setVisibility(View.VISIBLE);
		} else {
			button.setVisibility(View.GONE);
		}
	}

	public void setOnDeleteButtonClickLisner(OnClickListener listener) {
		Button button = (Button) findViewById(R.id.deleteButton);
		button.setOnClickListener(listener);
	}

	public void viewData(boolean isLocal, String CatalogId, BookmarkData data) {

		TextView title = (TextView) findViewById(R.id.titleText);
		if (data.title != null) {
			title.setText(String.valueOf(data.title));
		}

		String subtext = null;
		if (data.subpage <= 1) {
			subtext = String.format("%d", data.page);
		} else {
			subtext = String.format("%d-%d", data.page, data.subpage);
		}
		if (!isLocal) {
			String catalogTitle = data.catalogTitle;
			if (catalogTitle == null) {
				catalogTitle = "";
			}
			if (data.catalogId.equals(CatalogId)) {
				subtext = String.format("Page %s   %s", subtext, catalogTitle);
			} else {
				subtext = String
						.format("Page %s   %s »", subtext, catalogTitle);
			}
		}
		TextView sub = (TextView) findViewById(R.id.subText);
		sub.setText(subtext);
	}
}
