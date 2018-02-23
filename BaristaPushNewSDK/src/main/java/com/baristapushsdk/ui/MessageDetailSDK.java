package com.baristapushsdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baristapushsdk.BaseActivity;
import com.baristapushsdk.Constants;
import com.baristapushsdk.Push;
import com.baristapushsdk.R;
import com.baristapushsdk.api.ApiManager;
import com.baristapushsdk.entity.PushMessage;
import com.baristapushsdk.ui.adapter.NotificationAdapter;
import com.baristapushsdk.ui.adapter.ViewpageAdapter;
import com.baristapushsdk.utils.SharedPreferenceUtil;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Trung Kien on 10/6/2015.
 */
public class MessageDetailSDK extends BaseActivity {

    private TextView tvMessageContent;
    private String url_image, url_link, message_content;
    private Button button_readmore, button_back;
    private RelativeLayout content_image,content_read_more;
    private ImageView image_view;
    private RelativeLayout loadingLayout;
    private String id;
    private ImageView icon_new;
    private TextView tvTitle;
    private int height_image;
    private int width_image;
    private TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_message_detail_sdk);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvMessageContent = (TextView) findViewById(R.id.tvMessageContent);
        image_view = (ImageView) findViewById(R.id.image_view);
        icon_new = (ImageView) findViewById(R.id.icon_new);
        button_readmore = (Button) findViewById(R.id.button_readmore);
        button_back = (Button) findViewById(R.id.button_back);
        content_image = (RelativeLayout) findViewById(R.id.content_image);
        content_read_more = (RelativeLayout) findViewById(R.id.content_read_more);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);

        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        id = getIntent().getStringExtra("mess_id");
        loadingLayout.setVisibility(View.VISIBLE);
        if (!getIntent().getBooleanExtra("isread",false)){
            icon_new.setVisibility(View.VISIBLE);
        }else {
            icon_new.setVisibility(View.GONE);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 10, 0, 0);
            tvTitle.setLayoutParams(llp);
        }
        loadContentMessage(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), id);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra("isNotification",true)){
            SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_CLICK_NOTIFICATION,true);
            SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_CLICK_NOTIFICATION_DETAIL,true);
        }
    }

    private final void updateRecordStatus(final String bundleId, final String u_push_id) {
        // Get context
        final Context context = BaseActivity.getAppContext();

        final String regId = Push.getRegistrationId(context);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String strVersion = String.valueOf(android.os.Build.VERSION.RELEASE);
        String[] version = strVersion.split("\\.");
        String osVersion = version[0] + "." + version[1];

        if (!TextUtils.isEmpty(regId)) {
            ApiManager.updateRecordStatus(bundleId, android_id, regId, "Android", osVersion, u_push_id, new ApiManager.OnUpdateRecordStatus() {
                @Override
                public void onUpdateRecord(boolean isSuccess) {
                    if (isSuccess) {
                        if (!getIntent().getBooleanExtra("isread",true)){
                            if (Push.getUnLimitedMessage().equals("1")) {
                                countBadge(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), "0");
                            }else {
                                countBadge(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), "1");
                            }
                        }
                    }

                }
            });
        }
    }

    public static final void countBadge(final String bundleId, String limit_badge) {
        // Get context
        final Context context = BaseActivity.getAppContext();

        final String regId = Push.getRegistrationId(context);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String strVersion = String.valueOf(android.os.Build.VERSION.RELEASE);
        String[] version = strVersion.split("\\.");
        String osVersion = version[0] + "." + version[1];

        if (!TextUtils.isEmpty(regId)) {
            ApiManager.countBadge(bundleId, android_id ,regId, "Android", osVersion, limit_badge, new ApiManager.OnGetBadgeCount() {
                @Override
                public void onGetBadgeCount(boolean isSuccess, String count) {
                    if (isSuccess) {
                        SharedPreferenceUtil.putString(SharedPreferenceUtil.KEY_BADGE, count);
                        Push.setBadgeCount(Integer.parseInt(count));
                    }
                }
            });
        }
    }

    private final void loadContentMessage(final String bundleId, final String u_push_id) {
        // Get context
        final Context context = BaseActivity.getAppContext();
        final String regId = Push.getRegistrationId(context);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String strVersion = String.valueOf(android.os.Build.VERSION.RELEASE);
        String[] version = strVersion.split("\\.");
        String osVersion = version[0] + "." + version[1];
        if (!TextUtils.isEmpty(regId)) {
            ApiManager.loadContentMessage(bundleId, android_id, regId, "Android", osVersion, u_push_id, new ApiManager.OnLoadContent() {
                @Override
                public void onLoadContent(boolean isSuccess, String data) {
                    if (isSuccess) {
                        innitLayout(data);
                        updateRecordStatus(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), u_push_id);
                    }else {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(BaseActivitySDK.getInstance(),
                                BaseActivitySDK.getInstance().getResources().getString(R.string.load_fail),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void innitLayout(String data){

        try {
            JSONObject object = new JSONObject(data);
            message_content = object.getString("message");
            url_image = object.getString("image");
            url_link = object.getString("url");
            tvTitle.setText(object.getString("title"));
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());
            Date date1 = new Date(getIntent().getStringExtra("date"));
            tvDate.setText(sdf.format(date1));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvMessageContent.setText(message_content);

        if (url_image.equals("null") || url_image.equals("")){
            image_view.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.GONE);
        }else {
            image_view.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 10, 0, 0);
            tvTitle.setLayoutParams(llp);
            new LoadImageHeightTask().execute();
        }
        content_image.setVisibility(View.VISIBLE);
        if (url_link.equals("")){
            button_readmore.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)content_read_more.getLayoutParams();
            params.height = (int) getResources().getDimension(R.dimen.content_red_more);
        }else {
            button_readmore.setVisibility(View.VISIBLE);
        }

        button_readmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse(url_link));
//                startActivity(browserIntent);
                Intent intent = new Intent(MessageDetailSDK.this, ReadMoreActivity.class);
                intent.putExtra("start_Url", url_link);
                startActivity(intent);
            }
        });
    }

    private class LoadImageHeightTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                url = new URL(Constants.BASE_IMAGE_URL + url_image);
                Bitmap image = Picasso.with(MessageDetailSDK.this).load(Constants.BASE_IMAGE_URL + url_image).get();
                width_image = image.getWidth();
                height_image = image.getHeight();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels;
            int height = displaymetrics.heightPixels/2;
            double a = (double)width_image/height_image;
            double b = (double)width/height;
            double c = (double)height_image/width_image;
            double d = (double)width_image/height_image;

            if(width_image > height_image) {
                if (a >= b) {
                    image_view.getLayoutParams().width = width;
                    image_view.getLayoutParams().height = (int) (width * c);
                } else {
                    if (a < b) {
                        image_view.getLayoutParams().width = height;
                        image_view.getLayoutParams().height = (int) (height * d);
                    }
                }
                image_view.setScaleType(ImageView.ScaleType.FIT_XY);
            }else {
                image_view.getLayoutParams().height = height;
                image_view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.loading)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            imageLoader.displayImage(Constants.BASE_IMAGE_URL + url_image, image_view, options, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    loadingLayout.setVisibility(View.GONE);
                    image_view.setImageBitmap(loadedImage);
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MessageDetailSDK.this, MessageListSDK.class);
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    protected void onDestroy() {
        Push.setKeyBackHome("0");
        super.onDestroy();
    }
}
