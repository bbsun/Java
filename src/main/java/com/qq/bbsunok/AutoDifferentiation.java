package com.qq.bbsunok;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.mines.jtk.util.ArrayMath;

import static edu.mines.jtk.util.ArrayMath.*;
/**
 * 自动微分的简单实现
 * @author bingbing sun (bingbing.sun@kaust.edu.sa)
 * @version 20210731
 */
/**
 * 张量数据
 */
class Tensor{
	/**
	 * 保存的数据
	 */
	private float[] data=null;
	/**
	 * 是否计算梯度
	 */
	private boolean requiresGrad=false;
	/**
	 * 保存在计算图中的父对象
	 */
	private List<Tensor> precedents = null;
	/**
	 * 运算类型
	 * 由父对象运算得到当前对象
	 */
	private OperationType operationType=null;
	/**
	 * 保存的梯度数据
	 */
	private float[] grad=null;
	/**
	 * 生成张量
	 * @param data 数据
	 */
	public Tensor(float[] data) {
		this(data,false);
	}
	/**
	 * 生成张量
	 * @param data 数据
	 * @param requiresGrad 是否计算梯度
	 */
	public Tensor(float[] data, boolean requiresGrad) {
		this(data,requiresGrad,null,null);
	}
	/**
	 * 生成张量
	 * @param data 数据
	 * @param requiresGrad 是否计算梯度
	 * @param precedents   父对象列表
	 * @param operationType 运算类型
	 */
	public Tensor(float[] data, boolean requiresGrad, List<Tensor> precedents, OperationType operationType) {
		this.data = data;
		this.requiresGrad = requiresGrad;
		this.precedents = precedents;
		this.operationType = operationType;
	}
	/**
	 * 返回对数据的引用
	 * @return 数据的引用
	 */
	public float[] getData() {
		return data;
	}
	/**
	 * 返回对梯度的引用
	 * @return 梯度数据的引用
	 */
	public float[] getGradient() {
		return this.grad;
	}
	/**
	 * 返回张量各个维度的大小，当前只考虑1维的情形，该函数返回1个整型标量
	 * @return 当前张量（向量）的长度
	 */
	public int getDataShape() {
		return data.length;
	}
	/**
	 * 设置父对象列表
	 * @param precedents 父对象列表
	 */
	public void setPrecedents(List<Tensor> precedents) {
		this.precedents = precedents;
	}
	/**
	 * 设置运算类型
	 * @param operationType
	 */
	public void setOperation(OperationType operationType) {
		this.operationType = operationType;
	}
	/**
	 * 设置是否计算梯度
	 * @param requiresGrad 是否计算梯度
	 */
	public void setRequiresGrad(boolean requiresGrad) {
		this.requiresGrad = requiresGrad;
	}
	/**
	 * 设置梯度
	 * 若梯度为null,将创建梯度，否则在当前梯度上累加
	 * @param grad
	 */
	public void setGradient(float[] grad) {
		if(this.grad==null)
			this.grad = new float[grad.length];
		this.grad = add(this.grad,grad);
	}
	/**
	 * 进行梯度的反向传播
	 * @param start 当前张量是否属于最终计算出的张量，即其没有子对象。
	 */
	public void backward(boolean last) {
		if(this.requiresGrad==false) 
			return;
		if(last==true) {
			int n = getDataShape();
			float[] grad = new float[n];
			for(int i=0;i<n;i++)
				grad[i]=1;
			this.grad = grad;
		}
		if(this.operationType!=null && this.precedents!=null) {
		switch(this.operationType) {
			case ADD:
				Operation.addBackward(this.precedents.get(0), this.precedents.get(1), this);
				break;
			case SUB:
				Operation.subBackward(this.precedents.get(0), this.precedents.get(1), this);
				break;
			case MUL:
				Operation.mulBackward(this.precedents.get(0), this.precedents.get(1), this);
				break;
			case DIV:
				Operation.divBackward(this.precedents.get(0), this.precedents.get(1), this);
				break;
			case MEAN:
				Operation.meanBackward(this.precedents.get(0),this);
				break;
			case SUM:
				Operation.sumBackward(this.precedents.get(0),this);
				break;
		}}
		if(this.precedents!=null) {
			for( Tensor p: precedents) {
				p.backward(false);
			}
		}
	}
	@Override
	public String toString() {
		StringBuilder info = new StringBuilder();
		//输出DATA数据
		info.append("{data:[");
		for(float x: this.data) 
			info.append(String.format("%12.5f ", x));
		info.append("],");
		info.append(" requiresGrad: "+this.requiresGrad+", ");
		info.append("precedents: [");
		if(precedents!=null) {
			for(Tensor t:precedents) {
				info.append(t.toString()+" ");
			}
		}else {
			info.append("null");
		}
		info.append("], operationType: ");
		info.append(operationType);
		info.append("}");
		return info.toString();
	}
}
/**
 * 运算类型
 */
