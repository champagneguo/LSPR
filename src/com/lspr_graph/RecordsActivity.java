package com.lspr_graph;

import java.util.ArrayList;
import java.util.HashMap;

import com.lspr.greendao.DaoMaster;
import com.lspr.greendao.DaoSession;
import com.lspr.greendao.record;
import com.lspr.greendao.recordDao;
import com.lspr.greendao.DaoMaster.DevOpenHelper;

import de.greenrobot.dao.query.QueryBuilder;

import android.app.Activity;
import android.app.DownloadManager.Query;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

public class RecordsActivity extends Activity {

	private static final String TAG = "RecordsActivity";
	private ListView mListView;
	private RecordAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mArrayList = new ArrayList<HashMap<String, Object>>();
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private recordDao mrecordDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record);
		DevOpenHelper myHelper = new DevOpenHelper(RecordsActivity.this,
				"lsprDao.db", null);
		db = myHelper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		mrecordDao = daoSession.getRecordDao();

		mListView = (ListView) findViewById(R.id.activity_record_lv);
		mAdapter = new RecordAdapter(RecordsActivity.this, mArrayList);
		mListView.setAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mArrayList.clear();
		QueryBuilder<record> qb = mrecordDao.queryBuilder();
		for (int i = 0; i < qb.buildCount().count(); i++) {
			HashMap<String, Object> hashmap = new HashMap<String, Object>();
			hashmap.put("result", qb.list().get(i).getResult());
			hashmap.put("time", qb.list().get(i).getDate());
			mArrayList.add(hashmap);
		}
		mAdapter.setArrayList(mArrayList);
		mAdapter.notifyDataSetChanged();

	}
}
