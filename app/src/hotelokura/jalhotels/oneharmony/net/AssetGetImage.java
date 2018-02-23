package hotelokura.jalhotels.oneharmony.net;

import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.util.BitmapUtil;

import android.graphics.Bitmap;

public class AssetGetImage extends AssetGet<Bitmap> {
	static final String TAG = "AssetGetImage";

	public AssetGetImage(String filename, AsyncCallback<Bitmap> callback) {
		super(filename, callback);
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
}
