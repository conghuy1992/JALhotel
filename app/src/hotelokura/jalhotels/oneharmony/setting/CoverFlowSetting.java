package hotelokura.jalhotels.oneharmony.setting;

import hotelokura.jalhotels.oneharmony.MainApplication;

/**
 * 設定 coverflow.csv
 */
public class CoverFlowSetting extends CatalogListSetting {

	public CoverFlowSetting() {
		super();
	}

	public CoverFlowSetting(CsvLine line) {
		super(line);
	}

    /**
     * catalogIdに該当する設定を探す
     *
     * @param catalogId
     * @return
     */
    static public CsvLine findCoverFlowCsv(String catalogId) {
        // カバーフローから探す
        CsvArray coverFlowArray = MainApplication.getInstance()
                .getCoverflowCsvArray();
        if (coverFlowArray == null) {
            return null;
        }
        for (CsvLine csv : coverFlowArray) {
            CoverFlowSetting setting = new CoverFlowSetting(csv);

            if (setting.getID() == null) {
                continue;
            }
            if (setting.getID().equals(catalogId)) {
                return csv;
            }
        }
        return null;
    }

	/**
	 * タイトル設定
	 * 
	 * <pre>
	 * mode = 10 [カタログ一覧表示]時の、一覧画面のタイトルを指定する。
	 * 空欄時はApp.settingsで指定された元々のタイトル(CataloglistTitle)を使用。
	 * 
	 * Mode=30[地図表示]時の、一覧画面のタイトルを指定する。
	 * </pre>
	 * 
	 * @return
	 */
	public String getContentTitle() {
		return line.getString(7, null);
	}
}
