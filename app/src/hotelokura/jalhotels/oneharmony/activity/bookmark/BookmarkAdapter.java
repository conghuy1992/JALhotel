package hotelokura.jalhotels.oneharmony.activity.bookmark;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class BookmarkAdapter extends BaseAdapter {
	private Context context;
	private BookmarkDatas datas;

	private int deleteStyle = 0;
	private int deletePosition = -1;

	public BookmarkAdapter(Context context, BookmarkDatas datas) {
		super();
		this.context = context;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.getDatas().size();
	}

	@Override
	public Object getItem(int position) {
		return datas.getDatas().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BookmarkItemView view;
		if (convertView == null) {
			view = new BookmarkItemView(context);

		} else {
			view = (BookmarkItemView) convertView;
		}

		BookmarkData data = datas.getDatas().get(position);
		view.setParentAdapter(this);
		view.viewData(datas.isLocal(), datas.getCatalogId(), data);

		if (deleteStyle == 1) {
			// (-)ボタンタップで削除ボタン表示
			view.showDeleteSwitchButton(position);
		}

		if (deletePosition == position) {
			view.deleteMode(true);
			view.setOnDeleteButtonClickLisner(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					datas.remove(deletePosition);
					datas.save();

					deletePosition = -1;
					notifyDataSetChanged();
				}
			});
		} else {
			view.deleteMode(false);
		}
		return view;
	}

	public int getDeleteStyle() {
		return deleteStyle;
	}

	public void setDeleteStyle(int deleteStyle) {
		this.deleteStyle = deleteStyle;
	}

	public int getDeletePosition() {
		return deletePosition;
	}

	public void setDeletePosition(int deletePosition) {
		this.deletePosition = deletePosition;
	}

}
