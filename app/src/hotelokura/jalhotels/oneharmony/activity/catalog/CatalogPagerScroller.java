package hotelokura.jalhotels.oneharmony.activity.catalog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class CatalogPagerScroller extends Scroller {
	private float scrollFactor = 1;

	public CatalogPagerScroller(Context context) {
		super(context);
	}

	public CatalogPagerScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	@SuppressLint("NewApi")
	public CatalogPagerScroller(Context context, Interpolator interpolator,
			boolean flywheel) {
		super(context, interpolator, flywheel);
	}

	public void setScrollFactor(float scrollFactor) {
		this.scrollFactor = scrollFactor;
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy,
				(int) (duration * scrollFactor));
	}
}
