package hotelokura.jalhotels.oneharmony.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.map.MyListFragment;
import hotelokura.jalhotels.oneharmony.activity.web.WebViewActivity;
import hotelokura.jalhotels.oneharmony.analytics.CheckAction;
import hotelokura.jalhotels.oneharmony.lib.slidinguppanel.SlidingUpPanelLayout;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.setting.MapSetting;
import hotelokura.jalhotels.oneharmony.util.LogUtil;

/**
 * Created by barista7 on 2/24/16.
 */
public class ActivityReservation extends Activity implements View.OnClickListener {
    private String TAG = "ActivityReservation";
    private ImageView imgback, imgfinish;
    private String filter_id = "";
    private ArrayList<MapSetting> mMapSettingArray = new ArrayList<MapSetting>();
    private ArrayList<String> data, data2, data3, ARR_URL;
    private CustomAdapter adapterArea, adapterCountry, adapterHotel;
    private String area = "", country = "", hotel = "", GOTO_URL = "";
    private Button btnsearch;
    private TextView spinnerArea, spinnerCountry, spinnerHotel;
    private int _CLICK = 0;
    private boolean flag = false, flag2 = false, flag3 = false;
    private FrameLayout frarea, frcountry, frhotel, frcancel;
    private String URL_CANCEL = "https://gc.synxis.com/rez.aspx?Chain=9542&start=searchres&shell=GCOC";
    private int pos_area = 0, pos_country = 0, pos_hotel = 0;
    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        isTablet = MainApplication.getInstance().isTabletDevice();
        if (!isTablet) {
            setContentView(R.layout.activity_reservation);
        } else {
            setContentView(R.layout.activity_reservation_tablet);
        }
        initView();
    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void initView() {
        data = new ArrayList<String>();
        data2 = new ArrayList<String>();
        data3 = new ArrayList<String>();
        ARR_URL = new ArrayList<String>();
        adapterArea = new CustomAdapter(this, data);
        adapterCountry = new CustomAdapter(this, data2);
        adapterHotel = new CustomAdapter(this, data3);
        spinnerArea = (TextView) findViewById(R.id.spinnerArea);
        spinnerCountry = (TextView) findViewById(R.id.spinnerCountry);
        spinnerHotel = (TextView) findViewById(R.id.spinnerHotel);
        frarea = (FrameLayout) findViewById(R.id.frarea);
        frcountry = (FrameLayout) findViewById(R.id.frcountry);
        frhotel = (FrameLayout) findViewById(R.id.frhotel);
        frcancel = (FrameLayout) findViewById(R.id.frcancel);
        getCSVdata();
        btnsearch = (Button) findViewById(R.id.btnsearch);
        imgback = (ImageView) findViewById(R.id.imgback);
        imgfinish = (ImageView) findViewById(R.id.imgfinish);
        imgback.setOnClickListener(this);
        imgfinish.setOnClickListener(this);
        btnsearch.setOnClickListener(this);
        frarea.setOnClickListener(this);
        frcountry.setOnClickListener(this);
        frhotel.setOnClickListener(this);
        frcancel.setOnClickListener(this);
    }

    private String str_area[];

    public void getCSVdata() {
        filter_id = getIntent().getStringExtra("filter_id");
        if (filter_id != null) {
            //ストアリストからマップ設定のリストを作成
            makeMapSettings(MapSetting.findId(
                    MainApplication.getInstance().getStorelistCsvArray(), filter_id));

        } else {
            LogUtil.e(TAG, "filter_id is NULL");
        }
        for (MapSetting setting : mMapSettingArray) {
            String params = setting.getCategory();
            String[] paramArray = params.split(":");
            if (!data.contains(paramArray[1])) {
                data.add(paramArray[1]);
            }
        }
        str_area = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            str_area[i] = data.get(i);
        }
    }

    private void makeMapSettings(ArrayList<CsvLine> storeList) {
        if (storeList != null) {
            for (CsvLine stor : storeList) {
                mMapSettingArray.add(new MapSetting(stor));
            }
        }
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
//                    Log.e("setNumberPickerTextColor", "");
                } catch (IllegalAccessException e) {
//                    Log.e("setNumberPickerTextColor", e);
                } catch (IllegalArgumentException e) {
//                    Log.e("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }

    public void Chooser_Dialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_chooserreservation);
        dialog.setCanceledOnTouchOutside(false);
//        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numpk);
        TextView done = (TextView) dialog.findViewById(R.id.done);
        String arr_data[] = null;
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooser_value(dialog);
            }
        });
        np.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooser_value(dialog);
            }
        });

        setDividerColor(np, Color.parseColor("#D3D3D3"));
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerTextColor(np, Color.BLACK);
        np.setMinValue(0);

        if (_CLICK == 1) {
            np.setMaxValue(str_area.length - 1);
            np.setDisplayedValues(str_area);
            np.setValue(pos_area);
        } else if (_CLICK == 2) {
            arr_data = new String[data2.size()];
            for (int i = 0; i < data2.size(); i++) {
                arr_data[i] = data2.get(i);
            }
            np.setMaxValue(arr_data.length - 1);
            np.setDisplayedValues(arr_data);
            np.setValue(pos_country);
        } else if (_CLICK == 3) {
            arr_data = new String[data3.size()];
            for (int i = 0; i < data3.size(); i++) {
                arr_data[i] = data3.get(i);
            }
            np.setMaxValue(arr_data.length - 1);
            np.setDisplayedValues(arr_data);
            np.setValue(pos_hotel);
        }
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                if (_CLICK == 1)
                    pos_area = newVal;
                if (_CLICK == 2)
                    pos_country = newVal;
                if (_CLICK == 3)
                    pos_hotel = newVal;
            }
        });
