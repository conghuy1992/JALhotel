package hotelokura.jalhotels.oneharmony.setting;

public class BGImageSetting {
	private String imageName;
	private LogoImageSetting logoSetting;

	public BGImageSetting(String imageName, LogoImageSetting logoSetting) {
		this.imageName = imageName;
		this.logoSetting = logoSetting;
	}

	public String getImageName() {
		return imageName;
	}

	public LogoImageSetting getLogoSetting() {
		return logoSetting;
	}
}
