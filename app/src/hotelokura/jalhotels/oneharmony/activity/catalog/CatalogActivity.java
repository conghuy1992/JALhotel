package hotelokura.jalhotels.oneharmony.activity.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.map.MapActivity;
import hotelokura.jalhotels.oneharmony.analytics.CheckAction;
import hotelokura.jalhotels.oneharmony.net.AsyncCallback;
import hotelokura.jalhotels.oneharmony.net.AsyncResult;
import hotelokura.jalhotels.oneharmony.net.DownloadManager;
import hotelokura.jalhotels.oneharmony.setting.CatalogSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.setting.PageListSetting;
import hotelokura.jalhotels.oneharmony.setting.PlistDict;
import hotelokura.jalhotels.oneharmony.util.AdManager;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.view.ProgressIndicatorDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import hotelokura.jalhotels.oneharmony.activity.ActivitySkeleton;
import hotelokura.jalhotels.oneharmony.activity.CatalogDickCachUtil;
import hotelokura.jalhotels.oneharmony.activity.bookmark.BookmarkActivity;
import hotelokura.jalhotels.oneharmony.activity.help.HelpActivity;
import hotelokura.jalhotels.oneharmony.activity.web.WebViewActivity;

public class CatalogActivity extends ActivitySkeleton {
    static final String TAG = "CatalogActivity";

    private ProgressIndicatorDialog progress;
    private AlertDialog downloadAlertDialog;
    private Dialog pageLinkSelectDialog;

    private String catalogLastMod;
    private String pageListLastMod;

    private int startPage;
    private String catalogId = null;
    private String catalogUrl = null;

    public static CatalogSetting catalogSetting;

    private CatalogPageStructure pageStructure;
    private CatalogPagerView pagerView;
    private CatalogPagerEventPanel pagerPanel;
    private CatalogPagerAdapter pagerAdpter;

    private boolean isReStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        MainApplication main = MainApplication.getInstance();
        if (main == null || main.getAppSetting() == null) {
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_catalog);

        AdManager.startInterstitial(this);

        // 前画面から情報取得
        int realStartPage = getIntent().getIntExtra("start_page", -1);
        this.startPage = realStartPage == -1 ? 0 : realStartPage;

        catalogId = getIntent().getStringExtra("catalog_id");
        catalogUrl = getIntent().getStringExtra("catalog_url");

        MainApplication.getInstance().setCurrentCatalogId(catalogId);

        // ページャー設定
        pagerView = (CatalogPagerView) findViewById(R.id.pagerView);
        pagerView.setOnCatalogPagerViewListener(onCatalogPagerViewListener);
        pagerView.setScrollFactor(1.0f);
        pagerView.setRequiredInfoForGA((MainApplication) this.getApplication(), this.catalogId);

        // スクロールやページングのタップ関係イベント設定
        pagerPanel = (CatalogPagerEventPanel) findViewById(R.id.eventPanel);
        pagerPanel
                .setOnCatalogPagerEventPanelListener(onCatalogPagerEventPanelListener);
        pagerPanel.setPagerView(pagerView);

        // アダプタ設定
        pagerAdpter = new CatalogPagerAdapter(this, pagerView);
        pagerView.setAdapter(pagerAdpter);

