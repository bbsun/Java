package com.qq.bbsunok;
import java.util.logging.Logger;
import edu.mines.jtk.util.ArrayMath;
/**
 * 基于二分法的快速傅里叶变换。
 * 输入数据的长度必须为2的整数幂。
 * @author bbsun
 *
 */
public class FastFourierTransform {
	private static Logger _logger = Logger.getGlobal();
	private static final double PI=3.1415926;
	/**
	 *傅里叶变换类型。
	 */
	public enum Type{
		/**
		 * 正变换，相位旋转为负。
		 */
		FORWARD,
		/**
		 * 反（逆）变换，相位旋转为正。
		 */
		BACKWARD;
	}
	/**
	 * 快速傅里叶变换计算。
	 * <p>
	 * 采用Inplace实现。
	 * @param in 输入数组
	 * @param type 变换类型
	 */
	public static void runInplace(float[][] in, Type type){
		int n  = in.length;
		int m  = in[0].length;
		final float scale = type==Type.FORWARD?1.0f:(float) (1.0f/n);
		final double sign = type==Type.FORWARD?-1.0:1.0;
		int p = getPower(n);
		if(m!=2) 
			throw new ArithmeticException("输入数组的第二维应该是2，代表实部和虚部。");
		if(Math.pow(2,p)!=n)
			throw new ArithmeticException("数组的长度必须为2的整数幂");
		// 重新排列数据
		reOrder(in);
		for(int i=1;i<=p;i++) {
			// x数组的长度
			int xn = (int) Math.pow(2,(i-1));
			double dp = 2*PI/2/xn*sign;
			// z数组的长度
			int zn = xn*2;
			for(int j=0;j<n;j+=zn) {
				for(int k=j;k<(j+xn);k++) {
					int ix    = k;
					int iy    = ix + xn ;
					int iz    = k;
					double xr = in[ix][0] ;
					double xi = in[ix][1] ;
					double yr = in[iy][0] ;
					double yi = in[iy][1];
					double pr = Math.cos(k*dp);
					double pi = Math.sin(k*dp);
					double t1 = pr*yr-pi*yi;
					double t2 = pr*yi+pi*yr;
					in[iz][0]    = (float) (xr + t1);
					in[iz][1]    = (float) (xi + t2);
					in[iz+xn][0] = (float) (xr - t1);
					in[iz+xn][1] = (float) (xi - t2);
				}
			}
		}
		//归一化，使得正变换接反变换得到原数组
		ArrayMath.mul(scale, in, in);
	}
	/**
	 * 快速傅里叶变换。
	 * @param in 输入数组
	 * @param type 变换类型
	 * @return 快速傅里叶变换后的数组
	 */
	public static float[][] run(float[][] in, Type type)
	{
		int n  = in.length;
		int m  = in[0].length;
		final float scale = type==Type.FORWARD?1.0f:(float) (1.0f/n);
		final double sign = type==Type.FORWARD?-1.0:1.0;
		int p = getPower(n);
		if(m!=2) 
			throw new ArithmeticException("输入数组的第二维应该是2，代表实部和虚部。");
		if(Math.pow(2,p)!=n)
			throw new ArithmeticException("数组的长度必须为2的整数幂");
		// 傅里叶变换系数结果
		float [][] out = new float[n][2];
		// 零时变量
		float [][] tmp = null ;
		// 重新排列数据
		reOrder(in);
		for(int i=1;i<=p;i++) {
			// x数组的长度
			int xn = (int) Math.pow(2,(i-1));
			double dp = 2*PI/2/xn*sign;
			// z数组的长度
			int zn = xn*2;
			for(int j=0;j<n;j+=zn) {
				for(int k=j;k<(j+xn);k++) {
					int ix    = k;
					int iy    = ix + xn ;
					int iz    = k;
					double xr = in[ix][0] ;
					double xi = in[ix][1] ;
					double yr = in[iy][0] ;
					double yi = in[iy][1];
					double pr = Math.cos(k*dp);
					double pi = Math.sin(k*dp);
					double zr = xr + (pr*yr-pi*yi);
					double zi = xi + (pr*yi+pi*yr);
					out[iz][0] = (float) zr;
					out[iz][1] = (float) zi;
				}
				for(int k=(j+xn);k<(j+zn);k++) {
					int ix    = k -xn;
					int iy    = ix + xn ;
					int iz    = k;
					double xr = in[ix][0] ;
					double xi = in[ix][1] ;
					double yr = in[iy][0] ;
					double yi = in[iy][1];
					double pr = Math.cos((k-xn)*dp);
					double pi = Math.sin((k-xn)*dp);
					double zr = xr - (pr*yr-pi*yi);
					double zi = xi - (pr*yi+pi*yr);
					out[iz][0] = (float) zr;
					out[iz][1] = (float) zi;
				}
			}
			tmp = in;
			in  = out;
			out = tmp;
		}
		//归一化，使得正变换接反变换得到原数组
		out = ArrayMath.mul(scale, in);
		return out;
	}
	public static void main(String[] args) {
		float[][] a = new float[][] {{0,0},{1,0},{2,0},{3,0},{4,0},{5,0},{6,0},{7,0}};
		printFloatAsComplex(a);
		FastFourierTransform.runInplace(a, Type.FORWARD);
		printFloatAsComplex(a);
		FastFourierTransform.runInplace(a, Type.BACKWARD);
		printFloatAsComplex(a);
	}
	/**
	 * 打印复数数组
	 * @param a 输入数组
	 */
	private static void printFloatAsComplex(float[][] a) {
		StringBuilder info = new StringBuilder();
		info.append("\n");
		for(float[] x: a) 
			info.append(String.format("%12.4f,%12.4f \n", x[0],x[1]));
		_logger.info(info.toString());
	}
	/**
	 * Bitreserve排序
	 * @param a 输入(输出）数组
	 */
	private static void reOrder(float [][] a) {
		int n = a.length;
        int shift = 1 + Integer.numberOfLeadingZeros(n);
        for (int k = 0; k < n; k++) {
            int j = Integer.reverse(k) >>> shift;
            if (j > k) {
            	float tr = a[j][0];
            	float ti = a[j][1];
                a[j][0]  = a[k][0];
                a[j][1]  = a[k][1];
                a[k][0]  = tr;
                a[k][1] = ti;
            }
        }
	}
	/**
	 * 计算log2(n)
	 * @param n 整型数
	 * @return log2(n)
	 */
	private static int getPower(int n) {
		int p=1;
		while(n/2>1) {
			n=n/2;
			p++;
		}
		return p;
	}
}
