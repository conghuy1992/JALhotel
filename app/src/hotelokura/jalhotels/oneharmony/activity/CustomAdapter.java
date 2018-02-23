package hotelokura.jalhotels.oneharmony.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hotelokura.jalhotels.oneharmony.R;

/**
 * Created by barista7 on 2/25/16.
 */
public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> arr;

    public CustomAdapter(Context c, ArrayList<String> s) {
        this.context = c;
        this.arr = s;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.layout_customadapter, null);
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        textView.setText(arr.get(position));
        return convertView;
    }
}
