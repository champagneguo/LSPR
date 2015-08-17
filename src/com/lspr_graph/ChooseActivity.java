package com.lspr_graph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ChooseActivity extends Activity implements OnClickListener {

	private static final String TAG = "ChooseActivity";
	private Button mButton_RealData, mButton_LocalData;
	private long waitTime = 2000;
	private long touchTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);
		init();
	}

	public void init() {
		mButton_RealData = (Button) findViewById(R.id.activity_choose_realdata);
		mButton_LocalData = (Button) findViewById(R.id.activity_choose_localdata);
		mButton_LocalData.setOnClickListener(this);
		mButton_RealData.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_choose_localdata:

			Intent intent = new Intent(ChooseActivity.this,
					LoadFilesActivity.class);
			startActivity(intent);
			break;

		case R.id.activity_choose_realdata:

			Intent intent1 = new Intent(ChooseActivity.this,
					RealDataActivity.class);
			startActivity(intent1);
			break;

		default:
			break;
		}
	}

	// 实现再按一次推出系统。
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				touchTime = currentTime;
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
