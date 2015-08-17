package com.lspr.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Util {

	// 全局变量存储三个文件是否全部加载；
	public static boolean Flag = false;
	public static ArrayList<Double> Temp = new ArrayList<Double>();
	public static ArrayList<Double> Temp_D = new ArrayList<Double>();
	public static ArrayList<Double> Temp_S = new ArrayList<Double>();
	public static ArrayList<Double> Temp_R = new ArrayList<Double>();
	// public static Double Temp_
	public static LinkedHashMap<Double, Double> T = new LinkedHashMap<Double, Double>();
	public static LinkedHashMap<Double, Double> M = new LinkedHashMap<Double, Double>();
	public static LinkedHashMap<Double, Double> T_Normal = new LinkedHashMap<Double, Double>();
	public static LinkedHashMap<Double, Double> M_Normal = new LinkedHashMap<Double, Double>();

	public static WeakReference<Bitmap> mBitmap_folder;
	public static WeakReference<Bitmap> mBitmap_file;
	public static Bitmap mBitmapFolder;
	public static Bitmap mBitmapFile;

	public static void setBitmapFolder(Context context, int id) {
		mBitmapFolder = BitmapFactory
				.decodeResource(context.getResources(), id);

	}

	public static Bitmap getBitmapFolder() {
		return mBitmapFolder;
	}

	public static void setBitmapFile(Context context, int id) {
		mBitmapFile = BitmapFactory.decodeResource(context.getResources(), id);

	}

	public static Bitmap getBitmapFile() {
		return mBitmapFile;
	}

	public static void setT(LinkedHashMap<Double, Double> T) {
		Util.T = T;
	}

	public static LinkedHashMap<Double, Double> getT() {
		return T;
	}

	public static void setT_Normal(LinkedHashMap<Double, Double> T_Normal) {
		Util.T_Normal = T_Normal;

	}

	public static LinkedHashMap<Double, Double> getT_Normal() {
		return T_Normal;
	}

	public static LinkedHashMap<Double, Double> getM() {
		return M;
	}

	public static void setM(LinkedHashMap<Double, Double> M) {
		Util.M = M;
	}

	public static void setM_Normal(LinkedHashMap<Double, Double> M_Normal) {
		Util.M_Normal = M_Normal;

	}

	public static LinkedHashMap<Double, Double> getM_Normal() {
		return M_Normal;
	}

}
