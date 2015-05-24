package com.lspr.chart;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class SplineChart02View extends TouchView {

	private String TAG = "SplineChart02View";
	private SplineChart chart = new SplineChart();
	private LinkedHashMap<Double, Double> M;

	// 分类轴标签集合；
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<SplineData> chartData = new LinkedList<SplineData>();

	public SplineChart02View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	public SplineChart02View(Context context,
			LinkedHashMap<Double, Double> Hashmap) {
		super(context);
		this.M = Hashmap;
		initView();
		// TODO Auto-generated constructor stub
	}

	public SplineChart02View(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
	}

	public SplineChart02View(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		initView();
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

		// 线1的数据
		Log.e(TAG, "完成数据初始化");
		Log.e(TAG, "" + M.size());
		Log.e(TAG, "" + M.get(400.52));
		SplineData dataSeries1 = new SplineData(" M值  ", M, (int) Color.rgb(54,
				148, 238));
		Log.e(TAG, dataSeries1.toString()); // 隐藏点；
		dataSeries1.setDotStyle(XEnum.DotStyle.HIDE);
		// 将线变细；
		dataSeries1.getLinePaint().setStrokeWidth(2);

		// 线2数据

		LinkedHashMap<Double, Double> linePoint2 = new LinkedHashMap<Double, Double>();
		// linePoint2.put(571.82, 0.52571);
		linePoint2.put(571.82, 0.27925);

		SplineData dataSeries2 = new SplineData(" 极值点", linePoint2,
				(int) Color.rgb(255, 165, 132));
		dataSeries2.setLabelVisible(true);
		dataSeries2.setDotStyle(XEnum.DotStyle.DOT);
		dataSeries2.getDotLabelPaint().setColor(Color.RED);

		// 设定数据源
		chartData.add(dataSeries1);
		chartData.add(dataSeries2);

	}

	private void chartRender() {

		try {

			// 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			int[] ltrb = getBarLnDefaultSpadding();
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

			// 显示边框
			chart.showRoundBorder();

			// 数据源
			chart.setCategories(labels);
			chart.setDataSource(chartData);

			// 坐标系
			// 数据轴最大值
			chart.getDataAxis().setAxisMax(0.35);
			chart.getDataAxis().setAxisMin(-0.15);
			// 数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(0.05);

			// 标签轴最大值
			chart.setCategoryAxisMax(1000);
			// 标签轴最小值
			chart.setCategoryAxisMin(300);

			// 设置图的背景色
			// chart.setBackgroupColor(true,Color.BLACK);
			// 设置绘图区的背景色
			// chart.getPlotArea().setBackgroupColor(true, Color.WHITE);

			// 背景网格
			PlotGrid plot = chart.getPlotGrid();
			plot.showHorizontalLines();
			plot.showVerticalLines();
			plot.getHorizontalLinePaint().setStrokeWidth(3);
			plot.getHorizontalLinePaint().setColor(
					(int) Color.rgb(127, 204, 204));
			plot.setHorizontalLineStyle(XEnum.LineStyle.DOT);

			// 把轴线设成和横向网络线一样和大小和颜色,演示下定制性，这块问得人较多
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

			// 定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为: x值,y值
			// 请自行分析定制
			chart.setDotLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					// TODO Auto-generated method stub
					String label = "(" + value + ")";
					return (label);
				}

			});
			// 设置纵坐标格式类型
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
			// 标题
			// chart.setTitle("Spline Chart");
			// chart.addSubtitle("(M值趋势图 Demo)");
			chart.setTitle("M值");
			chart.getAxisTitle().setLowerAxisTitle("(波长/nm)");

			// 显示平滑曲线
			// chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEZIERCURVE);

			// 激活点击监听
			chart.ActiveListenItemClick();
			// 为了让触发更灵敏，可以扩大5px的点击监听范围
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
		// 图所占范围大小
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

	// 触发监听；
	private void triggerClick(float x, float y) {

		PointPosition record = chart.getPositionRecord(x, y);
		if (null == record)
			return;

		SplineData lData = chartData.get(record.getDataID());
		LinkedHashMap<Double, Double> linePoint = lData.getLineDataSet();
		int pos = record.getDataChildID();
		int i = 0;
		Iterator it = linePoint.entrySet().iterator();
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
