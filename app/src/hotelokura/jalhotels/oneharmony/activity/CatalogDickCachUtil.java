package hotelokura.jalhotels.oneharmony.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.content.SharedPreferences;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.setting.CatalogSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.setting.PageListSetting;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListData;

public class CatalogDickCachUtil {
	private static final String TAG = "CatalogDickCachUtil";

	private CatalogDickCachUtil() {
	}

	/**
	 * App.plistが更新されているかチェックする<br>
	 * 更新されていたらキャッシュはクリア
	 * 
	 * @param baseUrl
	 * @param appLastMod
	 */
	public static void chackAppSetting(String baseUrl, String appLastMod) {

		boolean updated = false;
		boolean saved = false;

		if (baseUrl == null || appLastMod == null) {
			// ローカルファイルなので更新無し
			return;
		}

		// プリファレンス取得
		SharedPreferences sharedPreferences = MainApplication.getInstance()
				.getSharedPreferences();

		// プリファレンスに記憶したLastModified取得
		String appLastModKey = "App_LastModified";
		String lastModified = sharedPreferences.getString(appLastModKey, null);

		if (lastModified == null) {
			// プリファレンス存在しない(初回)
			saved = true;
		} else {
			if (!appLastMod.equals(lastModified)) {
				// 設定が更新された
				updated = true;
			}
		}

		if (updated) {
			// 更新されていたらキャッシュクリア
			LogUtil.d(TAG, "chackAppSetting: removeCache" + baseUrl);
			MainApplication.getInstance().removeCacheDir(baseUrl);
			saved = true;
		}

		if (saved) {
			// プリファレンスに保存
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if (appLastMod != null) {
				editor.putString(appLastModKey, appLastMod);
			}
			editor.commit();
		}
	}

