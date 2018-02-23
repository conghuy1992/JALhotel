package hotelokura.jalhotels.oneharmony.setting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Color;

/**
 * 設定 [ID]_settings.plist
 */
public class CatalogSetting extends Setting {

	protected PlistDict dict;

	public CatalogSetting() {
		super();
	}

	public CatalogSetting(PlistDict dict) {
		super();
		this.dict = dict;
	}

	public PlistDict getDict() {
		return dict;
	}

	public void setDict(PlistDict dict) {
		this.dict = dict;
	}

	/**
	 * カートボタンを押した時の外部リンクURLを指定する。
	 * 
	 * @return
	 */
	public String getCartLinkURL() {
		return dict.getString("CartLinkURL", null);
	}

	/**
	 * データファイルフォーマットのバージョン指定。
	 * 
	 * <pre>
	 * 現時点では 1 固定
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getDataVersion() {
		return dict.getInteger("DataVersion", 1);
	}

	/**
	 * ビルボタンを押した時の外部リンクURLを指定する。
	 * 
	 * @return
	 */
	public String getExternalLinkURL() {
		return dict.getString("ExternalLinkURL", null);
	}

	/**
	 * インフォ画面から開く外部リンクURLを指定する。
	 * 
	 * @return
	 */
	public String getInfoLinkURL() {
		return dict.getString("InfoLinkURL", null);
	}

	/**
	 * インフォ画面での補助文言その１
	 * 
	 * @return
	 */
	public String getInfoMessageSub1() {
		return dict.getString("InfoMessageSub1", null);
	}

	/**
	 * インフォ画面での補助文言その２
	 * 
	 * @return
	 */
	public String getInfoMessageSub2() {
		return dict.getString("InfoMessageSub2", null);
	}

	/**
	 * インフォ画面でのメッセージタイトル
	 * 
	 * @return
	 */
	public String getInfoMessageTitle() {
		return dict.getString("InfoMessageTitle", null);
	}

	/**
	 * カタログページ設定ファイルのファイル名を指定する。
	 * 
	 * @return
	 */
	public String getPagelistFile() {
		return dict.getString("PagelistFile", null);
	}

	/**
	 * 簡易PDFモードを使用するかどうか
	 * 
	 * <pre>
	 * YES ： 使用する
	 * NO ： 使用しない
	 * </pre>
	 * 
	 * @return
	 */
	public Boolean getSimpleStyle() {
		return dict.getBoolean("SimpleStyle", false);
	}

	/**
	 * 簡易PDFモードを使用する場合、表示するPDFファイルを指定する。
	 * 
	 * <pre>
	 * PDFファイルはリソースとして内蔵させること。
	 * </pre>
	 * 
	 * @return
	 */
	public String getSimpleStylePDF() {
		return dict.getString("SimpleStylePDF", null);
	}

	/**
	 * SNS連携機能を使って紹介する、紹介先URLを指定。
	 * 
	 * @return
	 */
	public String getSNSLinkURL() {
		return dict.getString("SNSLinkURL", null);
	}

	/**
	 * SNS連携機能を使って紹介する時の、デフォルトで設定しておく文言。
	 * 
	 * @return
	 */
	public String getSNSMessage() {
		return dict.getString("SNSMessage", null);
	}

	/**
	 * SNS連携機能を使ってメールを送る時の、メールタイトル。
	 * 
	 * @return
	 */
	public String getSNSSubject() {
		return dict.getString("SNSSubject", null);
	}

	/**
	 * SNS連携機能でFacebookを使用するかどうか。
	 * 
	 * <pre>
	 * YES：使用しない
	 * NO：使用する（省略時のデフォルト値）
	 * </pre>
	 * 
	 * @return
	 */
	public Boolean getSNSFacebookDisabled() {
		return dict.getBoolean("SNSFacebookDisabled", false);
	}

	/**
	 * カタログのタイトル
	 * 
	 * @return
	 */
	public String getTitle() {
		return dict.getString("Title", null);
	}

