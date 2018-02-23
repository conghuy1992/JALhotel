package hotelokura.jalhotels.oneharmony.setting;

public class LogoSettingArray extends Setting {

	protected PlistArray array;

	public LogoSettingArray() {
		super();
	}

	public LogoSettingArray(PlistArray array) {
		super();
		this.array = array;
	}

	public int getSize() {
		return array.size();
	}

	public LogoSetting getLogoSetting(int index) {
		PlistDict dict = array.getDict(index, null);
		if (dict != null) {
			return new LogoSetting(dict);
		} else {
			return null;
		}
	}
}
