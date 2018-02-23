package hotelokura.jalhotels.oneharmony.activity.catalog;

import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class CatalogPageView extends SurfaceView implements Callback {
	static final String TAG = "CatalogPageView";

	private SparseArray<CatalogPage> pages;
	private int pageCount = 0;

	private float pageScale;
	private float pageScaleOld;

	private RectF canvasRect = null;

	private boolean drawing = false;
	private boolean drawReservation = false;

	public CatalogPageView(Context context) {
		super(context);
		init(context);
	}

	public CatalogPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CatalogPageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// surfaceが作成された
		LogUtil.d(TAG, "surfaceCreated");
		doDraw();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// surfaceのサイズが変更された
		LogUtil.d(TAG, "surfaceChanged");
		viewPage();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// surfaceが破棄された
		LogUtil.d(TAG, "surfaceDestroyed");
	}

	public void setPages(SparseArray<CatalogPage> pages, int pageCount) {
		this.pages = pages;
		this.pageCount = pageCount;
		doDraw();
	}

	/**
	 * キャンバスは左に吸着しているか
	 * 
	 * @return
	 */
	public boolean isLeftAdsorption() {
		return (canvasRect.left >= 0);
	}

	/**
	 * キャンバスは右に吸着しているか
	 * 
	 * @return
	 */
	public boolean isRightAdsorption() {
		return (canvasRect.right <= getWidth());
	}

	/**
	 * 画面描画
	 */
	public void doDraw() {
		doDraw(getHolder());
	}

	/**
	 * 画面描画
	 * 
	 * @param holder
	 */
	synchronized private void doDraw(SurfaceHolder holder) {
		if (!drawing) {
			// 描画開始
			drawing = true;
			Canvas canvas = holder.lockCanvas();
			if (canvas != null) {
				canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

				for (int i = 0; i < pageCount; i++) {
					CatalogPage page = pages.get(i);
					if (page != null) {
						page.doDraw(canvas, new RectF(0, 0, getWidth(),
								getHeight()));
					}
				}

				holder.unlockCanvasAndPost(canvas);
			}
			// 描画終了
			drawing = false;
			if (drawReservation) {
				// 次の描画が予約されているので再帰
				drawReservation = false;
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						doDraw();
					}
				}, 5);
			}
		} else {
			// 描画中に描画命令がきたので予約
			drawReservation = true;
		}
	}

	/**
	 * 画像位置を補正して画面更新する
	 */
	private void drawPage() {
		// 画面外に移動しないように座標を補正する
		correctionPage();

		float x = canvasRect.left;
		float y = canvasRect.top;
		for (int i = 0; i < pageCount; i++) {
			float width = (canvasRect.width() / pageCount);
			float height = (canvasRect.height());

			CatalogPage page = pages.get(i);
			if (page != null) {
				page.setLevel(pageScale);
				page.setRectangle(x, y, width, height);
			}
			// 次のX座標
			x += width;
		}
		doDraw();
	}

	/**
	 * 画面外に移動しないように座標を補正する
	 */
	private void correctionPage() {
		if (canvasRect.width() < getWidth()) {
			// 画面幅より小さい時は移動できない
			canvasRect.offsetTo(0, canvasRect.top);
		} else {
			if (canvasRect.left > 0) {
				// 左に吸着
				canvasRect.offsetTo(0, canvasRect.top);
			} else if (canvasRect.right < getWidth()) {
				// 右に吸着
				canvasRect.offsetTo(getWidth() - canvasRect.width(),
						canvasRect.top);
			}
		}
		if (canvasRect.height() < getHeight()) {
			// 画面高より小さい時は移動できない
			canvasRect.offsetTo(canvasRect.left, 0);
		} else {
			if (canvasRect.top > 0) {
				// 上に吸着
				canvasRect.offsetTo(canvasRect.left, 0);
			} else if (canvasRect.bottom < getHeight()) {
				// 下に吸着
				canvasRect.offsetTo(canvasRect.left,
						getHeight() - canvasRect.height());
			}
		}
	}

	/**
	 * 画面を1倍にして表示する
	 */
	public void viewPage() {
		pageScale = 1.0f;
		canvasRect = new RectF(0, 0, getWidth(), getHeight());
		drawPage();
	}

	/**
	 * 移動(スクロール用)
	 * 
	 * @param distanceX
	 * @param distanceY
	 */
	public void setMoveTo(float distanceX, float distanceY) {
		canvasRect.offset(-1 * distanceX, -1 * distanceY);
		drawPage();
	}

	/**
	 * 指定X,Y座標を画面中央にして拡大(ダブルタップ用)
	 * 
	 * @param centerX
	 * @param centerY
	 */
	public void zoomIn(float centerX, float centerY) {
		if (pageScale < 2.0f) {
			setPageScale(2.0f, centerX, centerY);
		} else if (pageScale < 4.0f) {
			setPageScale(4.0f, centerX, centerY);
		} else {
			setPageScale(1.0f, centerX, centerY);
		}
	}

	/**
	 * 指定X,Y座標を画面中央にして拡大
	 * 
	 * @param scale
	 * @param centerX
	 * @param centerY
	 */
	public void addPageScale(float scale, float centerX, float centerY) {
		setPageScale(pageScale + scale, centerX, centerY);
	}

	/**
	 * 指定X,Y座標を画面中央にして拡大
	 * 
	 * @param scale
	 * @param centerX
	 * @param centerY
	 */
	public void setPageScale(float scale, float centerX, float centerY) {
		pageScaleOld = pageScale;
		pageScale = scale;
		if (pageScale < 1.0f) {
			pageScale = 1.0f;
		} else if (pageScale > 4.0f) {
			pageScale = 4.0f;
		}
		// 拡大の中央を計算する
		// まずは左上の座標にする
		float x = canvasRect.left - centerX;
		float y = canvasRect.top - centerY;
		// 倍率
		x = x / pageScaleOld * pageScale;
		y = y / pageScaleOld * pageScale;
		// 中央に持ってくる
		x += (getWidth() / 2);
		y += (getHeight() / 2);

		// 拡大後のサイズ
		float width = getWidth() * pageScale;
		float height = getHeight() * pageScale;

		canvasRect = new RectF(x, y, x + width, y + height);
		drawPage();
	}

	/**
	 * 指定X,Y座標を画面上固定して拡大(ピンチ用)
	 * 
	 * @param scale
	 * @param centerX
	 * @param centerY
	 */
	public void addPageScalePinch(float scale, float centerX, float centerY) {
		setPageScalePinch(pageScale + scale, centerX, centerY);
	}

	/**
	 * 指定X,Y座標を画面上固定して拡大(ピンチ用)
	 * 
	 * @param scale
	 * @param centerX
	 * @param centerY
	 */
	public void setPageScalePinch(float scale, float centerX, float centerY) {
		pageScaleOld = pageScale;
		pageScale = scale;
		if (pageScale < 1.0f) {
			pageScale = 1.0f;
		} else if (pageScale > 4.0f) {
			pageScale = 4.0f;
		}
		// 拡大の中央を計算する
		// まずは左上の座標にする
		float x = canvasRect.left - centerX;
		float y = canvasRect.top - centerY;
		// 倍率
		x = x / pageScaleOld * pageScale;
		y = y / pageScaleOld * pageScale;
		// 元の位置に持ってくる
		x += centerX;
		y += centerY;

		// 拡大後のサイズ
		float width = getWidth() * pageScale;
		float height = getHeight() * pageScale;

		canvasRect = new RectF(x, y, x + width, y + height);
		drawPage();
	}
}
