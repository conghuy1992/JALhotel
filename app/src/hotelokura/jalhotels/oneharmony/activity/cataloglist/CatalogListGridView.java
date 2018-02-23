package hotelokura.jalhotels.oneharmony.activity.cataloglist;

import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListData.ActionMode;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.AbsListView.OnScrollListener;

public class CatalogListGridView extends GridView implements OnScrollListener {
	static final String TAG = "CatalogListGridView";

	private ActionMode actionMode = ActionMode.CatalogList;
	private int scrollMode = CatalogListActivity.SCROLL_MODE_YES;

	private boolean busy;

	private int columns = 2;
	private int widthSpacing = 0;
	private int heightSpacing = 0;

	public CatalogListGridView(Context context) {
		super(context);
		init(context);
	}

	public CatalogListGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CatalogListGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		LogUtil.d(TAG, "init");
		setOnScrollListener(this);
	}

	public ActionMode getActionMode() {
		return actionMode;
	}

	public void setActionMode(ActionMode actionMode) {
		this.actionMode = actionMode;
	}

	public int getScrollMode() {
		return scrollMode;
	}

	public void setScrollMode(int scrollMode) {
		this.scrollMode = scrollMode;
	}

	public boolean isBusy() {
		return busy;
	}

	@Override
	public void setNumColumns(int numColumns) {
		super.setNumColumns(numColumns);
		columns = numColumns;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		setNumColumns(columns);
	}

	public int getWidthSpacing() {
		return widthSpacing;
	}

	/**
	 * タイル間の横余白
	 * 
	 * @param widthSpacing
	 */
	public void setWidthSpacing(int widthSpacing) {
		this.widthSpacing = widthSpacing;
		setHorizontalSpacing(widthSpacing);
	}

	public int getHeightSpacing() {
		return heightSpacing;
	}

	/**
	 * タイル間の縦余白
	 * 
	 * @param heightSpacing
	 */
	public void setHeightSpacing(int heightSpacing) {
		this.heightSpacing = heightSpacing;
		setVerticalSpacing(heightSpacing);
	}

	public void clearImages() {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			CatalogListItemView v = (CatalogListItemView) getChildAt(i);
			v.clearImage();
		}
	}

	public void viewImages() {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			CatalogListItemView v = (CatalogListItemView) getChildAt(i);
			v.viewImage();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE: // スクロールしていない
			busy = false;

			int count = view.getChildCount();
			for (int i = 0; i < count; i++) {
				CatalogListItemView v = (CatalogListItemView) view
						.getChildAt(i);
				v.viewImage();
			}
			break;

		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // スクロール中
			busy = true;
			break;

		case OnScrollListener.SCROLL_STATE_FLING: // はじいたとき
			busy = true;
			break;
		}
	}
}
