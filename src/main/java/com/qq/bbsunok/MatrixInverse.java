package com.qq.bbsunok;

import edu.mines.jtk.dsp.Conv;
import edu.mines.jtk.la.DMatrix;
import edu.mines.jtk.mosaic.SimplePlot;

import static edu.mines.jtk.util.ArrayMath.*;
import java.util.logging.Logger;

/**
 * 矩阵求解
 * 
 * @author bbsun
 * @version 20210723
 */
public class MatrixInverse {
	/**
	 * 求矩阵的逆矩阵
	 * 
	 * @param args 输入参数
	 */
	public static void main(String[] args) {
		Logger logger = Logger.getGlobal();
		logger.info("采用Toeplitz法求最小相位信号\n");
		int n = 5;
		float[] a = new float[] { 1f, -0.5f, 2.0f,3.0f,-0.5f};
		float[] z = new float[n];
		int nFil = 1000;
		double[][] r = new double[nFil][1];
		float[] app = new float[nFil];
		float[] tru = new float[nFil];
		Conv.xcor(n, 0, a, n, 0, a, n, 0, z);
		SimplePlot.asSequence(z);
		for (int i = 0; i < n; i++)
			r[i][0] = z[i];
		
		DMatrix b = new DMatrix(r);
		logger.info(b.toString());
		DMatrix invFil = runToeplitz(b);
		//logger.info(invFil.toString());
		for (int i = 0; i < nFil; i++) {
			app[i] = (float) invFil.get(i, 0);
			//tru[i] = (float) pow(1.0 / 2, i);
		}
		SimplePlot.asSequence(app);
		/*SimplePlot sp = new SimplePlot();
		ArrayPlot.plot(app, "r-", sp);
		ArrayPlot.plot(tru, "b-", sp);
		float[] in = new float[10];
		in[0] = 1.0f;
		float[] out = FeedBackFiltering.run(app, in);
		dump(out);
		SimplePlot sp1 = new SimplePlot();
		ArrayPlot.plot(out, "ro", sp1);
		ArrayPlot.plot(a, "bo", sp1);
		SimplePlot.asSequence(out);
		SimplePlot.asSequence(a);
		logger.info("采用镶边法求矩阵的逆");
		DMatrix m = new DMatrix(new double[][] { { 100, 4, 5 }, { 7, 1, 4 }, { 10, 20, 30 } });
		String info = "";
		info += ("\n 矩阵为 \n" + m.toString());
		DMatrix im = runInverse(m);
		info += ("\n 逆矩阵为\n" + im.toString());
		info += (" \n 矩阵乘以其逆矩阵为 \n" + m.times(im).toString());
		logger.info(info);*/
	}
	/**
	 * 基于Toeplitz矩阵求解的谱分解算法
	 * @param r 实信号的相关系数
	 * @return 最小相位信号（的逆）
	 */
	public static float [] runToeplitz(float[] r) {
		int n = r.length;
		double[][] rd = new double[n][1];
		float []    x = new float[n];
		for (int i = 0; i < n; i++)
			rd[i][0] = r[i];
		DMatrix b = new DMatrix(rd);
		DMatrix y = runToeplitz(b);
		x[0] = (float) y.get(0,0);
		for(int i=1;i<n;i++)
			x[i] = (float) (y.get(i,0)*y.get(0,0));
		return x;
	}
	/**
	 * 基于Toeplitz矩阵求解的谱分解算法
	 * 
	 * @param r 实信号的相关系数
	 * @return 最小相位信号
	 */
	public static DMatrix runToeplitz(DMatrix r) {
		int n = r.getRowCount();
		int m = r.getColumnCount();
		DMatrix x = new DMatrix(n, m);
		if (m != 1)
			throw new ArithmeticException("输入的矩阵r和b都应该是列向量");
		if (r.get(0, 0) == 0)
			throw new ArithmeticException("矩阵r的第一个元素为非零");
		double v = r.get(0, 0);
		for (int i = 1; i < n; i++) {
			DMatrix xt = new DMatrix(x);
			double e = r.get(i, 0);
			for (int j = 1; j <= (i - 1); j++)
				e += xt.get(j, 0) * r.get(i - j, 0);
			for (int j = 1; j < i; j++)
				x.set(j, 0, xt.get(j, 0) - e / v * xt.get(i - j, 0));
			if(abs(e/v)>=1)
				throw new ArithmeticException("e/v should always less then 1: e="+e+",v="+v+"\n i="+i);
			x.set(i, 0, -e / v);
			v = v * (1.0 - pow((e / v), 2.0));
			
		}
		x.set(0, 0, 1.0 / sqrt(v));
		return x;
	}

	/**
	 * 计算矩阵的逆。
	 * 
	 * @param m 输入矩阵
	 * @return 输入矩阵的逆矩阵
	 */
	public static DMatrix runInverse(DMatrix m) throws ArithmeticException {
		int n1 = m.getRowCount();
		int n2 = m.getColumnCount();
		if (m.get(0, 0) == 0)
			throw new ArithmeticException("矩阵第一行第一列元素需为非零元素.");
		if (n1 != n2)
			throw new ArithmeticException("矩阵需要为方阵。");
		int n = n1;
		if (n == 1) {
			return new DMatrix(1, 1, 1.0 / m.get(0, 0));
		} else {
			DMatrix x;
			DMatrix y;
			DMatrix w;
			DMatrix ia = new DMatrix(1, 1, 1.0 / m.get(0, 0));
			for (int i = 0; i < (n - 1); i++) {
				int ic = i + 1;
				int br = 0;
				int er = i;
				int ir = i + 1;
				int bc = 0;
				int ec = i;
				int[] indr;
				int[] indc;
				indr = (br != er) ? new int[] { br, er } : new int[] { br };
				indc = (bc != ec) ? new int[] { bc, ec } : new int[] { bc };
				double g = m.get(i + 1, i + 1);
				DMatrix f = m.get(indr, ic);
				DMatrix e = m.get(ir, indc);
				double z = 1.0 / (g - e.times(ia.times(f)).get(0, 0));
				if (z == 0) {
					throw new ArithmeticException("矩阵为奇异矩阵。");
				}
				y = ia.times(f).times(z).times(-1.0);
				x = e.times(ia).times(1.0 / (e.times(ia).times(f).get(0, 0) - g));
				w = ia.minus(ia.times(f).times(x));
				ia = new DMatrix(i + 2, i + 2);
				ia.set(indr, indc, w);
				ia.set(indr, ic, y);
				ia.set(ir, indc, x);
				ia.set(i + 1, i + 1, z);
			}
			return ia;
		}
	}

}
