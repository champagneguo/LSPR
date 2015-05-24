package com.lspr.chart;

import java.util.List;

import org.xclcharts.event.touch.ChartTouch;
import org.xclcharts.event.zoom.ChartZoom;
import org.xclcharts.event.zoom.IChartZoom;
import org.xclcharts.renderer.XChart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public abstract class TouchView extends GraphicalView implements IChartZoom {

	private String TAG = "TouchView";

	private ChartZoom mChartZoom[];
	private ChartTouch mChartTouch[];

	// ��������
	private static final int INIT_ZOOM = 0;
	private static final int INIT_TOUCH = 1;

	public TouchView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TouchView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public abstract List<XChart> bindChart();

	private boolean initArrayZoom() {
		if (null != mChartZoom)
			return true;
		return initArray(INIT_ZOOM);
	}

	private boolean initArrayTouch() {
		if (null != mChartTouch)
			return true;
		return initArray(INIT_TOUCH);
	}

	private boolean initArray(int flag) {
		List<XChart> listCharts = bindChart();
		int count = listCharts.size();

		if (0 == count) {
			Log.e(TAG, "û�а���ص�ͼ�����!!!");
			return false;
		}

		if (INIT_ZOOM == flag) {
			mChartZoom = new ChartZoom[count];
		} else if (INIT_TOUCH == flag) {
			mChartTouch = new ChartTouch[count];
		} else {
			Log.e(TAG, "�����������ʶ��!!!");
			return false;
		}

		for (int i = 0; i < count; i++) {
			if (INIT_ZOOM == flag) {
				mChartZoom[i] = new ChartZoom(this, listCharts.get(i));
			} else if (INIT_TOUCH == flag) {
				mChartTouch[i] = new ChartTouch(this, listCharts.get(i));
			}
		}
		return true;
	}

	@Override
	public void setZoomRate(float rate) {
		// TODO Auto-generated method stub

		if (!initArrayZoom())
			return;
		if (null == mChartZoom)
			return;

		for (int i = 0; i < mChartZoom.length; i++) {
			mChartZoom[i].setZoomRate(rate);
		}
	}

	@Override
	public void zoomIn() {
		// TODO Auto-generated method stub

		if (!initArrayZoom())
			return;
		if (null == mChartZoom)
			return;

		for (int i = 0; i < mChartZoom.length; i++) {
			mChartZoom[i].zoomIn();
		}
	}

	@Override
	public void zoomOut() {
		// TODO Auto-generated method stub

		if (!initArrayZoom())
			return;
		if (null == mChartZoom)
			return;

		for (int i = 0; i < mChartZoom.length; i++) {
			mChartZoom[i].zoomOut();
		}
	}

	@Override
	public void render(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if (null == mChartTouch)
			if (!initArrayTouch())
				return false;

		for (int i = 0; i < mChartTouch.length; i++) {
			mChartTouch[i].handleTouch(event);
		}

		return true;
	}

}
