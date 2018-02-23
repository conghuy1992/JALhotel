package hotelokura.jalhotels.oneharmony.net;

import java.io.IOException;
import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.setting.CsvReader;

public class AssetGetCsvArray extends AssetGet<CsvArray> {
	static final String TAG = "AssetGetCsvArray";

	public AssetGetCsvArray(String filename, AsyncCallback<CsvArray> callback) {
		super(filename, callback);
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
}
