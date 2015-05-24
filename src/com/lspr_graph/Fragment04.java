package com.lspr_graph;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Fragment04 extends Fragment {
	private View parentView;
	private Fragment04Adapter mAdapter;
	private ArrayList<HashMap<String, Object>> mArrayList = new ArrayList<HashMap<String, Object>>();
	private MenuActivity mMenuActivity;
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mMenuActivity = (MenuActivity) this.getActivity();
		mArrayList = mMenuActivity.getArrayList();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		parentView = inflater.inflate(R.layout.fragment04, container, false);
		mListView = (ListView) parentView
				.findViewById(R.id.inspection_record_lv);
		mAdapter = new Fragment04Adapter(mMenuActivity, mArrayList);
		mListView.setAdapter(mAdapter);
		return parentView;
	}

}
