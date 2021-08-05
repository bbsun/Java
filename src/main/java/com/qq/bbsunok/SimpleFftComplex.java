package com.qq.bbsunok;
import edu.mines.jtk.dsp.FftComplex;
import static edu.mines.jtk.util.ArrayMath.*;
/**
 * 简单的复数傅里叶变换
 * @author bingbing sun
 * @version 20210805
 *
 */
public class SimpleFftComplex extends FftComplex{
	/**
	 * 构造函数
	 * @param nfft FFT实际计算的长度。有效的长度
	 * 可由方法{@link #nfftSmall(int)} 和{@link #nfftFast(int)}得到.
	 */
	public SimpleFftComplex(int nfft) {
		super(nfft);
	}
	/**
	 * 复数对复数FFT
	 * @param sign  FFT符号，+1或者-1
	 * @param inputR 输入数组的实部
	 * @param inputI 输入数组的虚步
	 * @param outputR 输出数组的实部
	 * @param outputI 输出数组的虚步
	 * @exception Exception 输入数组和输出数组的长度需要与实际计算使用的长度nfft一致.
	 */
	public void complexToComplex(int sign, float [] inputR, float [] inputI, float[] outputR, float[] outputI) throws Exception {
		if (inputR.length!=this.getNfft() || inputI.length !=this.getNfft() || inputR.length!=this.getNfft()|| inputI.length!=this.getNfft())
			throw new Exception("The length of input and output array should be equal to nfft \n!");
		int n = inputR.length*2;
		float [] in = new float[n];
		float [] out= new float[n];
		for(int i=0;i<n/2;i++) {
			in[2*i]   = inputR[i];
			in[2*i+1] = inputI[i];
		}
		//使得其输入输出和MATLAB的结果一致
		complexToComplex(sign, in, out);
		float scale = (sign==-1)?1.0f/this.getNfft():1.0f;
		for(int i=0;i<n/2;i++) {
			outputR[i]= out[2*i]*scale;
			outputI[i]= out[2*i+1]*scale;
		}
	}
	/**
	 * 返回频率采样（以弧度为单位）
	 * @param dt 时间采样
	 * @return 频率采样
	 */
	public float [] getFrequencySampling(float dt) {
		int nfft = getNfft();
		float [] s = new float[getNfft()];
		for(int i=0;i<nfft;i++) {
			float v = 2.0f*FLT_PI*i/nfft;
			v = (v>=FLT_PI)?(v-2*FLT_PI):v;
			s[i] = v/dt;
		}
		return s;
	}
	/**
	 * 返回频率采样（以Hz为单位）
	 * @param dt 时间采样
	 * @return 频率采用
	 */
	public float [] getFrequencySamplingInCircle(float dt) {
		float [] s= getFrequencySampling(dt);
		return mul(s,1.0f/(FLT_PI*2.f));
	}
	/**
	 * 返回时间采样
	 * @param dt 时间采样
	 * @return 时间采样
	 */
	public float [] getTimeSampling(float dt) {
		int nfft = getNfft();
		float [] s = new float[nfft];
		for(int i=0;i<nfft;i++) {
			float v = i;
			v = (v>=(nfft*1.0f/2))?(v-nfft):v;
			s[i] = v*dt;
		}
		return s;
	}
	/**
	 * 将零频率放置于中间
	 * @param a 频率采样
	 * @return  频率采样
	 * @see #fftShiftInverse(float[])
	 */
	public static float [] fftShift(float [] a) {
		int n=a.length;
		float [] b = new float[n];
		for(int i=0;i<n;i++) {
			int j=i+(n/2);
			if(i>=(n*1.0f/2.0f))
				j = (int) (i- Math.ceil(n*1.0f/2));
			b[j] = a[i];
		}
		return b;
	}
	/**
	 * 将零频放置开始位置
	 * @param a 频率采样
	 * @return 频率采样
	 * @see #fftShift(float[])
	 */
	public static float[] fftShiftInverse(float [] a) {
		int n=a.length;
		float [] b = new float[n];
		for(int i=0;i<n;i++) {
			int j=i+(n/2);
			if(i>=(n*1.0f/2.0f))
				j = (int) (i- Math.ceil(n*1.0f/2));
			b[i] = a[j];
		}
		return b;
	}
}
