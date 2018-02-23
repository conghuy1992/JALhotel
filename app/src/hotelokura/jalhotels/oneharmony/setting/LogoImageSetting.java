package hotelokura.jalhotels.oneharmony.setting;

public class LogoImageSetting extends Setting {
	protected PlistDict dict;

	public LogoImageSetting() {
		super();
	}

	public LogoImageSetting(PlistDict dict) {
		super();
		this.dict = dict;
	}

	public Integer getTopLogoHeight() {
		return dict.getInteger("TopLogoHeight", 0);
	}

	public int getTopLogoSize() {
		LogoSettingArray setting = getTopLogo();
		if (setting != null) {
			return setting.getSize();
		} else {
			return 0;
		}
	}

	public LogoSettingArray getTopLogo() {
		PlistArray array = dict.getArray("TopLogo", null);
		if (array != null) {
			return new LogoSettingArray(array);
		} else {
			return null;
		}
	}

	public LogoSettingArray getCenterLogo() {
		PlistArray array = dict.getArray("CenterLogo", null);
		if (array != null) {
			return new LogoSettingArray(array);
		} else {
			return null;
		}
	}

	public Integer getBottomLogoHeight() {
		return dict.getInteger("BottomLogoHeight", 0);
	}

	public int getBottomLogoSize() {
		LogoSettingArray setting = getBottomLogo();
		if (setting != null) {
			return setting.getSize();
		} else {
			return 0;
		}
	}

	public LogoSettingArray getBottomLogo() {
		PlistArray array = dict.getArray("BottomLogo", null);
		if (array != null) {
			return new LogoSettingArray(array);
		} else {
			return null;
		}
	}
}
