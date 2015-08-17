package com.lspr_graph;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import com.lspr.util.RootCmd;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RealDataDetailActivity extends Activity implements OnClickListener {

	private static final String TAG = "RealDataDetailActivity";
	private Button mButton_Close, mButton_Save;
	private TextView mTextView_datatime, mTextView_type,
			mTextView_serialnumber, mTextView_integration, mTextView_pixel,
			mTextView_wavelength;
	private int mIntegrationTime;
	private int length;
	private String mSpectrumType, mSpectrumSerialNumber;
	private Double mSpectrumValues[];
	private Double mWavelengthValues[];
	private LinkedHashMap<Double, Double> mLinkedHashMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_realdata_detail);
		init();
	}

	public void init() {

		mButton_Close = (Button) findViewById(R.id.activity_realdata_btn_close);
		mButton_Save = (Button) findViewById(R.id.activity_realdata_btn_save);
		mTextView_datatime = (TextView) findViewById(R.id.activity_realdetail_tv_datatimecontent);
		mTextView_type = (TextView) findViewById(R.id.activity_realdetail_tv_typecontent);
		mTextView_serialnumber = (TextView) findViewById(R.id.activity_realdetail_tv_serialnumbercontent);
		mTextView_integration = (TextView) findViewById(R.id.activity_realdetail_tv_integrationcontent);
		mTextView_pixel = (TextView) findViewById(R.id.activity_realdetail_tv_spectrumpixelcontent);
		mTextView_wavelength = (TextView) findViewById(R.id.activity_realdetail_tv_wavelengthcontent);
		mButton_Close.setOnClickListener(this);
		mButton_Save.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.activity_realdata_btn_close:
			finish();
			break;

		case R.id.activity_realdata_btn_save:
			finish();
			break;

		default:
			break;
		}

	}
}
