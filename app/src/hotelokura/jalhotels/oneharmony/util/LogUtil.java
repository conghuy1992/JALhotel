package hotelokura.jalhotels.oneharmony.util;

import android.util.Log;

public class LogUtil {

	public static boolean showLog = true;
	public static boolean showHttpHeaderLog = false;

	public static void d(String tag, String msg) {
		if (showLog)
			Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (showLog)
			Log.e(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (showLog)
			Log.i(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (showLog)
			Log.v(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (showLog)
			Log.w(tag, msg);
	}
}
