package hotelokura.jalhotels.oneharmony.activity.coverflow;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListActivity;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListData;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListGridView;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListNavigationView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class CoverFlowActivity extends CatalogListActivity {
	static final String TAG = "CoverFlowActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
        this.mGaScreenName = CatalogListActivity.TOP_SCREEN;
	}

	@Override
	protected void onStart() {
		LogUtil.d(TAG, "onStart");
		super.onStart();

		MainApplication.getInstance().setCurrentCatalogListTags(null);
	}

	@Override
	protected String settingName() {
		return "coverflow.csv";
	}

	@Override
	protected CatalogListData settingListData() {
		CatalogListData data = MainApplication.getInstance().getListData();
		if (data != null) {
			MainApplication.getInstance().setListData(null);
		} else {
			data = new CoverFlowData();
		}
		return data;
	}

	@Override
	protected void settingNavigationView(
			final CatalogListNavigationView navigationView) {
		navigationView.setVisibility(View.GONE);
	}

	@Override
	protected void settingBGLyout(ViewGroup layout) {
		AppSetting appSetting = MainApplication.getInstance().getAppSetting();

		// 背景色
		layout.setBackgroundColor(appSetting.getCoverFlowBGColor());
	}

	@Override
	protected int settingGridScrollMode() {
		return SCROLL_MODE_YES;
	}

	@Override
	protected Integer settingGridItemTextColor() {
		AppSetting appSetting = MainApplication.getInstance().getAppSetting();
		return appSetting.getCoverFlowTextColor();
	}

	@Override
	protected void changeGridPadding() {
		CatalogListGridView gridView = (CatalogListGridView) findViewById(R.id.gridView);
		ViewGroup backTopLogoLayout = (ViewGroup) findViewById(R.id.backTopLogoLayout);
		ViewGroup backBottomLogoLayout = (ViewGroup) findViewById(R.id.backBottomLogoLayout);
		boolean logoTop = backTopLogoLayout.getVisibility() != View.GONE;
		boolean logoBottom = backBottomLogoLayout.getVisibility() != View.GONE;

		int paddingTop = 0;
		int paddingBottom = 0;
		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			if (logoTop && logoBottom) {
				// ロゴ 上有り 下有り
				paddingTop = 0;
			} else if (logoTop && !logoBottom) {
				// ロゴ 上有り 下無し
				paddingTop = 40;
			} else if (!logoTop && logoBottom) {
				// ロゴ 上無し 下有り
				paddingTop = 40;
			} else if (!logoTop && !logoBottom) {
				// ロゴ 上無し 下無し
				paddingTop = 30;
			}
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			if (logoTop && logoBottom) {
				// ロゴ 上有り 下有り
				paddingTop = 0;
			} else if (logoTop && !logoBottom) {
				// ロゴ 上有り 下無し
				paddingTop = 10;
			} else if (!logoTop && logoBottom) {
				// ロゴ 上無し 下有り
				paddingTop = 10;
			} else if (!logoTop && !logoBottom) {
				// ロゴ 上無し 下無し
				paddingTop = 10;
			}
			break;
		}
		paddingTop = MainApplication.getInstance().dp2px(paddingTop);
		paddingBottom = MainApplication.getInstance().dp2px(paddingBottom);
		gridView.setPadding(gridView.getPaddingLeft(), paddingTop,
				gridView.getPaddingRight(), paddingBottom);
	}
}
