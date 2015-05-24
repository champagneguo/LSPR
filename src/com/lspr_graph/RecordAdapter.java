package com.lspr_graph;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, Object>> mArrayList = new java.util.ArrayList<HashMap<String, Object>>();
	private RecordsActivity recordsActivity;

	public RecordAdapter(RecordsActivity recordsActivity,
			ArrayList<HashMap<String, Object>> mArrayList) {
		// TODO Auto-generated constructor stub
		this.mArrayList = mArrayList;
		this.recordsActivity = recordsActivity;
	}

	public void setArrayList(ArrayList<HashMap<String, Object>> mArrayList) {
		this.mArrayList = mArrayList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder;
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = LayoutInflater.from(recordsActivity).inflate(
					R.layout.activity_record_lv, parent, false);
			viewholder.mTextView_number = (TextView) convertView
					.findViewById(R.id.activity_record_lv_number);
			viewholder.mTextView_result = (TextView) convertView
					.findViewById(R.id.activity_record_lv_result);
			viewholder.mTextView_time = (TextView) convertView
					.findViewById(R.id.activity_record_lv_time);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		viewholder.mTextView_number.setText(String.valueOf(position + 1));
		viewholder.mTextView_result.setText((String) mArrayList.get(position)
				.get("result"));
		viewholder.mTextView_time.setText(DateFormat.format("yyyy-MM-dd",
				((Date) mArrayList.get(position).get("time"))));

		return convertView;
	}

	class ViewHolder {

		TextView mTextView_number, mTextView_result, mTextView_time;
	}

}