enum OperationType{
	/**
	 * 加法运算
	 */
	ADD,
	/**
	 * 减法运算
	 */
	SUB,
	/**
	 * 乘法运算
	 */
	MUL,
	/**
	 * 除法运算
	 */
	DIV,
	/**
	 * 均值运算
	 */
	MEAN,
	/**
	 * 求和运算
	 */
	SUM
}
/**
 * 张量的基本运算及其伴随运算
 */
class Operation{
	/**
	 * 加法运算
	 * @param a 输入张量
	 * @param b 输入张量
	 * @return 张量a+b
	 */
	static Tensor add( Tensor a, Tensor b) {
		float[] data = ArrayMath.add(a.getData(),b.getData());
		List<Tensor> precedents = addPrecendets(a,b);
		return  new Tensor(data,true,precedents,OperationType.ADD);
	}
	/**
	 * 减法运算
	 * @param a 输入张量
	 * @param b 输入张量
	 * @return 张量a-b
	 */
	static Tensor sub( Tensor a, Tensor b) {
		float[] data = ArrayMath.sub(a.getData(),b.getData());
		List<Tensor> precedents = addPrecendets(a,b);
		return  new Tensor(data,true,precedents,OperationType.SUB);
	}
	/**
	 * 乘法运算
	 * @param a 输入张量
	 * @param b 输入张量
	 * @return 张量a*b
	 */
	static Tensor mul( Tensor a, Tensor b) {
		float[] data = ArrayMath.mul(a.getData(),b.getData());
		List<Tensor> precedents = addPrecendets(a,b);
		return  new Tensor(data,true,precedents,OperationType.MUL);
	}
	/**
	 * 除法运算
	 * @param a 输入张量
	 * @param b 输入张量
	 * @return 张量a/b
	 */
	static Tensor div( Tensor a, Tensor b) {
		float[] data = ArrayMath.div(a.getData(),b.getData());
		List<Tensor> precedents = addPrecendets(a,b);
		return  new Tensor(data,true,precedents,OperationType.DIV);
	}
	/**
	 * 均值运算
	 * @param a 输入张量
	 * @return 张量mean(a)
	 */
	static Tensor mean( Tensor a) {
		float [] data = new float[] {ArrayMath.sum(a.getData())/a.getDataShape()};
		List<Tensor> precedents = addPrecendets(a);
		return  new Tensor(data,true,precedents,OperationType.MEAN);
	}
	/**
	 * 求和运算
	 * @param a 输入张量
	 * @return 张量sum(a)
	 */
	static Tensor sum( Tensor a) {
		float [] data = new float[] {ArrayMath.sum(a.getData())};
		List<Tensor> precedents = addPrecendets(a);
		return  new Tensor(data,true,precedents,OperationType.SUM);
	}
	/**
	 * 对于加法运算反传梯度
	 * 对应的正运算为 c=a+b。
	 * @param a 加法正向运算中的输入a
	 * @param b 加法正向运算中的输入b
	 * @param c 加法正向运算中的输出c=a+b
	 */
	static void addBackward(Tensor a, Tensor b, Tensor c) {
		a.setGradient(c.getGradient());
		b.setGradient(c.getGradient());
	}
	/**
	 * 对于减法运算反传梯度
	 * 对应的正运算为c=a-b
	 * @param a 减法正向运算中的输入a
	 * @param b 减法正向运算中的输入b
	 * @param c 减法正向运算中的输出c=a-b
	 */
	static void subBackward(Tensor a, Tensor b, Tensor c) {
		a.setGradient(c.getGradient());
		b.setGradient(ArrayMath.mul(-1.0f,c.getGradient()));
	}
	/**
	 * 对于乘法运算反传梯度
	 * 对应的正运算为c=a*b
	 * @param a 乘法正向运算中的输入a
	 * @param b 乘法正向运算中的输入b
	 * @param c 乘法正向运算中的输出c=a*b
	 */
	static void mulBackward(Tensor a, Tensor b, Tensor c) {
		a.setGradient(ArrayMath.mul(c.getGradient(),b.getData()));
		b.setGradient(ArrayMath.mul(c.getGradient(),a.getData()));
	}
	/**
	 * 对于除法运算的反传梯度
	 * 对应的正向运算为c=a/b
	 * @param a 除法正向运算中的输入a
	 * @param b 除法正向运算中的输入b
	 * @param c 除法正向运算中的输入c
	 */
	static void divBackward(Tensor a, Tensor b, Tensor c) {
		
		int n = a.getDataShape();
		float [] gb = new float[n];
		float [] gc = c.getGradient();
		float [] da = a.getData();
		float [] db = b.getData();
		for(int i=0;i<n;i++) 
			gb[i] = -da[i]*gc[i]/pow(db[i],2.0f);
		a.setGradient(ArrayMath.div(c.getGradient(),b.getData()));
		b.setGradient(gb);
	}
	/**
	 * 对于取均值运算的反传梯度
	 * 对应的正向运算为c=mean(a);
	 * @param a 均值运算中的输入a
	 * @param c 均值运算中的输出c
	 */
	static void meanBackward(Tensor a, Tensor c) {
		int n = a.getDataShape();
		float [] ga = new float[n];
		float [] gc = c.getGradient();
		for(int i=0;i<n;i++) 
			ga[i]=gc[0]/n;
		a.setGradient(ga);
	}
	/**
	 * 对于求和运算的反传梯度
	 * 对应的正向运算为c=sum(a)
	 * @param a 求和运算中的输入a
	 * @param c 求和运算中的输出c
	 */
	static void sumBackward(Tensor a, Tensor c) {
		int n = a.getDataShape();
		float [] ga = new float[n];
		float [] gc = c.getGradient();
		for(int i=0;i<n;i++) 
			ga[i]=gc[0];
		a.setGradient(ga);
	}
	private static List<Tensor>  addPrecendets(Tensor a) {
		List<Tensor> precedents = new ArrayList<>();
		precedents.add(a);
		return precedents;
	}
	private static List<Tensor> addPrecendets(Tensor a, Tensor b){
		List<Tensor> precedents = addPrecendets(a);
		precedents.add(b);
		return precedents;
	}
	private Operation() {
	}
}
/**
 * 测试自动微分
 */
public class AutoDifferentiation {
	public static void main(String[] args) {
		Logger logger = Logger.getGlobal();
		// 计算函数f=ax^2+bx+c,及其当x=1时的导数
		Tensor a = new Tensor(new float[] {1.0f,2.0f},true);
		Tensor b = new Tensor(new float[] {-20.0f,3.0f},false);
		Tensor c = new Tensor(new float[] {3.0f,4.0f},false);
		Tensor x = new Tensor(new float[] {-345.0f,5.0f},false);
		Tensor two=new Tensor(new float[] {2.0f,2.0f},false);
		Tensor x2 = Operation.mul(x, x);
		Tensor bx = Operation.mul(b, x);
		Tensor ax2= Operation.mul(a,x2);
		Tensor f  = Operation.sum(Operation.add(Operation.add(ax2,bx),c));
		Tensor gx = Operation.add(Operation.mul(Operation.mul(two,x),a),b);
		f.backward(true);
		logger.info("ax2="); dump(ax2.getData());
		logger.info("bx=");  dump(bx.getData());
		logger.info("c=");   dump(c.getData());
		logger.info("f=");   dump(f.getData());
		logger.info("ga=");  dump(x.getGradient());
		logger.info("ga(true)="); dump((gx).getData());
	}
}
