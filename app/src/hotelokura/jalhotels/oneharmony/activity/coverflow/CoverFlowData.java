package hotelokura.jalhotels.oneharmony.activity.coverflow;

import android.content.res.Configuration;
import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.activity.CatalogDickCachUtil;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListData;
import hotelokura.jalhotels.oneharmony.net.AsyncCallback;
import hotelokura.jalhotels.oneharmony.net.AsyncResult;
import hotelokura.jalhotels.oneharmony.net.DownloadManager;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.setting.BGImageSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

public class CoverFlowData extends CatalogListData {
    static final String TAG = "CoverFlowData";

	@Override
	public String settingName() {
		return "coverflow.csv";
	}

	@Override
	public ActionMode settingActionMode() {
		return ActionMode.CoverFlow;
	}

	@Override
	public String settingNavigationLogoImage(String catalogId) {
		return null;
	}

	@Override
	public BGImageSetting settingBgImage(String id, int orientation) {
		AppSetting appSetting = MainApplication.getInstance().getAppSetting();
		BGImageSetting setting = null;
		switch (orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			setting = appSetting.getCoverFlowBGImageDpi();
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			setting = appSetting.getCoverFlowBGImageLandscapeDpi();
			break;
		}
		return setting;
	}

	@Override
	protected CsvArray settingCsvArray(CsvArray csvArray) {
		return csvArray;
	}

    /**
     * WebTop用にファイルをダウンロードする<br>
     *
     * トップメニューの場合
     * ・catalogList.csv
     * ・storlist.csv
     */
    public void downloadSettingForWebTop() {
        final String SETTING_NAME = "cataloglist.csv";
        downloadCsvCompleted = false;

        // 自身の設定ファイルをダウンロード
        DownloadManager.getInstance().offerCsv(SETTING_NAME, 0, catalogUrl, SETTING_NAME
                , new AsyncCallback<CsvArray>() {
            @Override
            public void onSuccess(AsyncResult<CsvArray> result) {
                CsvArray csvArray = result.getContent();
                catalogListLastMod = result.getLastModified();

                // ダウンロード時間を記憶
                CatalogDickCachUtil.saveCatalogListDownload(catalogUrl,
                        settingActionMode());

                // キャッシュクリアの確認
                CatalogDickCachUtil.chackCatalogListSetting(catalogUrl,
                        settingActionMode(), catalogListLastMod);

                // catalogListCsvArrayの作成
                downloadSettingSuccess(csvArray);

                // catalogListCsvArrayの作成
                MainApplication.getInstance().setCataloglistCsvArray(csvArray);

                //storlist.csvを取得
                downloadStoreListSettingForWebTop();
            }

            @Override
            public void onFailed(AsyncResult<CsvArray> result) {
                catalogListLastMod = null;
                MainApplication mainApp = MainApplication.getInstance();
                mainApp.setCataloglistCsvArray(null);
                getCallback().downloadFailed();
            }
        });
    }

    /**
     * 地図設定ファイルをダウンロードする<br>
     */
    protected void downloadStoreListSettingForWebTop() {
        final String SETTING_NAME = "storelist.csv";

        // 自身の設定ファイルをダウンロード
        DownloadManager.getInstance().offerCsv(SETTING_NAME, 0, catalogUrl,
                SETTING_NAME, new AsyncCallback<CsvArray>() {
            @Override
            public void onSuccess(AsyncResult<CsvArray> result) {
                CsvArray csvArray = result.getContent();

                // MainApplicationに保存
                MainApplication.getInstance().setStorelistCsvArray(csvArray);

                downloadCsvCompleted = true;
                checkCompleted();
            }

            @Override
            public void onFailed(AsyncResult<CsvArray> result) {
                MainApplication.getInstance().setStorelistCsvArray(new CsvArray());
                LogUtil.e(TAG,"Getting Stor List Fail!!");
                downloadCsvCompleted = true;
                checkCompleted();
            }
        });
    }
}
