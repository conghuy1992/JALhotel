package hotelokura.jalhotels.oneharmony.activity.catalog;

import android.text.TextUtils;

public class CatalogFileNameUtil {

	private CatalogFileNameUtil() {
	}

	public static String makeImageName(int level, int horizontal, int vertical,
			int filePage, String fileID, String fileType) {
		// 画像のファイル名を作成
		String filename = null;
        String extension;
        if (TextUtils.isEmpty(fileType)) {
            extension = "";
        } else {
            extension = "." + fileType;
        }
		switch (level) {
		case 1:
			filename = String.format("%s[%d_%04d]%s", fileID, level, filePage,
                    extension);
			break;
		case 2:
		case 4:
			filename = String.format("%s[%d_%04d_%02dx%02d]%s", fileID, level,
					filePage, vertical, horizontal, extension);
			break;
		}
		return filename;
	}
}
