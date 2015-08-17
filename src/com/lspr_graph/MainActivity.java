package com.lspr_graph;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

public class MainActivity extends Activity {

	private String TAG = "MainActivity";

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				Log.e(TAG, "handleMessage");
				Intent intent = new Intent(MainActivity.this,
						ChooseActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				finish();
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Thread myThread = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					sleep(2500);
					Log.e(TAG, "myThread:2500");
					Message message = new Message();
					message.what = 0x123;
					handler.sendMessage(message);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		myThread.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
