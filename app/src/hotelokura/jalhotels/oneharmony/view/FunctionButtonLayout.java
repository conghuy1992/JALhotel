package hotelokura.jalhotels.oneharmony.view;

import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.util.ButtonUtil;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FunctionButtonLayout extends LinearLayout {
	static final String TAG = "FunctionButtonLayout";

	public FunctionButtonLayout(Context context) {
		super(context);
		init(context);
	}

	public FunctionButtonLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

		TypedArray typed = context.obtainStyledAttributes(attrs,
				R.styleable.FunctionButtonLayout);

		String buttonText = typed
				.getString(R.styleable.FunctionButtonLayout_button_text);
		setButtonText(buttonText);

		int buttonResource = typed.getResourceId(
				R.styleable.FunctionButtonLayout_button_src, -1);
		setButtonResource(buttonResource);

		typed.recycle();
	}

	private void init(Context context) {
		LogUtil.d(TAG, "init");
		View.inflate(context, R.layout.view_function_button, this);
	}

	public void setButtonText(String text) {
		TextView view = (TextView) findViewById(R.id.textView);
		view.setText(text);
	}

	public void setButtonResource(int id) {
		ImageView view = (ImageView) findViewById(R.id.imageView);
		view.setImageResource(id);
	}

	public void setButtonEnable(boolean enable) {
		ImageView view = (ImageView) findViewById(R.id.imageView);
		ButtonUtil.drawableEnabled(view, enable);
		setEnabled(enable);
	}

	public void setButtonOnClickListener(OnClickListener listener) {
		setOnClickListener(listener);
	}

    public void setButtonTextGone() {
        TextView view = (TextView) findViewById(R.id.textView);
        view.getLayoutParams().height = 0;
    }

}
