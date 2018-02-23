package hotelokura.jalhotels.oneharmony.setting;

import java.util.Arrays;

import hotelokura.jalhotels.oneharmony.MainApplication;

/**
 * 設定 cataloglist.csv
 */
public class CatalogListSetting extends Setting {

	protected CsvLine line;

	// @todo MODEの値定数 暫定の定数の置き場所。移動するかも。　「CONST_」の部分は決定した定数位置により消す可能性あり。
	// MODE
	public static final int MODE_CATALOG = 1;
	public static final int MODE_CATALOG_LIST = 10;
	public static final int MODE_CATALOG_LIST_WEB = 11;
	public static final int MODE_MOVIE = 20;
	public static final int MODE_MAP = 31;
    public static final int MODE_MAP_LIST = 30;
	public static final int MODE_URL = 40;
	public static final int MODE_URL_SCHEMA = 41;
	public static final int MODE_NEWS = 50;
	public static final int MODE_NEWS_MENU = 60;
	public static final int MODE_RESERVATION = 70;


	/**
     * catalogIdに該当する設定を探す
     *
     * @param catalogId
     * @return
     */
    static public CsvLine findCatalogListCsv(String catalogId) {
        // カタログリストからIDを探す
        CsvArray catalogListArray = MainApplication.getInstance()
                .getCataloglistCsvArray();
        for (CsvLine csv : catalogListArray) {
            CatalogListSetting setting = new CatalogListSetting(csv);

            // mode=1以外は除外
            if (setting.getMode() != CatalogListSetting.MODE_CATALOG) {
                continue;
            }
            if (setting.getID().equals(catalogId)) {
                return csv;
            }
        }

        // カタログリストに無かったら、カバーフローから探す
        CsvArray coverFlowArray = MainApplication.getInstance()
                .getCoverflowCsvArray();
        for (CsvLine csv : coverFlowArray) {
            CoverFlowSetting setting = new CoverFlowSetting(csv);

            // mode=1以外は除外
            if (setting.getMode() != CoverFlowSetting.MODE_CATALOG) {
                continue;
            }
            if (setting.getID().equals(catalogId)) {
                return csv;
            }
        }
        return null;
    }

	/**
	 * 無効データを排除した新しいCsvArrayデータを作成して返す。
	 * 
	 * @param array
	 * @return
	 */
	public static CsvArray removeInvalid(CsvArray array) {
		if (array == null) {
			return null;
		}
		CsvArray newArray = new CsvArray();
		for (CsvLine line : array) {
			CatalogListSetting setting = new CatalogListSetting(line);
			int mode = setting.getMode().intValue();

			// modeが0以下が無効
			if (mode > 0) {
				newArray.add(line);
			}
		}
		return newArray;
	}

	/**
	 * 指定したtagに一致した物のみの、新しいCsvArrayデータを作成して返す。
	 * 
	 * @param array
	 * @param matchTags
	 * @return
	 */
	public static CsvArray findTag(CsvArray array, String[] matchTags) {
		if (array == null) {
			return null;
		}
		if (matchTags == null) {
			return array;
		}
		CsvArray newArray = new CsvArray();
		for (CsvLine line : array) {
			CatalogListSetting setting = new CatalogListSetting(line);
			String[] tags = setting.getTags();

			// tagが無い場合は無視。除外とする。
			if (tags == null) {
				continue;
			}

			// tagsとmatchTagsを比較して、一つでも一致すれば対象
			boolean matchFlg = false;
			for (String tag : tags) {
				if (Arrays.asList(matchTags).contains(tag)) {
					matchFlg = true;
					break;
				}
			}
			// 対象確定
			if (matchFlg) {
				newArray.add(line);
			}
		}
		return newArray;
	}

	public CatalogListSetting() {
		super();
	}

	public CatalogListSetting(CsvLine line) {
		super();
		this.line = line;
	}

