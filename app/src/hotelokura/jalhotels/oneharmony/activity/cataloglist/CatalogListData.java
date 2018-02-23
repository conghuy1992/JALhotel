package hotelokura.jalhotels.oneharmony.activity.cataloglist;

import java.io.IOException;
import java.io.InputStream;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.CatalogDickCachUtil;
import hotelokura.jalhotels.oneharmony.cache.DiskCache;
import hotelokura.jalhotels.oneharmony.cache.MemoryCache;
import hotelokura.jalhotels.oneharmony.cache.MemoryCache.OnMemoryCacheRemoveListener;
import hotelokura.jalhotels.oneharmony.net.AsyncCallback;
import hotelokura.jalhotels.oneharmony.net.AsyncResult;
import hotelokura.jalhotels.oneharmony.net.DownloadManager;
import hotelokura.jalhotels.oneharmony.setting.AppSetting;
import hotelokura.jalhotels.oneharmony.setting.BGImageSetting;
import hotelokura.jalhotels.oneharmony.setting.CatalogListSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.setting.CsvReader;
import hotelokura.jalhotels.oneharmony.setting.LogoImageSetting;
import hotelokura.jalhotels.oneharmony.setting.LogoSetting;
import hotelokura.jalhotels.oneharmony.setting.LogoSettingArray;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.util.NameUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;

public class CatalogListData {
	static final String TAG = "CatalogListData";

	public static interface Callback {
		public void downloadNaviLogoImageCompleted(Bitmap bmp);

		public void downloadCompleted();

        public void downloadFailed();
	}

	public static enum ActionMode {
		CoverFlow, CatalogList
	}

	private Activity parentActivity;
	private Callback callback;

	protected String[] mTags;
	protected String catalogId;
	protected String catalogUrl;

	private AlertDialog downloadAlertDialog;

	protected boolean downloadCsvCompleted = false;
	private int downloadBackImageCount;
	private boolean downloadBackImageCountUp;
	private boolean downloadingBackImage = true;

	private MemoryCache<Bitmap> memoryCache;

	protected BGImageSetting backImageSetting;
	protected BGImageSetting backImageLandscapeSetting;

	protected String catalogListLastMod;
	private CsvArray catalogListCsvArray;

	public CatalogListData() {
	}

	public void setParentActivity(Activity parentActivity) {
		this.parentActivity = parentActivity;
	}

	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public String[] getTag() {
		return mTags;
	}

