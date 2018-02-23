package com.baristapushsdk.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.util.Base64;
import android.widget.RemoteViews;

import com.baristapushsdk.ui.MessageBaseActivity;
import com.baristapushsdk.ui.MessageDetailSDK;
import com.baristapushsdk.utils.SharedPreferenceUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.baristapushsdk.Push;
import com.baristapushsdk.R;
import com.baristapushsdk.entity.PushMessage;
import com.baristapushsdk.ui.BaseActivitySDK;
import com.baristapushsdk.ui.MessageListSDK;
import com.baristapushsdk.utils.DebugLog;

/**
 * Created by Trung Kien on 10/6/2015.
 */
public class GcmIntentService extends IntentService {

    private static final String TAG = "GcmIntentService";

    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferenceUtil.setContextApp(getApplicationContext());
        if (SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_RETRIEVE_ACCEPT_PUSH).equals("1")) {
            Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            // The getMessageType() intent parameter must be the intent you received
            // in your BroadcastReceiver.
            String messageType = gcm.getMessageType(intent);
            DebugLog.e("messageType:" + messageType);
            if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

                DebugLog.e("extras:" + extras.toString());
                String json = extras.getString("data");
                if (json == null)
                    return;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault());
                df.setTimeZone(TimeZone.getDefault());

                try {
                    PushMessage message = new PushMessage();

                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject extra_message = jsonObject.getJSONObject("extra");

                    message.pushMesageDate = extra_message.getString("date").trim();
                    message.pushMesageId = extra_message.getString("u_push_done_id");
                    message.pushMessageTitle = jsonObject.getString("title");
                    message.pushMesageImageUrl = extra_message.getString("image");
                    message.pushMesageIsRead = false;

                    DebugLog.d(message.toString());
                    sendNotification(message);

                    if (Push.getUnLimitedMessage().equals("1")) {
                        Push.countBadgeSwitch(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), "0");
                    }else {
                        Push.countBadgeSwitch(SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_BUNDLE_ID), "1");
                    }

                    if (BaseActivitySDK.getInstance() != null) {
                        BaseActivitySDK.getInstance().onReceivedMessage(message);
                    }
                    if (MessageBaseActivity.getInstance() != null) {
                        MessageBaseActivity.getInstance().onReceivedMessageList(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    DebugLog.e("Exception" + e.toString());
                }
            }
            GcmBroadcastReveiver.completeWakefulIntent(intent);
        }
    }

    private void sendNotification(PushMessage pushMessage) {

        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification_custom);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setContent(remoteViews);
        remoteViews.setTextViewText(R.id.title, SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_APP_NAME));
        remoteViews.setTextViewText(R.id.text,pushMessage.pushMessageTitle);

        String previouslyEncodedImage = SharedPreferenceUtil.getString(SharedPreferenceUtil.KEY_APP_ICON);
        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            remoteViews.setImageViewBitmap(R.id.imagenotileft,bitmap);
        }else {
            remoteViews.setImageViewResource(R.id.imagenotileft, R.drawable.round_red);
            mBuilder.setSmallIcon(R.drawable.round_red);
        }
        mBuilder.setSound(alarmSound);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        mBuilder.setAutoCancel(true);

        Intent resultIntent = new Intent(this, MessageDetailSDK.class);
        resultIntent.putExtra("mess_id", pushMessage.pushMesageId);
        resultIntent.putExtra("image", pushMessage.pushMesageImageUrl);
        resultIntent.putExtra("title", pushMessage.pushMessageTitle);
        resultIntent.putExtra("date", pushMessage.pushMesageDate);
        resultIntent.putExtra("isread", pushMessage.pushMesageIsRead);
        resultIntent.putExtra("isNotification", true);

        //This ensures that navigating backward from the Activity leads out of the app to Home page
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent
        stackBuilder.addParentStack(MessageDetailSDK.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        // start the activity when the user clicks the notification text
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // pass the Notification object to the system
        myNotificationManager.notify(10, mBuilder.getNotification());

        wakeUpDevice();
    }

    private void wakeUpDevice(){
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.PARTIAL_WAKE_LOCK, "TAG");
        wl.acquire();
    }
}
