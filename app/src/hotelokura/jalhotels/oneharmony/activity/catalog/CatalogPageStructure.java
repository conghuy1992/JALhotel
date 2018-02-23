package hotelokura.jalhotels.oneharmony.activity.catalog;

import java.util.ArrayList;

import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.setting.PageListSetting;

import android.content.res.Configuration;
import android.util.SparseIntArray;

public class CatalogPageStructure {
	private CsvArray pageListCsvArray;
	private ArrayList<SparseIntArray> pageStructureList;

	public CatalogPageStructure(CsvArray pageListCsvArray, int orientation) {
		super();
		this.pageListCsvArray = PageListSetting
				.complementOmit(pageListCsvArray);
		changeOrientation(orientation);
	}

	public void changeOrientation(int orientation) {
		makePageStructureList(orientation);
	}

	public CsvArray getCsvArray() {
		return pageListCsvArray;
	}

	public int getCsvSize() {
		return pageListCsvArray.size();
	}

	public CsvLine getCsv(int index) {
		return pageListCsvArray.get(index);
	}

	public ArrayList<SparseIntArray> getStructureList() {
		return pageStructureList;
	}

	public int getStructureSize() {
		return pageStructureList.size();
	}

	public SparseIntArray getStructure(int index) {
		return pageStructureList.get(index);
	}

	private void makePageStructureList(int orientation) {
		pageStructureList = new ArrayList<SparseIntArray>();

		switch (orientation) {
		case Configuration.ORIENTATION_PORTRAIT: {
			for (int i = 0; i < pageListCsvArray.size(); i++) {
				SparseIntArray sparse = new SparseIntArray();
				sparse.put(0, i);
				pageStructureList.add(sparse);
			}
			break;
		}
		case Configuration.ORIENTATION_LANDSCAPE: {
			// 1画面で2ページ表示、設定により単独などがある。
			PageListSetting pageSetting = new PageListSetting();
			SparseIntArray pageStructure = null;
			int align = 0;
			for (int i = 0; i < pageListCsvArray.size(); i++) {
				if (align == 0 || align >= 2) {
					pageStructure = new SparseIntArray();
					pageStructureList.add(pageStructure);
					align = 0;
				}
				CsvLine line = pageListCsvArray.get(i);
				pageSetting.setLine(line);

				Integer layout = pageSetting.getLayout();
				if (layout != null) {
					switch (layout) {
					case 0: // 単独
						if (align != 0) {
							// 次が右になっているので、次Viewにする
							pageStructure = new SparseIntArray();
							pageStructureList.add(pageStructure);
							align = 0;
						}
						pageStructure.put(align++, i);
						align++;
						break;
					case 1: // 左
						if (align != 0) {
							// 次が右になっているので、次Viewにする
							pageStructure = new SparseIntArray();
							pageStructureList.add(pageStructure);
							align = 0;
						}
						pageStructure.put(align++, i);
						break;
					case 2: // 右
						align = 1;
						pageStructure.put(align++, i);
						break;
					}
				} else {
					pageStructure.put(align++, i);
				}
			}
			break;
		}
		default:
			break;
		}
	}
}
