package hotelokura.jalhotels.oneharmony.analytics;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;

import hotelokura.jalhotels.oneharmony.activity.catalog.CatalogToolbarView;

/**
 * GoogleAnalyticsなどの解析用処理を行う
 * com.google.android.gms.analytics.Tracker のインスタンスを
 * 維持する必要があるため、MainApplication にインスタンスを置く
 * GAの命名ロジックは GaNameFactory に移譲する
 */
public class CheckAction {

    private Application mApp = null;

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     * <p/>
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
//    public enum GaTracker {
//        // APP_TRACKER("UA-47916823-3") // brand
//        APP_TRACKER("UA-17142701-6"), // baricata
//        DEBUG_TRACKER("UA-56582858-1"); // debug
//        private String mPropertyName;
//
//        public String getPropertyName() {
//            return this.mPropertyName;
//        }
//
//        private GaTracker(String pName) {
//            this.mPropertyName = pName;
//        }
//    }

    // UA-*** 形式のGAトラッキングID
    ArrayList<String> mGaTrackingIdsList = new ArrayList<String>();

    /**
     * 初期化した com.google.android.gms.analytics.Tracker のインスタンスを
     * 維持することでセッションを保つ
     */
    HashMap<String, Tracker> mTrackers = new HashMap<String, Tracker>();
    final Object trackOperationLock = new Object();

    public CheckAction(Application app, ArrayList<String> trackingIdsList) {
        this.mApp = app;
        this.mGaTrackingIdsList.clear();
        this.mGaTrackingIdsList.addAll(trackingIdsList);
    }

    /**
     * アプリ個別のトラッキング情報（画面名）を送付する
     * 画面名については MainApplication.GaNameFromMenu 参照
     * メニュー用
     * @param prefix
     * @param index
     * @param mode
     * @param id
     * @param url
     * @param content_title
     */
    public void sendAppTrackFromMenu(
            String prefix,
            int index,
            int mode,
            String id,
            String url,
            String content_title) {
        GaNameFactory.GaScreenName gaName = GaNameFactory.getNameFromMenu(prefix, index, mode, id, url, content_title);
        this.sendAppTrackInner(gaName);
    }

    /**
     * アプリ個別のトラッキング情報（画面名）を送付する
     * 画面名については MainApplication.GaNameFromCatalog 参照
     * メニュー用
     * @param prefix
     * @param page
     */
    public void sendAppTrackFromCatalog(
            String prefix,
            int page) {
        GaNameFactory.GaScreenName gaName = GaNameFactory.getNameFromCatalog(prefix, page);
        this.sendAppTrackInner(gaName);
    }

    public void sendAppTrackFromMapList(
            String prefix,
            String text) {
        GaNameFactory.GaScreenName gaName = GaNameFactory.getNameFromMaplist(prefix, text);
        this.sendAppTrackInner(gaName);
    }

    public void sendAppTrackFromCatalogPageAction(
            String catalogId,
            int page,
            CatalogToolbarView.ButtonId buttonId) {
        GaNameFactory.GaScreenName gaName = GaNameFactory.getNameFromCatalogToolbar(catalogId, page, buttonId);
        this.sendAppTrackInner(gaName);
    }

    public void sendAppTrackFromWebtop(String prefix, int mode, String id, String url, String content_tilte) {
        GaNameFactory.GaScreenName gaName = GaNameFactory.getNameFromWebtop(prefix, mode, id, url, content_tilte);
        this.sendAppTrackInner(gaName);
    }

    public void sendAppTrackFromNews(String category, String title) {
        GaNameFactory.GaScreenName gaName = GaNameFactory.getNameFromNews(category, title);
        this.sendAppTrackInner(gaName);
    }

    /**
     * 名前を指定してアプリ個別のトラッキング情報（画面名）を送付する
     * @param nameInstance
     */
    private void sendAppTrackInner(final GaNameFactory.GaScreenName nameInstance) {

        final ArrayList<String> tIdsList = new ArrayList<String>();
        for(String tId : this.mGaTrackingIdsList) {
            Log.v("debug20141113", "tracking id : "+tId);
            tIdsList.add(tId);
        }

        // Set screen name.
        // Where path is a String representing the screen name.
        final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this.mApp);

        Handler handler = new Handler();
        handler.post(new Runnable(){

            @Override
            public void run() {

                synchronized (CheckAction.this.trackOperationLock) {
                    for(String tid : tIdsList) {
                        Tracker tr;
                        if(CheckAction.this.mTrackers.containsKey(tid)) {
                            tr = CheckAction.this.mTrackers.get(tid);
                        } else {
                            tr = analytics.newTracker(tid);
                        }
                        tr.setScreenName(nameInstance.getScreenName());
                        tr.send(new HitBuilders.AppViewBuilder().build());
                        Log.v("GA", "ga send "+nameInstance.getScreenName());
                        CheckAction.this.mTrackers.put(tid, tr);
                    }
                }
            }
        });
    }

}
