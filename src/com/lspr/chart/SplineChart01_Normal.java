package com.lspr.chart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotGrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class SplineChart01_Normal extends TouchView {

	private String TAG = "SplineChart01_Normal";
	private SplineChart chart = new SplineChart();
	private LinkedHashMap<Double, Double> T_Normal = new LinkedHashMap<Double, Double>();
	private String Title_Second = null;
	private HashMap<String, Double> mHashMap = new HashMap<String, Double>();

	// �������ǩ���ϣ�
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<SplineData> chartData = new LinkedList<SplineData>();

	public SplineChart01_Normal(Context context) {
		super(context);
		initView();
		// TODO Auto-generated constructor stub
	}

	public SplineChart01_Normal(FragmentActivity fragmentActivity,
			LinkedHashMap<Double, Double> Hashmap, String Title_Second,
			HashMap<String, Double> mHashMap) {
		super(fragmentActivity);
		this.T_Normal = Hashmap;
		this.Title_Second = Title_Second;
		this.mHashMap = mHashMap;
		initView();
		// TODO Auto-generated constructor stub
	}

	public SplineChart01_Normal(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		// TODO Auto-generated constructor stub
	}

	public SplineChart01_Normal(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
		// TODO Auto-generated constructor stub
	}

	private void initView() {
		chartLabels();
		chartDataSet();
		chartRender();
	}

	private void chartLabels() {
		labels.add("300");
		labels.add("400");
		labels.add("500");
		labels.add("600");
		labels.add("700");
		labels.add("800");
		labels.add("900");
		labels.add("1000");
	}

	private void chartDataSet() {

		SplineData dataSeries1 = new SplineData(" Tֵ  ", T_Normal,
				(int) Color.rgb(54, 148, 238));
		Log.e(TAG, dataSeries1.toString()); // ���ص㣻
		dataSeries1.setDotStyle(XEnum.DotStyle.HIDE);
		// ���߱�ϸ��
		dataSeries1.getLinePaint().setStrokeWidth(2);

		// ��2����

		LinkedHashMap<Double, Double> linePoint2 = new LinkedHashMap<Double, Double>();
		// linePoint2.put(571.82, 0.52571);
		linePoint2.put((Double) mHashMap.get("Peak_X"),
				(Double) mHashMap.get("Peak_Y"));
		SplineData dataSeries2 = new SplineData(" ��ֵ��", linePoint2,
				(int) Color.rgb(255, 165, 132));
		dataSeries2.setLabelVisible(true);
		dataSeries2.setDotStyle(XEnum.DotStyle.DOT);
		dataSeries2.getDotLabelPaint().setColor(Color.RED);

		// �趨����Դ
		chartData.add(dataSeries1);
		chartData.add(dataSeries2);

	}

	private void chartRender() {

		try {

			// ���û�ͼ��Ĭ������pxֵ,���ÿռ���ʾAxis,Axistitle....
			int[] ltrb = getBarLnDefaultSpadding();
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

			// ��ʾ�߿�
			chart.showRoundBorder();

			// ����Դ
			chart.setCategories(labels);
			chart.setDataSource(chartData);

			// ����ϵ
			// ���������ֵ
			chart.getDataAxis().setAxisMax(1.0);
			chart.getDataAxis().setAxisMin(0.0);
			// ������̶ȼ��
			chart.getDataAxis().setAxisSteps(0.1);

			// ��ǩ�����ֵ
			chart.setCategoryAxisMax(1000);
			// ��ǩ����Сֵ
			chart.setCategoryAxisMin(300);

			// ����ͼ�ı���ɫ
			// chart.setBackgroupColor(true,Color.BLACK);
			// ���û�ͼ���ı���ɫ
			// chart.getPlotArea().setBackgroupColor(true, Color.WHITE);

			// ��������
			PlotGrid plot = chart.getPlotGrid();
			plot.showHorizontalLines();
			plot.showVerticalLines();
			plot.getHorizontalLinePaint().setStrokeWidth(3);
			plot.getHorizontalLinePaint().setColor(
					(int) Color.rgb(127, 204, 204));
			plot.setHorizontalLineStyle(XEnum.LineStyle.DOT);

			// ��������ɺͺ���������һ���ʹ�С����ɫ,��ʾ�¶����ԣ�����ʵ��˽϶�
			chart.getDataAxis()
					.getAxisPaint()
					.setStrokeWidth(
							plot.getHorizontalLinePaint().getStrokeWidth());
			chart.getCategoryAxis()
					.getAxisPaint()
					.setStrokeWidth(
							plot.getHorizontalLinePaint().getStrokeWidth());

			chart.getDataAxis().getAxisPaint()
					.setColor(plot.getHorizontalLinePaint().getColor());
			chart.getCategoryAxis().getAxisPaint()
					.setColor(plot.getHorizontalLinePaint().getColor());

			chart.getDataAxis().getTickMarksPaint()
					.setColor(plot.getHorizontalLinePaint().getColor());
			chart.getCategoryAxis().getTickMarksPaint()
					.setColor(plot.getHorizontalLinePaint().getColor());

			// ���彻����ǩ��ʾ��ʽ,�ر�ע,������ͼ�������ԣ����Է��ظ�ʽΪ: xֵ,yֵ
			// �����з�������
			chart.setDotLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					// TODO Auto-generated method stub
					String label = "(" + value + ")";
					return (label);
				}

			});
			// �����������ʽ����
			chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					// TODO Auto-generated method stub
					Double tmp = Double.parseDouble(value);
					DecimalFormat df = new DecimalFormat("0.00");
					String label = df.format(tmp).toString();
					return (label);
				}
			});
			// ����
			// chart.setTitle("Spline Chart");
			// chart.addSubtitle(Title_Second);
			chart.setTitle(Title_Second);
			chart.getAxisTitle().setLowerAxisTitle("(����/nm)");

			// ��ʾƽ������
			// chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEZIERCURVE);

			// ����������
			chart.ActiveListenItemClick();
			// Ϊ���ô�������������������5px�ĵ��������Χ
			chart.extPointClickRange(5);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		// ͼ��ռ��Χ��С
		chart.setChartRange(w, h);
	}

	@Override
	public List<XChart> bindChart() {
		// TODO Auto-generated method stub
		List<XChart> lst = new ArrayList<XChart>();
		lst.add(chart);
		return lst;
	}

	@Override
	public void render(Canvas canvas) {
		// TODO Auto-generated method stub
		super.render(canvas);

		try {
			chart.render(canvas);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			triggerClick(event.getX(), event.getY());
		}
		return true;
	}

	// ����������
	private void triggerClick(float x, float y) {

		PointPosition record = chart.getPositionRecord(x, y);
		if (null == record)
			return;

		SplineData lData = chartData.get(record.getDataID());
		LinkedHashMap<Double, Double> linePoint = lData.getLineDataSet();
		int pos = record.getDataChildID();
		int i = 0;
		Iterator<Entry<Double, Double>> it = linePoint.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();

			if (pos == i) {
				Double xValue = (Double) entry.getKey();
				Double yValue = (Double) entry.getValue();

				Toast.makeText(
						this.getContext(),
						record.getPointInfo() + " Key:" + lData.getLineKey()
								+ " Current Value(key,value):"
								+ Double.toString(xValue) + ","
								+ Double.toString(yValue), Toast.LENGTH_SHORT)
						.show();
				break;
			}
			i++;
		}// end while

	}

}
