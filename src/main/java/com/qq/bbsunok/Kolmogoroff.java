package com.qq.bbsunok;
import static edu.mines.jtk.util.ArrayMath.*;
import edu.mines.jtk.dsp.HilbertTransformFilter;
import edu.mines.jtk.util.Cfloat;
/**
 * Kolmogoroff方法谱分解
 * <p>
 * 基于快速傅里叶变换和希尔伯特变换
 * @author bingbing sun 
 * @version 20210805
 *
 */
public class Kolmogoroff {
	/**
	 * Kolmogoroff谱分解
	 * @param s 谱，由傅里叶变换计算得到的谱
	 * @return 最小相位子波
	 * @throws Exception 数值长度问题
	 */
	public static float [] run(float [] s) throws Exception {
		int n = s.length;
		SimpleFftComplex spFft = new SimpleFftComplex(SimpleFftComplex.nfftSmall(n));
		float [] u = new float[n];
		float [] v = new float[n];
		float [] b = new float[n];
		float [] inputR= new float[n];
		float [] inputI= new float[n];
		u = log(s);
		//u = SimpleFftComplex.fftShift(u);
		/*int   nmax = 100000; // default max length.
		float emax = 0.0001f; // default max error.
		float fmin = 0.001f; // default min frequency.
		float fmax = 0.499f; // default max frequency.*/
		inputR = mul(copy(u),0.5f);
		spFft.complexToComplex(-1, inputR, inputI, inputR, inputI);
		float [] ts = spFft.getTimeSampling(1.0f);
		for(int i=1;i<n;i++) {
			float scale = (ts[i]<0)?0.0f:2.0f;
			inputR[i] *= scale;
			inputI[i] *= scale;
		}
		spFft.complexToComplex(1, inputR, inputI, inputR, inputI);
		//HilbertTransformFilter hlb = new HilbertTransformFilter(nmax,emax,fmin,fmax);
		//hlb.apply(n, u, v);
		//u = SimpleFftComplex.fftShiftInverse(u);
		//v = SimpleFftComplex.fftShiftInverse(v);
		for(int i=0;i<n;i++) {
			Cfloat cc   = new Cfloat(inputR[i],inputI[i]);
			Cfloat cexp = cc.exp();
			inputR[i] = cexp.r;
			inputI[i] = cexp.i;
		}
		spFft.complexToComplex(-1, inputR, inputI, inputR, inputI);
		for(int i=0;i<n;i++) {
			float scale = (ts[i]<0)?0.0f:1.0f;
			inputR[i]*=scale;
		}
		return inputR;
	}
	/**
	 * Kolmogoroff谱分解
	 * <p>
	 * 这里用近似的精度将非常低
	 * 使用近似的Hilbert变换
	 * @param s 谱，由傅里叶变换计算得到的谱
	 * @return 最小相位子波
	 * @throws Exception 数值长度问题
	 */
	public static float [] runApprox(float [] s) throws Exception {
		int n = s.length;
		SimpleFftComplex spFft = new SimpleFftComplex(SimpleFftComplex.nfftSmall(n));
		float [] u = new float[n];
		float [] v = new float[n];
		float [] b = new float[n];
		float [] inputR= new float[n];
		float [] inputI= new float[n];
		u = log(s);
		inputR = mul(copy(u),0.5f);
		//u = SimpleFftComplex.fftShift(u);
		int   nmax = 100000;  // max length.
		float emax = 0.0001f; // max error.
		float fmin = 0.001f;  // min frequency.
		float fmax = 0.499f;  // max frequency.
		inputR = SimpleFftComplex.fftShift(inputR);
		HilbertTransformFilter hlb = new HilbertTransformFilter();
		hlb.apply(n, inputR, inputI);
		inputI=mul(inputI,sqrt(sum(mul(inputR,inputR))/sum(mul(inputI,inputI))));
		inputR = SimpleFftComplex.fftShiftInverse(inputR);
		inputI = SimpleFftComplex.fftShiftInverse(inputI);
		for(int i=0;i<n;i++) {
			Cfloat cc   = new Cfloat(inputR[i],inputI[i]);
			Cfloat cexp = cc.exp();
			inputR[i] = cexp.r;
			inputI[i] = cexp.i;
		}
		spFft.complexToComplex(-1, inputR, inputI, inputR, inputI);
		float [] ts = spFft.getTimeSampling(1.0f);
		for(int i=0;i<n;i++) {
			float scale = (ts[i]<0)?0.0f:1.0f;
			inputR[i]*=scale;
		}
		return inputR;
	}
	/**
	 * 私有构造器
	 */
	private Kolmogoroff() {
		
	}
}
