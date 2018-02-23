package hotelokura.jalhotels.oneharmony.cache;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import hotelokura.jalhotels.oneharmony.util.LogUtil;

import android.net.Uri;

public class MemoryCache<T> {
	static final String TAG = "MemoryCache";

	public interface OnMemoryCacheRemoveListener<T> {
		public boolean onRemoveItem(String key, T item);
	}

	private OnMemoryCacheRemoveListener<T> onMemoryCacheRemoveListener;

	private HashMap<String, T> cacheMap;

	public MemoryCache() {
		LogUtil.d(TAG, "MemoryCache");
		this.cacheMap = new HashMap<String, T>();
	}

	public void setOnMemoryCacheRemoveListener(
			OnMemoryCacheRemoveListener<T> onMemoryCacheRemoveListener) {
		this.onMemoryCacheRemoveListener = onMemoryCacheRemoveListener;
	}

	/**
	 * 存在確認
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key) {
		return cacheMap.containsKey(key);
	}

	public boolean containsKey(String baseUrl, String filename) {
		URI uri = makeURI(baseUrl, filename);
		if (uri != null) {
			return this.containsKey(uri.getPath());
		} else {
			return false;
		}
	}

	/**
	 * 格納
	 * 
	 * @param key
	 * @param object
	 */
	synchronized public T put(String key, T object) {
		T item = this.get(key);
		if (item != null) {
			// すでに存在するならその値を使用する
			this.removeObject(key, object, this.onMemoryCacheRemoveListener); // 渡された物は削除
			return item;
		}

		// LogUtil.d(TAG, "put:" + key);
		cacheMap.put(key, object);
		return object;
	}

	/**
	 * 格納
	 * 
	 * @param baseUrl
	 * @param filename
	 * @param object
	 */
	public T put(String baseUrl, String filename, T object) {
		URI uri = makeURI(baseUrl, filename);
		if (uri != null) {
			return this.put(uri.getPath(), object);
		} else {
			return null;
		}
	}

	/**
	 * 取得
	 * 
	 * @param key
	 * @return
	 */
	public T get(int key) {
		return get(Integer.toString(key));
	}

	/**
	 * 取得
	 * 
	 * @param key
	 * @return
	 */
	public T get(String key) {
		if (cacheMap.containsKey(key)) {
			// LogUtil.d(TAG, "get:" + key);
			return cacheMap.get(key);
		} else {
			return null;
		}
	}

	/**
	 * 取得
	 * 
	 * @param baseUrl
	 * @param filename
	 * @return
	 */
	public T get(String baseUrl, String filename) {
		URI uri = makeURI(baseUrl, filename);
		if (uri != null) {
			return this.get(uri.getPath());
		} else {
			return null;
		}
	}

	private boolean removeObject(String key, T object,
			OnMemoryCacheRemoveListener<T> removeListener) {
		boolean flag = true;
		if (removeListener != null) {
			flag = removeListener.onRemoveItem(key, object);
		}
		return flag;
	}

	/**
	 * 削除
	 * 
	 * @param key
	 */
	synchronized public void remove(String key) {
		if (cacheMap.containsKey(key)) {
			T item = cacheMap.get(key);
			boolean flag = this.removeObject(key, item,
					this.onMemoryCacheRemoveListener);
			if (flag) {
				// LogUtil.d(TAG, "remove:" + key);
				cacheMap.remove(key);
			}
		}
	}

	/**
	 * 削除
	 * 
	 * @param baseUrl
	 * @param filename
	 */
	public void remove(String baseUrl, String filename) {
		URI uri = makeURI(baseUrl, filename);
		if (uri != null) {
			this.remove(uri.getPath());
		}
	}

	/**
	 * 全て削除
	 * 
	 * @param onMemoryCacheRemoveListener
	 */
	synchronized public void removeAll(
			OnMemoryCacheRemoveListener<T> onMemoryCacheRemoveListener) {

		Set<String> keys = cacheMap.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = it.next();
			T item = cacheMap.get(key);
			boolean flag = this.removeObject(key, item,
					onMemoryCacheRemoveListener);
			if (flag) {
				LogUtil.d(TAG, "remove:" + key);
				it.remove();
			}
		}
	}

	/**
	 * 全て削除
	 */
	public void removeAll() {
		removeAll(this.onMemoryCacheRemoveListener);
	}

	private URI makeURI(String baseUrl, String filename) {
		URI uri = null;
		try {
			uri = new URI(Uri.encode(baseUrl + filename, ":/"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}

}