	public CsvLine getLine() {
		return line;
	}

	public void setLine(CsvLine line) {
		this.line = line;
	}

	/**
	 * 挙動フラグ
	 * 
	 * <pre>
	 * 現在、以下の値が定義済。これ以外の設定は行わないこと。
	 * 0 :無効(この行は除外する)
	 * 1 :カタログ表示
	 * 10 :カタログ一覧表示
	 * 20 :動画表示<予定>
	 * 30 :地図表示
	 * 40 :URLリンク
	 * 41 :URLリンク外部ブラウザ(スキーマ)
	 * 負の数: 無効（コメント扱い）
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getMode() {
		return line.getInteger(0, 0);
	}

	/**
	 * 項目を識別するためのID文字列
	 * 
	 * <pre>
	 * 次画面に受け渡されて、識別に使用される。
	 * 
	 * mode=1の場合はカタログIDと同じ。
	 * mode=10の場合は、次画面での背景画像変更などのために使用できる。無指定も可。詳しくは「アプリケーション設定」参照。
	 * 
	 * Mode=30の場合は店舗の絞り込みを行うためのタグID。絞り込みが不要の時(全ての店舗を表示する時)は空とする。詳しくは「地図設定」を参照。
	 * </pre>
	 * 
	 * @return
	 */
	public String getID() {
		return line.getString(1, null);
	}

	/**
	 * カバーフロー項目のタイトル文言
	 * 
	 * @return
	 */
	public String getTitle() {
		return line.getString(2, null);
	}

	/**
	 * カバーフロー画像を指定する。
	 * 
	 * <pre>
	 * カタログ一覧ページへの表示用画像ファイル名を指定。
	 * 画像フォーマットは jpg 又は png とする。
	 * 画像サイズは任意だが、iPhone用は240x240、iPad用は580x580の正方形画像が丁度ぴったりとなる。
	 * 画像ファイルは、プロジェクトのリソースとして持つか、coverflow.csvと同じURLに置くこと。
	 * </pre>
	 * 
	 * @return
	 */
	public String getImage() {
		return line.getString(3, null);
	}

	/**
	 * カバーフロー項目の表示用説明文。
	 * 
	 * <pre>
	 * 改行文字を入れたい場合は、「\n」または「<br>」と記述すると、その箇所に改行が挿入される。
	 * 最大3行まで表示され、はみ出したぶんは省略される。
	 * </pre>
	 * 
	 * @return
	 */
	public String getText() {
		return line.getString(4, null);
	}

	public String getTextIndention() {
		return replaceIndention(getText(), null);
	}

	/**
	 * url設定
	 * 
	 * <pre>
	 * mode = 1[カタログ表示] の時、ネット対応するためのurl設定。
	 * 
	 * mode = 20[動画再生] の時、動画ファイルへのURLフルパス(ストリーミング再生用)又は、動画ファイル名(リソース内蔵再生用)
	 * 
	 * mode = 40[URLリンク] の時、開く対象のURL
	 * mode = 41[URLリンク外部] の時、開く対象のURL又は、iOSアプリのURLスキーム
	 * </pre>
	 * 
	 * @return
	 */
	public String getUrl() {
		return line.getString(5, null);
	}

	public String getUrlUnEscape() {
		String url = getUrl();
		return urlUnEscape(url, url);
	}

	/**
	 * tag設定
	 * 
	 * <pre>
	 * mode = 10 [カタログ一覧表示]時の、絞り込み表示用タグ。
	 * 複数記述する時は、セミコロンで区切って列挙する。絞り込みが不要の場合は、空欄とする。
	 * </pre>
	 * 
	 * @return
	 */
	public String getTag() {
		return line.getString(6, null);
	}

	public String[] getTags() {
		String text = getTag();
		if (text != null) {
			return text.split(";");
		} else {
			return null;
		}
	}

    public String getContentTitle() {
        return line.getString(7, null);
    }
}
