package com.qq.bbsunok;

import javax.swing.JFrame;
import edu.mines.jtk.mosaic.PointsView;
import edu.mines.jtk.mosaic.SimplePlot;
import edu.mines.jtk.util.ArrayMath;

/**
 * 简单快捷的数组绘图
 * @author bingbing sun
 * @version 20210822
 */
public class ArrayPlot {
	/**
	 * 对比画三个数据
	 * @param y1 数据1
	 * @param y2 数据2
	 * @param y3 数据3
	 * @param st1 数据1的线类型
	 * @param st2 数据2的线类型
	 * @param st3 数据3的线类型
	 */
	public static void comparePlot(float [] y1, float [] y2, float [] y3, String st1, String st2, String st3) {
		SimplePlot sp = new SimplePlot();
		plot(y1, st1, sp);
		plot(y2, st2, sp);
		plot(y3, st3, sp);
	}
	/**
	 * 对比画两个数据
	 * @param y1 数据1
	 * @param y2 数据2
	 * @param st1 数据1的线类型
	 * @param st2 数据2的线类型
	 */
	public static void comparePlot(float [] y1, float [] y2, String st1, String st2) {
		SimplePlot sp = new SimplePlot();
		plot(y1, st1, sp);
		plot(y2, st2, sp);
	}
	/**
	 * 对一维单精度数组y绘图
	 * @param y 输入的一维数组
	 */
	public static void plot(float[] y){
		float [] x = ArrayMath.rampfloat(0, 1, y.length);
		plot(x,y);
    }
	/**
	 * 对一维单精度数组y基于坐标x绘图
	 * @param x 数组的坐标
	 * @param y 数组
	 */
    public static void plot(float[] x, float[] y){
    	SimplePlot sp = new SimplePlot();
    	plot(x,y,"b-",sp);
    }
    /**
     * 对一维单精度数组y绘图
     * @param y 数组
     * @param st 线段样式
     * @param sp 画布
     */
    public static void plot(float[] y, String st, SimplePlot sp) {
    	float [] x = ArrayMath.rampfloat(0, 1, y.length);
    	plot(x,y,st,sp);
    }
    /**
     * 对于一维单精度数组基于坐标进行绘图
     * @param x 坐标
     * @param y 数组
     * @param st 线段样式
     * @param sp 画布
     */
    public static void plot(float[] x, float[] y, String st, SimplePlot sp){
    	PointsView pv = sp.addPoints(x,y);
    	pv.setStyle(st);
    	sp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    /**
     * 私有的构造器
     */
    private ArrayPlot() {
    	
    }
}
