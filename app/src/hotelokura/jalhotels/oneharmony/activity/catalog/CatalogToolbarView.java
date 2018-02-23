package hotelokura.jalhotels.oneharmony.activity.catalog;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.DrawableUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.view.FunctionButtonLayout;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CatalogToolbarView extends LinearLayout {
	static final String TAG = "CatalogToolbarView";

	public enum ButtonId {
		contents, thumbnail, bookmark, sns, cart, building, openTo, map
	};

	private LinearLayout contentsButtonLayout;
	private LinearLayout thumbnailButtonLayout;
	private LinearLayout bookmarkButtonLayout;
	private LinearLayout snsButtonLayout;
	private LinearLayout cartButtonLayout;
	private LinearLayout buildingButtonLayout;
	private LinearLayout openToButtonLayout;
    private LinearLayout mapButtonLayout;

	private CatalogPagingToolbarView pagingToolbar;

    private int resource_id_table[] = {
            // リンク用
            R.drawable.toolbaricon_home,
            R.drawable.toolbaricon_cart,
            R.drawable.toolbaricon_mail,
            R.drawable.toolbaricon_open_to,
            R.drawable.toolbaricon_building,
            R.drawable.toolbaricon_world,

            //お気に入り用
            R.drawable.toolbaricon_bookmark,
            R.drawable.toolbaricon_favorite1,
            R.drawable.toolbaricon_favorite2,
            R.drawable.toolbaricon_favorite3,
            R.drawable.toolbaricon_favorite4,

            //マップ用
            R.drawable.toolbaricon_compass,
            R.drawable.toolbaricon_map,
    };

	public CatalogToolbarView(Context context) {
		super(context);
		init(context);
	}

	public CatalogToolbarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

		contentsButtonLayout = createButtonLayout(ButtonId.contents);
		thumbnailButtonLayout = createButtonLayout(ButtonId.thumbnail);
		bookmarkButtonLayout = createButtonLayout(ButtonId.bookmark);
		snsButtonLayout = createButtonLayout(ButtonId.sns);
		cartButtonLayout = createButtonLayout(ButtonId.cart);
		buildingButtonLayout = createButtonLayout(ButtonId.building);
		openToButtonLayout = createButtonLayout(ButtonId.openTo);
        mapButtonLayout = createButtonLayout(ButtonId.map);

		pagingToolbar = new CatalogPagingToolbarView(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
				LinearLayout.LayoutParams.MATCH_PARENT);
		params.weight = 5;
		pagingToolbar.setLayoutParams(params);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_catalog_toolbar, this);

		int bgColor = getResources().getColor(R.color.bg_catalog_navi);
		ViewGroup layout1 = (ViewGroup) findViewById(R.id.toolbarLayout1);
		ViewGroup layout2 = (ViewGroup) findViewById(R.id.toolbarLayout2);

		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			layout1.setBackgroundDrawable(DrawableUtil
					.makeNavigationDrawable(bgColor));
			layout2.setBackgroundDrawable(DrawableUtil
					.makeNavigationDrawable(bgColor));
		} else {
			layout1.setBackground(DrawableUtil.makeNavigationDrawable(bgColor));
			layout2.setBackground(DrawableUtil.makeNavigationDrawable(bgColor));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void changeToolbar(int orientation) {
		boolean isOneLine = false; // １行表示するかしないか
		if (MainApplication.getInstance().isTabletDevice()) {
			// タブレットのときはツールバーの１行表示
			switch (getResources().getConfiguration().orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				isOneLine = true;
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				isOneLine = true;
				break;
			}
		}

		ViewGroup layout1 = (ViewGroup) findViewById(R.id.toolbarContentsLayout1);
		ViewGroup layout2 = (ViewGroup) findViewById(R.id.toolbarContentsLayout2);
		layout1.removeAllViews();
		layout2.removeAllViews();
		ViewGroup barLayout2 = (ViewGroup) findViewById(R.id.toolbarLayout2);
		if (isOneLine) {
			// 1行表示
			barLayout2.setVisibility(View.GONE);

			// １行表示時のページングバーの比率
			LinearLayout.LayoutParams params = null;
			switch (getResources().getConfiguration().orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				params = new LinearLayout.LayoutParams(0,
						LinearLayout.LayoutParams.MATCH_PARENT);
				params.weight = 5;
				pagingToolbar.setLayoutParams(params);
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				params = new LinearLayout.LayoutParams(0,
						LinearLayout.LayoutParams.MATCH_PARENT);
				params.weight = 3;
				break;
			}
			if (params != null) {
				pagingToolbar.setLayoutParams(params);
			}

			layout1.addView(contentsButtonLayout);
			layout1.addView(thumbnailButtonLayout);
			layout1.addView(bookmarkButtonLayout);
			layout1.addView(snsButtonLayout);
			layout1.addView(cartButtonLayout);
			layout1.addView(buildingButtonLayout);
            layout1.addView(mapButtonLayout);
			layout1.addView(pagingToolbar);
			layout1.addView(openToButtonLayout);
		} else {
			// ２行表示
			barLayout2.setVisibility(View.VISIBLE);

			layout1.addView(contentsButtonLayout);
			layout1.addView(thumbnailButtonLayout);
			layout1.addView(bookmarkButtonLayout);
			layout1.addView(snsButtonLayout);
			layout1.addView(cartButtonLayout);
			layout1.addView(buildingButtonLayout);
            layout1.addView(mapButtonLayout);
			layout1.addView(openToButtonLayout);

			layout2.addView(pagingToolbar);
		}
	}

	public CatalogPagingToolbarView getPagingToolbar() {
		return pagingToolbar;
	}

	public LinearLayout getButtonLayout(ButtonId id) {
		LinearLayout layout = null;
		switch (id) {
		case contents:
			layout = contentsButtonLayout;
			break;
		case thumbnail:
			layout = thumbnailButtonLayout;
			break;
		case bookmark:
			layout = bookmarkButtonLayout;
			break;
		case sns:
			layout = snsButtonLayout;
			break;
		case cart:
			layout = cartButtonLayout;
			break;
		case building:
			layout = buildingButtonLayout;
			break;
		case openTo:
			layout = openToButtonLayout;
			break;
        case map:
            layout = mapButtonLayout;
            break;
		}
		return layout;
	}

	private LinearLayout createButtonLayout(ButtonId id) {
		LinearLayout layout = new LinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
				LinearLayout.LayoutParams.MATCH_PARENT);
		params.weight = 1;
		layout.setLayoutParams(params);
		layout.setGravity(Gravity.CENTER);

		FunctionButtonLayout button = createFunctionButton(id);
		layout.addView(button);

		return layout;
	}

	private FunctionButtonLayout createFunctionButton(ButtonId id) {
		FunctionButtonLayout button = new FunctionButtonLayout(getContext());

		int textId = 0;
		int srcId = 0;
		switch (id) {
		case contents:
			textId = R.string.catalog_toolbaricon_contents;
			srcId = R.drawable.toolbaricon_contents;
			break;
		case thumbnail:
			textId = R.string.catalog_toolbaricon_thumbnail;
			srcId = R.drawable.toolbaricon_thumbnail;
			break;
		case bookmark:
			textId = R.string.catalog_toolbaricon_bookmark;
			srcId = R.drawable.toolbaricon_bookmark;
			break;
		case sns:
			textId = R.string.catalog_toolbaricon_sns;
			srcId = R.drawable.toolbaricon_sns;
			break;
		case cart:
			textId = R.string.catalog_toolbaricon_cart;
			srcId = R.drawable.toolbaricon_cart;
			break;
		case building:
			textId = R.string.catalog_toolbaricon_building;
			srcId = R.drawable.toolbaricon_building;
			break;
		case openTo:
			textId = R.string.catalog_toolbaricon_open_to;
			srcId = R.drawable.toolbaricon_open_to;
			break;
        case map:
            textId = R.string.catalog_toolbaricon_map;
            srcId = R.drawable.toolbaricon_map;
            break;
		}
		button.setButtonText(getResources().getString(textId));
		button.setButtonResource(srcId);

		return button;
	}

	public void setButtonLayoutVisibility(ButtonId id, boolean visibility) {
		LinearLayout layout = getButtonLayout(id);
		if (visibility == true) {
			if (layout == null) {
				layout = createButtonLayout(id);
			}
			layout.setVisibility(View.VISIBLE);
		} else {
			if (layout != null) {
				layout.setVisibility(View.GONE);
			}
		}
	}

	public void setButtonEnabled(ButtonId id, boolean enabled) {
		LinearLayout layout = getButtonLayout(id);
		FunctionButtonLayout button = (FunctionButtonLayout) layout
				.getChildAt(0);
		button.setButtonEnable(enabled);
	}

	public void setButtonOnClickListener(ButtonId id, OnClickListener listener) {
		LinearLayout layout = getButtonLayout(id);
		FunctionButtonLayout button = (FunctionButtonLayout) layout
				.getChildAt(0);
		button.setButtonOnClickListener(listener);
	}

    public void setButtonResource(ButtonId id, int resourceId, boolean dispText) {
        LinearLayout layout = getButtonLayout(id);
        FunctionButtonLayout button = (FunctionButtonLayout) layout
                .getChildAt(0);
        if (resourceId >= 0) button.setButtonResource(resource_id_table[resourceId]);
        if (!dispText) {
            button.setButtonTextGone();
        }
    }
}
