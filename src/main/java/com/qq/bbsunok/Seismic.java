package com.qq.bbsunok;
import static edu.mines.jtk.util.ArrayMath.*;
import static com.qq.bbsunok.ArrayPlot.*;
import edu.mines.jtk.dsp.Conv;
import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.HilbertTransformFilter;
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
	 * @return 子波
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
	 * @throws Exception 长度错误
	 */
	public static void main(String[] args) throws Exception {
		// testing of Hilbert Transform
		/*int n = SimpleFftComplex.nfftSmall(100);
		float [] xHil = wavelet(0.01f,n,10.0f,0.2f);
		SimplePlot.asSequence(xHil);
		HilbertTransformFilter hlb = new HilbertTransformFilter();
		float [] inputR = copy(xHil);
		float [] inputI = new float[n];
		float [] r1     = new float[n];
		float [] r2     = new float[n];
		float [] zero   = new float[n];
		hlb.apply(n, inputR, inputI);
		float normR = sum(mul(inputR,inputR));
		float normI = sum(mul(inputI,inputI));
		inputI=mul(inputI,sqrt(normR)/sqrt(normI));
		SimpleFftComplex  sFft = new SimpleFftComplex(SimpleFftComplex.nfftSmall(n));
		sFft.complexToComplex(1, inputR, zero, r1, r2);;
		float [] org = add(mul(r1,r1),mul(r2,r2));
		sFft.complexToComplex(1, zero, inputI, r1, r2);;
		float [] orgHil = add(mul(r1,r1),mul(r2,r2));
		sFft.complexToComplex(1, inputR, inputI, r1, r2);;
		float [] all = add(mul(r1,r1),mul(r2,r2));
		comparePlot(sqrt(org), sqrt(orgHil), sqrt(all), "b-", "r-","g-");*/
		int n = 1000;
		SimpleFftComplex  sFft = new SimpleFftComplex(SimpleFftComplex.nfftSmall(n));
		System.err.println(sFft.getNfft());
		n = sFft.getNfft();
		//float [] b = new float[n];
		float [] b = wavelet(0.001f,n,10.0f,0.3f);
		//b[0]= 1.0f;
		//b[1]=-0.5f;
		//for(int i=0;i<n;i++)
			//b[i] = (float) (pow(0.5,i))*1.0f;
		SimplePlot.asSequence(b);
		float [] inputR = copy(b);
		float [] inputI = new float[n];
		sFft.complexToComplex(1, inputR, inputI, inputR, inputI);;
		float [] r = add(mul(inputR,inputR),mul(inputI,inputI));
		SimplePlot.asSequence(r);
		float scale = max(r)*0.499f;
		float [] rx = mul(r,1.0f/scale);
		float [] b0 = Kolmogoroff.run(rx);
		b0 = mul(b0,sqrt(scale));
		SimplePlot.asSequence(b0);
		inputR = copy(b0);
		inputI = new float[n];
		sFft.complexToComplex(1, inputR, inputI, inputR, inputI);;
		float [] r0 = add(mul(inputR,inputR),mul(inputI,inputI));
		SimplePlot sp = new SimplePlot();
		ArrayPlot.plot(b, "b-o", sp);
		ArrayPlot.plot(b0,"r-", sp);
		SimplePlot sp1 = new SimplePlot();
		ArrayPlot.plot(r,"b-o", sp1);
		ArrayPlot.plot(r0,"r-", sp1);
		/*float [] inputR = new float[n];
		float [] inputI = new float[n];
		dump(inputR);
		dump(inputI);
		sFft.complexToComplex(-1, inputR, inputI, inputR, inputI);
		dump(inputR);
		dump(inputI);
		float [] fr = sFft.getFrequencySamplingInCircle(0.1f);
		dump(fr);
		float[] t = sFft.getTimeSampling(0.1f);
		float[] ts= SimpleFftComplex.fftShift(t);
		dump(t);
		dump(ts);
		dump(SimpleFftComplex.fftShiftInverse(ts));
		/*int nn = 5;
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
		ArrayPlot.plot(x, "b-", sp1);*/
		
	}

}