	/**
	 * カタログリストをダウンロードするか判定する<br>
	 * 戻るボタンで戻った時、60分間はダウンロードを行わないでキャッシュを利用する
	 * 
	 * @param baseUrl
	 * @param actionMode
	 * @return
	 */
	public static boolean chackCatalogListDownload(String baseUrl,
			CatalogListData.ActionMode actionMode) {
		if (baseUrl == null) {
			// ローカルファイルなので更新無し
			return false;
		}

		// プリファレンスのキー
		String lastDownloadKey;
		if (actionMode == CatalogListData.ActionMode.CoverFlow) {
			lastDownloadKey = "CoverFlow_LastDownload";
		} else {
			lastDownloadKey = "CatalogList_LastDownload";
		}

		// プリファレンス取得
		SharedPreferences sharedPreferences = MainApplication.getInstance()
				.getSharedPreferences();

		// プリファレンスに記憶したlastDownload取得
		String lastDownload = sharedPreferences
				.getString(lastDownloadKey, null);
		if (lastDownload == null) {
			// プリファレンス存在しない(初回)
			return true;
		} else {
			// lastDownloadをDate型に変換
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
					Locale.JAPANESE);
			Date lastDownloadDate = null;
			try {
				lastDownloadDate = sdf.parse(lastDownload);
			} catch (ParseException e) {
				e.printStackTrace();
				return true;
			}

			// 10分後の時間
			long nextDownloadTime = lastDownloadDate.getTime()
					+ (1000 * 60 * 10);
			Date nextDownloadDate = new Date(nextDownloadTime);
			// 現在時刻
			Date now = new Date();

			// 比較
			if (now.compareTo(nextDownloadDate) > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * カタログリストのダウンロード時刻を記憶する
	 * 
	 * @param baseUrl
	 * @param actionMode
	 */
	public static void saveCatalogListDownload(String baseUrl,
			CatalogListData.ActionMode actionMode) {
		if (baseUrl == null) {
			// ローカルファイルなので更新無し
			return;
		}

		// 現在日時
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
				Locale.JAPANESE);
		String nowString = sdf.format(now);

		// プリファレンスのキー
		String lastDownloadKey;
		if (actionMode == CatalogListData.ActionMode.CoverFlow) {
			lastDownloadKey = "CoverFlow_LastDownload";
		} else {
			lastDownloadKey = "CatalogList_LastDownload";
		}

		// プリファレンス取得
		SharedPreferences sharedPreferences = MainApplication.getInstance()
				.getSharedPreferences();

		// プリファレンスに保存
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(lastDownloadKey, nowString);
		editor.commit();
	}

	/**
	 * カタログリストが更新されているかチェックする<br>
	 * 更新されていたらキャッシュはクリア
	 * 
	 * @param baseUrl
	 * @param catalogId
	 * @param catalogListLastMod
	 */
	public static void chackCatalogListSetting(String baseUrl,
			CatalogListData.ActionMode actionMode, String catalogListLastMod) {

		boolean updated = false;
		boolean saved = false;

		if (baseUrl == null || catalogListLastMod == null) {
			// ローカルファイルなので更新無し
			return;
		}

		// プリファレンス取得
		SharedPreferences sharedPreferences = MainApplication.getInstance()
				.getSharedPreferences();

		// プリファレンスに記憶したLastModified取得
		String catalogListLastModKey;
		if (actionMode == CatalogListData.ActionMode.CoverFlow) {
			catalogListLastModKey = "CoverFlow_LastModified";
		} else {
			catalogListLastModKey = "CatalogList_LastModified";
		}
		String lastModified = sharedPreferences.getString(
				catalogListLastModKey, null);

		if (lastModified == null) {
			// プリファレンス存在しない(初回)
			saved = true;
		} else {
			if (!catalogListLastMod.equals(lastModified)) {
				// 設定が更新された
				updated = true;
			}
		}

		if (updated) {
			// 更新されていたらキャッシュクリア
			LogUtil.d(TAG, "chackCatalogListSetting: removeCache" + baseUrl);
			MainApplication.getInstance().removeCacheDir(baseUrl);
			saved = true;
		}

		if (saved) {
			// プリファレンスに保存
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if (catalogListLastMod != null) {
				editor.putString(catalogListLastModKey, catalogListLastMod);
			}
			editor.commit();
		}
	}

	/**
	 * カタログ基本設定とページリスト設定が更新されているかチェックする<br>
	 * 更新されていたらカタログページ設定のFileSourceに関するキャッシュはクリア
	 * 
	 * @param catalogId
	 * @param setting
	 * @param settingLastMod
	 * @param pageListCsvArray
	 * @param pageListLastMod
	 */
	public static void chackCatalogSetting(String catalogId,
			CatalogSetting setting, String settingLastMod,
			CsvArray pageListCsvArray, String pageListLastMod) {

		boolean updated = false;
		boolean saved = false;

		SharedPreferences sharedPreferences = MainApplication.getInstance()
				.getSharedPreferences();

		// カタログ基本設定の更新日時チェック
		String settingLastModKey = catalogId + "_Catalog_LastModified";
		if (settingLastMod == null) {
			// ローカルファイルなので更新無し
		} else {
			// プリファレンスに記憶したLastModified取得
			String lastModified = sharedPreferences.getString(
					settingLastModKey, null);
			if (lastModified == null) {
				// プリファレンス存在しない(初回)
				saved = true;
			} else {
				if (!settingLastMod.equals(lastModified)) {
					// カタログ基本設定が更新された
					updated = true;
				}
			}
		}

		// カタログ基本設定のUpdateDate設定チェック
		String settingUpdateKey = catalogId + "_Catalog_UpdateDate";
		String settingUpdate = setting.getUpdateDateString();
		if (settingUpdate == null) {
			// 設定無し
		} else {
			// プリファレンスに記憶したUpdateDate取得
			String updateDate = sharedPreferences.getString(settingUpdateKey,
					null);
			if (updateDate == null) {
				// プリファレンス存在しない(初回)
				saved = true;
			} else if (!settingUpdate.equals(updateDate)) {
				updated = true;
			}
		}

		// ページ一覧の更新日時チェック
		String pageListLastModKey = catalogId + "_PageList_LastModified";
		if (pageListLastMod == null) {
			// ローカルファイルなので更新無し
		} else {
			// プリファレンスに記憶したLastModified取得
			String lastModified = sharedPreferences.getString(
					pageListLastModKey, null);
			if (lastModified == null) {
				// プリファレンス存在しない(初回)
				saved = true;
			} else if (!pageListLastMod.equals(lastModified)) {
				// ページ一覧が更新された
				updated = true;
			}
		}

		// 更新されていたらキャッシュクリア
		if (updated) {
			// 削除対象のディレクトリ一覧作成
			HashMap<String, Boolean> dirs = new HashMap<String, Boolean>();
			PageListSetting page = new PageListSetting();
			for (CsvLine line : pageListCsvArray) {
				page.setLine(line);
				String fileSource = page.getFileSource();
				if (fileSource != null) {
					dirs.put(fileSource, true);
				}
			}
			// 削除
			for (Iterator<String> it = dirs.keySet().iterator(); it.hasNext();) {
				String dir = it.next();
				LogUtil.d(TAG, "chackCatalogSetting: removeCache" + dir);
				MainApplication.getInstance().removeCacheDir(dir);
			}
			saved = true;
		}

		// プリファレンスに保存
		if (saved) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if (settingLastMod != null) {
				editor.putString(settingLastModKey, settingLastMod);
			}
			if (settingUpdate != null) {
				editor.putString(settingUpdateKey, settingUpdate);
			}
			if (pageListLastMod != null) {
				editor.putString(pageListLastModKey, pageListLastMod);
			}
			editor.commit();
		}
	}
}
