package hotelokura.jalhotels.oneharmony.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.activity.catalog.CatalogActivity;
import hotelokura.jalhotels.oneharmony.activity.cataloglist.CatalogListActivity;
import hotelokura.jalhotels.oneharmony.activity.coverflow.CoverFlowActivity;
import hotelokura.jalhotels.oneharmony.activity.map.MapActivity;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.setting.CatalogListSetting;
import hotelokura.jalhotels.oneharmony.setting.CoverFlowSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

/**
 * テスト用レシーバ
 *
 * input from adb like
 * 'adb shell am broadcast -a test.android.intent.action.TEST1 --es type xxx'
 */
public class TestReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent arg) {
		LogUtil.d("TestReceiver", "onReceive");

        AppSetting appSetting = MainApplication.getInstance().getAppSetting();
        if (appSetting == null) {
            return;
        }

        Intent intent = null;
        String type = arg.getStringExtra("type");

        if (type.equals("catalog")) {
            CsvLine csvLine = CatalogListSetting.findCatalogListCsv("akachan");
            if (csvLine != null) {
                intent = new Intent(context, CatalogActivity.class);
                intent.putStringArrayListExtra("CatalogListCsv", csvLine);
            }
        } else if (type.equals("list")) {
            CsvLine csvLine = CoverFlowSetting.findCoverFlowCsv("catalog");
            if (csvLine != null) {
                intent = new Intent(context, CatalogListActivity.class);
                intent.putStringArrayListExtra("CoverFlowCsv", csvLine);
            }
        } else if (type.equals("top")) {
            intent = new Intent(context, CoverFlowActivity.class);
        } else if (type.equals("map")) {
            CsvLine csvLine = CoverFlowSetting.findCoverFlowCsv("hp");
            if (csvLine != null) {
                intent = new Intent(context, MapActivity.class);
                intent.putStringArrayListExtra("CatalogListCsv", csvLine);
                intent.putExtra("list", false);
            }
        }

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
