package hotelokura.jalhotels.oneharmony.setting;

import android.graphics.Color;

public class Setting {

	public Integer toColor(String text, Integer defaultValue) {
		if (text != null) {
			if (text.length() == 8) {
				StringBuffer sb = new StringBuffer();
				sb.append("#");
				sb.append(text.substring(text.length() - 2, text.length()));
				sb.append(text.substring(0, text.length() - 2));
				return Color.parseColor(sb.toString());
			} else if (text.length() == 6) {
				StringBuffer sb = new StringBuffer();
				sb.append("#ff");
				sb.append(text);
				return Color.parseColor(sb.toString());
			}
		}
		return defaultValue;
	}

	/**
	 * url文字列に含まれた特定の文字をエスケープ変換する。
	 * 
	 * <pre>
	 * 現在未使用
	 * </pre>
	 * 
	 * @see Setting#urlUnEscape(String, String)
	 * @param String
	 *            対象とする文字列
	 * @param String
	 *            デフォルト戻り値
	 * @return エスケープ済み文字列
	 */
	public String urlEscape(String text, String defaultValue) {
		if (text != null) {
			String sb = new String(text);
			sb = sb.replaceAll("\"", "&#34;");
			sb = sb.replaceAll(",", "&#44;");
			return sb;
		} else {
			return defaultValue;
		}
	}

	/**
	 * url文字列に含まれた特定のエスケープ文字を元に戻す。
	 * 
	 * <pre>
	 * ｜「&#34;」 を 「"」（ダブルクォート）に
	 * ｜「&#44;」 を 「,」（カンマ文字）に
	 * </pre>
	 * 
	 * @param String
	 *            対象とする文字列
	 * @param String
	 *            デフォルト戻り値
	 * @return アンエスケープ済み文字列
	 */
	public String urlUnEscape(String text, String defaultValue) {
		if (text != null) {
			String sb = new String(text);
			sb = sb.replaceAll("&#34;", "\"");
			sb = sb.replaceAll("&#44;", ",");
			return sb;
		} else {
			return defaultValue;
		}
	}

	/**
	 * 文字列中のアスタリスク（*）を、指定した文字列へ置き換える。
	 * 
	 * <pre>
	 * 置き換える文字列が無い場合も、アスタリスクは削除される。
	 * </pre>
	 * 
	 * @param String
	 *            対象とする文字列
	 * @param String
	 *            置換文字列
	 * @param String
	 *            デフォルト戻り値
	 * @return 変換済み文字列
	 */
	public String replaceAst(String text, String subStr, String defaultValue) {
		if (text != null) {
			String sb = new String(text);
			if (subStr != null) {
				sb = sb.replaceAll("\\*", subStr);
			} else {
				sb = sb.replaceAll("\\*", "");
			}
			return sb;
		} else {
			return defaultValue;
		}
	}

	/**
	 * 文字列中の改行文字を、有効な改行コードへ置き換える。
	 * 
	 * <pre>
	 * ｜「¥¥n」「<br>」を 「\n」（改行コード）に
	 * </pre>
	 * 
	 * @param String
	 *            対象とする文字列
	 * @param String
	 *            デフォルト戻り値
	 * @return 変換済み文字列
	 */
	public String replaceIndention(String text, String defaultValue) {
		if (text != null) {
			String sb = new String(text);
			sb = sb.replaceAll("\\\\n", "\n");
			sb = sb.replaceAll("<br>", "\n");
			return sb;
		} else {
			return defaultValue;
		}
	}

}
