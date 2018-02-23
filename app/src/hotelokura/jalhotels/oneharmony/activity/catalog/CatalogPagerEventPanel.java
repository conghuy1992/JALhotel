package hotelokura.jalhotels.oneharmony.activity.catalog;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.widget.LinearLayout;

public class CatalogPagerEventPanel extends LinearLayout {
	static final String TAG = "CatalogPagerEventPanel";

	public static interface OnCatalogPagerEventPanelListener {
		public void onSingleTap(MotionEvent e);

		public void onChangeScale();

		public void onPageEdge();
	}

	private OnCatalogPagerEventPanelListener onCatalogPagerEventPanelListener;
	private CatalogPagerView pagerView;

	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;

	private boolean pageScroll = false;
	private boolean pageScaling = false;

	private boolean pagingScroll = false;
	private int pagingIndex = -1;
	private float pagingMoveX = 0;
	private float pagingMoveXold = 0;

	public CatalogPagerEventPanel(Context context) {
		super(context);
		init(context);
	}

	public CatalogPagerEventPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LogUtil.d(TAG, "init");

		gestureDetector = new GestureDetector(context, onGestureListener);
		scaleGestureDetector = new ScaleGestureDetector(context,
				onScaleGestureListener);
	}

	public CatalogPagerView getPagerView() {
		return pagerView;
	}

	public void setPagerView(CatalogPagerView pagerView) {
		this.pagerView = pagerView;
	}

	public void setOnCatalogPagerEventPanelListener(
			OnCatalogPagerEventPanelListener onCatalogPagerEventPanelListener) {
		this.onCatalogPagerEventPanelListener = onCatalogPagerEventPanelListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ピンチ処理
		if (pageScaling) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				pageScaling = false;
				pagerView.getCurrentFragment().setBusy(false);
				return true;
			}
		}
		scaleGestureDetector.onTouchEvent(event);
		if (pageScaling) {
			return true;
		}

		// タップやスクロール処理
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// スクロールの終了検出
			pageScroll = false;
			// 注意 onFlingはこの後発生
		}
		gestureDetector.onTouchEvent(event);

		if (event.getAction() == MotionEvent.ACTION_UP) {
			// ページングの終了検出
			// 注意 onFlingの後に行いたいのでここで
			pagingScroll = false;
			pagingIndex = -1;
			if (pagerView.isFakeDragging()) {
				pagerView.endFakeDrag();
			}
			pagerView.getCurrentFragment().setBusy(false);
		}
		return true;
	}

	private SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onDown(MotionEvent e) {
			LogUtil.d(TAG, "onDown");
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			LogUtil.d(TAG, "onSingleTapUp");
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			LogUtil.d(TAG, "onSingleTapConfirmed");
			float x = e.getX();
			int width = getWidth();
			int space = MainApplication.getInstance().dp2px(50);
			if (x < space) {
				if (!pagerView.isFirstPage()) {
					pagerView.prevPage();
				} else {
					// 最後のページなので、ページングできない
					if (onCatalogPagerEventPanelListener != null) {
						onCatalogPagerEventPanelListener.onPageEdge();
					}
				}
			} else if (x > width - space) {
				if (!pagerView.isLastPage()) {
					pagerView.nextPage();
				} else {
					// 最後のページなので、ページングできない
					if (onCatalogPagerEventPanelListener != null) {
						onCatalogPagerEventPanelListener.onPageEdge();
					}
				}
			} else {
				if (onCatalogPagerEventPanelListener != null) {
					onCatalogPagerEventPanelListener.onSingleTap(e);
				}
			}
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// LogUtil.d(TAG, "onShowPress");
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// LogUtil.d(TAG, "onLongPress");
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			CatalogPageView v = pagerView.getCurrentFragment().getPageView();
			if (v != null) {
				if (!pageScroll) {
					LogUtil.d(TAG, "onScroll: Start");
					// 画像移動の最初
					pageScroll = true;
					pagerView.getCurrentFragment().setBusy(true);
				}
				if (!pagingScroll) {
					LogUtil.d(TAG, "onScroll: Normal");
					v.setMoveTo(distanceX, distanceY);
					boolean isAdsorption = false;
					if (distanceX < 0) {
						// 右移動
						if (v.isLeftAdsorption()) {
							// キャンバスは左に吸着している
							if (!pagerView.isFirstPage()) {
								isAdsorption = true;
							} else {
								// 最初のページなので、ページングできない
								if (onCatalogPagerEventPanelListener != null) {
									onCatalogPagerEventPanelListener
											.onPageEdge();
								}
							}
						}
					} else if (distanceX > 0) {
						// 左移動
						if (v.isRightAdsorption()) {
							// キャンバスは右に吸着している
							if (!pagerView.isLastPage()) {
								isAdsorption = true;
							} else {
								// 最後のページなので、ページングできない
								if (onCatalogPagerEventPanelListener != null) {
									onCatalogPagerEventPanelListener
											.onPageEdge();
								}
							}
						}
					}
					if (isAdsorption) {
						// 吸着しているので、ページング開始
						if (!pagerView.isFakeDragging()) {
							pagerView.beginFakeDrag();
						}
						pagingScroll = true;
						pagingIndex = pagerView.getCurrentItem();
						pagingMoveX = 0;
						pagingMoveXold = 0;
					}
				} else {
					LogUtil.d(TAG, "onScroll: Paging");
					// ページング中
					pagingMoveX += -distanceX;
					if ((pagingMoveXold < 0 && pagingMoveX > 0)
							|| (pagingMoveXold > 0 && pagingMoveX < 0)) {
						// 逆方向になったのでページング終了
						pagerView.fakeDragBy(-pagingMoveXold); // 0位置に戻す
						pagingScroll = false;
						pagingIndex = -1;

						// 向逆方向になった分をX方向移動する
						v.setMoveTo(pagingMoveX, distanceY);
					} else {
						// X方向の移動は無効
						v.setMoveTo(0, distanceY);
						pagerView.fakeDragBy(-distanceX);
					}
					pagingMoveXold = pagingMoveX;
				}
			}
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			LogUtil.d(TAG, "onFling " + velocityX);

			// 移動速度が一定値を超えた物をフリックとする
			if (velocityX > 300) {
				if (Math.abs(velocityX) > Math.abs(velocityY)) {
					// xが+で、yより移動料が大きい => 右移動
					CatalogPageView v = pagerView.getCurrentFragment()
							.getPageView();
					if (v != null && v.isLeftAdsorption()) {
						// キャンバスは左に吸着している
						if (pagerView.isFakeDragging()) {
							pagerView.endFakeDrag();
						}
						int itemIndex;
						if (pagingIndex >= 0) {
							itemIndex = pagingIndex;
						} else {
							itemIndex = pagerView.getCurrentItem();
						}
						if (!pagerView.isFirstPage(itemIndex)) {
							pagerView.setCurrentItem(itemIndex - 1, true);
						} else {
							// 最後のページなので、ページングできない
							if (onCatalogPagerEventPanelListener != null) {
								onCatalogPagerEventPanelListener.onPageEdge();
							}
						}
					}
				}
			} else if (velocityX < -300) {
				if (Math.abs(velocityX) > Math.abs(velocityY)) {
					// xが-で、yより移動料が大きい => 左移動
					CatalogPageView v = pagerView.getCurrentFragment()
							.getPageView();
					if (v != null && v.isRightAdsorption()) {
						// キャンバスは右に吸着している
						if (pagerView.isFakeDragging()) {
							pagerView.endFakeDrag();
						}
						int itemIndex;
						if (pagingIndex >= 0) {
							itemIndex = pagingIndex;
						} else {
							itemIndex = pagerView.getCurrentItem();
						}
						if (!pagerView.isLastPage(itemIndex)) {
							pagerView.setCurrentItem(itemIndex + 1, true);
						} else {
							// 最初のページなので、ページングできない
							if (onCatalogPagerEventPanelListener != null) {
								onCatalogPagerEventPanelListener.onPageEdge();
							}
						}
					}
				}
			}
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			LogUtil.d(TAG, "onDoubleTap");
			CatalogPageView v = pagerView.getCurrentFragment().getPageView();
			if (v != null) {
				v.zoomIn(e.getX(), e.getY());
				if (onCatalogPagerEventPanelListener != null) {
					onCatalogPagerEventPanelListener.onChangeScale();
				}
			}
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			LogUtil.d(TAG, "onDoubleTapEvent");
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				break;
			default:
				break;
			}
			return true;
		}
	};

	private SimpleOnScaleGestureListener onScaleGestureListener = new SimpleOnScaleGestureListener() {
		private float currentSpan;
		private float currentCenterX;
		private float currentCenterY;

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			LogUtil.d(TAG, "onScaleBegin");
			pageScroll = false;
			pagingScroll = false;
			pagingIndex = -1;
			if (pagerView.isFakeDragging()) {
				pagerView.endFakeDrag();
			}

			pageScaling = true;
			pagerView.getCurrentFragment().setBusy(true);
			currentSpan = detector.getCurrentSpan();
			currentCenterX = detector.getFocusX();
			currentCenterY = detector.getFocusY();
			if (onCatalogPagerEventPanelListener != null) {
				onCatalogPagerEventPanelListener.onChangeScale();
			}
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			LogUtil.d(TAG, "onScale");
			float span = detector.getCurrentSpan();
			float addScale = 1.0f - (currentSpan / span);
			addScale *= 2; // 補正

			CatalogPageView v = pagerView.getCurrentFragment().getPageView();
			if (v != null) {
				v.addPageScalePinch(addScale, currentCenterX, currentCenterY);
			}
			currentSpan = span;
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			LogUtil.d(TAG, "onScaleEnd");
		}
	};
}
