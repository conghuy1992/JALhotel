package hotelokura.jalhotels.oneharmony.activity.catalog;

import java.lang.reflect.Field;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.web.WebViewActivity;
import hotelokura.jalhotels.oneharmony.analytics.CheckAction;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupWindow;

public class CatalogPagerView extends ViewPager {
	static final String TAG = "CatalogPagerView";

	public static interface OnCatalogPagerViewListener {
		public void onPageSelected();
	}

	private OnCatalogPagerViewListener onCatalogPagerViewListener;

	private CatalogPagerScroller pagerScroller;

	private boolean pagingScroll;

	private int pagingScrollItem = -1;

    private MainApplication mApp = null;
    private String mCatalogId = null;

	public CatalogPagerView(Context context) {
		super(context);
		init(context);
	}

	public CatalogPagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

    /**
     * GAに必要なインスタンスを保持
     * @param app
     */
    public void setRequiredInfoForGA(MainApplication app, String catalogId) {
        this.mApp = app;
        this.mCatalogId = catalogId;
    }

	private void init(Context context) {
		LogUtil.d(TAG, "init");

		try {
			// アニメーションの速度を変更できるようにする
			// ViewPagerのmScrollerを無理矢理置き換える
			Class<?> viewPager = ViewPager.class;
			Field scroller = viewPager.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			Field interpolator = viewPager.getDeclaredField("sInterpolator");
			interpolator.setAccessible(true);

			pagerScroller = new CatalogPagerScroller(context);
			scroller.set(this, pagerScroller);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int state) {
				int currentItem = getCurrentItem();
				switch (state) {
					case ViewPager.SCROLL_STATE_DRAGGING:
						LogUtil.d(TAG, "onPageScrollStateChanged: DRAGGING "
								+ currentItem);
						pagingScroll = true;
						pagingScrollItem = currentItem;
						break;
					case ViewPager.SCROLL_STATE_SETTLING:
						LogUtil.d(TAG, "onPageScrollStateChanged: SETTLING "
								+ currentItem);
						break;
					case ViewPager.SCROLL_STATE_IDLE:
						LogUtil.d(TAG, "onPageScrollStateChanged: IDLE "
								+ currentItem);
						if (pagingScrollItem >= 0
								&& pagingScrollItem != currentItem) {
							idlePage(pagingScrollItem);
							beginPage();
							//Show menu popup when sliding to last pager
							//System.out.print(">>>>>>>>>>>>>>"+catalogSetting);
							if (isLastPage() && isShowMyPopup == false
									&& CatalogActivity.catalogSetting.getUseEndPageAction() == true)
								showMenuPopup();
						}
						pagingScroll = false;
						pagingScrollItem = -1;
						break;
					default:
						LogUtil.d(TAG, "onPageScrollStateChanged: Other "
								+ currentItem);
						break;
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
									   int positionOffsetPixels) {
				LogUtil.d(TAG, "onPageScrolled: " + position);
			}

			@Override
			public void onPageSelected(int position) {
				LogUtil.d(TAG, "onPageSelected: " + position);
				if (!pagingScroll || pagingScrollItem < 0) {
					// 直接ページ指定で移動したときは、ここでページ開始
					beginPage();
				} else {
					if (CatalogPagerView.this.mApp != null && CatalogPagerView.this.mCatalogId != null) {
						CheckAction ca = CatalogPagerView.this.mApp.getCheckAction();
						ca.sendAppTrackFromCatalog(CatalogPagerView.this.mCatalogId + "_page", position + 1);
					}
				}
				if (onCatalogPagerViewListener != null) {
					onCatalogPagerViewListener.onPageSelected();
				}
			}
		});
	}

	public void setOnCatalogPagerViewListener(
			OnCatalogPagerViewListener onCatalogPagerViewListener) {
		this.onCatalogPagerViewListener = onCatalogPagerViewListener;
	}

	public void setScrollFactor(float scrollFactor) {
		if (pagerScroller != null) {
			pagerScroller.setScrollFactor(scrollFactor);
		}
	}

	public CatalogPagerFragment getFragment(int position) {
		CatalogPagerAdapter adapter = (CatalogPagerAdapter) getAdapter();
		return (CatalogPagerFragment) adapter.instantiateItem(this, position);
	}

	public CatalogPagerFragment getCurrentFragment() {
		return getFragment(getCurrentItem());
	}

	@Override
	public void setCurrentItem(int item) {
		setCurrentItem(item, true);
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		int current = getCurrentItem();
		if (current == item) {
			// 同一ページ
			beginPage();
			if (onCatalogPagerViewListener != null) {
				onCatalogPagerViewListener.onPageSelected();
			}
			return;
		}
		showMenuPopup(item);

		if (current + 1 == item || current - 1 == item) {
			// 1ページの移動はOnPageChangeListener::onPageScrollStateChangedが発生する
			pagingScrollItem = current;
			super.setCurrentItem(item, smoothScroll);
			return;
		}

        String catalogId = this.mApp.getCurrentCatalogId();
        CheckAction ca = this.mApp.getCheckAction();
        ca.sendAppTrackFromCatalog(catalogId+"_page", item+1);

        // ２ページ以上の移動はOnPageChangeListener::onPageSelectedだけ
		idlePage(current);
		super.setCurrentItem(item, smoothScroll);
	}

	private PopupWindow mPopup;
	boolean isShowMyPopup = false;
	private WebView webView;

	private void showMenuPopup() {
		isShowMyPopup = true;
		View popUpView = inflate(getContext(), R.layout.popup_final_pager,null);
		mPopup = new PopupWindow(popUpView, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT,true);
		mPopup.setAnimationStyle(android.R.style.Animation_Dialog);
		mPopup.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0);

		Button btnOK  = (Button) popUpView.findViewById(R.id.btn_OK);
		btnOK.setText(CatalogActivity.catalogSetting.getEndPageActionText());
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				isShowMyPopup = false;
				String catalogId = mApp.getCurrentCatalogId();
				int current = getCurrentItem();
				CheckAction ca = mApp.getCheckAction();
				ca.sendAppTrackFromCatalog(catalogId+"_read_more_page", current + 1);
				Intent intent = new Intent(getContext(), WebViewActivity.class);
				intent.putExtra("StartUrl", CatalogActivity.catalogSetting.getEndPageActionURL());

				v.getContext().startActivity(intent);
			}
		});

		Button btnCancel = (Button) popUpView.findViewById(R.id.btn_Cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				isShowMyPopup = false;
			}
		});

	}

	private void showMenuPopup(int item) {
		//Add menu popup when sliding to last image detail
		if(isLastPage(item) && isShowMyPopup == false
				&& CatalogActivity.catalogSetting.getUseEndPageAction()){
			isShowMyPopup = true;
			View popUpView = inflate(getContext(), R.layout.popup_final_pager,null);
			mPopup = new PopupWindow(popUpView, ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT,true);
			mPopup.setAnimationStyle(android.R.style.Animation_Dialog);
			mPopup.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0);
			mPopup.setOutsideTouchable(true);
			mPopup.setTouchable(true);
			mPopup.setBackgroundDrawable(new BitmapDrawable());
			mPopup.getContentView().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isShowMyPopup) {
						mPopup.dismiss();
						isShowMyPopup = false;
					}
				}
			});
			Button btnOK  = (Button) popUpView.findViewById(R.id.btn_OK);
			btnOK.setText(CatalogActivity.catalogSetting.getEndPageActionText());
			btnOK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPopup.dismiss();
					isShowMyPopup = false;
					String catalogId = mApp.getCurrentCatalogId();
					int current = getCurrentItem();
					CheckAction ca = mApp.getCheckAction();
					ca.sendAppTrackFromCatalog(catalogId+"_read_more_page", current + 1);
					Intent intent = new Intent(getContext(), WebViewActivity.class);
					intent.putExtra("StartUrl", CatalogActivity.catalogSetting.getEndPageActionURL());
					v.getContext().startActivity(intent);
				}
			});

			Button btnCancel = (Button) popUpView.findViewById(R.id.btn_Cancel);
			btnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPopup.dismiss();
					isShowMyPopup = false;
				}
			});


		}
	}

	private void beginPage() {
		LogUtil.d(TAG, "beginPage: Current");
		final CatalogPagerFragment fragment = getCurrentFragment();
		fragment.setBusy(false);

		// 画面操作等を優先させるため、LV2以上の画像読み込みは遅延させる
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				fragment.setDownloadLevel(4);
				fragment.getPageView().viewPage();
			}
		}, 500);
	}

	private void idlePage(int position) {
		LogUtil.d(TAG, "idlePage:" + position);
		CatalogPagerFragment fragment = getFragment(position);
		fragment.setDownloadLevel(1);
		fragment.setBusy(true);
		fragment.getPageView().viewPage();
	}

	public boolean isLastPage() {
		return isLastPage(getCurrentItem());
	}

	public boolean isLastPage(int index) {
		if (index + 1 >= getAdapter().getCount()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isFirstPage() {
		return isFirstPage(getCurrentItem());
	}

	public boolean isFirstPage(int index) {
		if (index - 1 < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 次のページを表示
	 */
	public void nextPage() {
		if (isLastPage()) {
			return;
		}
		int current = getCurrentItem();
        final int next = current + 1;
		setCurrentItem(next);

        CheckAction ca = this.mApp.getCheckAction();
        ca.sendAppTrackFromCatalog(this.mApp.getCurrentCatalogId() + "_page", next + 1);
	}

	/**
	 * 前のページを表示
	 */
	public void prevPage() {
		if (isFirstPage()) {
			return;
		}
		int current = getCurrentItem();
        final int prev = current - 1;
		setCurrentItem(prev);

        CheckAction ca = this.mApp.getCheckAction();
        ca.sendAppTrackFromCatalog(this.mApp.getCurrentCatalogId() + "_page", prev + 1);
	}



}
