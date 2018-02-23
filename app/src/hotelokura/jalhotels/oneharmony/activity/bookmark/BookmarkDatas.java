package hotelokura.jalhotels.oneharmony.activity.bookmark;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;

import hotelokura.jalhotels.oneharmony.MainApplication;

public class BookmarkDatas {
	private String catalogId;
	private boolean isLocal;
	private ArrayList<BookmarkData> datas;

	private String filename;

	public BookmarkDatas(String catalogId, boolean isLocal) {
		this.catalogId = catalogId;
		this.isLocal = isLocal;
		init();
	}

	private void init() {
		// ファイルパスを作成
		if (isLocal) {
			filename = String.format("%s_bookmark.dat", catalogId);
		} else {
			filename = "generalbookmark.dat";
		}
		load();
	}

	public boolean isLocal() {
		return isLocal;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public ArrayList<BookmarkData> getDatas() {
		return datas;
	}

	public void add(String title, int page) {
		add(title, page, 0);
	}

	public void add(String title, int page, int subpage) {
		add(title, page, subpage, catalogId);
	}

	public void add(String title, int page, int subpage, String catalogTitle) {
		// ページ番号が同じ物を削除する
		removeByPage(page, subpage, catalogId);

		BookmarkData data = new BookmarkData();
		data.title = title;
		data.page = page;
		data.subpage = subpage;
		data.catalogTitle = catalogTitle;
		data.catalogId = catalogId;

		datas.add(0, data); // 先頭に追加する
	}

	public void remove(int index) {
		datas.remove(index);
	}

	public void removeByPage(int page, int subpage, String catalogId) {
		for (int i = datas.size() - 1; i >= 0; i--) {
			BookmarkData data = datas.get(i);

			// ローカルカタログか、データにカタログIDが無いか、カタログIDが一致している
			if (isLocal || data.catalogId.equals("")
					|| data.catalogId.equals(catalogId)) {

				// ページ番号が一致している
				if (data.page == page) {

					// サブページ番号が0か、一致している
					if (subpage == 0 || data.subpage == 0
							|| data.subpage == subpage) {

						datas.remove(i);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void load() {
		if (datas != null) {
			datas.clear();
			datas = null;
		}
		try {
			MainApplication app = MainApplication.getInstance();
			FileInputStream fis = app.openFileInput(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			datas = (ArrayList<BookmarkData>) ois.readObject();
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (datas == null) {
			datas = new ArrayList<BookmarkData>();
			save();
		}
	}

	public void save() {
		try {
			MainApplication app = MainApplication.getInstance();
			FileOutputStream fos = app.openFileOutput(filename,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(datas);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
