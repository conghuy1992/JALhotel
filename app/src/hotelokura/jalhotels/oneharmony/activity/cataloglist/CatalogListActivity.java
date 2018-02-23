package hotelokura.jalhotels.oneharmony.activity.cataloglist;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.ActivitySkeleton;
import hotelokura.jalhotels.oneharmony.activity.CatalogDickCachUtil;
import hotelokura.jalhotels.oneharmony.activity.catalog.CatalogActivity;
import hotelokura.jalhotels.oneharmony.activity.map.MapActivity;
import hotelokura.jalhotels.oneharmony.activity.news.NewsActivity;
import hotelokura.jalhotels.oneharmony.activity.web.WebViewActivity;
import hotelokura.jalhotels.oneharmony.analytics.CheckAction;
import hotelokura.jalhotels.oneharmony.cache.MemoryCache;
import hotelokura.jalhotels.oneharmony.net.DownloadManager;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.setting.BGImageSetting;
import hotelokura.jalhotels.oneharmony.setting.CatalogListSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.setting.LogoImageSetting;
import hotelokura.jalhotels.oneharmony.setting.LogoSetting;
import hotelokura.jalhotels.oneharmony.setting.LogoSettingArray;
import hotelokura.jalhotels.oneharmony.util.AdManager;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.util.NameUtil;
import hotelokura.jalhotels.oneharmony.view.ProgressIndicatorDialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CatalogListActivity extends ActivitySkeleton implements
		OnItemClickListener {
	static final String TAG = "CatalogListActivity";

    public static final String TOP_SCREEN = "top";
    protected String mGaScreenName = "";

    public static final int SCROLL_MODE_NO = 0;
    public static final int SCROLL_MODE_YES = 1;

	protected String mContentTitle;
	protected String[] mFilterTags;
	protected String mId;
	protected String catalogUrl;

    private AppSetting mSettings;

	private ProgressIndicatorDialog progress;

	private int lastOrientation;
	private boolean recreated;
	private boolean downloadImageNoThread = true;

	private CatalogListData listData;
	private CatalogListItemData itemData;
	protected BGImageSetting currentBackImageSetting;
	protected int currentBackImageTopLogoSize;
	protected int currentBackImageTopLogoCount;
	protected int currentBackImageBottomLogoSize;
	protected int currentBackImageBottomLogoCount;

	private CatalogListGridView gridView;
	private CatalogListAdapter gridAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onCreate: " + settingName());
		super.onCreate(savedInstanceState);

		MainApplication main = MainApplication.getInstance();
		if (main == null || main.getAppSetting() == null) {
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cataloglist);

        AdManager.startInterstitial(this);

		// 前画面から情報取得
        mFilterTags = getIntent().getStringArrayExtra("tags");
		if (mFilterTags != null) {
            mContentTitle = getIntent().getStringExtra("title");
            mId = getIntent().getStringExtra("id");
		} else {
            mContentTitle = null;
            mId = null;
        }
        // MainApplicationにカレントのid、tag、titleを保存
        MainApplication.getInstance().setCurrentCatalogListId(mId);
        MainApplication.getInstance().setCurrentCatalogListTitle(mContentTitle);
        MainApplication.getInstance().setCurrentCatalogListTags(mFilterTags);

        // GAの画面名はtagから取得
        this.mGaScreenName = (this.mId == null || this.mId.isEmpty()) ?
									 (this.mFilterTags != null) ?
									 this.mFilterTags[0] : "" :
										this.mId;


        // AppSettingを取得
        mSettings = MainApplication.getInstance().getAppSetting();
		catalogUrl = mSettings.getNetCataloglistURL();

		// カタログ一覧のデータ
		listData = settingListData();
		listData.setTags(mFilterTags);
		listData.setCatalogId(mId);
		listData.setCatalogUrl(catalogUrl);
		listData.setParentActivity(this);
		listData.setCallback(new CatalogListData.Callback() {
			@Override
			public void downloadNaviLogoImageCompleted(Bitmap bmp) {
				CatalogListNavigationView navigationView = (CatalogListNavigationView) findViewById(R.id.navigationView);
				navigationView.setTitleImageLogo(bmp);
			}

			@Override
			public void downloadCompleted() {
                viewGrid();
			}

            @Override
            public void downloadFailed() {
            }
        });

		// カタログ一覧のアイテムデータ
		itemData = MainApplication.getInstance().getItemData();
		if (itemData == null) {
			itemData = new CatalogListItemData(listData.settingName());
		} else {
			MainApplication.getInstance().setItemData(null);
		}
		itemData.setCatalogUrl(catalogUrl);
		itemData.setCallback(new CatalogListItemData.Callback() {
			@Override
			public void downloadCompleted(String filename) {
				gridView.viewImages();
			}
		});

		// グリッドの設定
		gridView = (CatalogListGridView) findViewById(R.id.gridView);
		gridView.setOnItemClickListener(this);
		gridView.setActionMode(listData.settingActionMode());
		gridView.setScrollMode(settingGridScrollMode());

		// ナビゲーションの設定
		CatalogListNavigationView navigationView = (CatalogListNavigationView) findViewById(R.id.navigationView);
		if (mFilterTags != null) {
			// カバーフローがある時はこの場で表示
			// 無かった時はデータ取得後に表示しないと、ステータスバーに一瞬重なってしまう
			settingNavigationView(navigationView);
		}
		// 戻るボタン
		navigationView.setBackButtionListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 背景の設定
		ViewGroup layout = (ViewGroup) findViewById(R.id.layout);
		settingBGLyout(layout);

		// 画面再作成時か判定
		boolean recreateMode = getIntent().getBooleanExtra("RecreateMode",
				false);
		if (recreateMode) {
			// 画面再作成時
			getIntent().removeExtra("RecreateMode");
			downloadImageNoThread = false;
		}
	}

	@Override
	protected void onStart() {
		LogUtil.d(TAG, "onStart " + settingName());
		super.onStart();
		if (recreated) {
			// 画面を再作成するので何もしない
			return;
		}

		// ロード中インジケータを表示
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

		// 設定ファイルをダウンロードするかの判定
		boolean isDownload = false;
		if (listData.getCatalogListCsvArray() == null
				|| CatalogDickCachUtil.chackCatalogListDownload(catalogUrl,
						listData.settingActionMode())) {
			// 初期表示、またはダウンロードから60分たっていた
			isDownload = true;
		}
		if (isDownload) {
			// 設定が無い
			progress.show();
			// キャッシュクリア
			recycleImages();
			listData.recycleImages();
			// 設定ダウンロード
			listData.downloadSetting();
		} else {
			// すでに設定がある
			if (!listData.isCompleted()) {
				// 背景画像が無い
				progress.show();
				listData.downloadImages();
			} else {
				CatalogListNavigationView navigationView = (CatalogListNavigationView) findViewById(R.id.navigationView);
				navigationView.setTitleImageLogo(listData
						.getNavigationLogoImage());
				viewGrid();
			}
		}
	}

	@Override
	protected void onResume() {
		LogUtil.d(TAG, "onResume: " + settingName());
		super.onResume();
		if (recreated) {
			// 画面を再作成するので何もしない
			return;
		}
	}

	@Override
	protected void onPause() {
		LogUtil.d(TAG, "onPause: " + settingName());
		super.onPause();
		if (recreated) {
			// 画面を再作成するので何もしない
			return;
		}
	}

	@Override
	protected void onStop() {
		LogUtil.d(TAG, "onStop: " + settingName());
		super.onStop();
		if (recreated) {
			// 画面を再作成するので何もしない
			return;
		}

		DownloadManager.getInstance().clear(settingName());
		recycleImages();
		listData.dismissDownloadAlertDialog();
		if (progress != null) {
			progress.dismiss();
			progress = null;
		}

		lastOrientation = getResources().getConfiguration().orientation;
	}

	@Override
	protected void onRestart() {
		LogUtil.d(TAG, "onRestart: " + settingName());
		super.onRestart();

		int orientation = getResources().getConfiguration().orientation;
		if (lastOrientation != orientation) {
			LogUtil.d(TAG, "orientation:" + lastOrientation + " != "
					+ orientation);
			// 画面の向きが変わった状態で戻ってきた
			// 画面を再起動する
			recreated = true;
			MainApplication.getInstance().setListData(listData);
			MainApplication.getInstance().setItemData(itemData);
			listData = null;
			itemData = null;

			Intent intent = getIntent();
			intent.putExtra("RecreateMode", true);
			startActivity(intent);
			finish();
			overridePendingTransition(0, 0);
		} else {
			recreated = false;
			downloadImageNoThread = false;
		}
	}

	@Override
	protected void onDestroy() {
		LogUtil.d(TAG, "onDestroy: " + settingName());
		super.onDestroy();
		if (recreated) {
			// 画面を再作成するので何もしない
			return;
		}

		// 背景画像の解放
		if (listData != null) {
			listData.recycleImages();
		}
	}

	@Override
	public void onLowMemory() {
		LogUtil.d(TAG, "onLowMemory: " + settingName());
		super.onLowMemory();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		LogUtil.d(TAG, "onConfigurationChanged: " + settingName());
		super.onConfigurationChanged(newConfig);

		changeGridColumns();
		changeBGImage();
	}

	protected String settingName() {
		return "cataloglist.csv";
	}

	protected CatalogListData settingListData() {
		CatalogListData data = MainApplication.getInstance().getListData();
		if (data != null) {
			MainApplication.getInstance().setListData(null);
		} else {
			data = new CatalogListData();
		}
		return data;
	}

	/**
	 * ナビゲーションバーの設定
	 *
	 * @param navigationView
	 */
	protected void settingNavigationView(
			final CatalogListNavigationView navigationView) {
		navigationView.setVisibility(View.VISIBLE);
		if (mFilterTags == null) {
			// カバーフロー無しの時は戻るボタンを非表示
			navigationView.setBackButtionVisibility(View.GONE);
		}

        mSettings = MainApplication.getInstance().getAppSetting();
		// 背景色
		navigationView.setBGColor(mSettings.getCataloglistTitleBGColor());
		// タイトル
		navigationView.setTitle(mSettings.getCataloglistTitle());
		if (mContentTitle != null && !mContentTitle.equals("")) {
			// カバーフローからの値を優先する
            navigationView.setTitle(mContentTitle);
		}
		// タイトル文字色
		navigationView.setTitleColor(mSettings.getCataloglistTitleTextColor());
	}

	/**
	 * 背景の設定(背景画像を除く)
	 *
	 * @param layout
	 */
	protected void settingBGLyout(ViewGroup layout) {
        mSettings = MainApplication.getInstance().getAppSetting();

		// 背景色
		layout.setBackgroundColor(mSettings.getCataloglistBGColor1());
	}

	/**
	 * グリッドのスクロールモード
	 *
	 * @return
	 */
	protected int settingGridScrollMode() {
		return SCROLL_MODE_YES;
	}

	/**
	 * グリッドアイテムの文字色
	 *
	 * @return
	 */
	protected Integer settingGridItemTextColor() {
        mSettings = MainApplication.getInstance().getAppSetting();
		return mSettings.getCataloglistTextColor();
	}

	/**
	 * アイテム画像のキャッシュ
	 *
	 * @return
	 */
	public MemoryCache<Bitmap> getMemoryCache() {
		if (itemData == null) {
			return null;
		}
		return itemData.getMemoryCache();
	}

	/**
	 * 画像のメモリ解放
	 */
	private void recycleImages() {
		// 画面に表示している物を非表示にする
		// タイトルロゴ
		CatalogListNavigationView navigationView = (CatalogListNavigationView) findViewById(R.id.navigationView);
		navigationView.clearImages();
		// 背景画像
		ImageView backImageView = (ImageView) findViewById(R.id.backImageView);
		ViewGroup backTopLogoLayout = (ViewGroup) findViewById(R.id.backTopLogoLayout);
		ViewGroup backBottomLogoLayout = (ViewGroup) findViewById(R.id.backBottomLogoLayout);
		for (int i = 0; i < backTopLogoLayout.getChildCount(); i++) {
			ImageView v = (ImageView) backTopLogoLayout.getChildAt(i);
			v.setImageDrawable(null);
		}
		backTopLogoLayout.removeAllViews();
		for (int i = 0; i < backBottomLogoLayout.getChildCount(); i++) {
			ImageView v = (ImageView) backBottomLogoLayout.getChildAt(i);
			v.setImageDrawable(null);
		}
		backTopLogoLayout.removeAllViews();
		backImageView.setImageDrawable(null);
		// グリッド
		gridView.clearImages();
		gridView.setAdapter(null);
		gridAdapter = null;

		// アイテム画像を解放
		if (itemData != null) {
			itemData.recycleImages();
		}
	}

	/**
	 * グリッドに表示開始
	 */
	private void viewGrid() {
		if (mFilterTags == null) {
			// カバーフローが無いときのナビゲーションバーはここで表示
			// ステータスバーに一瞬重なってしまう対策
			CatalogListNavigationView navigationView = (CatalogListNavigationView) findViewById(R.id.navigationView);
			settingNavigationView(navigationView);
		}

		// グリッドアダプターの設定
		gridAdapter = new CatalogListAdapter(this,
				listData.getCatalogListCsvArray(), gridView);
		gridAdapter.setTextColor(settingGridItemTextColor());
		gridView.setAdapter(gridAdapter);

		changeGridColumns();
		changeBGImage();

		// アイテム画像のダウンロード
		itemData.setDownloadImageNoThread(downloadImageNoThread);
		itemData.downloadItemImages(listData.getCatalogListCsvArray());

		if (progress != null) {
			progress.dismiss();
			progress = null;
		}
	}

	/**
	 * グリッドの列数を変更
	 */
	private void changeGridColumns() {
		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			gridView.setNumColumns(2);
			gridView.setHeightSpacing(MainApplication.getInstance().dp2px(
                    (int)(MainApplication.getInstance().getInch() * 6)));
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			gridView.setNumColumns(3);
			gridView.setHeightSpacing(MainApplication.getInstance().dp2px(
                    (int)(MainApplication.getInstance().getInch() * 2)));
			break;
		}
	}

	private void changeBGImageMatrix() {
		if (listData == null || listData.getMemoryCache() == null) {
			// 画面がすでに停止している
			return;
		}
		if (currentBackImageSetting == null) {
			return;
		}
		String imageName = currentBackImageSetting.getImageName();
		if (imageName == null) {
			return;
		}
		Bitmap bmp = listData.getMemoryCache().get(imageName);
		if (bmp == null) {
			return;
		}
		LogUtil.d(TAG, "changeBGImageMatrix: " + settingName());
		ImageView backImageView = (ImageView) findViewById(R.id.backImageView);
		float viewWidth = backImageView.getWidth();
		float viewHeight = backImageView.getHeight();
		float bmpWidth = 0;
		float bmpHeight = 0;

		// 拡大率を求める 縦または横一杯まで拡大し、オーバーする部分ははみ出すようにする。
		float scale = viewWidth / (float) bmp.getWidth();
		bmpHeight = (float) bmp.getHeight() * scale;
		if (bmpHeight < viewHeight) {
			// 横幅の拡大率では画面サイズに不足している
			scale = viewHeight / (float) bmp.getHeight();
			bmpHeight = (float) bmp.getHeight() * scale;
		}
		bmpWidth = (float) bmp.getWidth() * scale;

		// センタリング位置を求める
		float diffX = viewWidth - bmpWidth;
		float diffY = viewHeight - bmpHeight;
		float addX = diffX / 2;
		float addY = diffY / 2;

		// Matrixに設定
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		matrix.postTranslate(addX, addY);

		// Matrixで画像を表示
		backImageView.setScaleType(ScaleType.MATRIX);
		backImageView.setImageMatrix(matrix);
		backImageView.setImageBitmap(bmp);
		backImageView.invalidate();
	}

	protected void changeGridPadding() {
	}

	/**
	 * 背景画像の表示を変更
	 */
	protected void changeBGImage() {
		// 画面に表示している背景画像を一旦解除
		ImageView backImageView = (ImageView) findViewById(R.id.backImageView);
		ViewGroup backTopLogoLayout = (ViewGroup) findViewById(R.id.backTopLogoLayout);
		ViewGroup backBottomLogoLayout = (ViewGroup) findViewById(R.id.backBottomLogoLayout);
		for (int i = 0; i < backTopLogoLayout.getChildCount(); i++) {
			ImageView v = (ImageView) backTopLogoLayout.getChildAt(i);
			v.setImageDrawable(null);
		}
		backTopLogoLayout.removeAllViews();
		backTopLogoLayout.setVisibility(View.INVISIBLE);
		for (int i = 0; i < backBottomLogoLayout.getChildCount(); i++) {
			ImageView v = (ImageView) backBottomLogoLayout.getChildAt(i);
			v.setImageDrawable(null);
		}
		backBottomLogoLayout.removeAllViews();
		backBottomLogoLayout.setVisibility(View.INVISIBLE);
		backImageView.setImageDrawable(null);
		backImageView.setScaleType(ScaleType.CENTER_CROP);

		// 読み込んだ背景画像を端末の向きにより、選択して表示。
		int topDp = 0; // 上部ロゴの高さ(単位dp)
		int bottomDp = 0; // 下部ロゴの高さ
		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			currentBackImageSetting = listData.getBackImageSetting();
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			currentBackImageSetting = listData.getBackImageLandscapeSetting();
			break;
		}
		changeBGImage(currentBackImageSetting);

        if (currentBackImageSetting == null) {
            finish();
            return;
        }

		if (currentBackImageSetting.getLogoSetting() == null) {
			backTopLogoLayout.setVisibility(View.GONE);
			backBottomLogoLayout.setVisibility(View.GONE);
		} else {
			topDp = currentBackImageSetting.getLogoSetting().getTopLogoHeight();
			bottomDp = currentBackImageSetting.getLogoSetting()
					.getBottomLogoHeight();

			if (topDp == 0) {
				backTopLogoLayout.setVisibility(View.GONE);
			} else {
				RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, MainApplication
								.getInstance().dp2px(topDp));
				topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				backTopLogoLayout.setLayoutParams(topParams);

				if (currentBackImageTopLogoSize == currentBackImageTopLogoCount) {
					backTopLogoLayout.setVisibility(View.VISIBLE);
				} else {
					if (gridView.getScrollMode() == SCROLL_MODE_YES) {
						backTopLogoLayout.setVisibility(View.GONE);
					}
				}
			}
			if (bottomDp == 0) {
				backBottomLogoLayout.setVisibility(View.GONE);
			} else {
				RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, MainApplication
								.getInstance().dp2px(bottomDp));
				bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				backBottomLogoLayout.setLayoutParams(bottomParams);

				if (currentBackImageBottomLogoSize == currentBackImageBottomLogoCount) {
					backBottomLogoLayout.setVisibility(View.VISIBLE);
				} else {
					if (gridView.getScrollMode() == SCROLL_MODE_YES) {
						backBottomLogoLayout.setVisibility(View.GONE);
					}
				}
			}
		}
		if (settingGridScrollMode() == SCROLL_MODE_NO) {
			changeGridPadding();
		}
	}

	private void changeBGImage(BGImageSetting setting) {
		if (listData == null || listData.getMemoryCache() == null) {
			// 画面がすでに停止している
			return;
		}
		if (setting == null) {
			return;
		}
		String imageName = setting.getImageName();
		if (imageName == null) {
			return;
		}

		// 背景画像の変更
		Bitmap bmp = listData.getMemoryCache().get(imageName);
		final ImageView backImageView = (ImageView) findViewById(R.id.backImageView);
		backImageView.setImageBitmap(bmp);
		// 背景画像の拡大処理を設定
        ViewTreeObserver observer = backImageView.getViewTreeObserver();
        if(observer != null) {
            observer.addOnGlobalLayoutListener(
                    new OnGlobalLayoutListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onGlobalLayout() {
                            LogUtil.d(TAG, "onGlobalLayout: " + settingName());

                            ViewGroup mainLayout = (ViewGroup) findViewById(R.id.mainLayout);
                            int viewWidth = mainLayout.getWidth();
                            int dispWidth = 0;

                            Display display = getWindowManager()
                                    .getDefaultDisplay();
                            int sdk = android.os.Build.VERSION.SDK_INT;
                            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
                                dispWidth = display.getWidth();
                            } else {
                                Point size = new Point();
                                display.getSize(size);
                                dispWidth = size.x;
                            }

                            if (viewWidth == dispWidth) {
                                // 背景画像の拡大処理
                                changeBGImageMatrix();
                                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    backImageView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                } else {
                                    backImageView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                }
                            }
                        }
                    });
        }

		// 背景ロゴの変更
		currentBackImageTopLogoSize = 0;
		currentBackImageTopLogoCount = 0;
		currentBackImageBottomLogoSize = 0;
		currentBackImageBottomLogoCount = 0;
		changeBGImage(NameUtil.removeFileExtension(imageName),
				setting.getLogoSetting());
	}

	private void changeBGImage(String baseImageName, LogoImageSetting setting) {
		if (setting == null) {
			return;
		}
		currentBackImageTopLogoSize = setting.getTopLogoSize();
		LogoSettingArray top = setting.getTopLogo();
		currentBackImageTopLogoCount = changeBGImage(baseImageName, top,
				R.id.backTopLogoLayout);

		currentBackImageBottomLogoSize = setting.getBottomLogoSize();
		LogoSettingArray bottom = setting.getBottomLogo();
		currentBackImageBottomLogoCount = changeBGImage(baseImageName, bottom,
				R.id.backBottomLogoLayout);
	}

	private int changeBGImage(String baseImageName, LogoSettingArray array,
			int resid) {
		if (array == null) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < array.getSize(); i++) {
			LogoSetting setting = array.getLogoSetting(i);
			boolean ret = changeBGImage(baseImageName, setting, resid);
			if (ret) {
				count++;
			}
		}
		return count;
	}

	private boolean changeBGImage(String baseImageName, LogoSetting setting,
			int resid) {
		if (setting == null) {
			return true;
		}
		String imageName = setting.getLogoImage(baseImageName);
		if (imageName == null) {
			return true;
		}
		ImageView view = new ImageView(this);

		Bitmap bmp = listData.getMemoryCache().get(imageName);
		switch (setting.getScaleType()) {
		case 0: // CENTER
			view.setScaleType(ScaleType.CENTER);
			break;
		case 1: // CENTER_CROP
			view.setScaleType(ScaleType.CENTER_CROP);
			break;
		case 2: // FIT_CENTER
			view.setScaleType(ScaleType.FIT_CENTER);
			break;
		case 3: // FIT_END
			view.setScaleType(ScaleType.FIT_END);
			break;
		case 4: // FIT_START
			view.setScaleType(ScaleType.FIT_START);
			break;
		case 5: // FIT_XY
			view.setScaleType(ScaleType.FIT_XY);
			break;
		}
		view.setImageBitmap(bmp);

		ViewGroup logoLayout = (ViewGroup) findViewById(resid);
		logoLayout.addView(view);

		return (bmp != null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//		Log.e(TAG,"pos:"+position);
		CatalogListItemView view = (CatalogListItemView) v;
		CsvLine line = view.getCsvLine();

		CatalogListSetting setting = new CatalogListSetting(line);

        MainApplication app = (MainApplication) this.getApplication();
        CheckAction ca = app.getCheckAction();
        ca.sendAppTrackFromMenu(this.mGaScreenName + "_menu",
                line.getLineNum(),
                setting.getMode(),
                setting.getID(),
                setting.getUrl(),
                setting.getContentTitle());

        switch (setting.getMode()) {
		case CatalogListSetting.MODE_URL_SCHEMA: {
			// 次画面起動 外部アプリ起動
			Uri uri = Uri.parse(setting.getUrlUnEscape());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			break;
		}
		case CatalogListSetting.MODE_URL: {
            String urlUnEscaped = setting.getUrlUnEscape();
            if(urlUnEscaped == null || urlUnEscaped.isEmpty() || urlUnEscaped.equals("about:blank")) {
                // 移動する意味の無いURLが指定されていた場合は何もしない
                return;
            }
			// 次画面起動 内部ブラウザ起動
            if ((urlUnEscaped.startsWith("http:") || urlUnEscaped.startsWith("https:"))
                    && !urlUnEscaped.startsWith("https://play.google.com/") ) {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("StartUrl", urlUnEscaped);
                intent.putExtra("title", setting.getContentTitle());
                startActivity(intent);
                break;
            } else{
                //Google Play は外部ブラウザで表示
                Uri uri = Uri.parse(urlUnEscaped);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            }
		}
		case CatalogListSetting.MODE_CATALOG: {
			// 次画面起動 カタログ画面遷移
            CatalogActivity.startActivity(this, setting.getID(), setting.getUrl(), 0);
			break;
		}
		case CatalogListSetting.MODE_MOVIE: {
			// 次画面起動 動画<予定>
			break;
		}
		case CatalogListSetting.MODE_MAP: {
			// 次画面起動 地図
            MapActivity.startActivity(this, setting.getID(), false);
			break;
		}
        case CatalogListSetting.MODE_MAP_LIST: {
            // 次画面起動 地図
            MapActivity.startActivity(this, setting.getID(), true);
            break;
        }
		case CatalogListSetting.MODE_CATALOG_LIST: {
			// 次画面起動 カタログ一覧
			startActivity(this, setting.getTags(), setting.getContentTitle(), setting.getID());
			break;
		}
        case CatalogListSetting.MODE_NEWS: {
            // 次画面起動 ニュース
            NewsActivity.startActivity(this, mSettings.getNewsURL(), MainApplication.getInstance().getAppId());
            break;
        }
		default:
			break;
		}
	}

    public static void startActivity(Activity activity, String[] tags, String title, String id) {
        startActivity(activity, tags, title, id, -1);
    }

    public static void startActivity(Activity activity, String[] tags, String title, String id, int flag) {
        Intent intent = new Intent(activity, CatalogListActivity.class);
        if (tags != null) intent.putExtra("tags", tags);
        if (id != null) intent.putExtra("id", id);
        if (title != null) intent.putExtra("title", title);
        if (flag > 0) intent.setFlags(flag);
        activity.startActivity(intent);
    }
}
