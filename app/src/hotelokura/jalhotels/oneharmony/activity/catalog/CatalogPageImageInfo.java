package hotelokura.jalhotels.oneharmony.activity.catalog;

import java.util.HashMap;

import android.graphics.Point;
import android.util.SparseArray;

public class CatalogPageImageInfo {
	static final String TAG = "CatalogPageImageInfo";

	public static class PageImageInfo {
		static final String TAG = "PageImageInfo";

		private HashMap<Point, String> imageNames = new HashMap<Point, String>();
		private int horizontal = -1;
		private int vertical = -1;

		public PageImageInfo() {
		}

		public void setHorizontal(int horizontal) {
			this.horizontal = horizontal;
		}

		public int getHorizontal() {
			return horizontal;
		}

		public void setVertical(int vertical) {
			this.vertical = vertical;
		}

		public int getVertical() {
			return vertical;
		}

		public void setImageName(Point point, String filename) {
			imageNames.put(point, filename);
		}

		public String getImageName(Point point) {
			if (imageNames.containsKey(point)) {
				return imageNames.get(point);
			} else {
				return null;
			}
		}

		public void removeAll() {
			imageNames.clear();
		}
	}

	private SparseArray<PageImageInfo> pageInfos = new SparseArray<PageImageInfo>();

	public CatalogPageImageInfo() {
	}

	private PageImageInfo getPageIamgeInfo(int level) {
		PageImageInfo info = pageInfos.get(level);
		if (info == null) {
			info = new PageImageInfo();
			pageInfos.put(level, info);
		}
		return info;
	}

	public void setHorizontal(int level, int horizontal) {
		PageImageInfo info = getPageIamgeInfo(level);
		info.setHorizontal(horizontal);
	}

	public int getHorizontal(int level) {
		PageImageInfo info = getPageIamgeInfo(level);
		return info.getHorizontal();
	}

	public void setVertical(int level, int vertical) {
		PageImageInfo info = getPageIamgeInfo(level);
		info.setVertical(vertical);
	}

	public int getVertical(int level) {
		PageImageInfo info = getPageIamgeInfo(level);
		return info.getVertical();
	}

	public void setImageName(int level, int horizontal, int vertical,
			String filename) {
		setImageName(level, new Point(horizontal, vertical), filename);
	}

	public void setImageName(int level, Point point, String filename) {
		PageImageInfo info = getPageIamgeInfo(level);
		info.setImageName(point, filename);
	}

	public void setImageName(int level, int horizontal, int vertical,
			int filePage, String fileID, String fileType) {

		// 画像のファイル名を作成
		String filename = CatalogFileNameUtil.makeImageName(level, horizontal,
				vertical, filePage, fileID, fileType);

		setImageName(level, horizontal, vertical, filename);
	}

	public String getImageName(int level, int horizontal, int vertical) {
		return getImageName(level, new Point(horizontal, vertical));
	}

	public String getImageName(int level, Point point) {
		PageImageInfo info = getPageIamgeInfo(level);
		return info.getImageName(point);
	}

	public void removeAll() {
		for (int i = 1; i <= 4; i++) {
			PageImageInfo info = pageInfos.get(i, null);
			if (info != null) {
				info.removeAll();
				pageInfos.remove(i);
			}
		}
	}
}
