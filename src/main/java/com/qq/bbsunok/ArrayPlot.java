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
