package hotelokura.jalhotels.oneharmony.setting;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import hotelokura.jalhotels.oneharmony.lib.opencsv.CSVReader;

public class CsvReader {

	static public CsvArray read(InputStream is) {
		CsvArray array = new CsvArray();

		try {
            InputStreamReader isr = new InputStreamReader(is);
            String[] tokens;
            Boolean first = true;
            CSVReader reader = new CSVReader(isr,',','"',0);
			while ((tokens = reader.readNext()) != null) {
				if (first) {
					// １行名はタイトル
					first = false;
					continue;
				}

				if (tokens.length == 0) {
					// 空行は無視する
					continue;
				}

				CsvLine csv = new CsvLine();
				for (int i = 0; i < tokens.length; i++) {
					csv.add(tokens[i]);
				}
				array.add(csv);
			}
            reader.close();
			isr.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return array;
	}
}
