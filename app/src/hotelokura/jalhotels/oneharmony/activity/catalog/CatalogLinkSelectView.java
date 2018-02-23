package hotelokura.jalhotels.oneharmony.activity.catalog;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 左右ページリンク選択ダイアログ
 * 
 * <pre></pre>
 */
public class CatalogLinkSelectView extends LinearLayout {
	static final String TAG = "PageLinkSelectView";

	public CatalogLinkSelectView(Context context) {
		super(context);
		init(context);
	}

	public CatalogLinkSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.dialog_page_link_select, this);
	}

	/**
	 * 設定 イベント 左ページリンクボタン
	 * 
	 * @param listener
	 */
	public void setOnClickLeftButton(OnClickListener listener) {
		Button button = (Button) findViewById(R.id.selectLeftButton);
		button.setOnClickListener(listener);
	}

	/**
	 * 設定 イベント 右ページリンクボタン
	 * 
	 * @param listener
	 */
	public void setOnClickRightButton(OnClickListener listener) {
		Button button = (Button) findViewById(R.id.selectRightButton);
		button.setOnClickListener(listener);
	}

	/**
	 * 設定 イベント キャンセルボタン
	 * 
	 * @param listener
	 */
	public void setOnClickCancelButton(OnClickListener listener) {
		Button button = (Button) findViewById(R.id.selectCancelButton);
		button.setOnClickListener(listener);
	}

}
