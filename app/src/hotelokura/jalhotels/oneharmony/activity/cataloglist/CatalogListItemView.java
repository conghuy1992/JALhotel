package hotelokura.jalhotels.oneharmony.activity.cataloglist;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;

import hotelokura.jalhotels.oneharmony.cache.MemoryCache;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.setting.CatalogListSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.view.ImageIndicatorView;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CatalogListItemView extends RelativeLayout {
	static final String TAG = "CatalogListItemView";

	private CatalogListActivity parentActivity;
	private CatalogListGridView parentGridView;

	private CatalogListSetting setting = new CatalogListSetting();
    private int mTopMenuLayout = 0;
    private int mSecondMenuLayout = 1;

    private boolean mIsDummy = false;
    private boolean mIsSmall = false;

	public CatalogListItemView(Context context) {
		super(context);
		init(context);
	}

	public CatalogListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CatalogListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		LogUtil.d(TAG, "init");
		this.parentActivity = (CatalogListActivity) context;
		View.inflate(context, R.layout.view_cataloglist_item_no_text, this);
        AppSetting appSetting = MainApplication.getInstance().getAppSetting();
        mTopMenuLayout = appSetting.getTopMenuLayout();
        mSecondMenuLayout = appSetting.getSecondMenuLayout();
	}

	public void setParentGridView(CatalogListGridView parentGridView) {
		this.parentGridView = parentGridView;

		// タイトルとテキストの表示/非表示
		// 表示：VISIBLE or 非表示：GONE
		if (parentGridView != null
				&& ((parentGridView.getActionMode() == CatalogListData.ActionMode.CoverFlow && mTopMenuLayout == 0)
                || (parentGridView.getActionMode() == CatalogListData.ActionMode.CatalogList && mSecondMenuLayout == 0))) {
			// カバーフローの時
			TextView titleView = (TextView) findViewById(R.id.titleView);
			TextView textView = (TextView) findViewById(R.id.textView);
			titleView.setVisibility(View.GONE);
			textView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mode = MeasureSpec.getMode(widthMeasureSpec);
		float width = MeasureSpec.getSize(widthMeasureSpec);
		float height = MeasureSpec.getSize(heightMeasureSpec);

        // GridViewの情報を取得
        int itemCount = parentGridView.getAdapter().getCount();
        int col = parentGridView.getColumns();
        int row = (int) Math.ceil((float) itemCount / col);

        // 画面のサイズを取得
        float dispWidth = parentGridView.getWidth();
        float dispHeight = parentGridView.getHeight();
        float margin = 0;

		if (parentGridView != null
				&& parentGridView.getScrollMode() == CatalogListActivity.SCROLL_MODE_NO) {

            dispWidth -= parentGridView.getWidthSpacing() * col; // 列の余白
            dispHeight -= parentGridView.getHeightSpacing() * row; // 行の余白
            margin = ((float) dispWidth / col) - width; // 列のマージン
			height = ((float) dispHeight / row) - margin; // 行のマージン

			// 画面全体の縦を、行数で割る。このときマージン分も考慮する。
			switch (getResources().getConfiguration().orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				// 縦の時
				height = height * 0.9f; // ぴったりサイズなので、少し補正
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				// 横の時
				height = height * 0.9f; // ぴったりサイズなので、少し補正
				break;
			}
			if (width < height) {
				// 元のサイズより大きくなってしまうのでwidth優先に戻す
				height = width;
			}
			width = height;
		} else {
            int dummyObjectSize = 20;

            // 画面全体の縦を、行数で割る。このときマージン分も考慮する。
            switch (getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    row = 3; // 縦の時
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    row = 2; // 横の時
                    dummyObjectSize = 17;
                    break;
            }

            int dummyObjectMargin = (int)(dummyObjectSize * 0);

            dispHeight *= 0.92f; // GridViewの全体paddingを考慮
            dispHeight -= parentGridView.getHeightSpacing() * (row + 1); // 行の余白(dummyObjectの分も考慮)
            dispHeight -= (dummyObjectSize + dummyObjectMargin) * 2; // 先頭と末端のスクロール余白
            height = ((float) dispHeight / row);

            if (parentGridView.getActionMode() == CatalogListData.ActionMode.CoverFlow) {
                if (mTopMenuLayout == 0) {
                    width = height;
                } else {
                    width = width * 0.9f;
                    height = width * 1.26f;
                }
            } else {
                if (mSecondMenuLayout == 0) {
                    width = height;
                } else {
                    width = width * 0.9f;
                    height = width * 1.26f;
                }
            }

            if (mIsSmall) {
                height = MainApplication.getInstance().dp2px(dummyObjectSize);
            }
        }

		widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) width, mode);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height, mode);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public CatalogListSetting getSetting() {
		return setting;
	}

	public void setCsvLine(CsvLine line) {
		setting.setLine(line);

		clearImage();
	}

	public CsvLine getCsvLine() {
		return setting.getLine();
	}

	/**
	 * 設定 文字色
	 * 
	 * @param int 文字色
	 * @return
	 */
	public void setTextColor(Integer color) {
		if (color != null) {
			TextView titleView = (TextView) findViewById(R.id.titleView);
			titleView.setTextColor(color);

			TextView textView = (TextView) findViewById(R.id.textView);
			textView.setTextColor(color);
		}
	}

	public void viewSetting() {
		// カタログタイトル
		TextView titleView = (TextView) findViewById(R.id.titleView);
		titleView.setText(setting.getTitle());
		// カタログ説明文
		TextView textView = (TextView) findViewById(R.id.textView);
		textView.setText(setting.getTextIndention());
		viewImage();
	}

	public void clearImage() {
		ImageIndicatorView imageView = (ImageIndicatorView) findViewById(R.id.catalogImage);
		imageView.clearImage();
	}

	public void viewImage() {
        if (mIsDummy) {
            return;
        }
		ImageIndicatorView imageView = (ImageIndicatorView) findViewById(R.id.catalogImage);
		if (imageView.getStatus() == ImageIndicatorView.Status.LOADED) {
			// すでに画像は表示済み
			return;
		}

		// MemoryCacheを取得
		MemoryCache<Bitmap> memoryCache = parentActivity.getMemoryCache();
		if (memoryCache == null) {
			// 画面がすでに停止している
			imageView.setImage(null);
			return;
		} else {
			// MemoryCacheから検索
			String filename = setting.getImage();
			if (memoryCache.containsKey(filename)) {
				// 存在するなら表示
				Bitmap bitmap = memoryCache.get(filename);
				imageView.setImage(bitmap);
			}
		}
	}

    public void setDummy(boolean dummy, boolean isSmall) {
        ImageIndicatorView imageView = (ImageIndicatorView) findViewById(R.id.catalogImage);
        TextView titleView = (TextView) findViewById(R.id.titleView);
        TextView textView = (TextView) findViewById(R.id.textView);
        if (dummy) {
            imageView.setVisibility(GONE);
            titleView.setVisibility(GONE);
            textView.setVisibility(GONE);
            mIsDummy = true;
            mIsSmall = isSmall;
        } else {
            imageView.setVisibility(VISIBLE);
            if (parentGridView != null
                    && ((parentGridView.getActionMode() == CatalogListData.ActionMode.CoverFlow && mTopMenuLayout == 1)
                    || (parentGridView.getActionMode() == CatalogListData.ActionMode.CatalogList && mSecondMenuLayout == 1))) {
                // カバーフローの時
                titleView = (TextView) findViewById(R.id.titleView);
                textView = (TextView) findViewById(R.id.textView);
                titleView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            }
            mIsDummy = false;
            mIsSmall = false;
        }
    }
}
