package hotelokura.jalhotels.oneharmony.activity.catalog;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseIntArray;

public class CatalogPagerAdapter extends FragmentPagerAdapter {
	static final String TAG = "CatalogPagerAdapter";

	private ArrayList<SparseIntArray> pageStructureList;

	public CatalogPagerAdapter(FragmentActivity context,
			CatalogPagerView pagerView) {
		super(context.getSupportFragmentManager());
	}

	@Override
	public int getCount() {
		if (pageStructureList == null) {
			return 0;
		}
		return pageStructureList.size();
	}

	@Override
	public Fragment getItem(int position) {
		return CatalogPagerFragment.newInstance(position, this);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public ArrayList<SparseIntArray> getPageStructureList() {
		return pageStructureList;
	}

	public void setPageStructureList(ArrayList<SparseIntArray> pageStructureList) {
		this.pageStructureList = pageStructureList;
		notifyDataSetChanged();
	}
}
