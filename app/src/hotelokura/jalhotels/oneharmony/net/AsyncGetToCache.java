package hotelokura.jalhotels.oneharmony.net;

import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.cache.DiskCache;

public class AsyncGetToCache extends AsyncGet<InputStream> {
	static final String TAG = "AsyncGetToCache";

	public AsyncGetToCache(String baseUrl, String filename,
			AsyncCallback<InputStream> callback) {
		super(baseUrl, filename, callback);
	}

	@Override
	protected InputStream createContent(InputStream is) {
		try {
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
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
