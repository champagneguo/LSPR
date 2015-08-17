package com.lspr_graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

import com.lspr.adapter.SearchFileAdapter;
import com.lspr.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchFileActivity extends Activity {

	public static final String TAG = "SearchFileActivity";
	private TextView mTextView_title;
	private ListView mListView;
	private SearchFileAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mArrayList;
	private ProgressDialog mProgressDialog;
	private MyAsyncTask myAsyncTask;
	private String title;
	private final Uri File_URI = MediaStore.Files.getContentUri("external");
	private static final String[] PROJECTIONS_File = new String[] {
			BaseColumns._ID, MediaStore.Files.FileColumns.TITLE,
			MediaStore.Files.FileColumns.DATA,
			MediaStore.Files.FileColumns.MIME_TYPE,
			MediaStore.Files.FileColumns.PARENT };
	private int mParentID = 0;
	private File file;
	private String type = "text/plain";
	private Stack<Integer> mStack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchfile);

		updateUI();

	}

	public void updateUI() {

		Util.setBitmapFolder(SearchFileActivity.this,
				R.drawable.activity_loadfiles_folder);
		Util.setBitmapFile(SearchFileActivity.this,
				R.drawable.activity_loadfile_file);
		title = this.getIntent().getStringExtra("title");
		mArrayList = new ArrayList<HashMap<String, Object>>();
		mTextView_title = (TextView) findViewById(R.id.activity_searchfile_title);
		mListView = (ListView) findViewById(R.id.activity_searchfile_lv);
		mAdapter = new SearchFileAdapter(SearchFileActivity.this, mArrayList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				if (((String) mArrayList.get(position).get("type"))
						.equals("folder")) {
					mStack.push((Integer) mArrayList.get(position).get(
							"parentid"));
					enterIntoFolder(
							(String) mArrayList.get(position).get("path"),
							(String) mArrayList.get(position).get("name"),
							(Long) mArrayList.get(position).get("_id"));

				} else if (((String) mArrayList.get(position).get("type"))
						.equals("file")) {

					loadfile((String) mArrayList.get(position).get("path"),
							(String) mArrayList.get(position).get("name"),
							(Long) mArrayList.get(position).get("_id"));

				}

			}
		});
		mTextView_title.setText(title);
		mStack = new Stack<Integer>();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e(TAG, "onResume:----------" + mParentID);
		String sdState = Environment.getExternalStorageState();
		if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		mStack.clear();
		searchFile(mParentID);
		mStack.push(mParentID);

	}

	public void searchFile(int id) {
		mArrayList.clear();
		Cursor cursor = this.getContentResolver().query(File_URI,
				PROJECTIONS_File, MediaStore.Files.FileColumns.PARENT + " = ?",
				new String[] { String.valueOf(id) }, null);
		cursor.moveToFirst();
		Log.e(TAG, "searchFile:---------cursorcount:" + cursor.getCount());
		if (cursor.getCount() > 0) {
			do {
				file = new File(cursor.getString(cursor
						.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
				Log.e(TAG, "file:-------------->>>>" + file);
				if (file.isDirectory()
						|| type.equals(cursor.getString(cursor
								.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)))) {

					HashMap<String, Object> hashmap = new HashMap<String, Object>();
					hashmap.put("_id", cursor.getLong(cursor
							.getColumnIndex(BaseColumns._ID)));
					hashmap.put(
							"name",
							cursor.getString(cursor
									.getColumnIndex(MediaStore.Files.FileColumns.TITLE)));
					hashmap.put("path", cursor.getString(cursor
							.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
					hashmap.put(
							"parentid",
							cursor.getInt(cursor
									.getColumnIndex(MediaStore.Files.FileColumns.PARENT)));
					if (file.isDirectory()) {
						hashmap.put("type", "folder");
					} else if (file.isFile()) {
						hashmap.put("type", "file");
					}
					mArrayList.add(hashmap);
				}

			} while (cursor.moveToNext());

		} else {
			Toast.makeText(SearchFileActivity.this, "当前文件夹没有文件",
					Toast.LENGTH_SHORT).show();
		}

		cursor.close();
		cursor = null;
		Log.e(TAG, "mArrayList:-----------:" + mArrayList.size());

	}

	public void searchFile(long id) {
		mArrayList.clear();
		Cursor cursor = this.getContentResolver().query(File_URI,
				PROJECTIONS_File, MediaStore.Files.FileColumns.PARENT + " = ?",
				new String[] { String.valueOf(id) }, null);
		cursor.moveToFirst();
		Log.e(TAG, "searchFile:---------cursorcount:" + cursor.getCount());
		if (cursor.getCount() > 0) {
			do {
				file = new File(cursor.getString(cursor
						.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
				Log.e(TAG, "file:-------------->>>>" + file);
				if (file.isDirectory()
						|| type.equals(cursor.getString(cursor
								.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)))) {

					HashMap<String, Object> hashmap = new HashMap<String, Object>();
					hashmap.put("_id", cursor.getLong(cursor
							.getColumnIndex(BaseColumns._ID)));
					hashmap.put(
							"name",
							cursor.getString(cursor
									.getColumnIndex(MediaStore.Files.FileColumns.TITLE)));
					hashmap.put("path", cursor.getString(cursor
							.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
					hashmap.put(
							"parentid",
							cursor.getInt(cursor
									.getColumnIndex(MediaStore.Files.FileColumns.PARENT)));
					if (file.isDirectory()) {
						hashmap.put("type", "folder");
					} else {
						hashmap.put("type", "file");
					}
					mArrayList.add(hashmap);
				}

			} while (cursor.moveToNext());

		} else {
			Toast.makeText(SearchFileActivity.this, "当前文件夹没有文件",
					Toast.LENGTH_SHORT).show();
		}

		cursor.close();
		cursor = null;
		Log.e(TAG, "mArrayList:-----------:" + mArrayList.size());

	}

	public void enterIntoFolder(String path, String name, Long id) {
		// TODO Auto-generated method stub

		Toast.makeText(SearchFileActivity.this, "进入：" + name + "文件夹",
				Toast.LENGTH_SHORT).show();
		searchFile(id);

		mAdapter.notifyDataSetChanged();

	}

	public void loadfile(String path, String name, Long id) {
		// TODO Auto-generated method stub

		if (title.contains("S")) {

			Util.Temp_S.clear();
			Log.e(TAG, "title:----------->>>>>" + title);

		} else if (title.contains("D")) {

			Util.Temp_D.clear();
			Log.e(TAG, "title:----------->>>>>" + title);

		} else if (title.contains("R")) {

			Util.Temp_R.clear();
			Log.e(TAG, "title:----------->>>>>" + title);

		}
		myAsyncTask = new MyAsyncTask(SearchFileActivity.this);
		myAsyncTask.execute(path, name);
		mProgressDialog = new ProgressDialog(SearchFileActivity.this);
		mProgressDialog.setTitle(title);
		mProgressDialog.setMessage("请稍等。。。");
		mProgressDialog.show();

	}

	public class MyAsyncTask extends AsyncTask<String, Integer, String> {

		public MyAsyncTask(SearchFileActivity searchFileActivity) {
			// TODO Auto-generated constructor stub

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String stringtemp = null;
			int count = 0;
			Double temp1, temp2;

			Util.Temp.clear();

			try {
				File file = new File(params[0]);
				InputStream is = new FileInputStream(file);
				InputStreamReader isReader = new InputStreamReader(is);
				BufferedReader bufferReader = new BufferedReader(isReader);
				try {
					while ((stringtemp = bufferReader.readLine()) != null) {
						count++;
						if (count >= 18 && count <= 1061) {
							temp1 = Double.parseDouble(stringtemp.substring(0,
									6));
							if (temp1 > 400 && temp1 < 901) {
								Log.e(TAG, "temp1:-------" + temp1);
								Util.Temp.add(temp1);
								temp2 = Double.parseDouble(stringtemp
										.substring(7, 14));
								Log.e(TAG, "temp2:-------" + temp2);

								if (title.contains("S")) {
									Log.e(TAG, "title:=-=====" + title);
									Util.Temp_S.add(temp2);

								} else if (title.contains("R")) {
									Log.e(TAG, "title:=-=====" + title);

									Util.Temp_R.add(temp2);

								} else if (title.contains("D")) {
									Log.e(TAG, "title:=-=====" + title);

									Util.Temp_D.add(temp2);

								}
							}
						}

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return params[1];
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (mStack.size() == 0) {
			finish();
		} else {
			Log.e(TAG, "onBackPressed:::" + mStack.peek());
			searchFile(mStack.pop());
			mAdapter.notifyDataSetChanged();
		}

	}

}
