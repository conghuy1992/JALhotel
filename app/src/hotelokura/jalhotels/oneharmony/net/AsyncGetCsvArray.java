package hotelokura.jalhotels.oneharmony.net;

import java.io.IOException;
import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.cache.DiskCache;
import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.setting.CsvReader;

public class AsyncGetCsvArray extends AsyncGet<CsvArray> {
	static final String TAG = "AsyncGetCsvArray";

	public AsyncGetCsvArray(String baseUrl, String filename,
			AsyncCallback<CsvArray> callback) {
		super(baseUrl, filename, callback);
	}

	@Override
	protected CsvArray createContent(InputStream is) {
		CsvArray csvArray = CsvReader.read(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return csvArray;
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
