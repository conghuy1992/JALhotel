package hotelokura.jalhotels.oneharmony.setting;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class PlistArray extends ArrayList<Object> {

	public PlistArray() {
		super();
	}

	public PlistArray(PlistArray array) {
		super(array);
	}

	public String getString(int index, String defaultValue) {
		if (this.size() > index) {
			String value = (String) this.get(index);
			if (value != null && value.length() > 0) {
				return value;
			}
		}
		return defaultValue;
	}

	public Integer getInteger(int index, Integer defaultValue) {
		if (this.size() > index) {
			Integer value = (Integer) this.get(index);
			if (value != null) {
				return value;
			}
		}
		return defaultValue;
	}

	public Float getFloat(int index, Float defaultValue) {
		if (this.size() > index) {
			Float value = (Float) this.get(index);
			if (value != null) {
				return value;
			}
		}
		return defaultValue;
	}

	public Boolean getBoolean(int index, Boolean defaultValue) {
		if (this.size() > index) {
			Boolean value = (Boolean) this.get(index);
			if (value != null) {
				return value;
			}
		}
		return defaultValue;
	}

	public PlistDict getDict(int index, PlistDict defaultValue) {
		if (this.size() > index) {
			PlistDict value = (PlistDict) this.get(index);
			if (value != null && value.size() > 0) {
				return value;
			}
		}
		return defaultValue;
	}

	public PlistArray getArray(int index, PlistArray defaultValue) {
		if (this.size() > index) {
			PlistArray value = (PlistArray) this.get(index);
			if (value != null && value.size() > 0) {
				return value;
			}
		}
		return defaultValue;
	}
}
