package hotelokura.jalhotels.oneharmony.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import android.graphics.Bitmap;
import android.net.Uri;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.util.BitmapUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

public class DiskCache {
	static final String TAG = "DiskCache";

	private String uniqueName;

	/**
	 * 
	 * @param uniqueName
	 *            CacheDirに作るフォルダ名(ユニークな名前、主にappName)
	 */
	public DiskCache(String uniqueName) {
		LogUtil.d(TAG, "DiskCache:" + uniqueName);
		this.uniqueName = uniqueName;
	}

	public File getDiskCacheDir() {
		File cacheDir = null;
		if (MainApplication.isExternalStorageAvailable()) {
			// SDCardがあるならそちらを使う
			cacheDir = MainApplication.getInstance().getExternalCacheDir();
			if (cacheDir == null || !cacheDir.canWrite() || !cacheDir.canRead()) {
				// 読み書きができないなら使わない
				cacheDir = null;
			}
		}
		cacheDir = null; // TODO SDCardだと遅いので強制的にローカルキャッシュにしておく
		if (cacheDir == null) {
			cacheDir = MainApplication.getInstance().getCacheDir();
		}
		return new File(cacheDir, uniqueName);
	}

	public File createFileName(File cacheDir, String key) {
		// Prefixとか付けるならここで。
		return new File(cacheDir, key);
	}

	public void put(String key, InputStream is) {
		File cacheDir = getDiskCacheDir();
		File file = createFileName(cacheDir, key);

		LogUtil.d(TAG, "put:" + file.getAbsolutePath());
		writeFile(file, is);
	}

	public void put(String baseUrl, String filename, InputStream is) {
		URI uri = null;
		try {
			uri = new URI(Uri.encode(baseUrl + filename, ":/"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (uri != null) {
			this.put(uri.getPath(), is);
		}
	}

	public InputStream get(String key) {
		File cacheDir = getDiskCacheDir();
		File file = createFileName(cacheDir, key);

		InputStream is = null;
		if (file.exists()) {
			try {
				LogUtil.d(TAG, "get:" + file.getAbsolutePath());
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return is;
	}

	public InputStream get(String baseUrl, String filename) {
		URI uri = null;
		try {
			uri = new URI(Uri.encode(baseUrl + filename, ":/"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (uri != null) {
			return this.get(uri.getPath());
		} else {
			return null;
		}
	}

	public Bitmap getBitmap(String baseUrl, String filename) {
		InputStream is = get(baseUrl, filename);
		return createBitmap(is);
	}

	synchronized private Bitmap createBitmap(InputStream is) {
		Bitmap bmp = null;
		if (is != null) {
			bmp = BitmapUtil.getInstance().decodeStream(is);
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bmp;
	}

	public void remove(String key) {
		File cacheDir = getDiskCacheDir();
		File file = createFileName(cacheDir, key);
		if (file.exists()) {
			LogUtil.d(TAG, "remove:" + file.getAbsolutePath());
			file.delete();
		}
	}

	public void remove(String baseUrl, String filename) {
		URI uri = null;
		try {
			uri = new URI(Uri.encode(baseUrl + filename, ":/"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (uri != null) {
			this.remove(uri.getPath());
		}
	}

	public void removeDir(String baseUrl) {
		URI uri = null;
		try {
			uri = new URI(Uri.encode(baseUrl, ":/"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (uri != null) {
			File cacheDir = getDiskCacheDir();
			this.removeDir(new File(cacheDir, uri.getPath()));
		}
	}

	/**
	 * DiskCacheディレクトリのキャッシュファイルを全て削除する。
	 * 
	 * @return
	 */
	public void removeAll() {
		File cacheDirFile = getDiskCacheDir();

		if (cacheDirFile.exists()) {
			removeDir(cacheDirFile);
		}
	}

	private boolean removeDir(File dir) {
		LogUtil.d(TAG, "removeDir:" + dir.getAbsolutePath());
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = removeDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	synchronized private void writeFile(File file, InputStream is) {
		if (file.exists()) {
			return;
		}
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer, 0, buffer.length)) > 0) {
				bos.write(buffer, 0, len);
			}
			bos.flush();
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
