package com.qq.bbsunok;

import edu.mines.jtk.util.ArrayMath;

/**
 * 实现反馈滤波器,输入为最小相位滤波器的逆
 * <p>
 * Y(Z)=X(Z)/A(Z), A(Z)为最小相位
 * @author bingbing sun
 * @version 2021.08.02
 *
 */
class FeedBackFiltering{
	/**
	 * 实现反馈滤波
	 * @param a 最小相位滤波器的逆 
	 * @param x 输入信号
	 * @return 滤波后的信号
	 */
    public static float[] run(float[] a, float[] x){
        int na = a.length;
        int nx = x.length;
        int ny = nx;
        float[] y = new float[ny];
        for(int k=0;k<ny;k++){
            int ie = ArrayMath.min(na-1,k);
            double sum=0;
            for(int i=1;i<=ie;i++){
                sum+=y[k-i]*a[i];
            }
            y[k] = (float)((x[k]-sum)/a[0]);
        }
        return y;
    }
    /**
     * 私有构造器
     */
    private FeedBackFiltering() {
    }
}
