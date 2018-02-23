package hotelokura.jalhotels.oneharmony.activity.catalog;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.ButtonUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CatalogPagingToolbarView extends RelativeLayout {
	static final String TAG = "CatalogPagingToolbarView";

	public CatalogPagingToolbarView(Context context) {
		super(context);
		init(context, null);
	}

	public CatalogPagingToolbarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CatalogPagingToolbarView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_catalog_paging_toolbar, this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void setTopButtonOnClickListener(OnClickListener listener) {
		ImageButton button = (ImageButton) findViewById(R.id.topButton);
		button.setOnClickListener(listener);
	}

	public void setPrevButtonOnClickListener(OnClickListener listener) {
		ImageButton button = (ImageButton) findViewById(R.id.prevButton);
		button.setOnClickListener(listener);
	}

	public void setEndButtonOnClickListener(OnClickListener listener) {
		ImageButton button = (ImageButton) findViewById(R.id.endButton);
		button.setOnClickListener(listener);
	}

	public void setNextButtonOnClickListener(OnClickListener listener) {
		ImageButton button = (ImageButton) findViewById(R.id.nextButton);
		button.setOnClickListener(listener);
	}

	public void setSeekOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
		SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(listener);
	}

	public void setSeekMax(int max) {
		SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setMax(max);

		changeButtonEnable();
	}

	public void setSeekProgress(int progress) {
		SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setProgress(progress);

		changeButtonEnable();
	}

	private void changeButtonEnable() {
		SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
		int progress = seekBar.getProgress();

		ImageButton top = (ImageButton) findViewById(R.id.topButton);
		ImageButton prev = (ImageButton) findViewById(R.id.prevButton);
		ImageButton next = (ImageButton) findViewById(R.id.nextButton);
		ImageButton end = (ImageButton) findViewById(R.id.endButton);

		if (progress == 0) {
			ButtonUtil.drawableEnabled(top, false);
			ButtonUtil.drawableEnabled(prev, false);
		} else {
			ButtonUtil.drawableEnabled(top, true);
			ButtonUtil.drawableEnabled(prev, true);
		}
		if (progress == seekBar.getMax()) {
			ButtonUtil.drawableEnabled(end, false);
			ButtonUtil.drawableEnabled(next, false);
		} else {
			ButtonUtil.drawableEnabled(end, true);
			ButtonUtil.drawableEnabled(next, true);
		}
	}
}
