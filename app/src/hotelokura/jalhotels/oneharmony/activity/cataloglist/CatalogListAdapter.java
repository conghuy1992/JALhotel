package hotelokura.jalhotels.oneharmony.activity.cataloglist;

import hotelokura.jalhotels.oneharmony.setting.CsvArray;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class CatalogListAdapter extends ArrayAdapter<CsvLine> {
	static final String TAG = "CatalogListAdapter";

	private CatalogListActivity parentActivity;
	private CatalogListGridView parentGridView;

	private Integer textColor = null;

	public CatalogListAdapter(CatalogListActivity context, CsvArray objects,
			CatalogListGridView parent) {
		super(context, 0, objects);
		this.parentActivity = context;
		this.parentGridView = parent;
	}

	public void setTextColor(Integer color) {
		textColor = color;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        convertView = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = new CatalogListItemView(parentActivity);
            holder.view = (CatalogListItemView)convertView;
            holder.view.setParentGridView(parentGridView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int columns = parentGridView.getColumns();
        int true_count = super.getCount();
        int count = getCount();

        if (position < columns || count - columns <= position) {
            int lastLineNum = true_count % columns;
            if (lastLineNum == 0) lastLineNum = columns;
            if (position < columns || count - lastLineNum <= position) {
                holder.view.setDummy(true, true);
            } else {
                holder.view.setDummy(true, false);
            }
            holder.view.setCsvLine(new CsvLine(new ArrayList<String>(1)));
        } else {
            holder.view.setDummy(false,false);
            CsvLine line = getItem(position-columns);
            line.setLineNum(position-columns);
            holder.view.setCsvLine(line);
            holder.view.setTextColor(textColor);
            holder.view.viewSetting();
        }

        return convertView;
    }

    @Override
    public int getCount() {
        int columns = parentGridView.getColumns();
        int count = super.getCount();
        return count + (columns * 2);
    }

    @Override
    public boolean isEnabled(int position) {
        int columns = parentGridView.getColumns();
        int count = getCount();
        if (position < columns || count - columns <= position) {
            return false;
        }
        return true;
    }

    static class ViewHolder {
        CatalogListItemView view;
    }
}
