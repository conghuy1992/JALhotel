package hotelokura.jalhotels.oneharmony.activity.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import hotelokura.jalhotels.oneharmony.R;

public class StoreListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final String[] mData;

    private Context mContext;
    private int mDepth;

    public StoreListAdapter(Context context, String[] data, int depth) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = data;
        mDepth = depth;
    }

    @Override
    public int getCount() {
        return mData.length;
    }

    @Override
    public String getItem(int position) {
        return mData[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.view_map_side_list_item, parent, false);

        TextView text = (TextView) convertView.findViewById(R.id.text);
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        if (position == 0 && mDepth > 1) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.bg_map_side_list_back));
            text.setText(mData[position]);
            icon.setImageResource(R.drawable.mapicon_list_back);
            ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
            arrow.setVisibility(View.GONE);
        } else {
            text.setText(mData[position]);
            icon.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
