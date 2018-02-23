package hotelokura.jalhotels.oneharmony.net;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;

import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.Uri;

public abstract class Downloader<T> {
	static final String TAG = "Downloader";

	public AsyncResult<T> connect(String url) {
		HttpClient httpClient = null;
		try {
			LogUtil.d(TAG, "connect: " + Uri.encode(url, ":/"));
			// Schemeの設定
			SchemeRegistry schreg = new SchemeRegistry();
			schreg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
//            schreg.register(new Scheme("https", MySSLSocketFactory
//                    .getFixedSocketFactory(), 443));
			SSLSocketFactory sslSocketFactory;
//			sslSocketFactory = MySSLSocketFactory.getFixedSocketFactory();
			sslSocketFactory = SSLSocketFactory.getSocketFactory();
			schreg.register(new Scheme("https", sslSocketFactory, 443));


			// タイムアウト時間の設定を取得
			int timeout = 10 * 1000;
			AppSetting appSetting = MainApplication.getInstance()
					.getAppSetting();
			if (appSetting != null) {
				timeout = appSetting.getCatalogDownloadTimeoutSec();
				timeout *= 1000; // 単位を秒に合わせる
			}

			// タイムアウト時間を設定
			HttpParams params = new BasicHttpParams();
			// 接続確立までのタイムアウト時間
			HttpConnectionParams.setConnectionTimeout(params, timeout);
			// 接続後、応答までのタイムアウト時間
			HttpConnectionParams.setSoTimeout(params, timeout);

			// HttpClientを取得
			ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(
					params, schreg);
			httpClient = new DefaultHttpClient(connManager, params);

			// 接続
			HttpGet httpGet = new HttpGet(new URI(Uri.encode(url, ":/")));
			HttpResponse httpResponse = httpClient.execute(httpGet);

			if (LogUtil.showHttpHeaderLog) {
				Header[] headers = httpResponse.getAllHeaders();
				for (int i = 0; i < headers.length; i++) {
					Header header = headers[i];
					LogUtil.d(TAG, "  Header: " + header.getName() + " = "
							+ header.getValue());
				}
			}

			// 応答の確認
			switch (httpResponse.getStatusLine().getStatusCode()) {
			case HttpStatus.SC_OK:
				Header lastModifiedHeader = httpResponse
						.getLastHeader("Last-Modified");
				String lastModified = "";
                if (lastModifiedHeader != null) {
                    lastModified = lastModifiedHeader.getValue();
                }

				T content = createContent(httpResponse.getEntity());
				return AsyncResult.createNormalResult(content, lastModified);
			case HttpStatus.SC_NOT_FOUND:
				return AsyncResult.createErrorResult(
						AsyncResult.ResultStatus.RESULT_STATUS_NOT_FOUND_ERROR,
						R.string.err_not_found);
			default:
				return AsyncResult.createErrorResult(
						AsyncResult.ResultStatus.RESULT_STATUS_SERVER_ERROR,
						R.string.err_status_server);
			}
		} catch (URISyntaxException e) {
			return AsyncResult.createErrorResult(
					AsyncResult.ResultStatus.RESULT_STATUS_URL_ERROR,
					R.string.err_status_url);
		} catch (ClientProtocolException e) {
			return AsyncResult.createErrorResult(
					AsyncResult.ResultStatus.RESULT_STATUS_NETWORK_ERROR,
					R.string.err_status_network);
		} catch (IllegalStateException e) {
			return AsyncResult.createErrorResult(
					AsyncResult.ResultStatus.RESULT_STATUS_UNKOWN_ERROR,
					R.string.err_status_unkown);
		} catch (IOException e) {
			return AsyncResult.createErrorResult(
					AsyncResult.ResultStatus.RESULT_STATUS_UNKOWN_ERROR,
					R.string.err_status_unkown);
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	abstract protected T createContent(HttpEntity entity);
}
