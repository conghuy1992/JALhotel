package hotelokura.jalhotels.oneharmony.activity.bookmark;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.HashMap;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.ActivitySkeleton;
import hotelokura.jalhotels.oneharmony.activity.catalog.CatalogActivity;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.setting.CatalogListSetting;
import hotelokura.jalhotels.oneharmony.setting.CatalogSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.setting.PageListSetting;
import hotelokura.jalhotels.oneharmony.setting.PlistDict;
import hotelokura.jalhotels.oneharmony.util.LogUtil;


/**
 * しおり画面
 */
public class BookmarkActivity extends ActivitySkeleton {
	static final String TAG = "BookmarkActivity";

	private String catalogId;
	private CatalogSetting catalogSetting;
	private PageListSetting pageListSetting;
	private int pageIndex;

	private GestureDetector gestureDetector;

	private BookmarkDatas bookmarks;
	private BookmarkAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		MainApplication main = MainApplication.getInstance();
		if (main == null || main.getAppSetting() == null) {
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bookmark);

		AppSetting app = MainApplication.getInstance().getAppSetting();

		// カタログリスト設定の行
        catalogId = getIntent().getStringExtra("CatalogId");

		// カタログ基本設定
		@SuppressWarnings("unchecked")
		HashMap<String, Object> catalogMap = (HashMap<String, Object>) getIntent()
				.getSerializableExtra("CatalogPlist");
		PlistDict catalogPlist = PlistDict.makePlistDict(catalogMap);
		if (catalogPlist != null) {
			catalogSetting = new CatalogSetting(catalogPlist);
		}
		// カタログページ設定の行
		CsvLine pageListCsv = CsvLine.makeCsvLine(getIntent()
				.getStringArrayListExtra("PageListCsv"));
		if (pageListCsv != null) {
			pageListSetting = new PageListSetting(pageListCsv);
		}
		// ページIndex
		pageIndex = getIntent().getIntExtra("PageIndex", -1);

		// しおりの設定
		boolean isLocal = app.getBookmarkStyle() == 1 ? false : true;
		int deleteStyle = app.getBookmarkDeleteStyle();

		bookmarks = new BookmarkDatas(catalogId, isLocal);
		adapter = new BookmarkAdapter(this, bookmarks);
		adapter.setDeleteStyle(deleteStyle);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(onItemClickListener);

		if (deleteStyle == 0) {
			// スワイプで削除ボタン表示
			gestureDetector = new GestureDetector(this, onGestureListener);
			listView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (gestureDetector.onTouchEvent(event)) {
						return true;
					}
					return false;
				}
			});
		}
		listView.setAdapter(adapter);

		BookmarkNavigationView navigation = (BookmarkNavigationView) findViewById(R.id.navigationView);
		navigation.setOnBackButtionListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		navigation.setOnAppendButtionListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendBookmark();
			}
		});

	}

	@Override
	protected void onStart() {
		LogUtil.d(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		LogUtil.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		LogUtil.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		LogUtil.d(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		LogUtil.d(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		LogUtil.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		LogUtil.d(TAG, "onLowMemory");
		super.onLowMemory();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		LogUtil.d(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}

	private SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			if (Math.abs(velocityX) > Math.abs(velocityY)) {
				// xが、yより移動量が大きい => 左右の移動
				ListView listView = (ListView) findViewById(R.id.listView);
				int position = listView.pointToPosition((int) e1.getX(),
						(int) e1.getY());

				// 削除ボタンの表示/非表示
				if (position == adapter.getDeletePosition()) {
					adapter.setDeletePosition(-1);
				} else {
					adapter.setDeletePosition(position);
				}
				adapter.notifyDataSetChanged();
			}
			return false;
		}
	};

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ListView listView = (ListView) parent;
			BookmarkData bookmark = (BookmarkData) listView
					.getItemAtPosition(position);

			CsvLine catalogListCsv = findCatalogListCsv(bookmark);
			if (catalogListCsv != null) {
				// カタログ画面遷移
				intentCatalog(bookmark, catalogListCsv);
			}
		}
	};

	/**
	 * しおりで指定されているカタログ画面へ遷移
	 *
	 * @param bookmark
	 * @param catalogListCsv
	 */
	private void intentCatalog(BookmarkData bookmark, CsvLine catalogListCsv) {
        CatalogListSetting setting = new CatalogListSetting(catalogListCsv);
        CatalogActivity.startActivity(this, setting.getID(), setting.getUrl(), bookmark.page - 1,
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	/**
	 * しおりに該当する設定を探す
	 *
	 * @param bookmark
	 * @return
	 */
	private CsvLine findCatalogListCsv(BookmarkData bookmark) {
		return CatalogListSetting.findCatalogListCsv(bookmark.catalogId);
	}

	/**
	 * しおりを追加する
	 */
	private void appendBookmark() {
		if (pageListSetting != null) {
			String title = pageListSetting.getTitle();
			int page = pageIndex + 1;
			int subpage = 0;

			if (title == null || title.equals("")) {
				if (subpage <= 0) {
					title = String.format("Page %d", page);
				} else {
					title = String.format("Page %d - %d", page, subpage);
				}
			}

			if (catalogSetting == null) {
				bookmarks.add(title, page, subpage);
			} else {
				String catalogTitle = catalogSetting.getTitle();
				if (catalogTitle == null) {
					catalogTitle = "";
				}
				bookmarks.add(title, page, subpage, catalogTitle);
			}
			bookmarks.save();

			adapter.setDeletePosition(-1);
			adapter.notifyDataSetChanged();
		}
	}
}
