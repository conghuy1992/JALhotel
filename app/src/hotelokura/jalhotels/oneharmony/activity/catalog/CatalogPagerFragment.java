package hotelokura.jalhotels.oneharmony.activity.catalog;

import java.util.ArrayList;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.catalog.CatalogPage.Align;
import hotelokura.jalhotels.oneharmony.cache.MemoryCache;
import hotelokura.jalhotels.oneharmony.cache.MemoryCache.OnMemoryCacheRemoveListener;
import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class CatalogPagerFragment extends Fragment {
	static final String TAG = "CatalogPagerFragment";

	public static Fragment newInstance(int position, CatalogPagerAdapter adapter) {

		CatalogPagerFragment f = new CatalogPagerFragment();
		Bundle args = new Bundle();
		args.putInt("position", position);
		f.setArguments(args);
		return f;
	}

	private int position;
	private CatalogActivity parentActivity;

	private CatalogPageView pageView;

	private SparseArray<CatalogPage> pages;
	private int pageCount;

	private MemoryCache<Bitmap> memoryCache;

	@Override
	public void onAttach(Activity activity) {
		LogUtil.d(TAG, "onAttach");
		super.onAttach(activity);

		parentActivity = (CatalogActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = -1;
		if (getArguments() != null) {
			position = getArguments().getInt("position");
		}
		LogUtil.d(TAG, "onCreate: " + position);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtil.d(TAG, "onCreateView: " + position);

		View v = inflater.inflate(R.layout.view_catalog_fragment, container,
				false);
		FrameLayout layout = (FrameLayout) v.findViewById(R.id.layout);

		pageView = new CatalogPageView(parentActivity);
		layout.addView(pageView);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onActivityCreated: " + position);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		LogUtil.d(TAG, "onStart: " + position);
		super.onStart();

		// キャッシュの設定
		memoryCache = new MemoryCache<Bitmap>();

		// CatalogPage作成
		CatalogPageStructure pageStructure = parentActivity.getPageStructure();
		SparseIntArray structure = pageStructure.getStructure(position);
		CsvArray pageListCsvArray = pageStructure.getCsvArray();
		makeCatalogPage(structure, pageListCsvArray);

		pageView.setPages(pages, pageCount);
		pageView.viewPage();
	}

	@Override
	public void onResume() {
		LogUtil.d(TAG, "onResume: " + position);
		super.onResume();
	}

	@Override
	public void onPause() {
		LogUtil.d(TAG, "onPause: " + position);
		super.onPause();
	}

	@Override
	public void onStop() {
		LogUtil.d(TAG, "onStop: " + position);
		super.onStop();

		recycleImages();
	}

	@Override
	public void onDestroyView() {
		LogUtil.d(TAG, "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		LogUtil.d(TAG, "onDetach");
		super.onDetach();
	}

	@Override
	public void onLowMemory() {
		LogUtil.d(TAG, "onLowMemory");
		super.onLowMemory();
	}

	/**
	 * 画像のメモリ解放
	 */
	private void recycleImages() {
		if (memoryCache != null) {
			memoryCache.removeAll(new OnMemoryCacheRemoveListener<Bitmap>() {
				@Override
				public boolean onRemoveItem(String key, Bitmap item) {
					if (item != null) {
						item.recycle();
					}
					return true;
				}
			});
			memoryCache = null;
		}
		System.gc();
	}

	public void setBusy(boolean b) {
		for (int i = 0; i < pageCount; i++) {
			CatalogPage page = pages.get(i, null);
			if (page != null) {
				page.setBusy(b);
			}
		}
	}

	public void setDownloadLevel(int level) {
		int oldLevel = 1;
		final ArrayList<String> nameList = new ArrayList<String>();
		for (int i = 0; i < pageCount; i++) {
			CatalogPage page = pages.get(i, null);
			if (page != null) {
				oldLevel = page.getDownloadLevel();
				page.setDownloadLevel(level);
				if (level == 1) {
					nameList.add(page.getLv1ImageName());
					page.stopDownload();
				}
			}
		}
		if (level == 1 && memoryCache != null) {
			memoryCache.removeAll(new OnMemoryCacheRemoveListener<Bitmap>() {
				@Override
				public boolean onRemoveItem(String key, Bitmap item) {
					for (int i = 0; i < nameList.size(); i++) {
						String name = nameList.get(i);
						if (key.equals(name)) {
							// Lv1の画像は削除しない
							return false;
						}
					}
					if (item != null) {
						item.recycle();
					}
					return true;
				}
			});
			System.gc();
		}
		if (level > oldLevel) {
			pageView.viewPage();
		}
	}

	public CatalogPageView getPageView() {
		return pageView;
	}

	public MemoryCache<Bitmap> getMemoryCache() {
		return memoryCache;
	}

	private void makeCatalogPage(SparseIntArray structure,
			CsvArray pageListCsvArray) {
		pages = new SparseArray<CatalogPage>();
		CatalogPage page1 = null;
		CatalogPage page2 = null;

		int index1 = structure.get(0, -1);
		if (index1 != -1) {
			// 左(中央)ページ作成
			page1 = new CatalogPage(this, pageView,
					pageListCsvArray.get(index1));
			pages.put(0, page1);
			pageCount = 1;
		}
		int index2 = structure.get(1, -1);
		if (index2 != -1) {
			// 右ページ作成
			page2 = new CatalogPage(this, pageView,
					pageListCsvArray.get(index2));
			if (page1 != null) {
				// 左右ページがあるので中央に寄せる
				page1.setAlign(Align.AlignRight);
				page2.setAlign(Align.AlignLeft);
				pages.put(1, page2);
				pageCount = 2;
			} else {
				// 左ページが無いので、左(中央)に入れる
				pages.put(0, page2);
				pageCount = 1;
			}
		}
	}
}
