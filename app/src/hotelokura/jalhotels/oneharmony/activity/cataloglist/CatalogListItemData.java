package hotelokura.jalhotels.oneharmony.activity.cataloglist;

import hotelokura.jalhotels.oneharmony.cache.MemoryCache;
import hotelokura.jalhotels.oneharmony.net.AsyncCallback;
import hotelokura.jalhotels.oneharmony.net.AsyncResult;
import hotelokura.jalhotels.oneharmony.net.DownloadManager;
import hotelokura.jalhotels.oneharmony.setting.CatalogListSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import android.graphics.Bitmap;

public class CatalogListItemData {
	static final String TAG = "CatalogListItemData";

	public static interface Callback {
		public void downloadCompleted(String filename);
	}

	private Callback callback;

	private String settingName;
	private String catalogUrl;
	private boolean downloadImageNoThread;

	private MemoryCache<Bitmap> memoryCache;

	public CatalogListItemData(String settingName) {
		this.settingName = settingName;
	}

	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public String getCatalogUrl() {
		return catalogUrl;
	}

	public void setCatalogUrl(String catalogUrl) {
		this.catalogUrl = catalogUrl;
	}

	public String getSettingName() {
		return settingName;
	}

	public boolean isDownloadImageNoThread() {
		return downloadImageNoThread;
	}

	public void setDownloadImageNoThread(boolean downloadImageNoThread) {
		this.downloadImageNoThread = downloadImageNoThread;
	}

	public MemoryCache<Bitmap> getMemoryCache() {
		return memoryCache;
	}

	public void downloadItemImages(CsvArray csvArray) {
		if (memoryCache == null) {
			// キャッシュの設定
			memoryCache = new MemoryCache<Bitmap>();
			memoryCache
					.setOnMemoryCacheRemoveListener(new MemoryCache.OnMemoryCacheRemoveListener<Bitmap>() {
						@Override
						public boolean onRemoveItem(String key, Bitmap item) {
							if (item != null) {
								item.recycle();
							}
							return true;
						}
					});
		}

		CatalogListSetting setting = new CatalogListSetting();
		for (CsvLine line : csvArray) {
			setting.setLine(line);
			// 画像ファイル名
			String filename = setting.getImage();
			// 画像のダウンロード
			downloadItemImage(0, filename);
		}
	}

	private void downloadItemImage(final int retry, final String filename) {
		// 画像のダウンロード
		if (memoryCache != null && memoryCache.containsKey(filename)) {
			if (callback != null) {
				callback.downloadCompleted(filename);
			}
		} else {
			DownloadManager.getInstance().offerImage(settingName, 5,
					catalogUrl, filename, new AsyncCallback<Bitmap>() {
						@Override
						public void onSuccess(AsyncResult<Bitmap> result) {
							Bitmap bmp = result.getContent();
							if (memoryCache == null) {
								// 画面がすでに停止している
								bmp.recycle();
							} else {
								bmp = memoryCache.put(filename, bmp);
								if (callback != null) {
									callback.downloadCompleted(filename);
								}
							}
						}

						@Override
						public void onFailed(AsyncResult<Bitmap> result) {
							if (memoryCache == null) {
								// 画面がすでに停止している
								return;
							} else {
								if (retry < 2) {
									// リトライ
									downloadItemImage(retry + 1, filename);
								} else {
									// リトライオーバー
									memoryCache.put(filename, null);
									if (callback != null) {
										callback.downloadCompleted(filename);
									}
								}
							}
						}
					}, downloadImageNoThread);
		}
	}

	public void clearDownloadOffer() {
		DownloadManager.getInstance().clear(settingName);
	}

	/**
	 * 画像のメモリ解放
	 */
	public void recycleImages() {
		// ビットマップを解放
		if (memoryCache != null) {
			memoryCache.removeAll();
			memoryCache = null;
		}
		System.gc();
	}
}
