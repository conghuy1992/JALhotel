package hotelokura.jalhotels.oneharmony.net;

import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.cache.DiskCache;
import hotelokura.jalhotels.oneharmony.util.BitmapUtil;

import android.graphics.Bitmap;

public class AsyncGetImage extends AsyncGet<Bitmap> {
	static final String TAG = "AsyncGetImage";

	public AsyncGetImage(String baseUrl, String filename,
			AsyncCallback<Bitmap> callback) {
		super(baseUrl, filename, callback);
	}

	@Override
	protected Bitmap createContent(InputStream is) {
		Bitmap bmp = null;
		try {
			bmp = BitmapUtil.getInstance().decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}

	@Override
	protected InputStream findByCache(String baseUrl, String filename) {
		DiskCache disk = MainApplication.getInstance().getDickCache();
		InputStream is = disk.get(baseUrl, filename);
		return is;
	}

	@Override
	protected void writeToCache(String baseUrl, String filename, InputStream is) {
		DiskCache disk = MainApplication.getInstance().getDickCache();
		disk.put(baseUrl, filename, is);
	}
}
