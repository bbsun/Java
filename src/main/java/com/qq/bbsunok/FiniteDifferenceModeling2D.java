package com.qq.bbsunok;
import java.util.Map;
/**
 * 二维有限差分模拟程序
 * @author bingbing sun
 * @version 20210805
 *
 */
public class FiniteDifferenceModeling2D {
	/**
	 * 计算的类别
	 */
	enum Mission{
		/**
		 * 正演模拟，即由速度场模拟得到
		 */
		FORWARD_MODELING,
		/**
		 * 波恩近似模拟，即由背景介质和介质扰动，模拟波场扰动
		 */
		BORN_MODELING,
		/**
		 * 伴随计算，即计算梯度
		 */
		ADJOINT
	}
	/**
	 * 模拟的方法
	 */
	enum Model{
		/**
		 * 采用标量波动方程基于速度VP进行模拟
		 */
		SCALAR_VP_MODELING,
		/**
		 * 采用带密度的声波方程进行模拟,且采用一阶方程基于完全匹配层吸收边界条件
		 */
		ACOUSTIC_VP_RHO_FIRST_ORDER_PML_MODELING,
		/**
		 * 采用带密度的声波方程进行模拟，其采用二阶方程基于单程波吸收边界条件
		 */
		ACOUSTIC_VP_RHO_SECOND_ORDER_ONE_WAY_MODELING
	}
	/**
	 * 震源类型
	 */
	enum SourceType{
		/**
		 * 震源为压力源
		 */
		PRESSURE
	}
	/**
	 * 接收点类型
	 */
	enum RecordType{
		/**
		 * 接收点采集压力数据
		 */
		PRESSURE
	}
	/**
	 * 有限差分波动方程模拟
	 * @param dt 时间采样
	 * @param dx 水平空间采样
	 * @param dz 深度空间采样
	 * @param sx 震源位置坐标（以网格点为单位）
	 * @param sz 震源位置坐标（以网格点为单位）
	 * @param rx 接收点坐标（以网格点为单位）
	 * @param rz 接受点坐标（以网格点为单位）
	 * @param npml 吸收边界的网格点数
	 * @param order 空间有限差分阶数，(2,4,6,8,10)
	 * @param sou  震源子波
	 * @param rec  地震记录
	 * @param free 是否采用自由表面边界条件
	 * @param v    模型集合
	 * @param dv   速度扰动集合
	 * @param sourceType 震源类型
	 * @param recordType 接受点类型
	 * @param mission    任务类型
	 * @param model      模拟采用的模型
	 */
	public static void run(float dt, float dx, float dz, float [] sx, float [] sz, float [] rx, float [] rz, int npml, int order,
			float [][] sou, float[][][] rec, boolean free, Map<String, float[][]> v, Map<String, float[][]> dv,SourceType sourceType, RecordType recordType, Mission mission, Model model) {	
		
		switch(model){
			case SCALAR_VP_MODELING:
				float[][] vp   =  v.get("VP");
				float[][] dvp  = dv.get("D_VP");
				float[][] r    = rec[0];
				runAcousticVp(dt, dx, dz, sx, sz, rx, rz, npml, order, sou, r, free, vp, dvp);
				break;
		}
	}
	/**
	 * 有限差分方法标量波动方程模拟
	 * @param dt 时间采样
	 * @param dx 水平空间采样
	 * @param dz 深度空间采样
	 * @param sx 震源位置坐标（以网格点为单位）
	 * @param sz 震源位置坐标（以网格点为单位）
	 * @param rx 接收点坐标（以网格点为单位）
	 * @param rz 接受点坐标（以网格点为单位）
	 * @param npml 吸收边界的网格点数
	 * @param order 空间有限差分阶数，(4或8)
	 * @param sou  震源子波
	 * @param rec  地震记录
	 * @param free 是否采用自由表面边界条件
	 * @param v    模型集合
	 * @param dv   速度扰动集合
	 */
	private static void runAcousticVp(float dt, float dx, float dz, float [] sx, float [] sz, float [] rx, float [] rz, int npml, int order,
			float [][] sou, float[][] rec, boolean free, float [][] v, float [][] dv) {
		
	}
	/**
	 * 测试程序
	 * @param args 参数
	 */
	public static void main(String[] args) {

	}
	/**
	 * 私有构造器
	 */
	private FiniteDifferenceModeling2D() {
		
	}
}
