package hotelokura.jalhotels.oneharmony.util;

import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.MainApplication;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

public class BitmapUtil {

	public static BitmapUtil instance;

	public static BitmapUtil getInstance() {
		if (instance == null) {
			instance = new BitmapUtil();
		}
		return instance;
	}

	synchronized public Bitmap decodeStream(InputStream is) {

		// ビットマップ作成オブジェクトの設定
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.RGB_565;
		options.inPurgeable = true;
		DisplayMetrics dm = MainApplication.getInstance().getResources()
				.getDisplayMetrics();
		options.inDensity = dm.densityDpi;

		return BitmapFactory.decodeStream(is, null, options);
	}
}