        // ナビゲーションの設定
        CatalogNavigationView navigationView = (CatalogNavigationView) findViewById(R.id.navigationView);
        navigationView.setHomeButtionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        navigationView.setHelpButtionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentHelp();
            }
        });

        // ツールバーの設定
        CatalogToolbarView toolbarView = (CatalogToolbarView) findViewById(R.id.toolbarView);
        // ページングツールバーの設定
        CatalogPagingToolbarView pagingBar = toolbarView.getPagingToolbar();
        pagingBar.setTopButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 先頭へ移動
                setCurrentPageIndex(0);
            }
        });
        pagingBar.setEndButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 最後へ移動
                int end = pageStructure.getStructureSize() - 1;
                if (end >= 0) {
                    pagerView.setCurrentItem(end, false);
                }
            }
        });
        pagingBar.setNextButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 次へ移動
                pagerView.nextPage();
            }
        });
        pagingBar.setPrevButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 前へ移動
                pagerView.prevPage();
            }
        });
        pagingBar
                .setSeekOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        // ページング情報の更新
                        SparseIntArray structure = pageStructure
                                .getStructure(progress);
                        changePagingInfo(structure);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // 指定へ移動
                        pagerView.setCurrentItem(seekBar.getProgress(), false);
                    }
                });


        // メニュー非表示
        hideMenu();

        // 向きを変える
        changeOrientation();

        MainApplication app = (MainApplication) this.getApplication();
        String catalogId = app.getCurrentCatalogId();
        CheckAction ca = app.getCheckAction();
        ca.sendAppTrackFromCatalog(catalogId + "_page", this.startPage + 1);
    }

    @Override
    protected void onStart() {
        LogUtil.d(TAG, "onStart");
        super.onStart();

        if (!isReStart || pageStructure == null) {
            // カタログ設定をダウンロード
            progress = new ProgressIndicatorDialog(this);
            progress.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (progress != null) {
                        progress.dismiss();
                        progress = null;
                    }
                    finish();
                }
            });
            progress.show();
            downloadSetting();
        }
        isReStart = false;
    }

    @Override
    protected void onResume() {
        LogUtil.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.d(TAG, "onPause");
        super.onPause();

        startPage = getCurrentPageIndex();
    }

    @Override
    protected void onStop() {
        LogUtil.d(TAG, "onStop");
        super.onStop();

        DownloadManager.getInstance().clear(catalogId);
        pagerAdpter.setPageStructureList(null);
        if (downloadAlertDialog != null) {
            downloadAlertDialog.dismiss();
            downloadAlertDialog = null;
        }
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    @Override
    protected void onRestart() {
        LogUtil.d(TAG, "onRestart");
        super.onRestart();

        isReStart = false;
        if (pageStructure != null) {
            isReStart = true;
            // 別画面から戻ってきたとき
            // メニュー非表示
            hideMenu();

            progress = new ProgressIndicatorDialog(this);
            progress.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (progress != null) {
                        progress.dismiss();
                        progress = null;
                    }
                    finish();
                }
            });
            progress.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPage();
                }
            }, 0);
        }
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        LogUtil.d(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);

        // 現在表示しているページ
        int pageIndexOld = getCurrentPageIndex();

        if (pageStructure == null) {
            finish();
            return;
        }

        // 向きを変える
        pageStructure
                .changeOrientation(getResources().getConfiguration().orientation);
        pagerAdpter.setPageStructureList(pageStructure.getStructureList());

        // ページングツールバー
        CatalogToolbarView toolbarView = (CatalogToolbarView) findViewById(R.id.toolbarView);
        CatalogPagingToolbarView bar = toolbarView.getPagingToolbar();
        bar.setSeekMax(pagerAdpter.getCount() - 1);

        // 縦横更新後のページを、更新前のページにする
        setCurrentPageIndex(pageIndexOld);

        // 向きを変える
        changeOrientation();
    }

    public CatalogPageStructure getPageStructure() {
        return pageStructure;
    }

    public CatalogPagerView getPagerView() {
        return pagerView;
    }

    public CatalogPagerAdapter getPagerAdpter() {
        return pagerAdpter;
    }

    private void showDownloadAlertDialog(AsyncResult<?> result,
                                         final DialogInterface.OnClickListener listener) {
        if (downloadAlertDialog != null) {
            downloadAlertDialog.dismiss();
            downloadAlertDialog = null;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                CatalogActivity.this);
        alertDialogBuilder.setTitle(R.string.err_title);
        alertDialogBuilder.setMessage(result.getMessage());
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(R.string.err_button_retry,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        downloadAlertDialog = null;
                        if (listener != null) {
                            listener.onClick(dialog, which);
                        }
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // キャンセル実行
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // 前画面に戻る
                downloadAlertDialog = null;
                finish();
            }
        });
        downloadAlertDialog = alertDialogBuilder.create();
        downloadAlertDialog.show();
    }

    /**
     * カタログ設定のダウンロード
     */
    private void downloadSetting() {
        DownloadManager.getInstance().offerPlist(catalogId, 0, catalogUrl,
                catalogId + "_settings.plist", new AsyncCallback<PlistDict>() {
                    @Override
                    public void onSuccess(AsyncResult<PlistDict> result) {
                        catalogLastMod = result.getLastModified();
                        catalogSetting = new CatalogSetting(result.getContent());
                        MainApplication.getInstance().setCurrentCatalogSetting(catalogSetting);
                        viewCatalog();
                    }

                    @Override
                    public void onFailed(AsyncResult<PlistDict> result) {
                        // エラーダイアログ表示
                        showDownloadAlertDialog(result,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // 再ダウンロード
                                        downloadSetting();
                                    }
                                });
                    }
                });
    }

    /**
     * カタログ設定で画面を表示<br>
     * ページ一覧の取得開始
     */
    private void viewCatalog() {
        // 背景色
        pagerView.setBackgroundColor(catalogSetting.getBGColor());
        // ナビゲーションバー
        CatalogNavigationView navigationView = (CatalogNavigationView) findViewById(R.id.navigationView);
        navigationView.setTitle(catalogSetting.getTitle());
        // ツールバー
        settingFunctionToolbar();
        changeOrientation();

        // ページリストをダウンロード
        downloadPageList();
    }

    private void downloadPageList() {
        // ページリストの名前
        String pagelistFile = catalogSetting.getPagelistFile();
        if (pagelistFile == null) {
            pagelistFile = catalogId + "_pagelist.csv";
        }
        // ページリストをダウンロード
        DownloadManager.getInstance().offerCsv(catalogId, 0, catalogUrl,
                pagelistFile, new AsyncCallback<CsvArray>() {
                    @Override
                    public void onSuccess(AsyncResult<CsvArray> result) {
//                        pageListCsvArray = result.getContent();
                        pageListLastMod = result.getLastModified();
                        pageStructure = new CatalogPageStructure(result
                                .getContent(), getResources()
                                .getConfiguration().orientation);
                        viewPage();
                    }

                    @Override
                    public void onFailed(AsyncResult<CsvArray> result) {
                        // エラーダイアログ表示
                        showDownloadAlertDialog(result,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // 再ダウンロード
                                        downloadPageList();
                                    }
                                });
                    }
                });
    }

    /**
     * カタログページを表示する
     */
    private void viewPage() {
        // メニュー表示
        viewMenu();

        // キャッシュクリアの確認
        CatalogDickCachUtil.chackCatalogSetting(catalogId, catalogSetting,
                catalogLastMod, pageStructure.getCsvArray(), pageListLastMod);

        // ページング情報
        final CatalogPagingInfoView info = (CatalogPagingInfoView) findViewById(R.id.pagingInfoView);
        info.setMaxPage(pageStructure.getCsvSize());
        final CatalogPagingTextView text = (CatalogPagingTextView) findViewById(R.id.pagingTextView);
        text.setMaxPage(pageStructure.getCsvSize());
        text.setBGColor(catalogSetting.getTextInfoBGColor());
        text.setTextColor(catalogSetting.getTextInfoTextColor());
        text.setTabOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuVisible()) {
                    text.toggleItemVisibility();
                } else {
                    viewMenu();
                }
            }
        });

        // ページリストをセット
        pagerAdpter.setPageStructureList(pageStructure.getStructureList());

        // ページングツールバー
        CatalogToolbarView toolbarView = (CatalogToolbarView) findViewById(R.id.toolbarView);
        CatalogPagingToolbarView bar = toolbarView.getPagingToolbar();
        bar.setSeekMax(pagerAdpter.getCount() - 1);

        setCurrentPageIndex(startPage);

        // Lv1画像の先行ダウンロード
        downloadLv1Images();

        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    /**
     * Lv1画像を先行ダウンロード
     */
    private void downloadLv1Images() {
        PageListSetting setting = new PageListSetting();
        CsvArray array = pageStructure.getCsvArray();
        for (CsvLine line : array) {
            setting.setLine(line);
            // Lv1のファイル名
            String fileID = setting.getFileID();
            String fileType = setting.getFileType();
            int filePage = setting.getPageOfFile();
            String filename = CatalogFileNameUtil.makeImageName(1, 0, 0,
                    filePage, fileID, fileType);

            // Lv1画像のダウンロード
            // ここではキャッシュに保存したいだけなので、取得後すぐに破棄する(画像にしてしまうとメモリを使い過ぎる)
            String baseUrl = setting.getFileSource();
            DownloadManager.getInstance().offerToCache(catalogId, 5, baseUrl,
                    filename, new AsyncCallback<InputStream>() {
                        @Override
                        public void onSuccess(AsyncResult<InputStream> result) {
                            InputStream is = result.getContent();
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailed(AsyncResult<InputStream> result) {
                        }
                    });
        }
    }

    private void settingFunctionToolbar() {
        CatalogToolbarView bar = (CatalogToolbarView) findViewById(R.id.toolbarView);
        boolean dispText = MainApplication.getInstance().getAppSetting().getCatalogToolbarButtonText();

        // 目次ボタン (現在未対応)
        bar.setButtonLayoutVisibility(CatalogToolbarView.ButtonId.contents, false);

        // サムネイルボタン (現在未対応)
        bar.setButtonLayoutVisibility(CatalogToolbarView.ButtonId.thumbnail, false);

        // しおりボタン
        bar.setButtonLayoutVisibility(CatalogToolbarView.ButtonId.bookmark, catalogSetting.getUseBookmark());
//        bar.setButtonResource(CatalogToolbarView.ButtonId.bookmark, catalogSetting.getBookmarkImage(), dispText);
        bar.setButtonResource(CatalogToolbarView.ButtonId.bookmark, 8, dispText);
        bar.setButtonOnClickListener(CatalogToolbarView.ButtonId.bookmark, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "" + catalogSetting.getExternalLinkURL());
                intentBookmark();
            }
        });

        // SNSボタン (現在未対応)
        bar.setButtonLayoutVisibility(CatalogToolbarView.ButtonId.sns, false);

        // カートボタン
        final String cartlinkUrl = catalogSetting.getCartLinkURL();
        bar.setButtonLayoutVisibility(CatalogToolbarView.ButtonId.cart, (catalogSetting.getUseCartLink() && cartlinkUrl != null));
        bar.setButtonResource(CatalogToolbarView.ButtonId.cart, 4, dispText);
