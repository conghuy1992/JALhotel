package hotelokura.jalhotels.oneharmony.setting;

import java.util.HashMap;

@SuppressWarnings("serial")
public class PlistDict extends HashMap<String, Object> {

	public static PlistDict makePlistDict(HashMap<String, Object> map) {
		PlistDict dict = null;
		if (map != null) {
			dict = new PlistDict(map);
		}
		return dict;
	}

	public PlistDict() {
		super();
	}

	public PlistDict(HashMap<String, Object> map) {
		super(map);
	}

	public PlistDict(PlistDict dict) {
		super(dict);
	}

	public String getString(String key, String defaultValue) {
		if (this.containsKey(key)) {
			String value = (String) this.get(key);
			if (value != null && value.length() > 0) {
				return value;
			}
		}
		return defaultValue;
	}

	public Integer getInteger(String key, Integer defaultValue) {
		if (this.containsKey(key)) {
			Integer value = (Integer) this.get(key);
			if (value != null) {
				return value;
			}
		}
		return defaultValue;
	}

	public Float getFloat(String key, Float defaultValue) {
		if (this.containsKey(key)) {
			Float value = (Float) this.get(key);
			if (value != null) {
				return value;
			}
		}
		return defaultValue;
	}

	public Boolean getBoolean(String key, Boolean defaultValue) {
		if (this.containsKey(key)) {
			Boolean value = (Boolean) this.get(key);
			if (value != null) {
				return value;
			}
		}
		return defaultValue;
	}

	public PlistDict getDict(String key, PlistDict defaultValue) {
		if (this.containsKey(key)) {
			PlistDict value = (PlistDict) this.get(key);
			if (value != null && value.size() > 0) {
				return value;
			}
		}
		return defaultValue;
	}

	public PlistArray getArray(String key, PlistArray defaultValue) {
		if (this.containsKey(key)) {
			PlistArray value = (PlistArray) this.get(key);
			if (value != null && value.size() > 0) {
				return value;
			}
		}
		return defaultValue;
	}
}
