package hotelokura.jalhotels.oneharmony.setting;

/**
 * 設定 [ID]_contents.csv
 */
public class ContentsSetting extends Setting {

	protected CsvLine line;

	public ContentsSetting() {
		super();
	}

	public ContentsSetting(CsvLine line) {
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
	 * メニューの階層レベル
	 * 
	 * <pre>
	 * 1 :第１階層
	 * 2 :第２階層
	 * 3 :第３階層
	 * 4 :第４階層....(以降同じ)
	 * 
	 *  1の場合、目次の最初のページに表示される。
	 *  2の場合、1の右(選択後次ページ)に展開されて表示される。
	 * 　3の場合、更に次の階層。
	 * 以降、階層の数は無制限。
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getLevel() {
		return line.getInteger(0, null);
	}

	/**
	 * 本文ページ番号を指定
	 * 
	 * <pre>
	 * 下位階層呼び出し用の見出し項目の場合、省略して良い。
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getPage() {
		return line.getInteger(1, null);
	}

	/**
	 * 目次画面に表示するタイトル文言
	 * 
	 * @return
	 */
	public String getTitle() {
		return line.getString(2, null);
	}
}
