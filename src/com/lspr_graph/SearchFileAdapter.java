package com.lspr_graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchFileAdapter extends BaseAdapter {

	private SearchFileActivity msearchFileActivity;
	private ArrayList<HashMap<String, Object>> mArrayList = new java.util.ArrayList<HashMap<String, Object>>();

	public SearchFileAdapter(SearchFileActivity searchFileActivity,
			ArrayList<HashMap<String, Object>> mArrayList) {
		// TODO Auto-generated constructor stub
		this.msearchFileActivity = searchFileActivity;
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
			convertView = LayoutInflater.from(msearchFileActivity).inflate(
					R.layout.activity_searchfile_lv, parent, false);
			viewholder.mImageView_type = (ImageView) convertView
					.findViewById(R.id.activity_searchfile_lv_imagetype);
			viewholder.mTextView_filename = (TextView) convertView
					.findViewById(R.id.activity_searchfile_lv_filename);
			viewholder.mTextView_filepath = (TextView) convertView
					.findViewById(R.id.activity_searchfile_lv_filepath);
			convertView.setTag(viewholder);

		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		if (((String) mArrayList.get(position).get("type")).equals("folder")) {

			viewholder.mImageView_type.setImageBitmap(Util.getBitmapFolder());

		} else if (((String) mArrayList.get(position).get("type"))
				.equals("file")) {

			viewholder.mImageView_type.setImageBitmap(Util.getBitmapFile());

		}

		viewholder.mTextView_filename.setText((String) mArrayList.get(position)
				.get("name"));
		viewholder.mTextView_filepath.setText((String) mArrayList.get(position)
				.get("path"));
		return convertView;
	}

	public class ViewHolder {
		ImageView mImageView_type;
		TextView mTextView_filename;
		TextView mTextView_filepath;
	}

}
