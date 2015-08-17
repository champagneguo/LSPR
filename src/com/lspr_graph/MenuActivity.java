package com.lspr_graph;

import java.util.ArrayList;
import java.util.HashMap;

import com.lspr.fragment.Fragment02;
import com.lspr.fragment.Fragment03;
import com.lspr.fragment.Fragment04;
import com.lspr.greendao.DaoMaster;
import com.lspr.greendao.DaoSession;
import com.lspr.greendao.record;
import com.lspr.greendao.recordDao;
import com.lspr.greendao.DaoMaster.DevOpenHelper;
import com.lspr.util.Util;

import de.greenrobot.dao.query.QueryBuilder;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class MenuActivity extends FragmentActivity {

	long waitTime = 2000;
	long touchTime = 0;
	private String TAG = "MenuActivity";
	private Class[] fragments = { Fragment02.class, Fragment03.class,
			Fragment04.class };
	private RadioGroup radioGroup;
	private FragmentTabHost mTabHost;
	private int count = 0;
	private ArrayList<HashMap<String, Object>> mArrayList = new ArrayList<HashMap<String, Object>>();
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private recordDao mrecordDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {

			// ͸��״̬��

			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			// ͸��������

			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		}
		setContentView(R.layout.activity_menu);
		Log.e(TAG, "onCreate");
		// callback = (CallBack) MenuActivity.this;
		DevOpenHelper myHelper = new DevOpenHelper(MenuActivity.this,
				"lsprDao.db", null);
		db = myHelper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		mrecordDao = daoSession.getRecordDao();

		radioGroup = (RadioGroup) findViewById(R.id.activitymenu_radio_rg);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

		mTabHost.setup(this, getSupportFragmentManager(),
				R.id.activitymenu_tabcontent);
		Log.e(TAG, "mTabHost.setup");
		// Fragment�ĸ�����
		count = fragments.length;
		for (int i = 0; i < count; i++) {
			// Ϊÿ��Tab����ͼ������ݣ�
			TabSpec tabSpect = mTabHost.newTabSpec(i + "").setIndicator(i + "");
			// ��Tab��ť��ӵ�Tabѡ��У�

			mTabHost.addTab(tabSpect, fragments[i], null);

		}

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.tab_rb_2:
					mTabHost.setCurrentTab(0);
					break;
				case R.id.tab_rb_3:
					mTabHost.setCurrentTab(1);
					break;
				case R.id.tab_rb_4:
					mTabHost.setCurrentTab(2);
					break;
				}

			}
		});
		// ���δ����ʱ��Ĭ���ڵ�һ��Fragment��
		mTabHost.setCurrentTab(0);

	}

	public void getState(boolean flag) {

		Log.e(TAG, "Fragment01.CallBack:" + flag);
		Util.Flag = flag;

	}

	// ʵ���ٰ�һ���Ƴ�ϵͳ��
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				Toast.makeText(this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT).show();
				touchTime = currentTime;
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
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

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e(TAG, "onPause()");
	}

	public ArrayList<HashMap<String, Object>> getArrayList() {
		return mArrayList;
	}

}
