
package hotelokura.jalhotels.oneharmony.activity.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.catalog.CatalogActivity;
import hotelokura.jalhotels.oneharmony.setting.CatalogListSetting;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

public class InfoListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;

    private ArrayList<ListElement> mElements;
    private Activity mParentActivity;
    private Context mContext;
    private View.OnClickListener mOnClickListener;

    public InfoListAdapter(Activity activity, ArrayList<ListElement> elements, View.OnClickListener onClickListener) {
        mParentActivity = activity;
        mContext = activity.getApplicationContext();
        mOnClickListener = onClickListener;
        mInflater = LayoutInflater.from(mContext);
        mElements = elements;
    }

    @Override
    public int getCount() {
        return mElements.size();
    }

    @Override
    public ListElement getItem(int position) {
        return mElements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mElements == null || mElements.get(position) == null) {
            LogUtil.e(MapActivity.TAG, "mElements or mElements.get(position) is null");
            return mInflater.inflate(R.layout.blank_layout, null);
        }

        if (mElements.get(position).mDistance != null) {
            float distance = mElements.get(position).mDistance;
            convertView = mInflater.inflate(R.layout.view_map_loclist_item, parent, false);

            convertView.setBackgroundColor((position % 2 == 0) ?
                    mContext.getResources().getColor(R.color.bg_map_slide_store_list) :
                    mContext.getResources().getColor(R.color.bg_map_slide_store_list_2));

            TextView title = (TextView) convertView.findViewById(R.id.titleText);
            title.setText(mElements.get(position).mTitle);

            TextView distanceView = (TextView) convertView.findViewById(R.id.distance);
            float adjustedDistance = (float)Math.floor((double)(distance / 1000) * 10) / 10;
            distanceView.setText(String.valueOf(adjustedDistance) + " km");

        } else {
            if (mElements.get(position).mUrl != null) {
                convertView = mInflater.inflate(R.layout.view_map_info_item_sp, parent, false);

                final String settingUrl = mElements.get(position).mUrl;
                Button website = (Button) convertView.findViewById(R.id.link);
                if (settingUrl != null && (settingUrl.startsWith("http://") || settingUrl.startsWith("https://"))) {
                    //ウェブサイトリンクの設定
                    website.setText(mContext.getResources().getString(R.string.map_info_website));
                    website.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(settingUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (settingUrl != null && settingUrl.startsWith("catalog:")) {
                    //カタログリンクの設定
                    website.setText(mContext.getResources().getString(R.string.map_info_catalog));
                    website.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] params = settingUrl.split(":");
                            //params[1] カタログID
                            if (params[1] != null) {
                                //params[2] ページ番号
//                                String pageNum = params[2];
                                int pageNumber = 0;
                                try {
                                    pageNumber = Integer.parseInt(params[2]) - 1;
                                } catch(Exception exception) {
                                    pageNumber = 0;
                                }
                                String pageNum = String.valueOf(pageNumber);
                                LogUtil.e(MapActivity.TAG, "PAGE NUM = " + pageNum);

                                if (pageNum == null || pageNum.length() < 1) {
                                    pageNum = "0";
                                }

                                CsvLine catalogListCsv = CatalogListSetting.findCatalogListCsv(params[1]);

                                if (catalogListCsv != null) {
                                    CatalogListSetting setting = new CatalogListSetting(catalogListCsv);
                                    CatalogActivity.startActivity(mParentActivity, setting.getID(),
                                            setting.getUrl(), Integer.valueOf(pageNum),
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else {
                                    Toast.makeText(mContext,
                                            mContext.getResources().getString(R.string.map_toast_no_catalog),
                                            Toast.LENGTH_SHORT).show();
                                    LogUtil.e(MapActivity.TAG, "catalogListCsv is null");
                                }
                            } else {
                                Toast.makeText(mContext,
                                        mContext.getResources().getString(R.string.map_toast_no_catalog),
                                        Toast.LENGTH_SHORT).show();
                                LogUtil.e(MapActivity.TAG, "catalog id is null");
                            }
                        }
                    });
                } else {
                    LogUtil.e(MapActivity.TAG, "url of storlist.csv is empty or invalid data!!");
                    return mInflater.inflate(R.layout.blank_layout, null);
                }
            } else if (mElements.get(position).mLinkText != null) {
                convertView = mInflater.inflate(R.layout.view_map_info_item_link, parent, false);

                TextView title = (TextView) convertView.findViewById(R.id.titleText);
                title.setText(mElements.get(position).mTitle);
            } else {
                convertView = mInflater.inflate(R.layout.view_map_info_item, parent, false);

                TextView title = (TextView) convertView.findViewById(R.id.titleText);
                title.setText(mElements.get(position).mTitle);

                TextView subText = (TextView) convertView.findViewById(R.id.subText);
                final String text = mElements.get(position).mText;
                if (text != null && text.length() > 0) {
                    subText.setText(text);

                    //サブテキストがある場合のみタイトルの幅を制限する
                    int titleWidth = (int)(mContext.getResources().getDisplayMetrics().widthPixels * 0.2);
                    if (titleWidth > MainApplication.getInstance().dp2px(150)) {
                        titleWidth = MainApplication.getInstance().dp2px(150);
                    }
                    title.getLayoutParams().width = titleWidth;
                } else {
                    subText.setVisibility(View.GONE);
                }

                ImageView button = (ImageView) convertView.findViewById(R.id.button);

                switch (mElements.get(position).mButtontype) {
                    case ListElement.MAP_BUTTON_TYPE_NONE:
                        button.setVisibility(View.GONE);
                        break;
                    case ListElement.MAP_BUTTON_TYPE_NAVI:
                        button.setImageResource(R.drawable.mapicon_btn_map);
                        button.setOnClickListener(mOnClickListener);
                        break;
                    case ListElement.MAP_BUTTON_TYPE_CALL:
                        if (mElements.get(position).mTel != null) {
                            final String tel = mElements.get(position).mTel;
                            button.setImageResource(R.drawable.mapicon_btn_call);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("tel:" + tel);
                                    Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(i);
                                }
                            });
                        }
                        break;
                }
            }
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        if (mElements.get(position).mDistance != null) {
            return true;
        }
        if (mElements.get(position).mLinkText != null) {
            return true;
        }
        return false;
    }
}
