package hotelokura.jalhotels.oneharmony.setting;

/**
 * 設定 [ID]_pagelist.csv
 * 
 * <pre></pre>
 * 
 */
public class PageListSetting extends Setting {

	/**
	 * 省略項目を補完する
	 * 
	 * <pre>
	 * 		省略時、上の行を引き継ぐ項目を解決する
	 * </pre>
	 * 
	 * @param csvArray
	 * @return
	 */
	public static CsvArray complementOmit(CsvArray csvArray) {
		PageListSetting pageOld = new PageListSetting(csvArray.get(0));
		PageListSetting page = new PageListSetting();
		for (int i = 1; i < csvArray.size(); i++) {
			page.setLine(csvArray.get(i));

			if (page.getFileID() == null) {
				page.setFileID(pageOld.getFileID());
			}
			if (page.getFileType() == null) {
				page.setFileType(pageOld.getFileType());
			}
			if (page.getFileSource() == null) {
				page.setFileSource(pageOld.getFileSource());
			}

			pageOld.setLine(page.getLine());
		}
		return csvArray;
	}

	protected CsvLine line;

	public PageListSetting() {
		super();
	}

	public PageListSetting(CsvLine line) {
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
	 * データファイルのID
	 * 
	 * <pre>
	 * PDFファイルの場合、拡張子を除いたファイル名を指定。
	 * 画像ファイルの場合、倍率やページ番号などを除いたベース名を指定。
	 * 
	 * 2行目以降省略可能。空欄にした場合、上の行と同じと見なす。
	 * </pre>
	 * 
	 * @return
	 */
	public String getFileID() {
		return line.getString(0, null);
	}

	private void setFileID(String value) {
		line.set(0, value);
	}

	/**
	 * データファイルの種類
	 * 
	 * <pre>
	 * 基本的に拡張子を記述する。
	 * 拡張子のドットは不要。
	 * 
	 * 2行目以降省略可能。空欄にした場合、上の行と同じと見なす。
	 * </pre>
	 * 
	 * @return
	 */
	public String getFileType() {
		return line.getString(1, null);
	}

	private void setFileType(String value) {
		line.set(1, value);
	}

	/**
	 * データファイル中のページ番号
	 * 
	 * <pre>
	 * PDFファイルの場合、そのPDF中のページ番号を指定する。
	 * 
	 * 画面にカタログを表示する時のページ番号とは関係ないので注意。
	 * (画面上のページ番号は、CSVファイルのレコードを上から順に並べたものを使用する。)
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getPageOfFile() {
		return line.getInteger(2, null);
	}

	/**
	 * ファイルの読み込み先を指定
	 * 
	 * <pre>
	 * res :リソースファイルから読み込む
	 * http://example.... :URLから読み込む
	 * 
	 * 2行目以降省略可能。空欄にした場合、上の行と同じと見なす。
	 * 1行目を省略した場合は、リソース内蔵(res)とみなす。
	 * </pre>
	 * 
	 * @return
	 */
	public String getFileSource() {
		String text = line.getString(3, null);
		if (text != null) {
			if (text.equalsIgnoreCase("res")) {
				return null;
			}
            if (!text.endsWith("/")) {
                text = text + "/";
            }
		}
		return text;
	}

	private void setFileSource(String value) {
		line.set(3, value);
	}

	/**
	 * サムネイル画像ファイル取り扱いフラグ
	 * 
	 * <pre>
	 * 0 :画像ファイルはキャッシュとして作成する
	 * 1 :画像ファイルはリソースに存在する
	 * 2 :画像ファイルはサーバに存在する
	 * 空欄 :自動判定
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getLv0() {
		return line.getInteger(4, null);
	}

	/**
	 * 等倍画像ファイル取り扱いフラグ
	 * 
	 * <pre>
	 * 0 :画像ファイルはキャッシュとして作成する
	 * 1 :画像ファイルはリソースに存在する
	 * 2 :画像ファイルはサーバに存在する
	 * 空欄 :自動判定
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getLv1() {
		return line.getInteger(5, null);
	}

	/**
	 * ２倍画像ファイル取り扱いフラグ
	 * 
	 * <pre>
	 * 0 :画像ファイルはキャッシュとして作成する
	 * 1 :画像ファイルはリソースに存在する
	 * 2 :画像ファイルはサーバに存在する
	 * 空欄 :自動判定
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getLv2() {
		return line.getInteger(6, null);
	}

	/**
	 * ４倍画像ファイル取り扱いフラグ
	 * 
	 * <pre>
	 * 0 :画像ファイルはキャッシュとして作成する
	 * 1 :画像ファイルはリソースに存在する
	 * 2 :画像ファイルはサーバに存在する
	 * 空欄 :自動判定
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getLv4() {
		return line.getInteger(7, null);
	}

	/**
	 * ページのレイアウトを指定する。
	 * 
	 * <pre>
	 * 横画面での見開きレイアウトの挙動を設定。
	 * 0 :単独で表示するページとして指定
	 * 1 :左側ページ(先行ページ)として指定
	 * 2 :右側ページとして指定
	 * 空欄 :自動判定
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getLayout() {
		return line.getInteger(8, null);
	}

	/**
	 * ページのタイトルを指定。
	 * 
	 * @return
	 */
	public String getTitle() {
		return line.getString(9, null);
	}

	/**
	 * ページごとの外部リンクURLを指定
	 * 
	 * @return
	 */
	public String getLinkURL() {
		return line.getString(10, null);
	}

	/**
	 * ページごとのテキスト
	 * 
	 * <pre>
	 * 日本語を使用する場合、UTF-8で記述。
	 * HTMLタグにて文字の修飾が可能。
	 * 改行不可・ダブルクオートとカンマ文字の使用不可。下記代替文字でエスケープする。
	 *  改行 = <br>
	 *  ダブルクオート= &#34;
	 *  カンマ文字 = &#44;
	 * </pre>
	 * 
	 * @return
	 */
	public String getText() {
		return line.getString(11, null);
	}

	/**
	 * 等倍表示時のページ画像の横幅(px)
	 * 
	 * <pre>
	 * FileSourceがPDFの時は不要。
	 * Lv1の値が1の時は省略可能。
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getPageWidth() {
		return line.getInteger(12, null);
	}

	/**
	 * 等倍表示時のページ画像の高さ(px)
	 * 
	 * <pre>
	 * FileSourceがPDFの時は不要。
	 * Lv1の値が1の時は省略可能。
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getPageHeight() {
		return line.getInteger(13, null);
	}

	/**
	 * 2倍・4倍画像のタイル画像サイズ横幅
	 * 
	 * <pre>
	 * FileSourceがPDFの時は不要。
	 * Lv2=0かつ、Lv4=0の時は不要。
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getTileWidth() {
		return line.getInteger(14, null);
	}

	/**
	 * 2倍・4倍画像のタイル画像サイズ高さ
	 * 
	 * <pre>
	 * FileSourceがPDFの時は不要。
	 * Lv2=0かつ、Lv4=0の時は不要。
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getTileHeight() {
		return line.getInteger(15, null);
	}
}
