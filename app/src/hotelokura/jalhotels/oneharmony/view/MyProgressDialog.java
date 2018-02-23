package hotelokura.jalhotels.oneharmony.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import hotelokura.jalhotels.oneharmony.R;

public class MyProgressDialog extends Dialog {

	public MyProgressDialog(Context context) {
		super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_progress);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
	}

    public void setMessage(String message) {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);
    }
}
