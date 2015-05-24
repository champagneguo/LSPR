package com.lspr_graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.lspr.chart.SplineChart01View;
import com.lspr.chart.SplineChart01_Normal;

import android.app.Activity;
import android.content.Entity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

public class Fragment02 extends Fragment {

	private View parentView;
	private String TAG = "Fragment02";
	private ZoomControls mZoomControls;
	private SplineChart01View splineChartView;
	private SplineChart01_Normal splineChartView_normal;
	private FrameLayout framelayout;
	private String Title_Second = "标准图";
	private Button mButton_Normal;
	private FrameLayout.LayoutParams frameParm;
	private DisplayMetrics dm;
	private int scrWidth;
	private int scrHeigth;
	private RelativeLayout.LayoutParams layoutParams;
	private RelativeLayout relativeLayout;
	private Double mT_Normal_Key[], mT_Normal_Value[];
	private Double mT_Max = 0D, mT_Min = 0D, mT_Temp = 0D;
	private HashMap<String, Double> mHashMap = new HashMap<String, Double>();
	private boolean Flag = false;

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
		if (parentView == null) {
			parentView = inflater
					.inflate(R.layout.fragment02, container, false);
		}
		mButton_Normal = (Button) parentView
				.findViewById(R.id.fragment02_normalization);
		mButton_Normal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.e(TAG, "onClick-------------111");

				Flag = true;
				Util.T_Normal.clear();
				mT_Normal_Key = new Double[Util.T.size()];
				mT_Normal_Value = new Double[Util.T.size()];
				int j = 0;
				Iterator it = Util.T.entrySet().iterator();
				while (it.hasNext()) {
					Entry entry = (Entry) it.next();
					mT_Normal_Key[j] = ((Double) entry.getKey());
					mT_Normal_Value[j] = ((Double) entry.getValue());
					j++;
				}
				Log.e(TAG, "onClick----------j::::::" + j);
				mT_Max = mT_Normal_Value[0];
				mT_Min = mT_Normal_Value[0];
				for (int i = 0; i < Util.T.size(); i++) {

					if (mT_Normal_Value[i] > mT_Max) {
						mT_Max = mT_Normal_Value[i];
					}
					if (mT_Normal_Value[i] < mT_Min) {
						mT_Min = mT_Normal_Value[i];
					}
				}
				Log.e(TAG, "");
				mT_Temp = mT_Max - mT_Min;
				Log.e(TAG, "onClick-------------333:mT_Temp:" + mT_Temp);

				for (int i = 0; i < Util.T.size(); i++) {
					Util.T_Normal.put(mT_Normal_Key[i],
							(mT_Normal_Value[i] - mT_Min) / mT_Temp);
					if (mT_Normal_Value[i] == mT_Min) {
						mHashMap.put("Peak_X", mT_Normal_Key[i]);
						mHashMap.put("Peak_Y", 0D);
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
				splineChartView_normal = new SplineChart01_Normal(
						getActivity(), Util.T_Normal, Title_Second, mHashMap);
				relativeLayout = new RelativeLayout(getActivity());
				relativeLayout.addView(splineChartView_normal, layoutParams);
				framelayout.addView(relativeLayout);
				Log.e(TAG, "framelayout------------");

			}
		});
		ViewGroup parent = (ViewGroup) parentView.getParent();
		Log.e(TAG, "onCreateView:parent:" + parent);
		if (parent != null) {
			parent.removeView(parentView);
		}

		return parentView;
	}

	public class OnZoomInClickListenerImpl implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			splineChartView.zoomIn();
		}

	}

	public class OnZoomOutClickListenerImpl implements OnClickListener {

		@Override
		public void onClick(View v) {
			splineChartView.zoomOut();
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Flag == false) {

			splineChartView = new SplineChart01View(getActivity(), Util.T,
					Title_Second);

			framelayout = (FrameLayout) parentView
					.findViewById(R.id.fragment02_framelayout);

			// mZoomControls控件的布局
			frameParm = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			frameParm.gravity = Gravity.BOTTOM | Gravity.RIGHT;

			// 放大缩小控件；
			mZoomControls = new ZoomControls(getActivity());
			mZoomControls.setIsZoomInEnabled(true);
			mZoomControls.setIsZoomOutEnabled(true);
			mZoomControls.setLayoutParams(frameParm);

			// 图表控件；
			dm = getResources().getDisplayMetrics();
			scrWidth = (int) (dm.widthPixels * 0.98);
			scrHeigth = (int) (dm.heightPixels * 0.9);
			layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeigth);
			// 居中显示；
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			relativeLayout = new RelativeLayout(getActivity());
			relativeLayout.addView(splineChartView, layoutParams);

			framelayout.addView(relativeLayout);
			// 添加放大缩小控件
			// framelayout.addView(mZoomControls);

			mZoomControls
					.setOnZoomInClickListener(new OnZoomInClickListenerImpl());

			mZoomControls
					.setOnZoomOutClickListener(new OnZoomOutClickListenerImpl());

		} else {
			Util.T_Normal.clear();
			mT_Normal_Key = new Double[Util.T.size()];
			mT_Normal_Value = new Double[Util.T.size()];
			int j = 0;
			Iterator it = Util.T.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				mT_Normal_Key[j] = ((Double) entry.getKey());
				mT_Normal_Value[j] = ((Double) entry.getValue());
				j++;
			}
			Log.e(TAG, "onClick----------j::::::" + j);
			mT_Max = mT_Normal_Value[0];
			mT_Min = mT_Normal_Value[0];
			for (int i = 0; i < Util.T.size(); i++) {

				if (mT_Normal_Value[i] > mT_Max) {
					mT_Max = mT_Normal_Value[i];
				}
				if (mT_Normal_Value[i] < mT_Min) {
					mT_Min = mT_Normal_Value[i];
				}
			}
			Log.e(TAG, "");
			mT_Temp = mT_Max - mT_Min;
			Log.e(TAG, "onClick-------------333:mT_Temp:" + mT_Temp);

			for (int i = 0; i < Util.T.size(); i++) {
				Util.T_Normal.put(mT_Normal_Key[i],
						(mT_Normal_Value[i] - mT_Min) / mT_Temp);
				if (mT_Normal_Value[i] == mT_Min) {
					mHashMap.put("Peak_X", mT_Normal_Key[i]);
					mHashMap.put("Peak_Y", 0D);
				}
			}
			Log.e(TAG, "onClick----------------");
			Title_Second = "线性归一";
			relativeLayout.removeAllViews();
			framelayout.removeView(relativeLayout);
			layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeigth);
			// 居中显示；
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			splineChartView_normal = new SplineChart01_Normal(getActivity(),
					Util.T_Normal, Title_Second, mHashMap);
			relativeLayout = new RelativeLayout(getActivity());
			relativeLayout.addView(splineChartView_normal, layoutParams);
			framelayout.addView(relativeLayout);
		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e(TAG, "onPause()");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e(TAG, "onStop() ");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.e(TAG, "onDestroyView() ");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "onDestroy() ");
	}

}
