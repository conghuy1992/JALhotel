package hotelokura.jalhotels.oneharmony.activity.map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import hotelokura.jalhotels.oneharmony.R;

/**
 * Created by barista5 on 2013/09/30.
 */
public class MyListFragment extends ListFragment {
    OnArticleSelectedListener mListener;

    static MyListFragment newInstance(String[] data, int depth) {

        MyListFragment instance = new MyListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putStringArray("data", data);
        args.putInt("depth", depth);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnArticleSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_map_storelist, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] data = {""};

        if (getArguments() != null && getArguments().getStringArray("data") != null) {
            data = getArguments().getStringArray("data");
            StoreListAdapter adapter = new StoreListAdapter(getActivity().getApplicationContext(),
                    data, getArguments().getInt("depth"));

            setListAdapter(adapter);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        int depth = 1;
        String[] data = {""};
        String selectedData = "";

        if (getArguments() != null && getArguments().getStringArray("data") != null) {
            data = getArguments().getStringArray("data");
            if (data.length > position) {
                selectedData = data[position];
            }
        }

        if (getArguments() != null && getArguments().getInt("depth") != 0) {
            depth = getArguments().getInt("depth");
        }

        mListener.onListItemClick(depth, position, selectedData);
    }

    // Container Activity must implement this interface
    public interface OnArticleSelectedListener {
        public void onListItemClick(int depth, int position, String selectedName);
    }

}