//        ListView listviewchooser = (ListView) dialog.findViewById(R.id.listviewchooser);
//        if (_CLICK == 1)
//            listviewchooser.setAdapter(adapterArea);
//        if (_CLICK == 2)
//            listviewchooser.setAdapter(adapterCountry);
//        if (_CLICK == 3)
//            listviewchooser.setAdapter(adapterHotel);
//        listviewchooser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (_CLICK == 1) {
//                    flag2 = true;
//                    flag3 = false;
//                    GOTO_URL = "";
//                    area = data.get(position);
//                    spinnerArea.setText(area);
//                    if (data2.size() > 0) {
//                        data2.clear();
//                    }
//                    if (data3.size() > 0) {
//                        data3.clear();
//                    }
//                    for (MapSetting setting : mMapSettingArray) {
//                        String params = setting.getCategory();
//                        String[] paramArray = params.split(":");
//                        if (paramArray[1].equals(data.get(position))) {
//                            data2.add(paramArray[2]);
//                        }
//                    }
//                    adapterCountry.notifyDataSetChanged();
//                    spinnerCountry.setText(getResources().getString(R.string.country));
//                    spinnerHotel.setText(getResources().getString(R.string.hotel));
//                }
//                if (_CLICK == 2) {
//                    flag3 = true;
//                    country = data2.get(position);
//                    spinnerCountry.setText(country);
//                    GOTO_URL = "";
//                    if (data3.size() > 0)
//                        data3.clear();
//                    if (ARR_URL.size() > 0)
//                        ARR_URL.clear();
//                    for (MapSetting setting : mMapSettingArray) {
//                        String params = setting.getCategory();
//                        String[] paramArray = params.split(":");
//                        String param_url = setting.getExt2();
//                        if (paramArray[2].equals(data2.get(position))) {
//                            data3.add(setting.getName());
////                            Log.e(TAG, "length:" + param_url.length());
////                            Log.e(TAG, param_url.substring(16, param_url.length()));
//                            ARR_URL.add(param_url.substring(16, param_url.length()));
//                        }
//                    }
//                    adapterHotel.notifyDataSetChanged();
//                    spinnerHotel.setText(getResources().getString(R.string.hotel));
//                }
//                if (_CLICK == 3) {
//                    hotel = data3.get(position);
//                    GOTO_URL = ARR_URL.get(position);
//                    spinnerHotel.setText(hotel);
//                }
//                dialog.dismiss();
//            }
//        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v == imgfinish) {
            finish();
        } else if (v == imgback) {
            finish();
        } else if (v == btnsearch) {
            if (GOTO_URL.length() > 0)
                intentBrowser(GOTO_URL);
        } else if (v == frarea) {
            _CLICK = 1;
            Chooser_Dialog();
        } else if (v == frcountry) {
            if (flag2) {
                _CLICK = 2;
                Chooser_Dialog();
            }
        } else if (v == frhotel) {
            if (flag3) {
                _CLICK = 3;
                Chooser_Dialog();
            }
        } else if (v == frcancel) {
            intentBrowser(URL_CANCEL);
        }
    }

    public void intentBrowser(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("StartUrl", url);
        startActivity(intent);
    }

    public void chooser_value(Dialog dialog) {
        if (_CLICK == 1) {
            flag2 = true;
            flag3 = false;
            pos_country = 0;
            pos_hotel = 0;
            GOTO_URL = "";
            area = data.get(pos_area);
            spinnerArea.setText(area);
            if (data2.size() > 0) {
                data2.clear();
            }
            if (data3.size() > 0) {
                data3.clear();
            }
            for (MapSetting setting : mMapSettingArray) {
                String params = setting.getCategory();
                String[] paramArray = params.split(":");
                if (paramArray[1].equals(data.get(pos_area)) && !data2.contains(paramArray[2])) {
                    data2.add(paramArray[2]);
                }
            }
            adapterCountry.notifyDataSetChanged();
            spinnerCountry.setText(getResources().getString(R.string.country));
            spinnerHotel.setText(getResources().getString(R.string.hotel));
        } else if (_CLICK == 2) {
            pos_hotel = 0;
            flag3 = true;
            country = data2.get(pos_country);
            spinnerCountry.setText(country);
            GOTO_URL = "";
            if (data3.size() > 0)
                data3.clear();
            if (ARR_URL.size() > 0)
                ARR_URL.clear();
            for (MapSetting setting : mMapSettingArray) {
                String params = setting.getCategory();
                String[] paramArray = params.split(":");
                String param_url = setting.getExt2();
                if (paramArray[2].equals(data2.get(pos_country))) {
                    data3.add(setting.getName());
                    ARR_URL.add(param_url.substring(16, param_url.length()));
                }
            }
            adapterHotel.notifyDataSetChanged();
            spinnerHotel.setText(getResources().getString(R.string.hotel));
        } else if (_CLICK == 3) {
            hotel = data3.get(pos_hotel);
            GOTO_URL = ARR_URL.get(pos_hotel);
            spinnerHotel.setText(hotel);
        }
        dialog.dismiss();
    }
}
