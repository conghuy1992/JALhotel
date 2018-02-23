package hotelokura.jalhotels.oneharmony.activity.catalog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.cache.MemoryCache;
import hotelokura.jalhotels.oneharmony.net.AsyncCallback;
import hotelokura.jalhotels.oneharmony.net.AsyncResult;
import hotelokura.jalhotels.oneharmony.net.DownloadManager;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.setting.PageListSetting;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

public class CatalogPage {
	static final String TAG = "CatalogPage";
	static int DRAW_MODE = 1;

	public static enum Align {
		AlignCenter, AlignRight, AlignLeft
	};

	private CatalogPagerFragment parentPagerFragment;
	private CatalogPageView parentPageView;

	private PageListSetting setting = new PageListSetting();
	private CatalogPageImageInfo imageInfo = new CatalogPageImageInfo();
	private String downloadTag;

	private boolean busy = false;

	private Align align = Align.AlignCenter;
	private RectF rectangle = null;
	private int downloadLevel = 1;
	private int level = 1;

	private float baseScale = 1.0f; // Lv1画像を画面サイズに合わせた拡大率

	public CatalogPage(CatalogPagerFragment pagerFragment,
			CatalogPageView pageView, CsvLine pageListCsv) {
		LogUtil.d(TAG, "init");
		this.parentPagerFragment = pagerFragment;
		this.parentPageView = pageView;
		this.setting.setLine(pageListCsv);
		this.setLevel(1);

		String fileID = setting.getFileID();
		String fileType = setting.getFileType();
		int filePage = setting.getPageOfFile();

		// ダウンロード用管理用のタグ作成
		downloadTag = setting.getFileID()
				+ String.valueOf(setting.getPageOfFile());

		// Lv1を取得
		imageInfo.setImageName(1, 0, 0, filePage, fileID, fileType);
		downloadImage(1, 0, 0);
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
		if (!busy) {
			parentPageView.doDraw();
		}
	}

	public String getLv1ImageName() {
		return imageInfo.getImageName(1, 0, 0);
	}

	public void setAlign(Align align) {
		this.align = align;
	}

	public Align getAlign() {
		return align;
	}

	public void setRectangle(float x, float y, float width, float height) {
		this.rectangle = new RectF(x, y, x + width, y + height);
	}

	public void setRectangle(RectF rect) {
		this.rectangle = rect;
	}

	public RectF getRectangle() {
		return rectangle;
	}

	public int getDownloadLevel() {
		return downloadLevel;
	}

	public void setDownloadLevel(int level) {
		downloadLevel = level;
	}

	public float getLevel() {
		return level;
	}

	public void setLevel(float pageScale) {
		if (pageScale >= 2.0f) {
			setLevel(4);
		} else if (pageScale >= 1.5f) {
			setLevel(2);
		} else {
			setLevel(1);
		}
	}

	private void setLevel(int level) {
		this.level = level;
		if (MainApplication.getInstance().isMDpi()) {
			// 高画質端末の時は、最低レベルを2とする
			if (this.level < 2) {
				this.level = 2;
			}
		}
	}

	private float calcBaseScale(float canvasWidth, float canvasHeight,
			Bitmap bitmap) {
		float scale = canvasWidth / (float) bitmap.getWidth();

		float bmpHeight = (float) bitmap.getHeight() * scale;
		if (bmpHeight > canvasHeight) {
			scale = canvasHeight / (float) bitmap.getHeight();
		}
		return scale;
	}

	public void doDraw(Canvas canvas, RectF dispRect) {
		MemoryCache<Bitmap> memoryCache = parentPagerFragment.getMemoryCache();
		if (memoryCache == null) {
			// 画面は既に停止している
			return;
		}
		String imageName = imageInfo.getImageName(1, 0, 0);
		if (imageName == null) {
			// まだLv1画像が取得できていない
			return;
		}
		Bitmap bmp = memoryCache.get(imageName);
		if (bmp == null) {
			// まだLv1画像が取得できていない
			return;
		}

		if (rectangle == null || canvas == null) {
			return;
		}
		float canvasWidth = rectangle.width();
		float canvasHeight = rectangle.height();

		// Canvasサイズに合わせる拡大率を求める(端末毎のサイズ可変)
		baseScale = calcBaseScale(canvasWidth, canvasHeight, bmp);

		// 実際に表示する時の画像サイズ
		float bitmapWidth = bmp.getWidth() * baseScale;
		float bitmapHeight = bmp.getHeight() * baseScale;

		float addX = 0;
		float addY = 0;
		// 横方向の位置
		switch (align) {
		case AlignCenter:
			// Canvasと画像のサイズ差分を求め、中央に表示する為の補正値を求める
			float diffX = canvasWidth - bitmapWidth;
			addX = diffX / 2;
			break;
		case AlignLeft:
			addX = 0;
			break;
		case AlignRight:
			addX = canvasWidth - bitmapWidth;
		}
		// 縦方向の位置は中央のみ
		float diffY = canvasHeight - bitmapHeight;
		addY = diffY / 2;

		addX += rectangle.left;
		addY += rectangle.top;

		if (DRAW_MODE == 0) {
			Paint paint = new Paint();
			Rect src = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
			RectF dest = new RectF(addX, addY, addX + bitmapWidth, addY
					+ bitmapHeight);
			canvas.drawBitmap(bmp, src, dest, paint);
		} else {

			BitmapDrawable drawable = new BitmapDrawable(MainApplication
					.getInstance().getResources(), bmp);
			drawable.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
			Matrix matrix = new Matrix();
			RectF src = new RectF(0, 0, bmp.getWidth(), bmp.getHeight());
			RectF dest = new RectF(addX, addY, addX + bitmapWidth, addY
					+ bitmapHeight);
			matrix.setRectToRect(src, dest, ScaleToFit.START);
			canvas.save();
			canvas.concat(matrix);
			drawable.draw(canvas);
			canvas.restore();
		}
		if (level >= 2) {
			Integer flag = setting.getLv2();
			if (flag == null || flag != 0) {
				doDrawLv(2, addX, addY, canvas, dispRect);
			}
		}
		if (level >= 4) {
			Integer flag = setting.getLv4();
			if (flag == null || flag != 0) {
				doDrawLv(4, addX, addY, canvas, dispRect);
			}
		}
	}

