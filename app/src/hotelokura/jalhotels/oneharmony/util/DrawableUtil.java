package hotelokura.jalhotels.oneharmony.util;

import hotelokura.jalhotels.oneharmony.MainApplication;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;

public class DrawableUtil {

	public final static float BrightUp = +0.20f;
	public final static float BrightDown = -0.20f;
	public final static float BrightButton = -0.15f; // BrightUp/Downに加算される

	public static Drawable makeNavigationDrawable(int color) {
		int startColor = changeColorBright(color, BrightUp); // 薄くする
		int endColor = changeColorBright(color, BrightDown); // 濃くする
		GradientDrawable drawable = new GradientDrawable(
				Orientation.TOP_BOTTOM,
				new int[] { startColor, color, endColor });

		return drawable;
	}

	public static Drawable makeNavigationButtonDrawable(int color) {
		Drawable drawable1 = makeNavigationButton(color, 0.0f);
		Drawable drawable2 = makeNavigationButton(color, BrightButton); // より濃くする

		StateListDrawable stateDrawable = new StateListDrawable();
		stateDrawable.addState(new int[] { android.R.attr.state_pressed },
				drawable2);
		stateDrawable.addState(new int[] { android.R.attr.state_focused },
				drawable2);
		stateDrawable.addState(new int[] { android.R.attr.state_enabled },
				drawable1);

		return stateDrawable;
	}

	private static Drawable makeNavigationButton(int color, float offset) {
		int startColor = changeColorBright(color, BrightUp + offset); // 薄くする
		int endColor = changeColorBright(color, BrightDown + offset); // 濃くする

		int radius1 = MainApplication.getInstance().dp2px(4);
		GradientDrawable drawable1 = new GradientDrawable(
				Orientation.TOP_BOTTOM,
				new int[] { endColor, color, startColor });
		drawable1.setCornerRadius(radius1);

		int radius2 = MainApplication.getInstance().dp2px(4);
		GradientDrawable drawable2 = new GradientDrawable(
				Orientation.TOP_BOTTOM,
				new int[] { startColor, color, endColor });
		drawable2.setCornerRadius(radius2);

		Drawable[] layers = new Drawable[2];
		layers[0] = drawable1;
		layers[1] = drawable2;
		LayerDrawable layerDrawable = new LayerDrawable(layers);

		int padding = MainApplication.getInstance().dp2px(1);
		layerDrawable.setLayerInset(1, padding, padding, padding, padding);

		return layerDrawable;
	}

	public static int changeColorBright(int color, float adjust) {
		if (adjust == 0.0f) {
			return color;
		}

		// RGBをHSVに変換
		float[] hsv = new float[3]; // Index毎に 0:色相 1:彩度 2:明度 と格納される
		Color.colorToHSV(color, hsv);

		// 明度を調整する
		hsv[2] += adjust;
		if (hsv[2] > 1.0f) {
			hsv[2] = 1.0f;
		} else if (hsv[2] < 0.0f) {
			hsv[2] = 0.0f;
		}

		// HSVをARGBに戻す
		int a = Color.alpha(color);
		int argb = Color.HSVToColor(a, hsv);
		return argb;
	}
}