	/**
	 * カタログデータの更新日付を設定する。
	 * 
	 * <pre>
	 * データを更新した場合、この値を新しい日付に必ず更新すること。
	 * </pre>
	 * 
	 * @return
	 */
	public Date getUpdateDate() {
		String d = getUpdateDateString();
		Date date = null;
		if (d != null) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
					Locale.JAPAN);
			try {
				date = df.parse(d);
			} catch (ParseException e) {
				date = null;
			}
		}
		return date;
	}

	public String getUpdateDateString() {
		return dict.getString("UpdateDate", null);
	}

	/**
	 * カートボタンを表示するかどうか。
	 * 
	 * @return
	 */
	public Boolean getUseCartLink() {
		return dict.getBoolean("UseCartLink", false);
	}

	/**
	 * 目次ボタンを表示するかどうか。
	 * 
	 * @return
	 */
	public Boolean getUseContents() {
		return dict.getBoolean("UseContents", false);
	}

	/**
	 * 外部リンク(ビル)ボタンを表示するかどうか
	 * 
	 * @return
	 */
	public Boolean getUseExternalLink() {
		return dict.getBoolean("UseExternalLink", false);
	}

	/**
	 * 地図ボタンを表示するかどうか。
	 * 
	 * @return
	 */
	public Boolean getUseMapLink() {
		return dict.getBoolean("UseMapLink", false);
	}

	/**
	 * ページリンク(各ページごとのリンク)を表示するかどうか。
	 * 
	 * <pre>
	 * NOにした場合、各ページ上での黄色い点滅も発生しなくなります。
	 * </pre>
	 * 
	 * @return
	 */
	public Boolean getUsePageLink() {
		return dict.getBoolean("UsePageLink", true);
	}

	/**
	 * SNSボタンを表示するかどうか。
	 * 
	 * @return
	 */
	public Boolean getUseSNS() {
		return dict.getBoolean("UseSNS", false);
	}

	/**
	 * サムネイルボタンを表示するかどうか。
	 * 
	 * @return
	 */
	public Boolean getUseThumbnail() {
		return dict.getBoolean("UseThumbnail", false);
	}

	/**
	 * ページの背景色
	 * 
	 * <pre>
	 * 省略時のデフォルトは黒
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getBGColor() {
		return toColor(dict.getString("BGColor", null), Color.BLACK);
	}

	/**
	 * 縦書き(右開き)指定
	 * 
	 * @return
	 */
	public Boolean getVerticalWriting() {
		return dict.getBoolean("VerticalWriting", false);
	}

	/**
	 * ページリンクの点滅回数
	 * 
	 * <pre>
	 * 0にすると点滅しない
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getPageLinkBlinkTimes() {
		return dict.getInteger("PageLinkBlinkTimes", 3);
	}

	/**
	 * カタログデータのネット取得チェックを禁止する。
	 * 
	 * @return
	 */
	public Boolean getNetCheckDisabled() {
		return dict.getBoolean("NetCheckDisabled", false);
	}

	/**
	 * ツールバーボタンの文字を太字にする
	 * 
	 * @return
	 */
	public Boolean getToolbarIconTextIsBold() {
		return dict.getBoolean("ToolbarIconTextIsBold", false);
	}

	/**
	 * カタログを開いた時にツールバーを初期表示する
	 * 
	 * <pre>
	 * ツールバーは一定時間経過後自動で隠れます
	 * </pre>
	 * 
	 * @return
	 */
	public Boolean getShowToolbarAtStart() {
		return dict.getBoolean("ShowToolbarAtStart", false);
	}

	/**
	 * テキスト情報を表示するかどうか
	 * 
	 * @return
	 */
	public Boolean getTextInfoUse() {
		return dict.getBoolean("TextInfoUse", false);
	}

	/**
	 * テキスト情報の背景色
	 * 
	 * @return
	 */
	public Integer getTextInfoBGColor() {
		return toColor(dict.getString("TextInfoBGColor", null),
				Color.parseColor("#80000000"));
	}

	/**
	 * テキスト情報の基本文字色
	 * 
	 * <pre>
	 * 半透明を指定した場合、本文には効果が無い。ページ数表示ラベルには影響する。
	 * 尚、本文はHTMLタグでの色変更も可能。
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getTextInfoTextColor() {
		return toColor(dict.getString("TextInfoTextColor", null),
				Color.parseColor("#FFFFFFFF"));
	}

	/**
	 * テキスト情報ページ切り替えスタイル。
	 * 
	 * <pre>
	 * 0: デフォルト設定(推奨値)
	 * 1: テキストが無い時は非表示
	 * 2: テキストが無い時も常に表示
	 * ※折りたたんでいる時は、たたまれたままです
	 * </pre>
	 * 
	 * @return
	 */
	public Integer getTextInfoPagingStyle() {
		return dict.getInteger("TextInfoPagingStyle", 0);
	}

	/**
	 * 地図ボタンでカタログから地図の一覧画面に遷移した場合のタイトル
	 * 
	 * @return
	 */
	public String getMapListTitle() {
		return dict.getString("MapListTitle", null);
	}

    /**
     * お気に入りボタンを表示するかどうか
     *
     * @return
     */
    public Boolean getUseBookmark() {
        return dict.getBoolean("UseBookmark", true);
    }

    /**
     * お気に入りボタン画像
     *
     * @return
     */
    public Integer getBookmarkImage() {
        return dict.getInteger("BookmarkImage", -1);
    }

    /**
     * お気に入りボタン画像
     *
     * @return
     */
    public Integer getExternalLinkImage() {
        return dict.getInteger("ExternalLinkImage", -1);
    }

    /**
     * お気に入りボタン画像
     *
     * @return
     */
    public Integer getPageLinkImage() {
        return dict.getInteger("PageLinkImage", -1);
    }

    /**
     * お気に入りボタン画像
     *
     * @return
     */
    public Integer getMapImage() {
        return dict.getInteger("MapImage", -1);
    }

	//==============================================================================================
	/**
	 * Returns boolean values show or not show menu popup end page
	 * @return
	 */
	public Boolean getUseEndPageAction(){
		return dict.getBoolean("UseEndPageAction", false);
	}

	/**
	 * Returns text on Yes button
	 * @return
	 */
	public String getEndPageActionText(){
		return dict.getString("EndPageActionText", null);
	}

	/**
	 * Returns link to a place when click Yes button.
	 * @return
	 */
	public String getEndPageActionURL(){
		return dict.getString("EndPageActionURL", null);
	}

	/**
	 * Returns do or donot Script
	 * @return
	 */
	public Boolean getUseEndPageScript(){
		return dict.getBoolean("UseEndPageScript", false);
	}

	/**
	 * Return URL Script to start it.
	 * @return
	 */
	public String getUseEndPageScriptURL(){
		return dict.getString("UseEndPageScriptURL", null);
	}
}
