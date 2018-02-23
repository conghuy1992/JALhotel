package com.baristapushsdk.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baristapushsdk.Constants;
import com.baristapushsdk.R;
import com.baristapushsdk.entity.PushMessage;
import com.baristapushsdk.ui.OnLoadMoreListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DataAdapter extends RecyclerView.Adapter {
	private final int VIEW_ITEM = 1;
	private final int VIEW_PROG = 0;

	private List<PushMessage> listMessage;

	// The minimum amount of items to have below your current scroll position
	// before loading more.
	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    SimpleDateFormat sdf;
    OnItemClick listenerItem;

	public DataAdapter(List<PushMessage> listmessage, RecyclerView recyclerView, OnItemClick listenerClick) {
        this.listenerItem = listenerClick;
        listMessage = listmessage;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading_list)
                .showImageForEmptyUri(R.drawable.loading_list)
                .showImageOnFail(R.drawable.loading_list).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ALPHA_8).build();

		if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

			final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
					.getLayoutManager();


					recyclerView
					.addOnScrollListener(new RecyclerView.OnScrollListener() {
						@Override
						public void onScrolled(RecyclerView recyclerView,
											   int dx, int dy) {
							super.onScrolled(recyclerView, dx, dy);

							totalItemCount = linearLayoutManager.getItemCount();
							lastVisibleItem = linearLayoutManager
									.findLastVisibleItemPosition();
							if (!loading
									&& totalItemCount <= (lastVisibleItem + visibleThreshold)) {
								// End has been reached
								// Do something
								if (onLoadMoreListener != null) {
									onLoadMoreListener.onLoadMore();
								}
								loading = true;
							}
						}
					});
		}
	}

	@Override
	public int getItemViewType(int position) {
		return listMessage.get(position) != null ? VIEW_ITEM : VIEW_PROG;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		RecyclerView.ViewHolder vh;
		if (viewType == VIEW_ITEM) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.item_notification, parent, false);

			vh = new MessageViewHolder(v, listenerItem);
		} else {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.progress_item, parent, false);

			vh = new ProgressViewHolder(v);
		}
		return vh;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof MessageViewHolder) {

            PushMessage pushMessage = listMessage.get(position);
            ((MessageViewHolder) holder).position = position;

            ((MessageViewHolder) holder).tvTitle.setText(pushMessage.pushMessageTitle + "");

            sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());
            Date date1 = new Date(pushMessage.pushMesageDate);
            if (pushMessage.pushMesageDate != null)
                ((MessageViewHolder) holder).tvDate.setText(sdf.format(date1));

            if (pushMessage.pushMesageIsRead) {
                ((MessageViewHolder) holder).imgIconNew.setVisibility(View.INVISIBLE);
                ((MessageViewHolder) holder).tvTitle.setTextColor(Color.BLACK);
                ((MessageViewHolder) holder).tvDate.setTextColor(Color.BLACK);
            } else {
                ((MessageViewHolder) holder).imgIconNew.setVisibility(View.VISIBLE);
                ((MessageViewHolder) holder).tvTitle.setTextColor(Color.BLACK);
                ((MessageViewHolder) holder).tvDate.setTextColor(Color.BLACK);
            }
            if (pushMessage.pushMesageImageUrl.equals("")) {
                ((MessageViewHolder) holder).imgView.setVisibility(View.GONE);
            } else {
                ((MessageViewHolder) holder).imgView.setVisibility(View.VISIBLE);
                //load image using library
                ImageLoader.getInstance().displayImage(
                        Constants.BASE_IMAGE_THUMBNAIL_URL + pushMessage.pushMesageImageUrl,
                        ((MessageViewHolder) holder).imgView, options,
                        animateFirstListener);
            }
			
		} else {
			((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
		}
	}

	public void setLoaded() {
		loading = false;
	}

	@Override
	public int getItemCount() {
		return listMessage.size();
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}

    public interface OnItemClick {
        void onItemClick(int position);
    }

	//
	public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvDate;
        public ImageView imgIconNew, imgView;
        public View line;
        public LinearLayout layoutContent;
        public int position;
        public OnItemClick listener1;

        public MessageViewHolder(View v, OnItemClick listenerClick) {
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

	public static class ProgressViewHolder extends RecyclerView.ViewHolder {
		public ProgressBar progressBar;

		public ProgressViewHolder(View v) {
			super(v);
			progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
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