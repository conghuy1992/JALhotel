package hotelokura.jalhotels.oneharmony.setting;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.graphics.Color;

import java.util.ArrayList;

/**
 * 設定 App.plist
 */
public class AppSetting extends Setting {

	protected PlistDict dict;

	public AppSetting() {
		super();
	}

	public AppSetting(PlistDict dict) {
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
	 * インフォメーション画面にて、バリカタのボタンを押した時のリンク先URL
	 *
	 * @return
	 */
	public String getBaricataLinkURL() {
		return dict.getString("BaricataLinkURL", null);
	}

	/**
	 * カタログ一覧ページの背景色１
	 *
	 * @return
	 */
	public Integer getCataloglistBGColor1() {
		return toColor(dict.getString("CataloglistBGColor1", null), Color.WHITE);
	}

	/**
	 * カタログ一覧ページの背景色２
	 *
	 * <pre>
	 * 背景色１から２までの色をグラデーションで表現する。背景色１が上部、２が下部の色。
	 * グラデーションを指定しない場合、背景色１と同じ色とすること。
	 * </pre>
	 *
	 * @return
	 */
	public Integer getCataloglistBGColor2() {
		return toColor(dict.getString("CataloglistBGColor2", null), null);
	}

	/**
	 * カタログ一覧ページのグラデーション角度。
	 *
	 * <pre>
	 * グラデーションにする場合、グラデーションにする角度を0.0～1.0の間で設定。
	 * 0.0の時は上下にまっすぐなグラデーションとなり、1.0の時は左上隅から右下隅への対角線状の斜めグラデーションとなる。
	 * </pre>
	 *
	 * @return
	 */
	public Float getCataloglistBGShift() {
		return dict.getFloat("CataloglistBGShift", null);
	}

	/**
	 * カタログ一覧ページの背景罫線色（横罫線）を指定。
	 *
	 * @return
	 */
	public Integer getCataloglistHLineColor() {
		return toColor(dict.getString("CataloglistHLineColor", null), null);
	}

	/**
	 * カタログ一覧ページの背景罫線色（縦罫線）を指定。
	 *
	 * @return
	 */
	public Integer getCataloglistVLineColor() {
		return toColor(dict.getString("CataloglistVLineColor", null), null);
	}

	/**
	 * カタログ一覧ページのカタログ画像枠線色を指定。
	 *
	 * @return
	 */
	public Integer getCataloglistOutlineColor() {
		return toColor(dict.getString("CataloglistOutlineColor", null), null);
	}

	/**
	 * カタログ一覧ページのカタログ画像枠線の表示方法を指定する。
	 *
	 * <pre>
	 * 0: 画像よりわずかに大きい矩形背景
	 * 1: 読み込み中は矩形背景、完了後は枠線のみ
	 * </pre>
	 *
	 * 初期値は0
	 *
	 * @return
	 */
	public Integer getCataloglistOutlineStyle() {
		return dict.getInteger("CataloglistOutlineStyle", 0);
	}

	/**
	 * カタログ一覧ページの各カタログの説明文文字色を指定。
	 *
	 * @return
	 */
	public Integer getCataloglistTextColor() {
		return toColor(dict.getString("CataloglistTextColor", null),
				Color.BLACK);
	}

	/**
	 * カタログ一覧ページのタイトルバーの文言を指定する。
	 *
	 * @return
	 */
	public String getCataloglistTitle() {
		return dict.getString("CataloglistTitle", "");
	}

	/**
	 * カタログ一覧ページのタイトルバーの背景色を指定する。
	 *
	 * @return
	 */
	public Integer getCataloglistTitleBGColor() {
		return toColor(dict.getString("CataloglistTitleBGColor", null),
				Color.BLACK);
	}

	/**
	 * カタログ一覧ページのタイトルバーのロゴマーク画像ファイルを指定する。
	 *
	 * <pre>
	 * 画像フォーマットは jpg 又は png とし、画像サイズの縦横比は任意。ピクセル数は任意だが縦64ピクセル程度が望ましい。
	 * 画像ファイルは、プロジェクトのリソースとして持つこと。
	 * </pre>
	 *
	 * <pre>
	 * アスタリスク「*」を含めた場合、その箇所にcoverflow.csvでのIDが展開される。カバーフロー画面から開いた場合にのみ有効。
	 * カタログセットごとに別のロゴを表示する時に設定すること。
	 * </pre>
	 *
	 * @return
	 */
	public String getCataloglistTitleIcon() {
		return dict.getString("CataloglistTitleIcon", null);
	}

	public String getCataloglistTitleIcon(String id) {
		return replaceAst(getCataloglistTitleIcon(), id, null);
	}

	/**
	 * カタログ一覧ページのタイトルバーの文言の文字色を指定する。
	 *
	 * @return
	 */
	public Integer getCataloglistTitleTextColor() {
		return toColor(dict.getString("CataloglistTitleTextColor", null),
				Color.WHITE);
	}

	/**
	 * カタログ一覧ページのテキストをセンタリングするかどうか。
	 *
	 * <pre>
	 * 省略時はNO
	 * </pre>
	 *
	 * @return
	 */
	public Boolean getCataloglistTextCentering() {
		return dict.getBoolean("CataloglistTextCentering", false);
	}

	/**
	 * カタログ一覧画面の背景画像ファイル名
	 *
	 * <pre>
	 * iPhone縦画面用
	 * 空の時は背景画像を使用しない。
	 * 画像形式はjpg又はpng
	 * </pre>
	 *
	 * <pre>
	 * アスタリスク「*」を含めた場合、その箇所にcoverflow.csvでのIDが展開される。カバーフロー画面から開いた場合にのみ有効。
	 * カタログセットごとに別の背景を表示する時に設定すること。
	 * </pre>
	 *
	 * @return
	 */
	public String getCataloglistBGImage() {
		return dict.getString("CataloglistBGImage", null);
	}

	/**
	 * カタログ一覧画面の背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCataloglistBGImageLogo() {
		PlistDict d = dict.getDict("CataloglistBGImageLogo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カタログ一覧画面の背景画像ファイル名
	 *
	 * <pre>
	 * iPhone横画面用
	 * 空の時はCataloglistBGImageを使用する。
	 * </pre>
	 *
	 * @return
	 */
	public String getCataloglistBGImageLandscape() {
		return dict.getString("CataloglistBGImageLandscape", null);
	}

	/**
	 * カタログ一覧画面の背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCataloglistBGImageLandscapeLogo() {
		PlistDict d = dict.getDict("CataloglistBGImageLandscapeLogo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カタログ一覧画面の背景画像ファイル名
	 *
	 * <pre>
	 * iPad縦画面用
	 * 空の時はCataloglistBGImageを使用する。
	 * </pre>
	 *
	 * @return
	 */
	public String getCataloglistBGImageIPad() {
		return dict.getString("CataloglistBGImageIPad", null);
	}

	/**
	 * カタログ一覧画面の背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCataloglistBGImageIPadLogo() {
		PlistDict d = dict.getDict("CataloglistBGImageIPadLogo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カタログ一覧画面の背景画像ファイル名
	 *
	 * <pre>
	 * iPad横画面用
	 * 空の時はCataloglistBGImageを使用する。
	 * </pre>
	 *
	 * @return
	 */
	public String getCataloglistBGImageLandscapeIPad() {
		return dict.getString("CataloglistBGImageLandscapeIPad", null);
	}

	/**
	 * カタログ一覧画面の背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCataloglistBGImageLandscapeIPadLogo() {
		PlistDict d = dict.getDict("CataloglistBGImageLandscapeIPadLogo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カタログ一覧画面の背景画像ファイル名
	 *
	 * <pre>
	 * Retina4インチ(iPhone5)縦画面用
	 * 空の時はCataloglistBGImageを使用する。
	 * </pre>
	 *
	 * @return
	 */
	public String getCataloglistBGImageRetina4() {
		return dict.getString("CataloglistBGImageRetina4", null);
	}

	/**
	 * カタログ一覧画面の背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCataloglistBGImageRetina4Logo() {
		PlistDict d = dict.getDict("CataloglistBGImageRetina4Logo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カタログ一覧画面の背景画像ファイル名
	 *
	 * <pre>
	 * Retina4インチ(iPhone5)横画面用
	 * 空の時はCataloglistBGImageを使用する。
	 * </pre>
	 *
	 * @return
	 */
	public String getCataloglistBGImageLandscapeRetina4() {
		return dict.getString("CataloglistBGImageLandscapeRetina4", null);
	}

	/**
	 * カタログ一覧画面の背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCataloglistBGImageLandscapeRetina4Logo() {
		PlistDict d = dict.getDict("CataloglistBGImageLandscapeRetina4Logo",
				null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カタログ一覧表示にカバーフローを使用するかどうか。
	 *
	 * <pre>
	 * 省略時はNO
	 * </pre>
	 *
	 * @return
	 */
	public Boolean getCataloglistUseCoverFlow() {
		return dict.getBoolean("CataloglistUseCoverFlow", false);
	}

	/**
	 * カバーフローのスタイル。
	 *
	 * <pre>
	 * 0: 横直線
	 * 1: 回転
	 * 2: 回転(内側)
	 * 3: 円筒
	 * 4: 円筒(内側)
	 * 5: 円盤
	 * 6: 円盤(逆)
	 * 7: カバーフロー
	 * 8: カバーフロー(タイプ２)
	 * 9: 前後
	 * 10: 前後(逆)
	 * </pre>
	 *
	 * <pre>
	 * 初期値は7
	 * </pre>
	 *
	 * @return
	 */
	public Integer getCataloglistCoverFlowStyle() {
		return dict.getInteger("CataloglistCoverFlowStyle", 7);
	}

	/**
	 * カバーフローの始点と終点がループするかどうか
	 *
	 * <pre>
	 * 初期値はNO
	 * </pre>
	 *
	 * @return
	 */
	public Boolean getCataloglistCoverFlowLoop() {
		return dict.getBoolean("CataloglistCoverFlowLoop", false);
	}

	/**
	 * アンケートのメール本文に付加するメッセージを指定。
	 *
	 * @return
	 */
	public String getEnqueteMailMessage() {
		return dict.getString("EnqueteMailMessage", null);
	}

	/**
	 * アンケートの送付先メールアドレス
	 *
	 * @return
	 */
	public String getEnqueteSendTo() {
		return dict.getString("EnqueteSendTo", null);
	}

	/**
	 * アンケートのメールタイトル
	 *
	 * @return
	 */
	public String getEnqueteSubject() {
		return dict.getString("EnqueteSubject", null);
	}

	/**
	 * アンケートを使用するかどうか。
	 *
	 * <pre>
	 * YES ： アンケートを使用する
	 * NO ： アンケートを使用しない
	 * </pre>
	 *
	 * @return
	 */
	public Boolean getEnqueteUse() {
		return dict.getBoolean("EnqueteUse", false);
	}

	/**
	 * キャッシュファイル削除ボタンをInfomation画面に表示するかどうか。
	 *
	 * <pre>
	 * YES：表示する
	 * NO：表示しない
	 * </pre>
	 *
	 * @return
	 */
	public Boolean getCacheClearButtonEnabled() {
		return dict.getBoolean("CacheClearButtonEnabled", false);
	}

	/**
	 * カタログリスト設定ファイルをネットに置く場合のフォルダへのURL
	 *
	 * <pre>
	 * ネット不使用時は省略するか、値を空とする。
	 * </pre>
	 *
	 * @return
	 */
	public String getNetCataloglistURL() {
		return dict.getString("NetCataloglistURL", null);
	}

	/**
	 * サムネイル画面で、1画面に表示するサムネイル数。
	 *
	 * <pre>
	 * この値を超えるとスクロールとページ切り替えの併用となる。
	 * </pre>
	 *
	 * <pre>
	 * 省略時のデフォルト値は100。
	 * 値は４と６の公倍数にすると縦横両画面での区切りが良い。
	 * ページ切り替えを使用しない場合は、総ページ数より大きい数(9999など)を指定。
	 * </pre>
	 *
	 * @return
	 */
	public Integer getThumbnailsParPage() {
		return dict.getInteger("ThumbnailsParPage", 100);
	}

	/**
	 * 著作権文字列を指定。
	 *
	 * <pre>
	 * Information画面で使用される。
	 * </pre>
	 *
	 * @return
	 */
	public String getCopyright() {
		return dict.getString("Copyright", null);
	}

	/**
	 * カバーフローメニューを使用するかどうか
	 *
	 * <pre>
	 * 省略時NO
	 * </pre>
	 *
	 * @return
	 */
	public Boolean getCoverFlowUse() {
		return dict.getBoolean("CoverFlowUse", false);
	}

	/**
	 * カバーフロー画面の表現方法を「カバーフロー」にするかどうか。
	 *
	 * <pre>
	 * 通常はYES。
	 * NOの場合は、カタログ一覧同様のスクロール画面となる。
	 * </pre>
	 *
	 * @return
	 */
	public Boolean getCoverFlowUseCoverFlow() {
		return dict.getBoolean("CoverFlowUseCoverFlow", true);
	}

	/**
	 * カバーフローの背景色。
	 *
	 * @return
	 */
	public Integer getCoverFlowBGColor() {
		return toColor(dict.getString("CoverFlowBGColor", null), Color.WHITE);
	}

	/**
	 * カバーフローの説明文文字色を指定。
	 *
	 * @return
	 */
	public Integer getCoverFlowTextColor() {
		return toColor(dict.getString("CoverFlowTextColor", null), Color.BLACK);
	}

	/**
	 * カバーフローの背景罫線色（横罫線）を指定。
	 *
	 * <pre>
	 * この設定は、CoverFlowUseCoverFlow = No の時のみ有効。
	 * Yesの時は罫線表示しない。
	 * </pre>
	 *
	 * @return
	 */
	public Integer getCoverFlowHLineColor() {
		return toColor(dict.getString("CoverFlowHLineColor", null), null);
	}

	/**
	 * カバーフローの背景罫線色（縦罫線）を指定。
	 *
	 * <pre>
	 * この設定は、CoverFlowUseCoverFlow = No の時のみ有効。
	 * Yesの時は罫線表示しない
	 * </pre>
	 *
	 * @return
	 */
	public Integer getCoverFlowVLineColor() {
		return toColor(dict.getString("CoverFlowVLineColor", null), null);
	}

	/**
	 * カバーフローの画像枠線色を指定
	 *
	 * @return
	 */
	public Integer getCoverFlowOutlineColor() {
		return toColor(dict.getString("CoverFlowOutlineColor", null), null);
	}

	/**
	 * カバーフローのカタログ画像枠線の表示方法を指定する。
	 *
	 * <pre>
	 * 0: 画像よりわずかに大きい矩形背景
	 * 1: 読み込み中は矩形背景、完了後は枠線のみ
	 * </pre>
	 *
	 * @return
	 */
	public Integer getCoverFlowOutlineStyle() {
		return dict.getInteger("CoverFlowOutlineStyle", 0);
	}

	/**
	 * カバーフローの背景画像ファイル名
	 *
	 * <pre>
	 * iPhone縦画面用
	 * 空の時は背景画像を使用しない。
	 * 画像形式はjpg又はpng
	 * </pre>
	 *
	 * @return
	 */
	public String getCoverFlowBGImage() {
		return dict.getString("CoverFlowBGImage", null);
	}

	/**
	 * カバーフローの背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCoverFlowBGImageLogo() {
		PlistDict d = dict.getDict("CoverFlowBGImageLogo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カバーフローの背景画像ファイル名
	 *
	 * <pre>
	 * iPhone横画面用
	 * 空の時はCoverFlowBGImageを使用する。
	 * </pre>
	 *
	 * @return
	 */
	public String getCoverFlowBGImageLandscape() {
		return dict.getString("CoverFlowBGImageLandscape", null);
	}

	/**
	 * カバーフローの背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCoverFlowBGImageLandscapeLogo() {
		PlistDict d = dict.getDict("CoverFlowBGImageLandscapeLogo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カバーフローの背景画像ファイル名
	 *
	 * <pre>
	 * iPad縦画面用
	 * 空の時はCoverFlowBGImageを使用する。
	 * </pre>
	 *
	 * @return
	 */
	public String getCoverFlowBGImageIPad() {
		return dict.getString("CoverFlowBGImageIPad", null);
	}

	/**
	 * カバーフローの背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCoverFlowBGImageIPadLogo() {
		PlistDict d = dict.getDict("CoverFlowBGImageIPadLogo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カバーフローの背景画像ファイル名
	 *
	 * <pre>
	 * iPad横画面用
	 * 空の時はCoverFlowBGImageを使用する。
	 * </pre>
	 *
	 * @return
	 */
	public String getCoverFlowBGImageLandscapeIPad() {
		return dict.getString("CoverFlowBGImageLandscapeIPad", null);
	}

	/**
	 * カバーフローの背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCoverFlowBGImageLandscapeIPadLogo() {
		PlistDict d = dict.getDict("CoverFlowBGImageLandscapeIPadLogo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カバーフローの背景画像ファイル名
	 *
	 * <pre>
	 * Retina4インチ(iPhone5)縦画面用
	 * 空の時はCoverFlowBGImageを使用する。
	 * </pre>
	 *
	 * @return
	 */
	public String getCoverFlowBGImageRetina4() {
		return dict.getString("CoverFlowBGImageRetina4", null);
	}

	/**
	 * カバーフローの背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCoverFlowBGImageRetina4Logo() {
		PlistDict d = dict.getDict("CoverFlowBGImageRetina4Logo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カバーフローの背景画像ファイル名
	 *
	 * <pre>
	 * Retina4インチ(iPhone5)横画面用
	 * 空の時はCoverFlowBGImageを使用する。
	 * </pre>
	 *
	 * @return
	 */
	public String getCoverFlowBGImageLandscapeRetina4() {
		return dict.getString("CoverFlowBGImageLandscapeRetina4", null);
	}

	/**
	 * カバーフローの背景画像Logoファイル設定
	 *
	 * @return
	 */
	public LogoImageSetting getCoverFlowBGImageLandscapeRetina4Logo() {
		PlistDict d = dict
				.getDict("CoverFlowBGImageLandscapeRetina4Logo", null);
		if (d != null) {
			return new LogoImageSetting(d);
		} else {
			return null;
		}
	}

	/**
	 * カバーフローからサブ画面を開く時のアニメーションスタイル。
	 *
	 * <pre>
	 *  -1: 横スライド
	 *   0: 上下移動
	 *   1: 横回転
	 *   2: 半透明
	 * </pre>
	 *
	 * カタログを直接開く時は横スライド固定。
	 *
	 * @return
	 */
	public Integer getCoverFlowTransitionStyle() {
		return dict.getInteger("CoverFlowTransitionStyle", -1);
	}

	/**
	 * カバーフローのスタイル。
	 *
	 * <pre>
	 * 0: 横直線
	 * 1: 回転
	 * 2: 回転(内側)
	 * 3: 円筒
	 * 4: 円筒(内側)
	 * 5: 円盤
	 * 6: 円盤(逆)
	 * 7: カバーフロー
	 * 8: カバーフロー(タイプ２)
	 * 9: 前後
	 * 10: 前後(逆)
	 * </pre>
	 *
	 * <pre>
	 * 初期値は7
	 * </pre>
	 *
	 * @return
	 */
	public Integer getCoverFlowStyle() {
		return dict.getInteger("CoverFlowStyle", 7);
	}

	/**
	 * カバーフローの始点と終点がループするかどうか。
	 *
	 * <pre>
	 * 初期値はNO
	 * </pre>
	 *
	 * @return
	 */
	public Boolean getCoverFlowLoop() {
		return dict.getBoolean("CoverFlowLoop", false);
	}

	/**
	 * カタログのページ切り換えスタイル
	 *
	 * <pre>
	 * 0: デフォルト設定(推奨値)
	 * 1: 画面横タップでメニューが閉じる
	 * 2: 画面横タップでメニューが閉じない
	 * </pre>
	 *
	 * <pre>
	 * 初期値は0
	 * 現在、0を設定すると2扱いとする。
	 * </pre>
	 *
	 * @return
	 */
	public Integer getCatalogPagingStyle() {
		return dict.getInteger("CatalogPagingStyle", 0);
	}

	/**
	 * アプリケーション本体の背景色。
	 *
	 * <pre>
	 * カバーフローをタップして画面がひっくり返る時、その後ろに見える背景色。
	 * </pre>
	 *
	 * @return
	 */
	public Integer getAppBGColor() {
		return toColor(dict.getString("AppBGColor", null), null);
	}

	/**
	 * 画面上部のステータスバーを非表示にする。
	 *
	 * <pre>
	 * YES: 非表示
	 * NO: 非表示にしない(推奨)
	 * </pre>
	 *
	 * 省略時はNO
	 *
	 * @return
	 */
	public Boolean getAppStatusBarHidden() {
		return dict.getBoolean("AppStatusBarHidden", false);
	}

	/**
	 * しおりの動作スタイル
	 *
	 * <pre>
	 * 0: ローカルしおり(カタログごとに別々)
	 * 1: 全体しおり(カタログを横断して動作)
	 * </pre>
	 *
	 * 初期値は0
	 *
	 * @return
	 */
	public Integer getBookmarkStyle() {
		return dict.getInteger("BookmarkStyle", 0);
	}

	/**
	 * しおりの削除スタイル
	 *
	 * <pre>
	 * 0: スワイプして削除ボタンを表示
	 * 1: 左端の(-)ボタンをタップして表示
	 * </pre>
	 *
	 * 初期値は0
	 *
	 * @return
	 */
	public Integer getBookmarkDeleteStyle() {
		return dict.getInteger("BookmarkDeleteStyle", 0);
	}

	/**
	 * 下記ファイルのダウンロードタイムアウト時間(秒)
	 *
	 * <pre>
	 * 〜_setting.csv
	 * 〜_pagelist.csv
	 * 〜_contents.csv
	 * </pre>
	 *
	 * 省略時は10秒
	 *
	 * @return
	 */
	public Integer getCatalogDownloadTimeoutSec() {
		return dict.getInteger("CatalogDownloadTimeoutSec", 10);
	}

	/**
	 * 地図にピンをドロップするときのアニメーションの可否
	 *
	 * <pre>
	 * YES:　アニメーションする
	 * NO: アニメーションしない
	 * </pre>
	 *
	 * 省略時はYES
	 *
	 * @return
	 */
	public Boolean getMapAnnotationAnimated() {
		return dict.getBoolean("MapAnnotationAnimated", true);
	}

	/**
	 * 地図機能のタイトルバーの背景色を指定する。
	 *
	 * @return
	 */
	public Integer getMapTitleBGColor() {
		return toColor(dict.getString("MapTitleBGColor", null), null);
	}

    /**
     * AdMobを使用するかどうか
     *
     * <pre>
     * 省略時はNO
     * </pre>
     *
     * @return
     */
    public Boolean getUseAdMob() {
        return dict.getBoolean("UseAdMob", false);
    }

    /**
     * AdMob広告の表示間隔
     *
     * @return
     */
    public Integer getAdInterval(String className) {
        String key = "AdInterval"+className.substring(0,className.length() - 8);
        LogUtil.d("APP SETTINGS", "getAdInterval key : " + key);
        return dict.getInteger(key, -1);
    }

    /**
     * トップメニューのレイアウト。
     *
     * <pre>
     * 0: 正方形
     * 1: 長方形
     * </pre>
     *
     * <pre>
     * 初期値は0
     * </pre>
     *
     * @return
     */
    public Integer getTopMenuLayout() {
        return dict.getInteger("TopMenuLayout", 0);
    }

    /**
     * セカンドメニューのレイアウト。
     *
     * <pre>
     * 0: 正方形
     * 1: 長方形
     * </pre>
     *
     * <pre>
     * 初期値は1
     * </pre>
     *
     * @return
     */
    public Integer getSecondMenuLayout() {
        return dict.getInteger("SecondMenuLayout", 1);
    }

    /**
     * WebTop表示を行うかどうか
     *
     * <pre>
     * false: 行わない
     * true: 行う
     * </pre>
     *
     * <pre>
     * 初期値はfalse
     * </pre>
     *
     * @return
     */
    public Boolean getUseWebTop() {
        return dict.getBoolean("UseWebTop", false);
    }

    /**
     * WebTopの接続先URL
     *
     * <pre>
     * UseWebTopがtrueの場合は必須
     * </pre>
     *
     * @return
     */
    public String getWebTopURL() {
        return dict.getString("WebTopURL", null);
    }

    public Integer getWebTopLayout() {
        return dict.getInteger("WebTopLayout", 0);
    }

    /**
     * ニュースページURL
     *
     * @return
     */
    public String getNewsURL() {
        return dict.getString("NewsURL", null);
    }

    /**
     * PushSDKで位置情報を取得するかどうか
     *
     * <pre>
     * false: 取得しない
     * true: 取得する
     * </pre>
     *
     * <pre>
     * 初期値はfalse
     * </pre>
     *
     * @return
     */
    public Boolean getUseGeoLocationPush() {
        return dict.getBoolean("UseGeoLocationPush", false);
    }

    /**
     * Pushの位置情報送信を行う間隔
     *
     * <pre>
     * 初期値は15分
     * </pre>
     *
     * @return
     */
    public Integer getGeoLocationInterval() {
        return dict.getInteger("GeoLocationInterval", 60 * 15);
    }

    /**
     * カタログ画面でツールバーボタンの下にテキストを表示するかどうか。
     *
     * <pre>
     * 省略時はYES
     * </pre>
     *
     * @return
     */
    public Boolean getCatalogToolbarButtonText() {
        return dict.getBoolean("CatalogToolbarButtonText", true);
    }

	public BGImageSetting getCataloglistBGImageDpi(String id) {
		MainApplication mainApp = MainApplication.getInstance();
		String str = null;
		LogoImageSetting logo = null;
		if (MainApplication.getInstance().isTabletDevice()) {
			str = getCataloglistBGImageIPad();
			if (str != null) {
				LogUtil.d("CataloglistBGImage", "IPad");
				logo = getCataloglistBGImageIPadLogo();
			}
		}
		if (str == null && mainApp.isMDpi()) {
			str = getCataloglistBGImageRetina4();
			if (str != null) {
				LogUtil.d("CataloglistBGImage", "Retina4");
				logo = getCataloglistBGImageRetina4Logo();
			}
		}
		if (str == null) {
			str = getCataloglistBGImage();
			if (str != null) {
				LogUtil.d("CataloglistBGImage", "Normal");
				logo = getCataloglistBGImageLogo();
			}
		}
		return new BGImageSetting(replaceAst(str, id, null), logo);
	}

	public BGImageSetting getCataloglistBGImageLandscapeDpi(String id) {
		MainApplication mainApp = MainApplication.getInstance();
		String str = null;
		LogoImageSetting logo = null;
		if (MainApplication.getInstance().isTabletDevice()) {
			str = getCataloglistBGImageLandscapeIPad();
			if (str != null) {
				LogUtil.d("CataloglistBGImageLandscape", "IPad");
				logo = getCataloglistBGImageLandscapeIPadLogo();
			}
		}
		if (str == null && mainApp.isMDpi()) {
			str = getCataloglistBGImageLandscapeRetina4();
			if (str != null) {
				LogUtil.d("CataloglistBGImageLandscape", "Retina4");
				logo = getCataloglistBGImageLandscapeRetina4Logo();
			}
		}
		if (str == null) {
			str = getCataloglistBGImageLandscape();
			if (str != null) {
				LogUtil.d("CataloglistBGImageLandscape", "Normal");
				logo = getCataloglistBGImageLandscapeLogo();
			}
		}
		if (str == null) {
			LogUtil.d("CataloglistBGImageLandscape", "Nothing");
			return getCataloglistBGImageDpi(id);
		}
		return new BGImageSetting(replaceAst(str, id, null), logo);
	}

	public BGImageSetting getCoverFlowBGImageDpi() {
		MainApplication mainApp = MainApplication.getInstance();
		String str = null;
		LogoImageSetting logo = null;
		if (MainApplication.getInstance().isTabletDevice()) {
			str = getCoverFlowBGImageIPad();
			if (str != null) {
				LogUtil.d("CoverFlowBGImage", "IPad");
				logo = getCoverFlowBGImageIPadLogo();
			}
		}
		if (str == null && mainApp.isMDpi()) {
			str = getCoverFlowBGImageRetina4();
			if (str != null) {
				LogUtil.d("CoverFlowBGImage", "Retina4");
				logo = getCoverFlowBGImageRetina4Logo();
			}
		}
		if (str == null) {
			str = getCoverFlowBGImage();
			if (str != null) {
				LogUtil.d("CoverFlowBGImage", "Normal");
				logo = getCoverFlowBGImageLogo();
			}
		}
		return new BGImageSetting(str, logo);
	}

	public BGImageSetting getCoverFlowBGImageLandscapeDpi() {
		MainApplication mainApp = MainApplication.getInstance();
		String str = null;
		LogoImageSetting logo = null;
		if (MainApplication.getInstance().isTabletDevice()) {
			str = getCoverFlowBGImageLandscapeIPad();
			if (str != null) {
				LogUtil.d("CoverFlowBGImageLandscape", "IPad");
				logo = getCoverFlowBGImageLandscapeIPadLogo();
			}
		}
		if (str == null && mainApp.isMDpi()) {
			str = getCoverFlowBGImageLandscapeRetina4();
			if (str != null) {
				LogUtil.d("CoverFlowBGImageLandscape", "Retina4");
				logo = getCoverFlowBGImageLandscapeRetina4Logo();
			}
		}
		if (str == null) {
			str = getCoverFlowBGImageLandscape();
			if (str != null) {
				LogUtil.d("CoverFlowBGImageLandscape", "Normal");
				logo = getCoverFlowBGImageLandscapeLogo();
			}
		}
		if (str == null) {
			LogUtil.d("CoverFlowBGImageLandscape", "Nothing");
			return getCoverFlowBGImageDpi();
		}
		return new BGImageSetting(str, logo);
	}

    public ArrayList<String> getTrackingIDs() {
        ArrayList<String> tList = new ArrayList<String>();

        String id1 = this.dict.getString("TrackingID1", "");
        String id2 = this.dict.getString("TrackingID2", "");
        String id3 = this.dict.getString("TrackingID3", "");
        String id4 = this.dict.getString("TrackingID4", "");
        if(!id1.isEmpty()) {
            tList.add(id1);
        }
        if(!id2.isEmpty()) {
            tList.add(id2);
        }
        if(!id3.isEmpty()) {
            tList.add(id3);
        }
        if(!id4.isEmpty()) {
            tList.add(id4);
        }
        return tList;
    }

	public int getLatestCompatVersion() {
		return this.dict.getInteger("CompatVersion", 0);
	}

	public boolean isPromptsUpdate() { return this.dict.getBoolean("PromptsUpdate", false); }

	public String getNewAppMessage(String key) {
		return this.dict.getString(key, "");
	}

	public String getNewAppPositive(String key) {
		return this.dict.getString(key, "");
	}

	public String getNewAppNegative(String key) {
		return this.dict.getString(key, "");
	}

	public String getGooglePlayURL() {
		return this.dict.getString("GooglePlayURL", "");
	}
}
