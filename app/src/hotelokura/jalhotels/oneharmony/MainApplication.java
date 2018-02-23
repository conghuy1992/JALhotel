package hotelokura.jalhotels.oneharmony;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListData;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListItemData;
import hotelokura.jalhotels.oneharmony.analytics.CheckAction;
import hotelokura.jalhotels.oneharmony.brand_preview.PreviewManager;
import hotelokura.jalhotels.oneharmony.cache.DiskCache;
import hotelokura.jalhotels.oneharmony.setting.AppNetSetting;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.setting.CatalogSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

public class MainApplication extends Application {
	static final String TAG = "MainApplication";

	private static MainApplication instance;
	private static String appName;

	private DiskCache dickCache;
	private AppNetSetting appNetSetting;
	private AppSetting appSetting;
	private CatalogListData listData;
	private CatalogListItemData itemData;

	private CsvArray coverflowCsvArray;
	private CsvArray cataloglistCsvArray;
    private CsvArray storelistCsvArray;

    private CatalogSetting currentCatalogSetting;

    private String mCurrentCatalogListId;
    private String mCurrentCatalogListTitle;
    private String[] mCurrentCatalogListTags;

    private String mCurrentCatalogId;

    private String mWebTopUrl;
    private String mWebTopTitle;

    private ApplicationInfo mAppliInfo;

	public static MainApplication getInstance() {
		return instance;
	}

    private CheckAction mCheckAction = null;

	public MainApplication() {
		super();
		LogUtil.d(TAG, "MainApplication");
		instance = this;
	}

	public DiskCache getDickCache() {
		return dickCache;
	}

	public AppNetSetting getAppNetSetting() {
		return appNetSetting;
	}

	public void setAppNetSetting(AppNetSetting appNetSetting) {
		this.appNetSetting = appNetSetting;
	}

	public AppSetting getAppSetting() {
		return appSetting;
	}

	public void setAppSetting(AppSetting appSetting) {
		this.appSetting = appSetting;
	}

	public CatalogListData getListData() {
		return listData;
	}

	public void setListData(CatalogListData listData) {
		this.listData = listData;
	}

	public CatalogListItemData getItemData() {
		return itemData;
	}

	public void setItemData(CatalogListItemData itemData) {
		this.itemData = itemData;
	}

	public CsvArray getCoverflowCsvArray() {
		return coverflowCsvArray;
	}

	public void setCoverflowCsvArray(CsvArray coverflowCsvArray) {
		this.coverflowCsvArray = coverflowCsvArray;
	}

	public CsvArray getCataloglistCsvArray() {
		return cataloglistCsvArray;
	}

	public void setCataloglistCsvArray(CsvArray cataloglistCsvArray) {
		this.cataloglistCsvArray = cataloglistCsvArray;
	}

    public CsvArray getStorelistCsvArray() {
        return storelistCsvArray;
    }

    public void setStorelistCsvArray(CsvArray storelistCsvArray) {
        this.storelistCsvArray = storelistCsvArray;
    }

    public CatalogSetting getCurrentCatalogSetting() {
        return currentCatalogSetting;
    }

    public void setCurrentCatalogSetting(CatalogSetting currentCatalogSetting) {
        this.currentCatalogSetting = currentCatalogSetting;
    }

    public String getCurrentCatalogListTitle() {
        return mCurrentCatalogListTitle;
    }

    public void setCurrentCatalogListTitle(String title) {
        mCurrentCatalogListTitle = title;
    }

    public String getCurrentCatalogListId() {
        return mCurrentCatalogListId;
    }

    public void setCurrentCatalogListId(String id) {
        mCurrentCatalogListId = id;
    }

    public String[] getCurrentCatalogListTags() {
        return mCurrentCatalogListTags;
    }

    public void setCurrentCatalogListTags(String[] tag) {
        mCurrentCatalogListTags = tag;
    }

    public String getCurrentCatalogId() {
        return mCurrentCatalogId;
    }

    public void setCurrentCatalogId(String catalogId) {
        mCurrentCatalogId = catalogId;
    }

    public String getWebTopUrl() {
        return mWebTopUrl;
    }

    public void setWebTopUrl(String url) {
        mWebTopUrl = url;
    }

    public String getWebTopTitle() {
        return  mWebTopTitle;
    }

