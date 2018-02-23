package hotelokura.jalhotels.oneharmony.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ActivitySkeleton extends FragmentActivity {
	static final String TAG = "ActivitySkeleton";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * リソースIDからcolor(int)を取得。
	 * <pre>getResources().getColor()の省略</pre>
	 * 
	 * @return int Color
	 */
	public int getColor(int colorId) {
		return getResources().getColor(colorId);
	}
}