//        bar.setButtonOnClickListener(CatalogToolbarView.ButtonId.cart, new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intentBrowser(cartlinkUrl);
//            }
//        });
//        bar.setButtonLayoutVisibility(CatalogToolbarView.ButtonId.cart, true);

        // マップボタン
        bar.setButtonLayoutVisibility(CatalogToolbarView.ButtonId.map, catalogSetting.getUseMapLink());
//        bar.setButtonResource(CatalogToolbarView.ButtonId.map, catalogSetting.getMapImage(), dispText);
        bar.setButtonResource(CatalogToolbarView.ButtonId.map, 2, dispText);
        bar.setButtonOnClickListener(CatalogToolbarView.ButtonId.map,
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        intentMap();
                        intentBrowser(cartlinkUrl);
                    }
                });

        // 外部リンクボタン
        final String externalLinkUrl = catalogSetting.getExternalLinkURL();
        bar.setButtonLayoutVisibility(CatalogToolbarView.ButtonId.building, (catalogSetting.getUseExternalLink() && externalLinkUrl != null));
//        bar.setButtonResource(CatalogToolbarView.ButtonId.building, catalogSetting.getExternalLinkImage(), dispText);
        bar.setButtonResource(CatalogToolbarView.ButtonId.building, 2, dispText);
        bar.setButtonOnClickListener(CatalogToolbarView.ButtonId.building,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        intentExternalBrowser(externalLinkUrl);
                        intentBrowser(externalLinkUrl);
                    }
                });
