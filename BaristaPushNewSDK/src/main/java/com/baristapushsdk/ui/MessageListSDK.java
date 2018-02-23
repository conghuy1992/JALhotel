package com.baristapushsdk.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baristapushsdk.Constants;
import com.baristapushsdk.Push;
import com.baristapushsdk.R;
import com.baristapushsdk.api.ApiManager;
import com.baristapushsdk.entity.PushMessage;
import com.baristapushsdk.ui.adapter.DataAdapter;
import com.baristapushsdk.ui.adapter.NotificationAdapter;
import com.baristapushsdk.utils.DebugLog;
import com.baristapushsdk.utils.NetworkUtil;
import com.baristapushsdk.utils.SharedPreferenceUtil;
import com.google.android.gms.internal.pa;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Trung Kien on 10/6/2015.
 */
public class MessageListSDK extends BaseActivitySDK {

    private RecyclerView mRecyclerView;
    private DataAdapter mAdapter;
    private Button btn_home, btn_setting;
    private static TextView tvUnread;
    private static  RelativeLayout content_unread;
    private boolean clicked;
    private RelativeLayout loadingLayout;
    private static List<PushMessage> listMessage;
    private static ProgressDialog progress;
    protected Handler handler;
    private int page_count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_message_list_sdk);

        handler = new Handler();

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

        SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG,true);
        initView();
        if (NetworkUtil.hasNetwork()) {
            if (SharedPreferenceUtil.getBoolean(SharedPreferenceUtil.KEY_REGISTER_STATUS, false)) {
                if(!SharedPreferenceUtil.getBoolean(SharedPreferenceUtil.KEY_CLICK_NOTIFICATION_DETAIL, false)) {
                    syncMessage();
                }
            }
        }else {
            Toast.makeText(BaseActivitySDK.getInstance(),
                    BaseActivitySDK.getInstance().getResources().getString(R.string.network_error)
                    , Toast.LENGTH_SHORT).show();
        }
    }

    void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvUnread = (TextView) findViewById(R.id.tvUnread);
        content_unread = (RelativeLayout) findViewById(R.id.content_unread);
        btn_home = (Button) findViewById(R.id.home);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Push.setKeyBackHome("0");
                onBackPressed();
            }
        });

        btn_setting = (Button) findViewById(R.id.go_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Push.setKeyBackHome("1");
                onBackPressed();
            }
        });
    }

    @Override
    public void onReceivedMessage(final PushMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listMessage.add(0, message);
                if (Push.getUnLimitedMessage().equals("0")){
                    if (listMessage.size() > Constants.NUMBER_MESSAGE_DISPLAY) {
                        listMessage.remove(Constants.NUMBER_MESSAGE_DISPLAY);
                    }
                }
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
                notifyUnread();
            }
        });
    }

    void notifyUnread() {
        if (Push.getUnLimitedMessage().equals("1")) {
            countBadge(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), "0");
        }else {
            countBadge(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), "1");
        }
    }

    public static final void countBadge(final String bundleId, String limit_badge) {
        // Get context
        final Context context = BaseActivitySDK.getInstance();

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
                        if (Push.getBadgeCount().equals("0")){
                            content_unread.setVisibility(View.GONE);
                        }else {
                            content_unread.setVisibility(View.VISIBLE);
                            tvUnread.setText(Push.getBadgeCount());
                        }
                    }
                }
            });
        }
    }

    private DataAdapter.OnItemClick onItemClick = new DataAdapter.OnItemClick() {
        @Override
        public void onItemClick(int position) {
            if (NetworkUtil.hasNetwork()) {
                if (clicked)
                    return;

                clicked = true;

                PushMessage pushMessage = null;

                pushMessage = listMessage.get(position);

                if(pushMessage == null)
                    return;
                if (!pushMessage.pushMesageIsRead){
                    mAdapter.notifyDataSetChanged();
                }

                go2Detail(pushMessage);

                if (!pushMessage.pushMesageIsRead){
                    listMessage.get(position).pushMesageIsRead = true;

                    notifyUnread();
                }

                DebugLog.e(pushMessage.toString());
            }else {
                Toast.makeText(BaseActivitySDK.getInstance(),
                        BaseActivitySDK.getInstance().getResources().getString(R.string.network_error)
                        , Toast.LENGTH_SHORT).show();
            }
        }
    };

    void go2Detail(PushMessage pushMessage){
        SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_CLICK_NOTIFICATION_DETAIL,false);
        Intent intent = new Intent(MessageListSDK.this, MessageDetailSDK.class);
        intent.putExtra("mess_id", pushMessage.pushMesageId);
        intent.putExtra("title", pushMessage.pushMessageTitle);
        intent.putExtra("date", pushMessage.pushMesageDate);
        intent.putExtra("isread", pushMessage.pushMesageIsRead);
        intent.putExtra("isNotification", false);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        clicked = false;
        if (Push.getBadgeCount().equals("0")){
            content_unread.setVisibility(View.GONE);
        }else {
            content_unread.setVisibility(View.VISIBLE);
            tvUnread.setText(Push.getBadgeCount());
        }
        if (SharedPreferenceUtil.getBoolean(SharedPreferenceUtil.KEY_EDIT, false)){

        }
        if(SharedPreferenceUtil.getBoolean(SharedPreferenceUtil.KEY_CLICK_NOTIFICATION_DETAIL, false)){
            syncMessage();
        }
    }

    private void syncMessage(){
        progress = new ProgressDialog(BaseActivitySDK.getInstance());
        progress.setMessage(BaseActivitySDK.getInstance().getResources().getString(R.string.sync));
        progress.setCancelable(true);
        progress.show();
        syncMessage(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID),"1");
    }

    private void syncMessage(final String bundleId, final String page) {
        // Get context
        final Context context = BaseActivitySDK.getInstance();

        final String regId = Push.getRegistrationId(context);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String strVersion = String.valueOf(android.os.Build.VERSION.RELEASE);
        String[] version = strVersion.split("\\.");
        String osVersion = version[0] + "." + version[1];

        if (!TextUtils.isEmpty(regId)) {
            ApiManager.syncMessages(bundleId, android_id, regId, "Android", osVersion, page, new ApiManager.OnSyncMessages() {
                @Override
                public void onSyncMessages(boolean isSuccess, String data, String delete_id) {
                    if (isSuccess) {
                        try {
                            JSONArray array = new JSONArray(data);
                            listMessage = new ArrayList<PushMessage>();

                            for(int i=0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                PushMessage message = new PushMessage();
                                message.pushMesageId = object.getString("u_push_done_id");
                                message.pushMesageDate = object.getString("date");
                                message.pushMessageTitle= object.getString("title");
                                message.pushMesageImageUrl= object.getString("image");
                                if (object.getString("read_flag").equals("0")){
                                    message.pushMesageIsRead = false;
                                }else {
                                    message.pushMesageIsRead = true;
                                }
                                listMessage.add(message);

                            }

                            mAdapter = new DataAdapter(listMessage, mRecyclerView, onItemClick);
                            mRecyclerView.setAdapter(mAdapter);

                            if (Push.getBadgeCount().equals("0")){
                                content_unread.setVisibility(View.GONE);
                            }else {
                                content_unread.setVisibility(View.VISIBLE);
                                tvUnread.setText(Push.getBadgeCount());
                            }

                            if (Push.getUnLimitedMessage().equals("1")) {
                                mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                                    @Override
                                    public void onLoadMore() {
                                        //add null , so the adapter will check view_type and show progress bar at bottom
                                        listMessage.add(null);
                                        mAdapter.notifyItemInserted(listMessage.size() - 1);

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                page_count++;
                                                syncMessageMore(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), String.valueOf(page_count));
                                            }
                                        }, 100);

                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    progress.dismiss();
                }
            });
        }
    }

    private void syncMessageMore(final String bundleId, String page) {
        // Get context
        final Context context = BaseActivitySDK.getInstance();

        final String regId = Push.getRegistrationId(context);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String strVersion = String.valueOf(android.os.Build.VERSION.RELEASE);
        String[] version = strVersion.split("\\.");
        String osVersion = version[0] + "." + version[1];

        if (!TextUtils.isEmpty(regId)) {
            ApiManager.syncMessages(bundleId, android_id, regId, "Android", osVersion, page, new ApiManager.OnSyncMessages() {
                @Override
                public void onSyncMessages(boolean isSuccess, String data, String delete_id) {
                    if (isSuccess) {
                        try {
                            JSONArray array = new JSONArray(data);
                            //   remove progress item
                            listMessage.remove(listMessage.size() - 1);
                            mAdapter.notifyItemRemoved(listMessage.size());
                            for(int i=0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                PushMessage message = new PushMessage();
                                message.pushMesageId = object.getString("u_push_done_id");
                                message.pushMesageDate = object.getString("date");
                                message.pushMessageTitle= object.getString("title");
                                message.pushMesageImageUrl= object.getString("image");
                                if (object.getString("read_flag").equals("0")){
                                    message.pushMesageIsRead = false;
                                }else {
                                    message.pushMesageIsRead = true;
                                }
                                listMessage.add(message);
                                mAdapter.notifyItemInserted(listMessage.size());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.setLoaded();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(SharedPreferenceUtil.getBoolean(SharedPreferenceUtil.KEY_CLICK_NOTIFICATION, false)){
            SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG_HOME,true);
            SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_CLICK_NOTIFICATION,false);
            SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_CLICK_NOTIFICATION_DETAIL,false);
            SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG,false);
            Intent intent = new Intent();
            intent.setClassName(MessageListSDK.this, SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_CLASS_NAME));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BaseActivitySDK.getInstance().startActivity(intent);
            finish();
        }else {
            SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG, false);
            SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG_HOME, true);
            super.onBackPressed();
            finish();
        }
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        Push.setKeyBackHome("0");
        super.onDestroy();
        SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_DIALOG,false);
        SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_CLICK_NOTIFICATION,false);
        SharedPreferenceUtil.putBoolean(SharedPreferenceUtil.KEY_CLICK_NOTIFICATION_DETAIL,false);
    }
}
