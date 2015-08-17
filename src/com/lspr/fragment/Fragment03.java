package com.lspr.fragment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.lspr.chart.SplineChart02View;
import com.lspr.chart.SplineChart02_Normal;
import com.lspr.util.Util;
import com.lspr_graph.R;

public class Fragment03 extends Fragment {

	private View parentView;
	private FrameLayout framelayout;
	private SplineChart02View splinechartm;
	private String TAG = "Fragment03";
	private String Title_Second = "标准图";
	private SplineChart02_Normal splineChart02_Normal;
	private Button mButton_Normal;
	private FrameLayout.LayoutParams frameParm;
	private DisplayMetrics dm;
	private int scrWidth;
	private int scrHeigth;
	private RelativeLayout.LayoutParams layoutParams;
	private RelativeLayout relativeLayout;
	private boolean Flag = false;
	private Double mM_Normal_Key[], mM_Normal_Value[];
	private Double mM_Max = 0D, mM_Min = 0D, mM_Temp = 0D;
	private HashMap<String, Double> mHashMap = new HashMap<String, Double>();

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.e(TAG, "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCreateView");
		parentView = inflater.inflate(R.layout.fragment03, container, false);
		mButton_Normal = (Button) parentView
				.findViewById(R.id.fragment03_normalization);
		mButton_Normal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Flag = true;

				Util.M_Normal.clear();
				mM_Normal_Key = new Double[Util.M.size()];
				mM_Normal_Value = new Double[Util.M.size()];
				int j = 0;
				Iterator it = Util.M.entrySet().iterator();
				while (it.hasNext()) {
					Entry entry = (Entry) it.next();
					mM_Normal_Key[j] = ((Double) entry.getKey());
					mM_Normal_Value[j] = ((Double) entry.getValue());
					j++;
				}
				Log.e(TAG, "onClick----------j::::::" + j);
				mM_Max = mM_Normal_Value[0];
				mM_Min = mM_Normal_Value[0];
				for (int i = 0; i < Util.M.size(); i++) {

					if (mM_Normal_Value[i] > mM_Max) {
						mM_Max = mM_Normal_Value[i];
					}
					if (mM_Normal_Value[i] < mM_Min) {
						mM_Min = mM_Normal_Value[i];
					}
				}
				Log.e(TAG, "");
				mM_Temp = mM_Max - mM_Min;
				Log.e(TAG, "onClick-------------333:mT_Temp:" + mM_Temp);

				for (int i = 0; i < Util.M.size(); i++) {
					Util.M_Normal.put(mM_Normal_Key[i],
							(mM_Normal_Value[i] - mM_Min) / mM_Temp);
					if (mM_Normal_Value[i] == mM_Max) {
						mHashMap.put("Peak_X", mM_Normal_Key[i]);
						mHashMap.put("Peak_Y", 1D);
					}
				}
				Log.e(TAG, "onClick----------------");
				Title_Second = "线性归一";
				relativeLayout.removeAllViews();
				framelayout.removeView(relativeLayout);
				layoutParams = new RelativeLayout.LayoutParams(scrWidth,
						scrHeigth);
				// 居中显示；
				layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
				splineChart02_Normal = new SplineChart02_Normal(getActivity(),
						Util.M_Normal, Title_Second, mHashMap);
				relativeLayout = new RelativeLayout(getActivity());
				relativeLayout.addView(splineChart02_Normal, layoutParams);
				framelayout.addView(relativeLayout);
			}
		});

		// 添加放大缩小控件
		// framelayout.addView(mZoomControls);

		return parentView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Flag == false) {

			framelayout = (FrameLayout) parentView
					.findViewById(R.id.fragment03_framelayout);

			splinechartm = new SplineChart02View(getActivity(), Util.M);

			// mZoomControls控件的布局
			frameParm = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			frameParm.gravity = Gravity.BOTTOM | Gravity.RIGHT;

			// 图表控件；
			dm = getResources().getDisplayMetrics();
			scrWidth = (int) (dm.widthPixels * 0.98);
			scrHeigth = (int) (dm.heightPixels * 0.9);
			layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeigth);
			// 居中显示；
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			relativeLayout = new RelativeLayout(getActivity());
			relativeLayout.addView(splinechartm, layoutParams);

			framelayout.addView(relativeLayout);

		} else {

			// TODO Auto-generated method stub
			Flag = true;

			Util.M_Normal.clear();
			mM_Normal_Key = new Double[Util.M.size()];
			mM_Normal_Value = new Double[Util.M.size()];
			int j = 0;
			Iterator it = Util.M.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				mM_Normal_Key[j] = ((Double) entry.getKey());
				mM_Normal_Value[j] = ((Double) entry.getValue());
				j++;
			}
			Log.e(TAG, "onClick----------j::::::" + j);
			mM_Max = mM_Normal_Value[0];
			mM_Min = mM_Normal_Value[0];
			for (int i = 0; i < Util.M.size(); i++) {

				if (mM_Normal_Value[i] > mM_Max) {
					mM_Max = mM_Normal_Value[i];
				}
				if (mM_Normal_Value[i] < mM_Min) {
					mM_Min = mM_Normal_Value[i];
				}
			}
			Log.e(TAG, "");
			mM_Temp = mM_Max - mM_Min;
			Log.e(TAG, "onClick-------------333:mT_Temp:" + mM_Temp);

			for (int i = 0; i < Util.M.size(); i++) {
				Util.M_Normal.put(mM_Normal_Key[i],
						(mM_Normal_Value[i] - mM_Min) / mM_Temp);
				if (mM_Normal_Value[i] == mM_Max) {
					mHashMap.put("Peak_X", mM_Normal_Key[i]);
					mHashMap.put("Peak_Y", 1D);
				}
			}
			Log.e(TAG, "onClick----------------");
			Title_Second = "线性归一";
			relativeLayout.removeAllViews();
			framelayout.removeView(relativeLayout);
			layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeigth);
			// 居中显示；
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			splineChart02_Normal = new SplineChart02_Normal(getActivity(),
					Util.M_Normal, Title_Second, mHashMap);
			relativeLayout = new RelativeLayout(getActivity());
			relativeLayout.addView(splineChart02_Normal, layoutParams);
			framelayout.addView(relativeLayout);

		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		Log.e(TAG, "onPause()");
	}

}
