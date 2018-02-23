package hotelokura.jalhotels.oneharmony.view;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class ImageIndicatorView extends ViewFlipper {
	static final String TAG = "ImageIndicatorView";

	public static enum Status {
		INIT, LOADED
	};

	private Status status = Status.INIT;
	private ImageView imageView;

	public ImageIndicatorView(Context context) {
		super(context);
		init(context);
	}

	public ImageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_image_indicator, this);

		imageView = new ImageView(context);
		addView(imageView);
	}

	public Status getStatus() {
		return status;
	}

	public void clearImage() {
		imageView.setImageDrawable(null);
		if (getDisplayedChild() == 1)
			showPrevious();
		status = Status.INIT;
	}

	public void setImage(Bitmap bitmap) {
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.no_image);
		}
		if (getDisplayedChild() == 0) {
			showNext();
		}
		status = Status.LOADED;
	}
}
