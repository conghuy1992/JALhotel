package hotelokura.jalhotels.oneharmony.view;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;

import hotelokura.jalhotels.oneharmony.R;

public class ProgressIndicatorDialog extends Dialog {
	static final String TAG = "ProgressIndicatorDialog";
    private EnableStopLoading mEnabledStopLoading = null;

	public ProgressIndicatorDialog(Context context) {
		super(context, R.style.ProgressIndicatorDialogTheme);
		setContentView(R.layout.dialog_progress_indicator);
	}

    public ProgressIndicatorDialog(Context context, EnableStopLoading activity) {
        super(context, R.style.ProgressIndicatorDialogTheme);
        setContentView(R.layout.dialog_progress_indicator);
        this.mEnabledStopLoading = activity;
    }

	public boolean dispatchTouchEvent(MotionEvent ev) {
        if(this.mEnabledStopLoading != null) {
            this.dismiss();
            this.mEnabledStopLoading.stopLoading();
            return true;
        }
		return false;
	}

    public interface EnableStopLoading {
        public void stopLoading();
    }
}
