package com.baristapushsdk.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baristapushsdk.Constants;
import com.baristapushsdk.Push;
import com.baristapushsdk.R;
import com.baristapushsdk.entity.PushMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Trung Kien on 10/12/2015.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<PushMessage> listMessage;
    private Context context;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public interface OnItemClick {
        void onItemClick(int position);
    }

    SimpleDateFormat sdf;
    OnItemClick listenerItem;

    public NotificationAdapter(Context context, List<PushMessage> data, OnItemClick listenerClick) {
        super();
        listMessage = data;
        this.listenerItem = listenerClick;
        this.context = context;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading_list)
                .showImageForEmptyUri(R.drawable.loading_list)
                .showImageOnFail(R.drawable.loading_list).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ALPHA_8).build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        ViewHolder vh = new ViewHolder(v, listenerItem);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PushMessage pushMessage = listMessage.get(position);
        holder.position = position;

        holder.tvTitle.setText(pushMessage.pushMessageTitle + "");

        sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        Date date1 = new Date(pushMessage.pushMesageDate);
        if (pushMessage.pushMesageDate != null)
            holder.tvDate.setText(sdf.format(date1));

        if (pushMessage.pushMesageIsRead) {
            holder.imgIconNew.setVisibility(View.INVISIBLE);
            holder.tvTitle.setTextColor(Color.BLACK);
            holder.tvDate.setTextColor(Color.BLACK);
        } else {
            holder.imgIconNew.setVisibility(View.VISIBLE);
            holder.tvTitle.setTextColor(Color.BLACK);
            holder.tvDate.setTextColor(Color.BLACK);
        }
        if (pushMessage.pushMesageImageUrl.equals("")) {
            holder.imgView.setVisibility(View.GONE);
        } else {
            holder.imgView.setVisibility(View.VISIBLE);
            //load image using library
            ImageLoader.getInstance().displayImage(
                    Constants.BASE_IMAGE_THUMBNAIL_URL + pushMessage.pushMesageImageUrl,
                    holder.imgView, options,
                    animateFirstListener);
        }
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvTitle;
        public TextView tvDate;
        public ImageView imgIconNew, imgView;
        public View line;
        public LinearLayout layoutContent;
        public int position;
        public OnItemClick listener1;

        public ViewHolder(View v, OnItemClick listenerClick) {
            super(v);
            this.listener1 = listenerClick;
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            imgIconNew = (ImageView) v.findViewById(R.id.icon_new);
            imgView = (ImageView) v.findViewById(R.id.icon_right);
            layoutContent = (LinearLayout) v.findViewById(R.id.layoutContent);
            layoutContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener1.onItemClick(position);
                }
            });
        }
    }

    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 300);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}