package hotelokura.jalhotels.oneharmony.setting;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class CsvLine extends ArrayList<String> {

    private int mLineNum = 0;

	public static CsvLine makeCsvLine(ArrayList<String> list) {
		CsvLine line = null;
		if (list != null) {
			line = new CsvLine(list);
		}
		return line;
	}

	public CsvLine() {
		super();
	}

	public CsvLine(ArrayList<String> list) {
		super(list);
	}

	public CsvLine(CsvLine line) {
		super(line);
	}

    public void setLineNum(int lineNum) {
        this.mLineNum = lineNum;
    }

    public int getLineNum() {
        return this.mLineNum;
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
			String temp = this.get(index);
			Integer value;
			try {
				value = Integer.valueOf(temp);
			} catch (NumberFormatException e) {
				value = null;
			}
			if (value != null) {
				return value;
			}
		}
		return defaultValue;
	}

	public Float getFloat(int index, Float defaultValue) {
		if (this.size() > index) {
			String temp = this.get(index);
			Float value;
			try {
				value = Float.valueOf(temp);
			} catch (NumberFormatException e) {
				value = null;
			}
			if (value != null) {
				return value;
			}
		}
		return defaultValue;
	}

    public Double getDouble(int index, Double defaultValue) {
        if (this.size() > index) {
            String temp = this.get(index);
            Double value;
            try {
                value = Double.valueOf(temp);
            } catch (NumberFormatException e) {
                value = null;
            }
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    public Boolean getBoolean(int index, Boolean defaultValue) {
		if (this.size() > index) {
			String temp = this.get(index);
			Boolean value;
			try {
				value = Boolean.valueOf(temp);
			} catch (Exception e) {
				value = null;
			}
			if (value != null) {
				return value;
			}
		}
		return defaultValue;
	}
}
