package hotelokura.jalhotels.oneharmony.setting;

/**
 * 設定 AppNet.plist
 */
public class AppNetSetting extends Setting {

	protected PlistDict dict;

	public AppNetSetting() {
		super();
	}

	public AppNetSetting(PlistDict dict) {
		super();
		this.dict = dict;
	}

	public PlistDict getDict() {
		return dict;
	}

	public void setDict(PlistDict dict) {
		this.dict = dict;
	}

	/**
	 * App.plist設定ファイルをネットに置く場合のフォルダへのURL
	 * 
	 * <pre>
	 * ネット不使用時は省略するか、値を空とする。
	 * </pre>
	 * 
	 * @return
	 */
	public String getNetAppURL() {
		return dict.getString("NetAppURL", null);
	}

	public int getAppCompatVersion() {
		return this.dict.getInteger("CompatVersion", 0);
	}

}
