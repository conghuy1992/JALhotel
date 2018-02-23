package hotelokura.jalhotels.oneharmony.net;

import java.io.IOException;
import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.setting.PlistDict;
import hotelokura.jalhotels.oneharmony.setting.PlistReader;

public class AssetGetPlist extends AssetGet<PlistDict> {
	static final String TAG = "AssetGetPlist";

	public AssetGetPlist(String filename, AsyncCallback<PlistDict> callback) {
		super(filename, callback);
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
}
