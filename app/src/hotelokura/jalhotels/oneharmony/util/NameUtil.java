package hotelokura.jalhotels.oneharmony.util;

public class NameUtil {

	/**
	 * ファイル名から拡張子を除く
	 * 
	 * @param filename
	 * @return
	 */
	public static String removeFileExtension(String filename) {
		int lastDotPos = filename.lastIndexOf('.');

		if (lastDotPos == -1) {
			return filename;
		} else if (lastDotPos == 0) {
			return filename;
		} else {
			return filename.substring(0, lastDotPos);
		}
	}
}
