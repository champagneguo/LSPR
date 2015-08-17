package com.lspr_graph;

import java.text.SimpleDateFormat;
import com.lspr.util.RootCmd;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class RealDataActivity extends Activity implements OnClickListener {

	private static final String TAG = "RealDataActivity";
	private Button mButton_Check, mButton_Start;
	private ProgressDialog mProgressDialog;
	private StringBuilder stringBuidler;
	private MyThread myThread;
	private SimpleDateFormat df;
	private Handler myhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x01:
				mProgressDialog.dismiss();
				df = new SimpleDateFormat("hh:mm:ss yyyy/MM/dd");

				break;

			case 0x02:
				mProgressDialog.dismiss();
				df = new SimpleDateFormat("hh:mm:ss yyyy/MM/dd");

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_realdata);
		init();
	}

	public void init() {
		mButton_Check = (Button) findViewById(R.id.activity_realdata_check);
		mButton_Start = (Button) findViewById(R.id.activity_realdata_start);
		mButton_Start.setOnClickListener(this);
		mButton_Check.setOnClickListener(this);
		mProgressDialog = new ProgressDialog(RealDataActivity.this);
		stringBuidler = new StringBuilder();
		myThread = new MyThread();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_realdata_check:
			Intent intent = new Intent(RealDataActivity.this,
					RealDataDetailActivity.class);
			startActivity(intent);
			break;
		case R.id.activity_realdata_start:
			mProgressDialog.setTitle("温馨提示：");
			mProgressDialog.setMessage("正在加载数据请稍后...");
			mProgressDialog.show();
			myThread.start();

			if (RootCmd.haveRoot()) {

				RootCmd.execRootCmdSilent("chmod 777 /dev/bus/usb/001/*");

				Log.e(TAG, "RootCmd------>>>>>>");

			} else {
				Toast.makeText(RealDataActivity.this, "设备没有root：",
						Toast.LENGTH_SHORT);
			}

			break;
		default:
			break;
		}

	}

	public class MyThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (testInit()) {

			} else {
			}
			Message message = myhandler.obtainMessage(0x02);
			myhandler.sendMessage(message);
		}

	}

	public native boolean testInit();

	public native int testGetInteragtion();

	public native String testGetSpectrumType();

	public native String testGetSpectrumNumber();

	public native int testGetLength();

	public native double testGetSpectrumValues(int a);

	public native double testGetWavelengthValues(int b);

	static {
		System.loadLibrary("api_test");
	}
}
