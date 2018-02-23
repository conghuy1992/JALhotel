package hotelokura.jalhotels.oneharmony.activity.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import hotelokura.jalhotels.oneharmony.R;

public class MenuAdapter extends BaseAdapter {

    private Context mContext;
    private final LayoutInflater mInflater;
    private final int[] mTitles = {
          R.string.map_menu_back,
          R.string.map_menu_show_map,
          R.string.map_menu_loc_search
    };
    private final int[] mIcons = {
          R.drawable.mapicon_menu_back,
          R.drawable.mapicon_menu_map,
          R.drawable.mapicon_menu_location
    };

    public MenuAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public String getItem(int position) {
        return mContext.getResources().getString(mTitles[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.view_map_side_list_item, parent, false);
        if (position == 0) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.bg_map_side_list_back));
        }

        TextView text = (TextView) convertView.findViewById(R.id.text);
        text.setText(mContext.getResources().getString(mTitles[position]));

        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        icon.setImageResource(mIcons[position]);

        ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
        arrow.setVisibility(View.GONE);

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

}
