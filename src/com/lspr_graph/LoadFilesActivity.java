package com.lspr_graph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import com.lspr.greendao.DaoMaster;
import com.lspr.greendao.DaoMaster.DevOpenHelper;
import com.lspr.greendao.DaoSession;
import com.lspr.greendao.record;
import com.lspr.greendao.recordDao;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoadFilesActivity extends Activity implements OnClickListener {

	private static final String TAG = "LoadFilesActivity";
	private Button Input_S, Input_R, Input_D, Input_confirm, Record;
	private TextView S_Path, R_Path, D_Path;
	private boolean flag_s = false, flag_r = false, flag_d = false;
	private Double tempT, tempM, tempT_X_Min, tempT_Y_Min;
	private ArrayList<Double> temp_T_X = new ArrayList<Double>();
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private recordDao mrecordDao;
	private MyAsyncTask myAsyncTask;
	private ProgressDialog mProressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loadfile);
		initData();
		// initNumbers();
	}

	public void initData() {

		Input_S = (Button) findViewById(R.id.activity_loadfile_s);
		Input_R = (Button) findViewById(R.id.activity_loadfile_r);
		Input_D = (Button) findViewById(R.id.activity_loadfile_d);
		Input_confirm = (Button) findViewById(R.id.activity_loadfile_confirm);
		Record = (Button) findViewById(R.id.activity_loadfile_record);
		S_Path = (TextView) findViewById(R.id.activity_loadfile_path_s);
		R_Path = (TextView) findViewById(R.id.activity_loadfiel_path_r);
		D_Path = (TextView) findViewById(R.id.activity_loadfile_path_d);
		Util.T.clear();
		Util.M.clear();
		DevOpenHelper myHelper = new DevOpenHelper(LoadFilesActivity.this,
				"lsprDao.db", null);
		db = myHelper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		mrecordDao = daoSession.getRecordDao();
		Input_S.setOnClickListener(this);
		Input_R.setOnClickListener(this);
		Input_D.setOnClickListener(this);
		Input_confirm.setOnClickListener(this);
		Record.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_loadfile_record:

			Intent intent1 = new Intent(LoadFilesActivity.this,
					RecordsActivity.class);
			startActivity(intent1);

			break;
		case R.id.activity_loadfile_s:

			flag_s = true;
			S_Path.setText("fragment01_path_s");
			Intent intent00 = new Intent(LoadFilesActivity.this,
					SearchFileActivity.class);
			intent00.putExtra("title", "加载S文件");
			startActivity(intent00);
			break;
		case R.id.activity_loadfile_r:

			flag_r = true;
			R_Path.setText("fragment01_path_r");
			Intent intent01 = new Intent(LoadFilesActivity.this,
					SearchFileActivity.class);
			intent01.putExtra("title", "加载R文件");
			startActivity(intent01);

			break;
		case R.id.activity_loadfile_d:

			flag_d = true;
			D_Path.setText("fragment01_path_d");
			Intent intent02 = new Intent(LoadFilesActivity.this,
					SearchFileActivity.class);
			intent02.putExtra("title", "加载D文件");
			startActivity(intent02);

			break;
		case R.id.activity_loadfile_confirm:

			if (flag_s && flag_r && flag_d) {

				mProressDialog = new ProgressDialog(LoadFilesActivity.this);
				mProressDialog.setTitle("正在生成图表");
				mProressDialog.setMessage("请稍等。。。");
				myAsyncTask = new MyAsyncTask();
				myAsyncTask.execute();
				mProressDialog.show();

			} else {
				Toast.makeText(LoadFilesActivity.this, "文件尚未加载完全",
						Toast.LENGTH_SHORT).show();
			}

			break;

		default:
			break;
		}

	}

	public class MyAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			for (int i = 0; i < Util.Temp.size(); i++) {

				tempT = (Util.Temp_S.get(i) - Util.Temp_D.get(i))
						/ (Util.Temp_R.get(i) - Util.Temp_D.get(i));
				tempM = -Math.log10(tempT);
				BigDecimal b = new BigDecimal(tempT);
				tempT = b.setScale(6, BigDecimal.ROUND_UP).doubleValue();
				BigDecimal c = new BigDecimal(tempM);
				tempM = c.setScale(6, BigDecimal.ROUND_UP).doubleValue();

				if (i == 0) {
					tempT_Y_Min = tempT;
				}
				if (tempT < tempT_Y_Min) {
					tempT_Y_Min = tempT;
				}
				temp_T_X.add(Util.Temp.get(i));
				Util.T.put(Util.Temp.get(i), tempT);
				Util.M.put(Util.Temp.get(i), tempM);
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			tempT_X_Min = temp_T_X.get(0);
			for (int j = 0; j < temp_T_X.size(); j++) {
				if (tempT_X_Min < temp_T_X.get(j)) {
					tempT_X_Min = temp_T_X.get(j);
				}
			}
			record mrecord = new record();
			mrecord.setResult("T值：(" + tempT_X_Min + "," + tempT_Y_Min + ")");
			mrecord.setDate(new Date());
			mrecordDao.insert(mrecord);
			Intent intent = new Intent(LoadFilesActivity.this,
					MenuActivity.class);
			startActivity(intent);
			mProressDialog.dismiss();
		}

	}
}
