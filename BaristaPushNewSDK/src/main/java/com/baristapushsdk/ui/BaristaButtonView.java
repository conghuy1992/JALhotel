package com.baristapushsdk.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baristapushsdk.R;
import com.baristapushsdk.utils.SharedPreferenceUtil;

/**
 * Created by Trung Kien on 10/12/2015.
 */
public class BaristaButtonView extends RelativeLayout{

    LayoutInflater mInflater;
    Context context;
    public BaristaButtonView(Context context) {
        super(context);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        init();

    }
    public BaristaButtonView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init();
    }
    public BaristaButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }
    public void init()
    {
        View view = mInflater.inflate(R.layout.barista_button_view, this, true);
        TextView badge = (TextView) view.findViewById(R.id.tv_badge_number);
        RelativeLayout content_badge_number = (RelativeLayout) view.findViewById(R.id.content_badge_number);
        /*if (SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BADGE).equals("0")
                || SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BADGE).equals("")){
            content_badge_number.setVisibility(View.GONE);
        }else {
            content_badge_number.setVisibility(View.VISIBLE);
            badge.setText(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BADGE));
        }*/
    }

}