	public void setTags(String[] tags) {
		this.mTags = tags;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getCatalogUrl() {
		return catalogUrl;
	}

	public void setCatalogUrl(String catalogUrl) {
		this.catalogUrl = catalogUrl;
	}

	public MemoryCache<Bitmap> getMemoryCache() {
		return memoryCache;
	}

	public CsvArray getCatalogListCsvArray() {
		return catalogListCsvArray;
	}

	public BGImageSetting getBackImageSetting() {
		return backImageSetting;
	}

	public BGImageSetting getBackImageLandscapeSetting() {
		return backImageLandscapeSetting;
	}

	public Bitmap getNavigationLogoImage() {
		final String logoImageName = settingNavigationLogoImage(catalogId);
		if (logoImageName != null) {
			if (memoryCache != null && memoryCache.containsKey(logoImageName)) {
				Bitmap bmp = memoryCache.get(logoImageName);
				return bmp;
			}
		}
		return null;
	}

	/**
	 * 動作モード
	 * 
	 * @return
	 */
	public ActionMode settingActionMode() {
		return ActionMode.CatalogList;
	}

	/**
	 * SCVファイル名
	 * 
	 * @return
	 */
	public String settingName() {
		return "cataloglist.csv";
	}

	/**
	 * タイトルロゴの名前
	 * 
	 * @param catalogId
	 * @return
	 */
	public String settingNavigationLogoImage(String catalogId) {
		AppSetting appSetting = MainApplication.getInstance().getAppSetting();
		return appSetting.getCataloglistTitleIcon(catalogId);
	}

	/**
	 * 背景画像の名前
	 * 
	 * @param id
	 * @param orientation
	 * @return
	 */
	public BGImageSetting settingBgImage(String id, int orientation) {
		AppSetting appSetting = MainApplication.getInstance().getAppSetting();
		BGImageSetting setting = null;
		switch (orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			setting = appSetting.getCataloglistBGImageDpi(id);
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			setting = appSetting.getCataloglistBGImageLandscapeDpi(id);
			break;
		}
		return setting;
	}

	public void dismissDownloadAlertDialog() {
		if (downloadAlertDialog != null) {
			downloadAlertDialog.dismiss();
			downloadAlertDialog = null;
		}
	}

	protected void showDownloadAlertDialog(AsyncResult<?> result) {
		if (downloadAlertDialog != null) {
			downloadAlertDialog.dismiss();
			downloadAlertDialog = null;
		}
		if (parentActivity == null) {
			return;
		}

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				parentActivity);
		alertDialogBuilder.setTitle(R.string.err_title);
		alertDialogBuilder.setMessage(result.getMessage());
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setPositiveButton(R.string.err_button_retry,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 再ダウンロード
						dialog.dismiss();
						downloadAlertDialog = null;
						downloadSetting();
					}
				});
		int negativId = (settingActionMode() == ActionMode.CoverFlow) ? R.string.err_button_finish
				: R.string.cancel;
		alertDialogBuilder.setNegativeButton(negativId,
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
				// 前画面に戻る(カバーフローのときは終了)
				downloadAlertDialog = null;
				parentActivity.finish();
			}
		});
		downloadAlertDialog = alertDialogBuilder.create();
		downloadAlertDialog.show();
	}

	public void createCacheToSetting() {
		DiskCache disk = MainApplication.getInstance().getDickCache();
		InputStream is = disk.get(catalogUrl, settingName());
		if (is != null) {
			CsvArray csvArray = CsvReader.read(is);
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// catalogListCsvArrayの作成
			downloadSettingSuccess(csvArray);

			downloadCsvCompleted = true;
		}
	}

	/**
	 * 自身の設定ファイルをダウンロードする<br>
	 * その後、背景画像などをダウンロードする
	 */
	public void downloadSetting() {
		downloadCsvCompleted = false;
		downloadBackImageCount = 0;
		downloadBackImageCountUp = true;
		downloadingBackImage = true;

		// 自身の設定ファイルをダウンロード
		DownloadManager.getInstance().offerCsv(settingName(), 0, catalogUrl,
				settingName(), new AsyncCallback<CsvArray>() {
					@Override
					public void onSuccess(AsyncResult<CsvArray> result) {
						CsvArray csvArray = result.getContent();
						catalogListLastMod = result.getLastModified();

						// ダウンロード時間を記憶
						CatalogDickCachUtil.saveCatalogListDownload(catalogUrl,
								settingActionMode());

						// キャッシュクリアの確認
						CatalogDickCachUtil.chackCatalogListSetting(catalogUrl,
								settingActionMode(), catalogListLastMod);

						// catalogListCsvArrayの作成
						downloadSettingSuccess(csvArray);

						downloadCsvCompleted = true;
						downloadImages();

                        //地図表示がある場合のみstorlist.csvを取得
                        for(CsvLine csvLine : catalogListCsvArray) {
                            LogUtil.d(TAG, "mode = " + csvLine.getInteger(0, -1));
                            if (CatalogListSetting.MODE_MAP == csvLine.getInteger(0,-1) ||
                                    CatalogListSetting.MODE_MAP_LIST == csvLine.getInteger(0,-1)) {
                                LogUtil.d(TAG,"Use Map!!");
                                downloadStoreListSetting();
                                break;
                            }
                        }
					}

					@Override
					public void onFailed(AsyncResult<CsvArray> result) {
						catalogListLastMod = null;
						MainApplication mainApp = MainApplication.getInstance();
						if (settingActionMode() == ActionMode.CoverFlow) {
							mainApp.setCoverflowCsvArray(catalogListCsvArray);
						} else {
							mainApp.setCataloglistCsvArray(null);
						}

						// エラーダイアログ表示
						showDownloadAlertDialog(result);
					}
				});
	}

	/**
	 * catalogListCsvArray変数の作成
	 * 
	 * @param csvArray
	 */
	protected void downloadSettingSuccess(CsvArray csvArray) {
		// MainApplicationに保存
		if (settingActionMode() == ActionMode.CoverFlow) {
			MainApplication.getInstance().setCoverflowCsvArray(csvArray);
		} else {
			MainApplication.getInstance().setCataloglistCsvArray(csvArray);
		}

		// 無効データ削除
		catalogListCsvArray = CatalogListSetting.removeInvalid(csvArray);
		catalogListCsvArray = settingCsvArray(catalogListCsvArray);
	}

	/**
	 * 読み込んだCSVの加工
	 * 
	 * @param csvArray
	 * @return
	 */
	protected CsvArray settingCsvArray(CsvArray csvArray) {
		// カバーフローからのカタログリスト時は、タグで絞り込み
		return CatalogListSetting.findTag(csvArray, mTags);
	}

	/**
	 * 画像をダウンロード
	 */
	public void downloadImages() {
		if (memoryCache == null) {
			// キャッシュの設定
			memoryCache = new MemoryCache<Bitmap>();
			memoryCache
					.setOnMemoryCacheRemoveListener(new OnMemoryCacheRemoveListener<Bitmap>() {
						@Override
						public boolean onRemoveItem(String key, Bitmap item) {
							if (item != null) {
								item.recycle();
							}
							return true;
						}
					});
		}

		// タイトルロゴのダウンロード
		final String logoImageName = settingNavigationLogoImage(catalogId);
		if (logoImageName == null) {
			if (callback != null) {
				callback.downloadNaviLogoImageCompleted(null);
			}
		} else {
			if (memoryCache != null && memoryCache.containsKey(logoImageName)) {
				Bitmap bmp = memoryCache.get(logoImageName);
				if (callback != null) {
					callback.downloadNaviLogoImageCompleted(bmp);
				}
			} else {
				DownloadManager.getInstance().offerImage(settingName(), 0,
						catalogUrl, logoImageName, new AsyncCallback<Bitmap>() {
							@Override
							public void onSuccess(AsyncResult<Bitmap> result) {
								Bitmap bmp = result.getContent();
								if (memoryCache == null) {
									// 画面がすでに停止している
									bmp.recycle();
								} else {
									bmp = memoryCache.put(logoImageName, bmp);
									if (callback != null) {
										callback.downloadNaviLogoImageCompleted(bmp);
									}
								}
							}

							@Override
							public void onFailed(AsyncResult<Bitmap> result) {
								if (callback != null) {
									callback.downloadNaviLogoImageCompleted(null);
								}
							}
						});
			}
		}

		// 背景画像のダウンロード
		downloadBackImageCountUp = true;
		downloadBackImageCount = 0;
		downloadingBackImage = true;

		// 背景画像(縦)のダウンロード
		backImageSetting = settingBgImage(catalogId,
				Configuration.ORIENTATION_PORTRAIT);
		boolean down1 = downloadBackImages(backImageSetting);

		// 背景画像(横)のダウンロード
		backImageLandscapeSetting = settingBgImage(catalogId,
				Configuration.ORIENTATION_LANDSCAPE);
		boolean down2 = downloadBackImages(backImageLandscapeSetting);

		// ダウンロード数のカウント終了
		downloadBackImageCountUp = false;

		if (!down1 && !down2 || downloadBackImageCount <= 0) {
			// 背景画像が１つも無かった。または全てキャッシュから。
			downloadingBackImage = false;
			checkCompleted();
		}
	}

	private boolean downloadBackImages(BGImageSetting setting) {
		if (setting == null) {
			return false;
		}
		final String imageName = setting.getImageName();
		if (imageName == null) {
			return false;
		}
		// 背景のダウンロード
		downloadBackImageCount++;
		if (memoryCache != null && memoryCache.containsKey(imageName)) {
			downloadBackImageCount--;
			checkCompleted();
		} else {
			DownloadManager.getInstance().offerImage(settingName(), 0,
					catalogUrl, imageName, new AsyncCallback<Bitmap>() {
						@Override
						public void onSuccess(AsyncResult<Bitmap> result) {
							Bitmap bmp = result.getContent();
							if (memoryCache == null) {
								// 画面がすでに停止している
								bmp.recycle();
							} else {
								bmp = memoryCache.put(imageName, bmp);
							}
							downloadBackImageCount--;
							checkCompleted();
						}

						@Override
						public void onFailed(AsyncResult<Bitmap> result) {
							downloadBackImageCount--;
							checkCompleted();
						}
					});
		}
		// 背景ロゴのダウンロード
		downloadBackLogoImages(NameUtil.removeFileExtension(imageName),
				setting.getLogoSetting());
		return true;
	}

	private void downloadBackLogoImages(String baseImageName,
			LogoImageSetting setting) {
		if (setting == null) {
			return;
		}
		LogoSettingArray top = setting.getTopLogo();
		downloadBackLogoImages(baseImageName, top);

		LogoSettingArray bottom = setting.getBottomLogo();
		downloadBackLogoImages(baseImageName, bottom);
	}

	private void downloadBackLogoImages(String baseImageName,
			LogoSettingArray array) {
		if (array == null) {
			return;
		}

		for (int i = 0; i < array.getSize(); i++) {
			LogoSetting setting = array.getLogoSetting(i);
			downloadBackLogoImage(baseImageName, setting);
		}
	}

	private void downloadBackLogoImage(String baseImageName, LogoSetting setting) {
		if (setting == null) {
			return;
		}
		final String imageName = setting.getLogoImage(baseImageName);
		if (imageName == null) {
			return;
		}
		downloadBackImageCount++;
		if (memoryCache != null && memoryCache.containsKey(imageName)) {
			downloadBackImageCount--;
			checkCompleted();
		} else {
			DownloadManager.getInstance().offerImage(settingName(), 0,
					catalogUrl, imageName, new AsyncCallback<Bitmap>() {
						@Override
						public void onSuccess(AsyncResult<Bitmap> result) {
							Bitmap bmp = result.getContent();
							if (memoryCache == null) {
								// 画面がすでに停止している
								bmp.recycle();
							} else {
								bmp = memoryCache.put(imageName, bmp);
							}
							downloadBackImageCount--;
							checkCompleted();
						}

						@Override
						public void onFailed(AsyncResult<Bitmap> result) {
							downloadBackImageCount--;
							checkCompleted();
						}
					});
		}
	}

	public boolean isCompleted() {
		if (!downloadCsvCompleted)
			// 設定ファイルのダウンロードが終わっていない
			return false;
		if (downloadBackImageCountUp)
			// 背景画像のダウンロード設定中
			return false;
		if (!downloadingBackImage || downloadBackImageCount <= 0) {
			// 背景画像のダウンロードが完了
			return true;
		}
		return false;
	}

	protected void checkCompleted() {
		if (isCompleted()) {
			// 背景画像のダウンロードが完了
			if (callback != null) {
				callback.downloadCompleted();
			}
		}
	}

	public void clearDownloadOffer() {
		DownloadManager.getInstance().clear(settingName());
	}

	/**
	 * 画像のメモリ解放
	 */
	public void recycleImages() {
		// ビットマップを解放
		if (memoryCache != null) {
			memoryCache.removeAll();
			memoryCache = null;
		}
		System.gc();
	}

    /**
     * 地図設定ファイルをダウンロードする<br>
     */
    protected void downloadStoreListSetting() {
        final String SETTING_NAME = "storelist.csv";

        // 自身の設定ファイルをダウンロード
        DownloadManager.getInstance().offerCsv(SETTING_NAME, 0, catalogUrl,
                SETTING_NAME, new AsyncCallback<CsvArray>() {
            @Override
            public void onSuccess(AsyncResult<CsvArray> result) {
                CsvArray csvArray = result.getContent();

                // MainApplicationに保存
                MainApplication.getInstance().setStorelistCsvArray(csvArray);

                //カタログリスト設定をダウンロード
                if (settingName().equals("coverflow.csv")) {
                    downloadCatalogSettingForMap();
                }
            }

            @Override
            public void onFailed(AsyncResult<CsvArray> result) {
                MainApplication.getInstance().setStorelistCsvArray(new CsvArray());
                LogUtil.e(TAG,"Getting Stor List Fail!!");
            }
        });
    }

    /**
     * 地図用にのカタログ設定ファイルをダウンロードする<br>
     */
    private void downloadCatalogSettingForMap() {
        final String SETTING_NAME = "cataloglist.csv";

        // 自身の設定ファイルをダウンロード
        DownloadManager.getInstance().offerCsv(SETTING_NAME, 0, catalogUrl,
                SETTING_NAME, new AsyncCallback<CsvArray>() {
            @Override
            public void onSuccess(AsyncResult<CsvArray> result) {
                CsvArray csvArray = result.getContent();
                catalogListLastMod = result.getLastModified();

                // キャッシュクリアの確認
                CatalogDickCachUtil.chackCatalogListSetting(catalogUrl,
                        ActionMode.CatalogList, catalogListLastMod);

                // MainApplicationに保存
                MainApplication.getInstance()
                        .setCataloglistCsvArray(csvArray);

            }

            @Override
            public void onFailed(AsyncResult<CsvArray> result) {
                catalogListLastMod = null;
                MainApplication mainApp = MainApplication.getInstance();
                mainApp.setCataloglistCsvArray(null);
                LogUtil.e(TAG, "downloadCatalogSettingForMap failed");
            }
        });
    }

    /**
     * WebTop用にファイルをダウンロードする<br>
     *
     * セカンドメニューの場合
     * ・cataloglist.csv
     * ・タイトルロゴ
     */
    public void downloadSettingForWebTop() {
        final String SETTING_NAME = "cataloglist.csv";
        downloadCsvCompleted = false;

        // 自身の設定ファイルをダウンロード
        DownloadManager.getInstance().offerCsv(SETTING_NAME, 0, catalogUrl, SETTING_NAME
                , new AsyncCallback<CsvArray>() {
            @Override
            public void onSuccess(AsyncResult<CsvArray> result) {
                CsvArray csvArray = result.getContent();
                catalogListLastMod = result.getLastModified();

                // ダウンロード時間を記憶
                CatalogDickCachUtil.saveCatalogListDownload(catalogUrl,
                        settingActionMode());

                // キャッシュクリアの確認
                CatalogDickCachUtil.chackCatalogListSetting(catalogUrl,
                        settingActionMode(), catalogListLastMod);

                // catalogListCsvArrayの作成
                downloadSettingSuccess(csvArray);

                // catalogListCsvArrayの作成
                MainApplication.getInstance().setCataloglistCsvArray(csvArray);

                downloadCsvCompleted = true;
                checkCompleted();

                //タイトルロゴを取得
                downloadTitleLogo();
            }

            @Override
            public void onFailed(AsyncResult<CsvArray> result) {
                catalogListLastMod = null;
                MainApplication mainApp = MainApplication.getInstance();
                mainApp.setCataloglistCsvArray(null);
                callback.downloadFailed();
            }
        });

    }

    protected void downloadTitleLogo() {
        if (memoryCache == null) {
            // キャッシュの設定
            memoryCache = new MemoryCache<Bitmap>();
            memoryCache
                    .setOnMemoryCacheRemoveListener(new OnMemoryCacheRemoveListener<Bitmap>() {
                        @Override
                        public boolean onRemoveItem(String key, Bitmap item) {
                            if (item != null) {
                                item.recycle();
                            }
                            return true;
                        }
                    });
        }

        // タイトルロゴのダウンロード
        final String logoImageName = settingNavigationLogoImage(catalogId);
        if (logoImageName == null) {
            if (callback != null) {
                callback.downloadNaviLogoImageCompleted(null);
            }
        } else {
            if (memoryCache != null && memoryCache.containsKey(logoImageName)) {
                Bitmap bmp = memoryCache.get(logoImageName);
                if (callback != null) {
                    callback.downloadNaviLogoImageCompleted(bmp);
                }
            } else {
                DownloadManager.getInstance().offerImage(settingName(), 0,
                        catalogUrl, logoImageName, new AsyncCallback<Bitmap>() {
                    @Override
                    public void onSuccess(AsyncResult<Bitmap> result) {
                        Bitmap bmp = result.getContent();
                        if (memoryCache == null) {
                            // 画面がすでに停止している
                            bmp.recycle();
                        } else {
                            bmp = memoryCache.put(logoImageName, bmp);
                            if (callback != null) {
                                callback.downloadNaviLogoImageCompleted(bmp);
                            }
                        }
                    }

                    @Override
                    public void onFailed(AsyncResult<Bitmap> result) {
                        if (callback != null) {
                            callback.downloadNaviLogoImageCompleted(null);
                        }
                    }
                });
            }
        }
    }
}
