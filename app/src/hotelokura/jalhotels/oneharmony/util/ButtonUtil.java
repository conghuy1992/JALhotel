package hotelokura.jalhotels.oneharmony.util;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ButtonUtil {

	public static int DrawableLeft = 0;
	public static int DrawableTop = 1;
	public static int DrawableRight = 2;
	public static int DrawableButton = 3;

	/**
	 * drawableTopを使用したボタンの有効・無効を設定
	 * 
	 * <pre>
	 * 無効の場合は 半透明 ＋ クリックイベント無視
	 * </pre>
	 * 
	 * @param button
	 * @param posi
	 *            DrawableLeft/DrawableTop/DrawableRight/DrawableButton
	 * @param enabled
	 * @return
	 */
	public static void drawableEnabled(Button button, int posi, boolean enabled) {
		Resources res = MainApplication.getInstance().getResources();
		int buttonColorDefault = res.getColor(R.color.buttom_highlight_default);
		int buttonColorUnavail = res.getColor(R.color.buttom_highlight_white);
		int buttonImgAlphaDefault = 0xff;
		int buttonImgAlphaUnavail = 0x66;

		// イベント
		button.setClickable(enabled);

		// テキスト
		if (enabled) {
			button.setTextColor(buttonColorDefault); // 不透過
		} else {
			button.setTextColor(buttonColorUnavail); // 透過
		}

		// 画像
		Drawable[] draws = button.getCompoundDrawables();
		Drawable icon = draws[posi].mutate();
		if (enabled) {
			icon.setAlpha(buttonImgAlphaDefault); // 不透過
		} else {
			icon.setAlpha(buttonImgAlphaUnavail); // 透過
		}
		button.invalidate();
	}

	public static void drawableEnabled(ImageButton button, boolean enabled) {
		int buttonImgAlphaDefault = 0xff;
		int buttonImgAlphaUnavail = 0x66;

		// イベント
		button.setEnabled(enabled);

		// 画像
		Drawable icon = button.getDrawable().mutate();
		if (enabled) {
			icon.setAlpha(buttonImgAlphaDefault); // 不透過
		} else {
			icon.setAlpha(buttonImgAlphaUnavail); // 透過
		}

		button.invalidate();
	}

	public static void drawableEnabled(ImageView button, boolean enabled) {
		int buttonImgAlphaDefault = 0xff;
		int buttonImgAlphaUnavail = 0x66;

		// 画像
		Drawable icon = button.getDrawable().mutate();
		if (enabled) {
			icon.setAlpha(buttonImgAlphaDefault); // 不透過
		} else {
			icon.setAlpha(buttonImgAlphaUnavail); // 透過
		}

		button.invalidate();
	}
}