    public void setWebTopTitle(String title) {
        mWebTopTitle = title;
    }

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		LogUtil.d(TAG, "onConfigurationChanged");
		// 端末の状態が変わった時（オリエンテーションの変更など
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		LogUtil.d(TAG, "onCreate");
		// Applicationクラス作成時
		super.onCreate();
		appName = getResources().getString(hotelokura.jalhotels.oneharmony.R.string.app_name);
		dickCache = new DiskCache(appName);
	}

	@Override
	public void onLowMemory() {
		LogUtil.d(TAG, "onLowMemory");
		// 使用出来るメモリが少なくなった時
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		LogUtil.d(TAG, "onTerminate");
		// Applicationクラス終了時
		super.onTerminate();
	}

	/**
	 * キャッシュを全て削除する。
	 */
	public void removeCache() {
		if (dickCache != null) {
			dickCache.removeAll();
		}
	}

	/**
	 * 指定ディレクトリ以下のキャッシュを全て削除する。
	 */
	public void removeCacheDir(String dir) {
		if (dickCache != null) {
			dickCache.removeDir(dir);
		}
	}

    /**
     * 指定ディレクトリ以下のキャッシュを全て削除する。
     */
    public void removeCacheFile(String dir, String filename) {
        if (dickCache != null) {
            dickCache.remove(dir, filename);
        }
    }

    public static AssetManager getAssetManager() {
		return instance.getResources().getAssets();
	}

	/**
	 * Dataディレクトリを返す(/dataなど)
	 *
	 * @return
	 */
	public static File getDataDir() {
		File file = Environment.getDataDirectory();
		return new File(file, appName);
	}

	/**
	 * DownloadCacheディレクトリを返す(/cacheなど)<br>
	 * これではなく、getCacheDir()を使うべき？
	 *
	 * @return
	 */
	public static File getDownloadCacheDir() {
		File file = Environment.getDownloadCacheDirectory();
		return new File(file, appName);
	}

	/**
	 * ExternalStorageディレクトリを返す(/sdcardなど)<br>
	 * これではなく、getExternalCacheDir()を使うべき？<br>
	 * <br>
	 * 取得した後、読み書き可能かチェックする必要がある<br>
	 * file.canRead() or file.canWrite();
	 *
	 * @return
	 */
	public static File getExternalStorageDir() {
		File file = Environment.getExternalStorageDirectory();
		return new File(file, appName);
	}

	/**
	 * ExternalStorageがマウントされているか
	 *
	 * @return
	 */
	public static boolean isExternalStorageAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * UUID文字列を作成する
	 *
	 * @return
	 */
	public static String makeUUIDString() {
		return UUID.randomUUID().toString();
	}

	/**
	 * アプリ設定の取得
	 *
	 * @return
	 */
	public SharedPreferences getSharedPreferences() {
		return PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
	}

	/**
	 * DPI値の取得
	 *
	 * @return
	 */
	public float getDpi() {
		return getResources().getDisplayMetrics().densityDpi;
	}

	public boolean isLDpi() {
		return true;
	}

	public boolean isMDpi() {
		float dpi = getDpi();
		return (DisplayMetrics.DENSITY_MEDIUM <= dpi);
	}

	public boolean isHDpi() {
		float dpi = getDpi();
		return (DisplayMetrics.DENSITY_HIGH <= dpi);
	}

	/**
	 * DPからPXへ変換
	 *
	 * @param dp
	 * @return
	 */
	public int dp2px(int dp) {
		return (int) (dp * getResources().getDisplayMetrics().density);
	}

	/**
	 * PXからDPへ変換
	 *
	 * @param px
	 * @return
	 */
	public int px2dp(int px) {
		return (int) (px / getResources().getDisplayMetrics().density);
	}

    /**
     * インチを取得
     *
     * @return
     */
    public double getInch() {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        // ピクセル数（width, height）を取得する
        final int widthPx = metrics.widthPixels;
        final int heightPx = metrics.heightPixels;

        // dpi (xdpi, ydpi) を取得する
        final float xdpi = metrics.xdpi;
        final float ydpi = metrics.ydpi;

        // インチ（width, height) を計算する
        final float widthIn = widthPx / xdpi;
        final float heightIn = heightPx / ydpi;

        // 画面サイズ（インチ）を計算する
        return Math.sqrt(widthIn * widthIn + heightIn * heightIn);
    }

	/**
	 * Tablet判定
	 *
	 * @return
	 */
	public boolean isTabletDevice() {
        if (6 < getInch()) {
            LogUtil.d("display", "tablet");
            return true;
        }

        LogUtil.d("display", "smartphone");
        return false;
	}

	/**
	 * ネットワークに接続しているか
	 *
	 * @return
	 */
	public boolean isNetConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null) {
			return cm.getActiveNetworkInfo().isConnected();
		}
		return false;
	}

	/**
	 * バージョンコードを取得する
	 */
	public int getVersionCode() {
		PackageManager pm = getPackageManager();
		int versionCode = 0;
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * バージョン名を取得する
	 */
	public String getVersionName() {
		PackageManager pm = getPackageManager();
		String versionName = "";
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

    /**
     * BRANDアプリかどうかを取得する
     */
    public boolean isBrand() {
        return false;
    }

    /**
     * ショッピングアプリかどうかを取得する
     */
    public boolean isEcApp() {
        return false;
    }

    /**
     * Manifestのアプリ情報を取得する
     */
    public ApplicationInfo getAppInfo() {
        if (mAppliInfo == null) {
            PackageManager pm = getPackageManager();
            if (pm != null) {
                try {
                    mAppliInfo = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException e) {}
            } else {
                LogUtil.e(TAG, "getPackageManager() fail.");
            }
        }
        return mAppliInfo;
    }

    /**
     * ManifestのAPI Keyを取得する
     */
    public String getApiKey() {
        ApplicationInfo appInfo = getAppInfo();
        if (appInfo != null && appInfo.metaData != null) {
            return appInfo.metaData.getString("com.google.android.maps.v2.API_KEY");
        }
        return null;
    }

    /**
     * アプリIDを取得する
     */
    public String getAppId() {
        PreviewManager pm = PreviewManager.getInstance(getApplicationContext());
        if (pm != null && pm.isPreview()) {
            return pm.getAppId();
        } else {
            return getString(hotelokura.jalhotels.oneharmony.R.string.app_id);
        }
    }

    public void initCheckAction() {
        ArrayList<String> trackingIdsList = this.appSetting.getTrackingIDs();
        this.mCheckAction = new CheckAction(this, trackingIdsList);
    }

    public CheckAction getCheckAction() {
        return this.mCheckAction;
    }
}
