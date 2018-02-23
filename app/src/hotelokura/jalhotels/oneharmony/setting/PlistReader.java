package hotelokura.jalhotels.oneharmony.setting;

import java.io.InputStream;
import java.io.InputStreamReader;

import hotelokura.jalhotels.oneharmony.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class PlistReader {
	static private enum TagType {
		TAG_TYPE_NONE, TAG_TYPE_KEY, TAG_TYPE_STRING, TAG_TYPE_DATE, TAG_TYPE_INTEGER, TAG_TYPE_REAL, TAG_TYPE_DATA, TAG_TYPE_TRUE, TAG_TYPE_FALSE, TAG_TYPE_DICT, TAG_TYPE_ARRAY
	}

	static private class TagValue {
		public TagType type;
		public String tag;
		public String value;
	}

	static public PlistDict read(InputStream is) {
		PlistDict dict = null;

		try {
			InputStreamReader isr = new InputStreamReader(is);

			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(isr);

			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {
				switch (type) {
				case XmlPullParser.START_TAG:
					TagValue data = parseTagValue(parser);
					if (data.type == TagType.TAG_TYPE_DICT) {
						dict = parseDict(parser);
					}
					break;
				case XmlPullParser.TEXT:
					break;
				case XmlPullParser.END_TAG:
					break;
				}
			}

			isr.close();
		} catch (Exception e) {
			LogUtil.e("PlistReader.read", e.getMessage());
		}
		return dict;
	}

	static private PlistDict parseDict(XmlPullParser parser) {
		PlistDict dict = new PlistDict();

		int depth = parser.getDepth();

		try {
			String key = null;

			parser.next();
			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {
				switch (type) {
				case XmlPullParser.START_TAG:
					TagValue data = parseTagValue(parser);
					switch (data.type) {
					case TAG_TYPE_NONE:
						break;
					case TAG_TYPE_KEY:
						key = data.value;
						break;
					case TAG_TYPE_DICT:
						if (key != null) {
							dict.put(key, parseDict(parser));
						}
						break;
					case TAG_TYPE_ARRAY:
						if (key != null) {
							dict.put(key, parseArray(parser));
						}
						break;
					default:
						if (key != null) {
							dict.put(key, parseTypeObject(data));
							key = null;
						}
						break;
					}
					break;
				case XmlPullParser.TEXT:
					break;
				case XmlPullParser.END_TAG:
					if (depth == parser.getDepth()) {
						return dict;
					}
					break;
				}
			}
		} catch (Exception e) {
			LogUtil.e("PlistReader.parseDict", e.getMessage());
		}
		return dict;
	}

	static private PlistArray parseArray(XmlPullParser parser) {
		PlistArray array = new PlistArray();

		int depth = parser.getDepth();

		try {
			parser.next();
			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {
				switch (type) {
				case XmlPullParser.START_TAG:
					TagValue data = parseTagValue(parser);
					switch (data.type) {
					case TAG_TYPE_NONE:
						break;
					case TAG_TYPE_DICT:
						array.add(parseDict(parser));
						break;
					case TAG_TYPE_ARRAY:
						array.add(parseArray(parser));
						break;
					default:
						array.add(parseTypeObject(data));
						break;
					}
					break;
				case XmlPullParser.TEXT:
					break;
				case XmlPullParser.END_TAG:
					if (depth == parser.getDepth()) {
						return array;
					}
					break;
				}
			}
		} catch (Exception e) {
			LogUtil.e("PlistReader.parseArray", e.getMessage());
		}
		return array;
	}

	static private TagValue parseTagValue(XmlPullParser parser) {
		TagValue data = null;
		try {
			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {
				switch (type) {
				case XmlPullParser.START_TAG:
					data = new TagValue();
					data.tag = parser.getName();
					data.type = parseTagType(data.tag);
					switch (data.type) {
					case TAG_TYPE_NONE:
					case TAG_TYPE_DICT:
					case TAG_TYPE_ARRAY:
						return data;
					default:
						break;
					}
					break;
				case XmlPullParser.TEXT:
					if (data != null) {
						data.value = parser.getText();
					}
					break;
				case XmlPullParser.END_TAG:
					if (data != null) {
						return data;
					}
					break;
				}
			}
		} catch (Exception e) {
			LogUtil.e("PlistReader.parseTagValue", e.getMessage());
		}
		return data;
	}

	static private Object parseTypeObject(TagValue data) {
		switch (data.type) {
		case TAG_TYPE_STRING:
			return data.value;
		case TAG_TYPE_INTEGER:
			try {
				return Integer.valueOf(data.value);
			} catch (NumberFormatException e) {
				return null;
			}
		case TAG_TYPE_REAL:
			try {
				return Float.valueOf(data.value);
			} catch (NumberFormatException e) {
				return null;
			}
		case TAG_TYPE_TRUE:
			return Boolean.valueOf(true);
		case TAG_TYPE_FALSE:
			return Boolean.valueOf(false);
		case TAG_TYPE_DATA:
			return data.value;
		case TAG_TYPE_DATE:
			return data.value;
		default:
			break;
		}
		return null;
	}

	static private TagType parseTagType(String tag) {
		if (tag.equals("key")) {
			return TagType.TAG_TYPE_KEY;
		} else if (tag.equals("string")) {
			return TagType.TAG_TYPE_STRING;
		} else if (tag.equals("integer")) {
			return TagType.TAG_TYPE_INTEGER;
		} else if (tag.equals("real")) {
			return TagType.TAG_TYPE_REAL;
		} else if (tag.equals("true")) {
			return TagType.TAG_TYPE_TRUE;
		} else if (tag.equals("false")) {
			return TagType.TAG_TYPE_FALSE;
		} else if (tag.equals("date")) {
			return TagType.TAG_TYPE_DATE;
		} else if (tag.equals("data")) {
			return TagType.TAG_TYPE_DATA;
		} else if (tag.equals("dict")) {
			return TagType.TAG_TYPE_DICT;
		} else if (tag.equals("array")) {
			return TagType.TAG_TYPE_ARRAY;
		}
		return TagType.TAG_TYPE_NONE;
	}
}
