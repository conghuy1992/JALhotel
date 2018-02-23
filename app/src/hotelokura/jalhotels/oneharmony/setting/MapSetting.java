package hotelokura.jalhotels.oneharmony.setting;

import android.util.Log;

/**
 * 設定 storelist.csv
 */
public class MapSetting extends Setting {

    protected CsvLine line;

    /**
     * 指定したIDをcatalogidに持つ新しいCsvArrayデータを作成して返す。
     *
     * @param array  CSV配列
     * @param findId カタログID
     * @return
     */
    public static CsvArray findId(CsvArray array, String findId) {
        if (array == null) {
            return null;
        }
        CsvArray newArray = new CsvArray();
        for (CsvLine line : array) {
            MapSetting setting = new MapSetting(line);
            String catalogId = setting.getCatalogId();
            if (catalogId != null && catalogId.equals(findId)) {
                newArray.add(line);
            }
        }
        return newArray;
    }

    public MapSetting() {
        super();
    }

    public MapSetting(CsvLine line) {
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
     * 地図設定のcatalogid
     *
     * @return
     */
    public String getCatalogId() {
        return line.getString(0, null);
    }

    /**
     * 地図設定のname
     *
     * @return
     */
    public String getName() {
        return line.getString(1, null);
    }

    /**
     * 地図設定のcategory
     * <p/>
     * <pre>
     * mode = 10 [カタログ一覧表示]時の、絞り込み表示用タグ。
     * 複数記述する時は、セミコロンで区切って列挙する。絞り込みが不要の場合は、空欄とする。
     * </pre>
     *
     * @return
     */
    public String getCategory() {
        return line.getString(2, null);
    }

    public String[] getCategorys() {
        String text = getCategory();
        if (text != null) {
            return text.split(":");
        } else {
            return null;
        }
    }

    /**
     * 地図設定のaddress
     *
     * @return
     */
    public String getAddress() {
        return line.getString(3, null);
    }

    /**
     * 地図設定のlatitude
     *
     * @return
     */
    public Double getLatitude() {
        return line.getDouble(4, null);
    }

    /**
     * 地図設定のlongtitude
     *
     * @return
     */
    public Double getLongtitude() {
        return line.getDouble(5, null);
    }

    /**
     * 地図設定のtel
     *
     * @return
     */
    public String getTel() {
        return line.getString(6, null);
    }

    /**
     * 地図設定のurl
     * <p/>
     * <pre>
     * 外部Webページを表示する場合
     *   http://~
     * アプリ内のカタログに遷移する場合
     *   catalog:[カタログID]:[ページ番号]
     * ポップアップ表示を行う場合
     *   空文字
     * </pre>
     *
     * @return
     */
    public String getUrl() {
        return line.getString(7, null);
    }

    /**
     * 地図設定のext1
     * <p/>
     * <pre>
     * コロン":"で区切り、左が見出し、右が内容
     * </pre>
     *
     * @return
     */
    public String getExt1() {
        return line.getString(8, null);
    }

    /**
     * 地図設定のext2
     * <p/>
     * <pre>
     * コロン":"で区切り、左が見出し、右が内容
     * </pre>
     *
     * @return
     */
    public String getExt2() {
        return line.getString(9, null);
    }

    /**
     * 地図設定のext3
     * <p/>
     * <pre>
     * コロン":"で区切り、左が見出し、右が内容
     * </pre>
     *
     * @return
     */
    public String getExt3() {
        return line.getString(10, null);
    }

}