//		bar.setButtonLayoutVisibility(ButtonId.building, false);

        // リンクボタン 有効無効は表示中のページリストを見て決まる
        bar.setButtonLayoutVisibility(CatalogToolbarView.ButtonId.openTo, catalogSetting.getUsePageLink());
//        bar.setButtonResource(CatalogToolbarView.ButtonId.openTo, catalogSetting.getPageLinkImage(), dispText);
        bar.setButtonResource(CatalogToolbarView.ButtonId.openTo, 11, dispText);
        bar.setButtonOnClickListener(CatalogToolbarView.ButtonId.openTo,
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intentMap();
                    }
                });
    }

    /**
     * ページリンク情報を現在のページで更新
     */
    private void changePageLink() {
        if (!catalogSetting.getUsePageLink()) {
            return;
        }
        // 現在表示中のindexを取得
        SparseIntArray structure = pageStructure.getStructure(pagerView
                .getCurrentItem());
        int index1 = structure.get(0, -1);
        int index2 = structure.get(1, -1);

        // ページリンクのURLを取得
        String pageLinkUrl1 = null;
        String pageLinkUrl2 = null;
        if (index1 != -1) {
            PageListSetting pageSetting = new PageListSetting(
                    pageStructure.getCsv(index1));
            pageLinkUrl1 = pageSetting.getLinkURL();
        }
        if (index2 != -1) {
            PageListSetting pageSetting = new PageListSetting(
                    pageStructure.getCsv(index2));
            pageLinkUrl2 = pageSetting.getLinkURL();
        }

        // ページリンク表示
        CatalogToolbarView bar = (CatalogToolbarView) findViewById(R.id.toolbarView);
        if (pageLinkUrl1 == null && pageLinkUrl2 == null) {
            bar.setButtonEnabled(CatalogToolbarView.ButtonId.cart, false);
        } else {
            final String pageLinkUrl1_ = pageLinkUrl1;
            final String pageLinkUrl2_ = pageLinkUrl2;
            bar.setButtonEnabled(CatalogToolbarView.ButtonId.cart, true);
            bar.setButtonOnClickListener(CatalogToolbarView.ButtonId.cart,
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intentPageLink(pageLinkUrl1_, pageLinkUrl2_);
                        }
                    });
        }

        // ページリンクインジケータ表示制御
        pageLinkIndicator(pageLinkUrl1, pageLinkUrl2);
    }

    /**
     * ページリンクのインジケータ表示
     *
     * @param pageLinkUrl1
     * @param pageLinkUrl2
     */
    private void pageLinkIndicator(final String pageLinkUrl1,
                                   final String pageLinkUrl2) {
        CatalogLinkIndicatorView linkIndicator = (CatalogLinkIndicatorView) findViewById(R.id.linkIndicator);

        Integer blinkTime = catalogSetting.getPageLinkBlinkTimes();
        if (blinkTime == null || blinkTime <= 0) {
            // 0のときは表示しない
            linkIndicator.setVisibility(View.GONE);
            return;
        }

        if (pageLinkUrl1 == null && pageLinkUrl2 == null) {
            linkIndicator.setVisibility(View.GONE);
        } else {
            if (!catalogSetting.getUsePageLink()) {
                linkIndicator.setVisibility(View.GONE);
            } else {
                linkIndicator.setVisibility(View.VISIBLE);
                linkIndicator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intentPageLink(pageLinkUrl1, pageLinkUrl2);
                    }
                });
                // 起動
                linkIndicator.start(blinkTime);
            }
        }
    }

    private void intentHelp() {
        if (catalogSetting == null) {
            return;
        }
        startPage = getCurrentPageIndex();
        Intent intent = new Intent();
        intent.setClass(this, HelpActivity.class);
        intent.putExtra("InfoMessageTitle",
                catalogSetting.getInfoMessageTitle());
        intent.putExtra("InfoMessageSub1", catalogSetting.getInfoMessageSub1());
        intent.putExtra("InfoMessageSub2", catalogSetting.getInfoMessageSub2());
        intent.putExtra("InfoLinkURL", catalogSetting.getInfoLinkURL());
        startActivity(intent);
    }

    private void intentExternalBrowser(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        MainApplication app = (MainApplication) this.getApplication();
        CheckAction ca = app.getCheckAction();
        ca.sendAppTrackFromCatalogPageAction(this.catalogId, this.getCurrentPageIndex(), CatalogToolbarView.ButtonId.openTo);

        startPage = getCurrentPageIndex();
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void intentBrowser(String url) {
        MainApplication app = (MainApplication) this.getApplication();
        CheckAction ca = app.getCheckAction();
        ca.sendAppTrackFromCatalogPageAction(this.catalogId, this.getCurrentPageIndex(), CatalogToolbarView.ButtonId.cart);

        startPage = getCurrentPageIndex();
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("StartUrl", url);
        startActivity(intent);
    }

    private void intentBookmark() {
        startPage = getCurrentPageIndex();
        CsvLine pageCsv = pageStructure.getCsv(getCurrentPageIndex());

        Intent intent = new Intent();
        intent.setClass(this, BookmarkActivity.class);
        intent.putExtra("CatalogId", catalogId);
        intent.putExtra("CatalogPlist", catalogSetting.getDict());
        intent.putStringArrayListExtra("PageListCsv", pageCsv);
        intent.putExtra("PageIndex", startPage);
        startActivity(intent);
    }

    private void intentPageLink(String pageLinkUrl1, String pageLinkUrl2) {
        startPage = getCurrentPageIndex();
        if (pageLinkUrl1 != null && pageLinkUrl2 != null) {
            showPageLinkSelectDialog(pageLinkUrl1, pageLinkUrl2);
        } else if (pageLinkUrl1 != null) {
            intentBrowser(pageLinkUrl1);
        } else if (pageLinkUrl2 != null) {
            intentBrowser(pageLinkUrl2);
        }
    }

    private void intentMap() {
        MainApplication app = (MainApplication) this.getApplication();
        CheckAction ca = app.getCheckAction();
        ca.sendAppTrackFromCatalogPageAction(this.catalogId, this.getCurrentPageIndex(), CatalogToolbarView.ButtonId.map);

        MapActivity.startActivity(this, MainApplication.getInstance().getCurrentCatalogId(), false);
    }

    private void showPageLinkSelectDialog(final String pageLinkUrl1,
                                          final String pageLinkUrl2) {
        // 専用レイアウトのダイアログ設定
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        CatalogLinkSelectView layout = new CatalogLinkSelectView(this);

        // イベント 左ページリンク選択
        layout.setOnClickLeftButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentBrowser(pageLinkUrl1);
                closePageLinkSelectDialog();
            }
        });
        // イベント 右ページリンク選択
        layout.setOnClickRightButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentBrowser(pageLinkUrl2);
                closePageLinkSelectDialog();
            }
        });
        // イベント キャンセル
        layout.setOnClickCancelButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePageLinkSelectDialog();
            }
        });

        dialog.setView(layout);
        pageLinkSelectDialog = dialog.create();
        pageLinkSelectDialog.show();
    }

    private void closePageLinkSelectDialog() {
        if (pageLinkSelectDialog != null) {
            pageLinkSelectDialog.dismiss();
            pageLinkSelectDialog = null;
        }
    }

    private void viewMenu() {
        CatalogNavigationView navi = (CatalogNavigationView) findViewById(R.id.navigationView);
        CatalogToolbarView tool = (CatalogToolbarView) findViewById(R.id.toolbarView);
        CatalogPagingInfoView info = (CatalogPagingInfoView) findViewById(R.id.pagingInfoView);
        CatalogPagingTextView text = (CatalogPagingTextView) findViewById(R.id.pagingTextView);

        navi.setVisibility(View.VISIBLE);
        tool.setVisibility(View.VISIBLE);
        if (catalogSetting == null) {
            info.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
        } else {
            if (catalogSetting.getTextInfoUse()) {
                info.setVisibility(View.GONE);
                text.setItemVisibility(View.VISIBLE);
            } else {
                info.setVisibility(View.VISIBLE);
                text.setVisibility(View.GONE);
            }
        }
    }

    private void hideMenu() {
        CatalogNavigationView navi = (CatalogNavigationView) findViewById(R.id.navigationView);
        CatalogToolbarView tool = (CatalogToolbarView) findViewById(R.id.toolbarView);
        CatalogPagingInfoView info = (CatalogPagingInfoView) findViewById(R.id.pagingInfoView);
        CatalogPagingTextView text = (CatalogPagingTextView) findViewById(R.id.pagingTextView);

        navi.setVisibility(View.GONE);
        tool.setVisibility(View.GONE);
        if (catalogSetting == null) {
            info.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
        } else {
            if (catalogSetting.getTextInfoUse()) {
                info.setVisibility(View.GONE);
                text.setItemVisibility(View.GONE);
            } else {
                info.setVisibility(View.GONE);
                text.setVisibility(View.GONE);
            }
        }
    }

    private boolean isMenuVisible() {
        CatalogNavigationView navi = (CatalogNavigationView) findViewById(R.id.navigationView);
        return (navi.getVisibility() != View.GONE);
    }

    private void toggleMenu() {
        if (isMenuVisible()) {
            hideMenu();
        } else {
            viewMenu();
        }
    }

    private CatalogPagerEventPanel.OnCatalogPagerEventPanelListener onCatalogPagerEventPanelListener = new CatalogPagerEventPanel.OnCatalogPagerEventPanelListener() {
        @Override
        public void onSingleTap(MotionEvent e) {
            LogUtil.d(TAG, "onSingleTap");
            // メニューの表示/非表示を切り替える
            toggleMenu();
        }

        @Override
        public void onChangeScale() {
            LogUtil.d(TAG, "onChangeScale");
            // メニューを非表示にする
            hideMenu();
        }

        @Override
        public void onPageEdge() {
            // メニューを表示にする
            viewMenu();
        }
    };

    private CatalogPagerView.OnCatalogPagerViewListener onCatalogPagerViewListener = new CatalogPagerView.OnCatalogPagerViewListener() {
        @Override
        public void onPageSelected() {
            LogUtil.d(TAG, "onPageSelected");
            // ページ情報を更新する
            changePage();
        }
    };

    /**
     * 現在のページ構造
     *
     * @return
     */
    private SparseIntArray getCurrenStructure() {
        if (pageStructure == null) {
            return null;
        } else {
            return pageStructure.getStructure(pagerView.getCurrentItem());
        }
    }

    /**
     * 現在のページインデックス
     *
     * @return
     */
    private int getCurrentPageIndex() {
        SparseIntArray structure = getCurrenStructure();
        if (structure == null) {
            return 0;
        } else {
            int index = structure.get(0, -1);
            if (index == -1) {
                index = structure.get(1, -1);
            }
            return index;
        }
    }

    /**
     * 指定ページを表示する
     *
     * @param pageIndex
     */
    private void setCurrentPageIndex(int pageIndex) {
        // ページ構造からpageIndexに一致する物を探す
        ArrayList<SparseIntArray> structureList = pageStructure
                .getStructureList();
        int structureIndex = 0;
        for (int i = 0; i < structureList.size(); i++) {
            SparseIntArray structure = structureList.get(i);
            int index = structure.get(0, -1);
            if (index != -1 && index == pageIndex) {
                structureIndex = i;
                break;
            }
            index = structure.get(1, -1);
            if (index != -1 && index == pageIndex) {
                structureIndex = i;
                break;
            }
        }
        pagerView.setCurrentItem(structureIndex, false);
    }

    /**
     * 情報を現在のページで更新
     */
    private void changePage() {
        // ページングツールバー
        int current = pagerView.getCurrentItem();
        CatalogToolbarView toolbarView = (CatalogToolbarView) findViewById(R.id.toolbarView);
        CatalogPagingToolbarView bar = toolbarView.getPagingToolbar();
        bar.setSeekProgress(current);

        // ページング情報
        SparseIntArray structure = getCurrenStructure();
        if (structure != null) {
            changePagingInfo(structure);
        }

        // ページリンクボタン
        changePageLink();
    }

    /**
     * ページング情報を更新
     *
     * @param structure
     */
    private void changePagingInfo(SparseIntArray structure) {
        int page1 = structure.get(0, -1);
        String page1Title = null;
        String page1Text = null;
        if (page1 != -1) {
            PageListSetting pageSetting = new PageListSetting(
                    pageStructure.getCsv(page1));
            page1Title = pageSetting.getTitle();
            page1Text = pageSetting.getText();
        }
        int page2 = structure.get(1, -1);
        String page2Title = null;
        String page2Text = null;
        if (page2 != -1) {
            PageListSetting pageSetting = new PageListSetting(
                    pageStructure.getCsv(page2));
            page2Title = pageSetting.getTitle();
            page2Text = pageSetting.getText();
        }

        CatalogPagingInfoView info = (CatalogPagingInfoView) findViewById(R.id.pagingInfoView);
        info.setCurrentPage(page1, page1Title, page2, page2Title);

        int orientation = getResources().getConfiguration().orientation;
        CatalogPagingTextView text = (CatalogPagingTextView) findViewById(R.id.pagingTextView);
        text.setCurrentPage(orientation, page1, page1Text, page2, page2Text);
        if (page1Text == null && page2Text == null) {
            if (catalogSetting.getTextInfoPagingStyle() == 1) {
                text.setVisibility(View.INVISIBLE);
            }
        } else {
            if (catalogSetting.getTextInfoPagingStyle() == 1) {
                text.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 向きを変える
     */
    private void changeOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        if (!MainApplication.getInstance().isTabletDevice()) {
            // スマホのとき
            {
                // ページ情報の位置
                View pagingInfoWeiht1View = findViewById(R.id.pagingInfoWeiht1View);
                View pagingInfoWeiht2View = findViewById(R.id.pagingInfoWeiht2View);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0);
                switch (orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        // 縦のときは5分の2位置
                        params1.weight = 2.0f;
                        params2.weight = 5.0f;
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        // 横のときは中央位置
                        params1.weight = 2.0f;
                        params2.weight = 2.0f;
                        break;
                }
                pagingInfoWeiht1View.setLayoutParams(params1);
                pagingInfoWeiht2View.setLayoutParams(params2);
            }
            {
                // ページテキストの高さ
                CatalogPagingTextView text = (CatalogPagingTextView) findViewById(R.id.pagingTextView);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 0);
                switch (orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        // 縦のとき
                        params1.weight = 1.0f;
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        // 横のとき
                        params1.weight = 2.0f;
                        break;
                }
                text.setLayoutParams(params1);
            }
        }
        // ツールバーの向き
        CatalogToolbarView toolbarView = (CatalogToolbarView) findViewById(R.id.toolbarView);
        toolbarView.changeToolbar(orientation);
    }

    public static void startActivity(Activity activity, String catalogId, String catalogUrl, int startPage) {
        startActivity(activity, catalogId, catalogUrl, startPage, -1);
    }

    public static void startActivity(Activity activity, String catalogId, String catalogUrl, int startPage, int flag) {
        Intent intent = new Intent(activity, CatalogActivity.class);
        if (catalogId != null) intent.putExtra("catalog_id", catalogId);
        if (catalogUrl != null) intent.putExtra("catalog_url", catalogUrl);
        if (startPage > 0) intent.putExtra("start_page", startPage);
        if (flag > 0) intent.setFlags(flag);
        activity.startActivity(intent);
    }
}
