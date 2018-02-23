package hotelokura.jalhotels.oneharmony.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;

import hotelokura.jalhotels.oneharmony.util.LogUtil;

import org.apache.http.HttpEntity;

import android.os.Handler;

public abstract class AsyncGet<T> implements GetTaskIF {
	static final String TAG = "AsyncGet";

	private String baseUrl = null;
	private String filename = null;
	private AsyncCallback<T> callback = null;

	public AsyncGet(String baseUrl, String filename, AsyncCallback<T> callback) {
		this.baseUrl = baseUrl;
		this.filename = filename;
		this.callback = callback;
	}

	public void execute() {
		LogUtil.d(TAG, "execute: " + baseUrl + filename);

		final Handler handler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				final AsyncResult<T> result;

				if (!MainApplication.getInstance().isNetConnected()) {
					// OFFラインモード(ネットワーク接続が無い)
					// まずキャッシュを探す
					InputStream is = AsyncGet.this.findByCache(baseUrl,
							filename);
					if (is != null) {
						T content = AsyncGet.this.createContent(is);
						result = AsyncResult.createNormalResult(content, null);
					} else {
						// キャッシュも無ければエラー
						result = AsyncResult.createErrorResult(
								AsyncResult.ResultStatus.NOT_CONNECTED_ERROR,
								R.string.err_not_connected);
					}
				} else {
					// ONラインモード
					// キャッシュを探す
					InputStream is = AsyncGet.this.findByCache(baseUrl,
							filename);
					if (is != null) {
						T content = AsyncGet.this.createContent(is);
						result = AsyncResult.createNormalResult(content, null);
					} else {
						// キャッシュが無ければ通信開始
						result = connect(baseUrl, filename);
					}
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						if (result.isError()) {
							LogUtil.d(TAG, "onFailed: " + baseUrl + filename
									+ ": " + result.getStatus().name());
							callback.onFailed(result);
						} else {
							LogUtil.d(TAG, "onSuccess: " + baseUrl + filename);
							callback.onSuccess(result);
						}
					}
				});
			}
		}).start();
	}

	private AsyncResult<T> connect(final String baseUrl, final String filename) {
		Downloader<T> loader = new Downloader<T>() {
			@Override
			protected T createContent(HttpEntity entity) {
				T content = null;
				try {
					InputStream is = entity.getContent();

					// キャッシュ書き込み用にInputStreamをコピーする
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer, 0, buffer.length)) > 0) {
						baos.write(buffer, 0, len);
					}
					// 書き込み用のInputStream
					InputStream is1 = new ByteArrayInputStream(
							baos.toByteArray());
					// コンテンツ作成用のInputStream
					InputStream is2 = new ByteArrayInputStream(
							baos.toByteArray());

					// キャッシュへ書き込み
					writeToCache(baseUrl, filename, is1);
					// コンテンツの作成
					content = AsyncGet.this.createContent(is2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return content;
			}
		};
		return loader.connect(baseUrl + filename);
	}

	abstract protected T createContent(InputStream is);

	protected InputStream findByCache(String baseUrl, String filename) {
		return null;
	}

	protected void writeToCache(String baseUrl, String filename, InputStream is) {
	}
}
