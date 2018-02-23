package hotelokura.jalhotels.oneharmony.net;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.cache.DiskCache;
import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.setting.PlistDict;
import hotelokura.jalhotels.oneharmony.util.BitmapUtil;

public class DownloadManager {
	static final String TAG = "DownloadManager";

	public static DownloadManager instance;

	public static DownloadManager getInstance() {
		if (instance == null) {
			instance = new DownloadManager();
		}
		return instance;
	}

	/**
	 * 同時接続数
	 */
	private static int ASSET_SESSION_COUNT = 3;
	private static int DOWNLOAD_SESSION_COUNT = 3;

	private DownloadQueue queueAsset;
	private DownloadQueue queueAsync;

	private DownloadManager() {
		super();
		queueAsset = new DownloadQueue(ASSET_SESSION_COUNT);
		queueAsync = new DownloadQueue(DOWNLOAD_SESSION_COUNT);
	}

	public void assetPlist(String tag, int priority, String filename,
			final AsyncCallback<PlistDict> callback) {

		AssetGetPlist task = new AssetGetPlist(filename,
				new AsyncCallback<PlistDict>() {
					@Override
					public void onSuccess(AsyncResult<PlistDict> Results) {
						queueAsset.complited();
						try {
							callback.onSuccess(Results);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailed(AsyncResult<PlistDict> Results) {
						queueAsset.complited();
						try {
							callback.onFailed(Results);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		queueAsset.offer(tag, priority, task);
	}

	public void offerPlist(final String tag, final int priority,
			String baseUrl, final String filename,
			final AsyncCallback<PlistDict> callback) {

		if (baseUrl == null || baseUrl.equals("")) {
			// baseUrlがnull or 空のときは、リソースファイルから検索する。
			assetPlist(tag, priority, filename, callback);
			return;
		}

		AsyncGetPlist task = new AsyncGetPlist(baseUrl, filename,
				new AsyncCallback<PlistDict>() {
					@Override
					public void onSuccess(AsyncResult<PlistDict> Results) {
						queueAsync.complited();
						try {
							callback.onSuccess(Results);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailed(AsyncResult<PlistDict> Results) {
						queueAsync.complited();
						// Net上に無いなら、リソースからから検索する。
						assetPlist(tag, priority, filename, callback);
					}
				});

		queueAsync.offer(tag, priority, task);
	}

	public void assetCsv(String tag, int priority, String filename,
			final AsyncCallback<CsvArray> callback) {

		AssetGetCsvArray task = new AssetGetCsvArray(filename,
				new AsyncCallback<CsvArray>() {
					@Override
					public void onSuccess(AsyncResult<CsvArray> Results) {
						queueAsset.complited();
						try {
							callback.onSuccess(Results);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailed(AsyncResult<CsvArray> Results) {
						queueAsset.complited();
						try {
							callback.onFailed(Results);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		queueAsset.offer(tag, priority, task);
	}

	public void offerCsv(final String tag, final int priority, String baseUrl,
			final String filename, final AsyncCallback<CsvArray> callback) {

		if (baseUrl == null || baseUrl.equals("")) {
			// baseUrlがnull or 空のときは、リソースファイルから検索する。
			assetCsv(tag, priority, filename, callback);
			return;
		}

		AsyncGetCsvArray task = new AsyncGetCsvArray(baseUrl, filename,
				new AsyncCallback<CsvArray>() {
					@Override
					public void onSuccess(AsyncResult<CsvArray> Results) {
						queueAsync.complited();
						try {
							callback.onSuccess(Results);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailed(AsyncResult<CsvArray> Results) {
						queueAsync.complited();
						// Net上に無いなら、リソースからから検索する。
						assetCsv(tag, priority, filename, callback);
					}
				});

		queueAsync.offer(tag, priority, task);
	}

	public void assetImage(String tag, int priority, String filename,
			final AsyncCallback<Bitmap> callback) {

		// ファイルがあるなら、すぐさま返す
		AssetManager asset = MainApplication.getAssetManager();
		InputStream is = null;
		try {
			is = asset.open(filename);
		} catch (IOException e) {
			// e.printStackTrace();
		}
		if (is != null) {
			Bitmap bmp = null;
			try {
				bmp = BitmapUtil.getInstance().decodeStream(is);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			AsyncResult<Bitmap> result = AsyncResult.createNormalResult(bmp,
					null);
			callback.onSuccess(result);
			return;
		}

		AssetGetImage task = new AssetGetImage(filename,
				new AsyncCallback<Bitmap>() {
					@Override
					public void onSuccess(AsyncResult<Bitmap> result) {
						queueAsset.complited();
						try {
							callback.onSuccess(result);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailed(AsyncResult<Bitmap> result) {
						queueAsset.complited();
						try {
							callback.onFailed(result);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		queueAsset.offer(tag, priority, task);
	}

	public void offerImage(final String tag, final int priority,
			String baseUrl, final String filename,
			final AsyncCallback<Bitmap> callback) {
		offerImage(tag, priority, baseUrl, filename, callback, true);
	}

	public void offerImage(final String tag, final int priority,
			String baseUrl, final String filename,
			final AsyncCallback<Bitmap> callback, boolean flag) {

		if (baseUrl == null || baseUrl.equals("")) {
			// baseUrlがnull or 空のときは、リソースファイルから検索する。
			assetImage(tag, priority, filename, callback);
			return;
		}

		// キャッシュがあるなら、すぐさま返す
		if (flag) {
			DiskCache disk = MainApplication.getInstance().getDickCache();
			InputStream is = disk.get(baseUrl, filename);
			if (is != null) {
				Bitmap bmp = null;
				try {
					bmp = BitmapUtil.getInstance().decodeStream(is);
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				AsyncResult<Bitmap> result = AsyncResult.createNormalResult(
						bmp, null);
				callback.onSuccess(result);
				return;
			}
		}

		AsyncGetImage task = new AsyncGetImage(baseUrl, filename,
				new AsyncCallback<Bitmap>() {
					@Override
					public void onSuccess(AsyncResult<Bitmap> result) {
						queueAsync.complited();
						try {
							callback.onSuccess(result);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailed(AsyncResult<Bitmap> result) {
						queueAsync.complited();
						// Net上に無いなら、リソースからから検索する。
						assetImage(tag, priority, filename, callback);
					}
				});

		queueAsync.offer(tag, priority, task);
	}

	public void offerToCache(final String tag, final int priority,
			String baseUrl, final String filename,
			final AsyncCallback<InputStream> callback) {

		if (baseUrl == null || baseUrl.equals("")) {
			// baseUrlがnull or 空のときは、リソースファイルからなので不要。
			return;
		}

		AsyncGetToCache task = new AsyncGetToCache(baseUrl, filename,
				new AsyncCallback<InputStream>() {
					@Override
					public void onSuccess(AsyncResult<InputStream> result) {
						queueAsync.complited();
						try {
							callback.onSuccess(result);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailed(AsyncResult<InputStream> result) {
						queueAsync.complited();
					}
				});

		queueAsync.offer(tag, priority, task);
	}

	public void clear() {
		queueAsync.clear();
		queueAsset.clear();
	}

	public void clear(String tag) {
		queueAsync.clear(tag);
		queueAsset.clear(tag);
	}
}
