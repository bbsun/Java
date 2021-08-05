package com.qq.bbsunok;
import static edu.mines.jtk.util.ArrayMath.*;

import edu.mines.jtk.dsp.Conv;
import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.mosaic.SimplePlot;
/**
 * 对地球物理有用的函数
 * @author Bingbing Sun
 * @version 20210804
 */
public class Seismic {
	/**
	 * 雷克子波
	 * @param dt 采样
	 * @param nt 长度
	 * @param fr 主频
	 * @param tau 时移
	 * @return
	 */
	public static float [] wavelet(float dt, int nt, float fr, float tau) {
		float[] w = new float[nt];
		for(int it=0;it<nt;it++){
			float t = it*dt-tau; 
			float t2= t*t;
			float v = FLT_PI*FLT_PI*fr*fr;
			w[it]=(1.0f-2.0f*v*t2)*exp(-v*t2);
		}
		return w;
	}
	/**
	 * 测试程序
	 * @param args 输入参数
	 */
	public static void main(String[] args) {
		int nn = 5;
		int nfft = (int) FftComplex.nfftSmall(nn);
		System.out.println(nfft);
		float dt = 0.02f;
		int nt   = (int) floor(5.0f/dt);
		//int nt   = 1000;
		float fr = 2;
		float tau= (nt/2)*dt;
		float [] x = wavelet(dt,nt,fr,tau);
		float [] x1 = wavelet(dt,nt,fr,tau-1.0f);
		float [] e = randfloat(nt);
		//SimplePlot.asSequence(e);
		for(int i=0;i<nt;i++) {
			x[i] +=(e[i]-0.5)*0.1f+x1[i]*0.5f;
			//x[i] = 2*pow(0.5f,i);
		}
		SimplePlot.asSequence(x);
		float [] y = new float[nt*2+1];
		Conv.xcor(nt,0,x,nt,0,x,nt*2+1,-nt,y);
		float [] r = copy(nt,nt,y);
		//r[0] = (float) (r[0]+max(r));
		SimplePlot.asSequence(r);
		float [] a = MatrixInverse.runToeplitz(r);
		SimplePlot.asSequence(a);
		float [] del= new float[nt];
		del[0] = 1.0f;
		float[] out = FeedBackFiltering.run(a, del);
		SimplePlot.asSequence(out);
		float [] rr = new float[nt];
		Conv.xcor(nt,0,out,nt,0,out,nt,0,rr);
		SimplePlot.asSequence(rr);
		SimplePlot sp = new SimplePlot();
		ArrayPlot.plot(rr,  "b-o", sp);
		ArrayPlot.plot(r, "r-", sp);
		float [] xDec = FeedBackFiltering.run(out,x);
		float [] x1Dec= FeedBackFiltering.run(out,xDec);
		//Conv.xcor(nt,0,x,nt,0,a,nt,0,xDec);
		SimplePlot.asSequence(rr);
		SimplePlot sp1 = new SimplePlot();
		//ArrayPlot.plot(a,  "b-o", sp1);
		ArrayPlot.plot(out,  "r-", sp1);
		ArrayPlot.plot(x, "b-", sp1);
		
	}

}
