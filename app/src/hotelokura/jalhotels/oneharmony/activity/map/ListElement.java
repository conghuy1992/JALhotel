package hotelokura.jalhotels.oneharmony.activity.map;

/**
 * Created by barista5 on 2013/09/25.
 */
public class ListElement {
    String mTitle = null;
    String mUrl = null;
    String mText = null;
    String mLinkText = null;
    int mButtontype = MAP_BUTTON_TYPE_NONE;
    String mTel = null;
    Float mDistance = null;
    int mIcon;

    public static final int MAP_BUTTON_TYPE_NONE = 0;
    public static final int MAP_BUTTON_TYPE_NAVI = 1;
    public static final int MAP_BUTTON_TYPE_CALL = 2;

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setLinkText(String text) {
        mLinkText = text;
    }

    public void setButtonType(int type) {
        mButtontype = type;
    }

    public void setTel(String tel) {
        mTel = tel;
    }

    public void setDistance(Float distance) {
        mDistance = distance;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }
}
