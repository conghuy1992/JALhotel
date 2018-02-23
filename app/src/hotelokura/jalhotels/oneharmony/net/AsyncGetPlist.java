package hotelokura.jalhotels.oneharmony.net;

import java.io.IOException;
import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.cache.DiskCache;
import hotelokura.jalhotels.oneharmony.setting.PlistDict;
import hotelokura.jalhotels.oneharmony.setting.PlistReader;

public class AsyncGetPlist extends AsyncGet<PlistDict> {
	static final String TAG = "AsyncGetPlist";

	public AsyncGetPlist(String baseUrl, String filename,
			AsyncCallback<PlistDict> callback) {
		super(baseUrl, filename, callback);
	}

	@Override
	protected PlistDict createContent(InputStream is) {
		PlistDict plistDict = PlistReader.read(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return plistDict;
	}

	@Override
	protected InputStream findByCache(String baseUrl, String filename) {
		if (!MainApplication.getInstance().isNetConnected()) {
			// OFFラインモード(ネットワーク接続が無い)
			DiskCache disk = MainApplication.getInstance().getDickCache();
			InputStream is = disk.get(baseUrl, filename);
			return is;
		} else {
			// ONラインモード
			// キャッシュを使わない
			return null;
		}
	}

	@Override
	protected void writeToCache(String baseUrl, String filename, InputStream is) {
		DiskCache disk = MainApplication.getInstance().getDickCache();
		disk.put(baseUrl, filename, is);
	}
}