	private void doDrawLv(int level, float addX, float addY, Canvas canvas,
			RectF dispRect) {
		MemoryCache<Bitmap> memoryCache = parentPagerFragment.getMemoryCache();
		if (memoryCache == null) {
			// 画面は既に停止している
			return;
		}

		float tileWidth = setting.getTileWidth() * baseScale / (float) level;
		float tileHeight = setting.getTileHeight() * baseScale / (float) level;
		float bitmapWidth = 0;
		float bitmapHeight = 0;

		float horizontalSize = 0;
		for (int h = 0; h < imageInfo.getHorizontal(level); h++) {
			float verticalSize = 0;
			for (int v = 0; v < imageInfo.getVertical(level); v++) {
				RectF dest = new RectF(addX + horizontalSize, addY
						+ verticalSize, addX + horizontalSize + tileWidth, addY
						+ verticalSize + tileHeight);

				if (!RectF.intersects(dispRect, dest)) {
					// 表示範囲外
					verticalSize += tileHeight;
					continue;
				}
				String inameName = imageInfo.getImageName(level, h, v);
				Bitmap bmp = memoryCache.get(inameName);
				if (bmp == null) {
					downloadImage(level, h, v);
					verticalSize += tileHeight;
					continue;
				}

				bitmapWidth = bmp.getWidth() * baseScale / (float) level;
				bitmapHeight = bmp.getHeight() * baseScale / (float) level;
				dest.set(addX + horizontalSize, addY + verticalSize, addX
						+ horizontalSize + bitmapWidth, addY + verticalSize
						+ bitmapHeight);

				if (DRAW_MODE == 0) {
					Paint paint = new Paint();
					Rect src = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
					canvas.drawBitmap(bmp, src, dest, paint);
				} else {
					BitmapDrawable drawable = new BitmapDrawable(
							MainApplication.getInstance().getResources(), bmp);
					drawable.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
					Matrix matrix = new Matrix();
					RectF src = new RectF(0, 0, bmp.getWidth(), bmp.getHeight());
					matrix.setRectToRect(src, dest, ScaleToFit.START);
					canvas.save();
					canvas.concat(matrix);
					drawable.draw(canvas);
					canvas.restore();
				}
				verticalSize += tileHeight;
			}
			horizontalSize += tileWidth;
		}
	}

	private void makeHVSize(int level, Bitmap lv1bmp) {
		int tileWidth = setting.getTileWidth();
		int tileHeight = setting.getTileHeight();

		int horizontal = (int) Math.ceil((lv1bmp.getWidth() * level)
				/ (float) tileWidth);
		int vertical = (int) Math.ceil((lv1bmp.getHeight() * level)
				/ (float) tileHeight);

		imageInfo.setHorizontal(level, horizontal);
		imageInfo.setVertical(level, vertical);

		// ファイル名を作成する
		String fileID = setting.getFileID();
		String fileType = setting.getFileType();
		int filePage = setting.getPageOfFile();
		for (int h = 0; h < imageInfo.getHorizontal(level); h++) {
			for (int v = 0; v < imageInfo.getVertical(level); v++) {
				imageInfo.setImageName(level, h, v, filePage, fileID, fileType);
			}
		}
	}

	private void downloadImage(final int level, final int horizontal,
			final int vertical) {

		if (busy) {
			// busy中(ピンチ中とか)はダウンロードしない
			return;
		}
		if (level > downloadLevel) {
			// ダウンロードレベル以上はダウンロードしない
			return;
		}
		MemoryCache<Bitmap> memoryCache = parentPagerFragment.getMemoryCache();
		if (memoryCache == null) {
			// 画面は既に停止している
			return;
		}
		final String inameName = imageInfo.getImageName(level, horizontal,
				vertical);
		if (inameName == null) {
			// 名前がまだ決まっていない
			return;
		}
		if (memoryCache.containsKey(inameName)) {
			// すでにキャッシュにあるか、またはロード中
			return;
		}

		// 優先度を作成
		int priority = 5;
		if (level == 1) {
			priority = 0;
		}

		// ダウンロード開始
		String baseUrl = setting.getFileSource();
		memoryCache.put(inameName, null); // ダウンロード中としてnull格納
		DownloadManager.getInstance().offerImage(downloadTag, priority,
				baseUrl, inameName, new AsyncCallback<Bitmap>() {
					@Override
					public void onSuccess(AsyncResult<Bitmap> result) {
						Bitmap bmp = result.getContent();
						MemoryCache<Bitmap> memoryCache = parentPagerFragment
								.getMemoryCache();
						if (memoryCache == null) {
							// 画面は既に停止している
							bmp.recycle();
							return;
						}
						bmp = memoryCache.put(inameName, bmp);

						switch (level) {
						case 1:
							makeHVSize(2, bmp);
							makeHVSize(4, bmp);
							break;
						case 2:
							break;
						case 4:
							break;
						}
						parentPageView.doDraw();
					}

					@Override
					public void onFailed(AsyncResult<Bitmap> result) {
					}
				});
	}

	/**
	 * ダウンロード待ちを削除する
	 */
	public void stopDownload() {
		DownloadManager.getInstance().clear(downloadTag);
	}
}
