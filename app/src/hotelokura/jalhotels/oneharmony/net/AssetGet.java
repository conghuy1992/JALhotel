package hotelokura.jalhotels.oneharmony.net;

import java.io.IOException;
import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;

import hotelokura.jalhotels.oneharmony.util.LogUtil;

import android.content.res.AssetManager;
import android.os.Handler;

public abstract class AssetGet<T> implements GetTaskIF {
	static final String TAG = "AssetGet";

	private String filename = null;
	private AsyncCallback<T> callback = null;

	public AssetGet(String filename, AsyncCallback<T> callback) {
		this.filename = filename;
		this.callback = callback;
	}

	public void execute() {
		LogUtil.d(TAG, "execute: " + filename);

		final Handler handler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				final AsyncResult<T> result;

				AssetManager asset = MainApplication.getAssetManager();
				InputStream is = null;
				try {
					is = asset.open(filename);
				} catch (IOException e) {
					// e.printStackTrace();
				}
				if (is != null) {
					T content = AssetGet.this.createContent(is);
					result = AsyncResult.createNormalResult(content, null);
				} else {
					result = AsyncResult.createErrorResult(
							AsyncResult.ResultStatus.RESULT_STATUS_NOT_FOUND_ERROR,
							R.string.err_not_found);
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						if (result.isError()) {
							LogUtil.d(TAG, "onFailed: " + filename + ": "
									+ result.getStatus().name());
							callback.onFailed(result);
						} else {
							LogUtil.d(TAG, "onSuccess: " + filename);
							callback.onSuccess(result);
						}
					}
				});
			}
		}).start();
	}

	abstract protected T createContent(InputStream is);
}
