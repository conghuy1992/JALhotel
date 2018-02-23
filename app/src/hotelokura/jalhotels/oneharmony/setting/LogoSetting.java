package hotelokura.jalhotels.oneharmony.setting;

public class LogoSetting extends Setting {
	protected PlistDict dict;

	public LogoSetting() {
		super();
	}

	public LogoSetting(PlistDict dict) {
		super();
		this.dict = dict;
	}

	public String getLogoImage() {
		return dict.getString("LogoImage", null);
	}

	public String getLogoImage(String id) {
		return replaceAst(getLogoImage(), id, null);
	}

	public Integer getScaleType() {
		return dict.getInteger("ScaleType", 0);
	}
}
