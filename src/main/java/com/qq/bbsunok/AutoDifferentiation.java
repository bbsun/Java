package com.qq.bbsunok;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.mines.jtk.util.ArrayMath;

import static edu.mines.jtk.util.ArrayMath.*;

/**
 * 自动微分的简单实现
 * @author bingbing sun 
 * @version 20210731
 */
/**
 * 张量数据
 */
class Tensor {
	/**
	 * 保存的数据
	 */
	private float[] data = null;
	/**
	 * 是否计算梯度
	 */
	private boolean requiresGrad = false;
	/**
	 * 保存在计算图中的父对象
	 */
	private List<Tensor> precedents = null;
	/**
	 * 运算类型 由父对象运算得到当前对象
	 */
	private OperationType operationType = null;
	/**
	 * 保存的梯度数据
	 */
	private float[] grad = null;
	/**
	 * 保存梯度张量以计算高阶导数
	 */
	private Tensor grad1 = null;
	/**
	 * 保存名字
	 */
	private String name = null;

	/**
	 * 生成张量
	 * 
	 * @param data 数据
	 */
	public Tensor(float[] data) {
		this(data, false);
	}

	/**
	 * 生成张量
	 * 
	 * @param data         数据
	 * @param requiresGrad 是否计算梯度
	 */
	public Tensor(float[] data, boolean requiresGrad) {
		this(data, requiresGrad, null, null);
	}

	/**
	 * 生成张量
	 * 
	 * @param data          数据
	 * @param requiresGrad  是否计算梯度
	 * @param precedents    父对象列表
	 * @param operationType 运算类型
	 */
	public Tensor(float[] data, boolean requiresGrad, List<Tensor> precedents, OperationType operationType) {
		this(data, requiresGrad, precedents, operationType, null);
	}

	/**
	 * 生成张量
	 * 
	 * @param data          数据
	 * @param requiresGrad  是否计算梯度
	 * @param precedents    父对象列表
	 * @param operationType 运算类型
	 * @param name          名字
	 */
	public Tensor(float[] data, boolean requiresGrad, List<Tensor> precedents, OperationType operationType,
			String name) {
		this.data = data;
		this.requiresGrad = requiresGrad;
		this.precedents = precedents;
		this.operationType = operationType;
		this.name = name;
	}

	/**
	 * 返回对数据的引用
	 * 
	 * @return 数据的引用
	 */
	public float[] getData() {
		return data;
	}

	/**
	 * 返回是否求梯度
	 * 
	 * @return 是否求梯度
	 */
	public boolean getRequiresGrad() {
		return requiresGrad;
	}

	/**
	 * 返回对梯度的引用
	 * 
	 * @return 梯度数据的引用
	 */
	public float[] getGradient() {
		return grad;
	}

	/**
	 * 返回对父对象的引用
	 * 
	 * @return 父对象的引用
	 */
	public List<Tensor> getPrecedents() {
		return precedents;
	}

	/**
	 * 返回梯度张量
	 * 
	 * @return 梯度张量
	 */
	public Tensor getGradienTensor() {
		return grad1;
	}

	/**
	 * 返回张量各个维度的大小，当前只考虑1维的情形，该函数返回1个整型标量
	 * 
	 * @return 当前张量（向量）的长度
	 */
	public int getDataShape() {
		return data.length;
	}
	/**
	 * 返回张量的名字
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置父对象列表
	 * 
	 * @param precedents 父对象列表
	 */
	public void setPrecedents(List<Tensor> precedents) {
		this.precedents = precedents;
	}

	/**
	 * 设置运算类型
	 * 
	 * @param operationType
	 */
	public void setOperation(OperationType operationType) {
		this.operationType = operationType;
	}

	/**
	 * 设置是否计算梯度
	 * 
	 * @param requiresGrad 是否计算梯度
	 */
	public void setRequiresGrad(boolean requiresGrad) {
		this.requiresGrad = requiresGrad;
	}

	/**
	 * 设置梯度 
	 * 
	 * @param g  输入梯度
	 */
	public void setGradient(float[] g) {
		if (this.precedents != null || this.grad == null)
			this.grad = g;
		else
			this.grad = add(this.grad, g);
	}
	/**
	 * 梯度自动微分
	 * 
	 * @param g 梯度
	 */
	public void setGradientTensor(Tensor g) {
		if (this.grad1 == null)
			this.grad1 = g;
		else 
			grad1 = Operation.add(this.grad1, g);
	}
	/**
	 * 设置名字
	 * @param name 名字
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 自动微分求梯度
	 * 
	 * @param last 是否为最后一层
	 */
	public void autoGrad(boolean last) {
		if (last == true) {
			int n = getDataShape();
			float[] d = ArrayMath.rampfloat(1.0f, 0.0f, n);
			this.setGradientTensor(new Tensor(d, false, null, null, "root0"));
		}
		if (this.operationType != null && this.precedents != null && this.getGradienTensor() != null) {
			//System.out.println(this.operationType);
			switch (this.operationType) {
			case NEG:
				Operation.negBackwardAuto(this.precedents.get(0), this);
				break;
			case EQUAL:
				Operation.copyBackwardAuto(this.precedents.get(0), this);
				break;
			case ADD:
				Operation.addBackwardAuto(this.precedents.get(0), this.precedents.get(1), this);
				break;
			case SUB:
				break;
			case MUL:
				Operation.mulBackwardAuto(this.precedents.get(0), this.precedents.get(1), this);
				break;
			case DIV:
				break;
			case MEAN:
				break;
			case SUM:
				break;
			default:
				break;
			}
			for (Tensor p : this.precedents)
				p.autoGrad(false);
		}
	}

	/**
	 * 进行梯度的反向传播
	 * 
	 * @param start 当前张量是否属于最终计算出的张量，即其没有子对象。
	 */
	public void backward(boolean last) {
		if (this.requiresGrad == false)
			return;
		if (last == true) {
			int n = getDataShape();
			float[] g = ArrayMath.rampfloat(1.0f, 0, n);
			this.grad = g;
		}
		/*System.out.println(this);
		if (this.getGradient() != null) {
			dump(this.getGradient());
		}*/
		if (this.operationType != null && this.precedents != null) {
			switch (this.operationType) {
			case NEG:
				Operation.negBackward(this.precedents.get(0), this);
				/*System.out.print("\t");
				System.out.print(this.precedents.get(0).getName());
				if (this.precedents.get(0).getGradient() != null)
					ArrayMath.dump(this.precedents.get(0).getGradient());*/
				break;
			case EQUAL:
				Operation.copyBackward(this.precedents.get(0), this);
				/*System.out.print("\t");
				System.out.print(this.precedents.get(0).getName());
				if (this.precedents.get(0).getGradient() != null)
					ArrayMath.dump(this.precedents.get(0).getGradient());*/
				break;
			case ADD:
				Operation.addBackward(this.precedents.get(0), this.precedents.get(1), this);
				/*System.out.print("\t");
				System.out.print(this.precedents.get(0).getName());
				if (this.precedents.get(0).getGradient() != null)
					ArrayMath.dump(this.precedents.get(0).getGradient());
				System.out.print("\t");
				System.out.print(this.precedents.get(1).getName());
				if (this.precedents.get(1).getGradient() != null)
					ArrayMath.dump(this.precedents.get(1).getGradient());*/
				break;
			case SUB:
				Operation.subBackward(this.precedents.get(0), this.precedents.get(1), this);
				break;
			case MUL:
				Operation.mulBackward(this.precedents.get(0), this.precedents.get(1), this);
				/*System.out.print("\t");
				System.out.print(this.precedents.get(0).getName());
				if (this.precedents.get(0).getGradient() != null)
					ArrayMath.dump(this.precedents.get(0).getGradient());
				System.out.print("\t");
				System.out.print(this.precedents.get(1).getName());
				if (this.precedents.get(1).getGradient() != null)
					ArrayMath.dump(this.precedents.get(1).getGradient());*/
				break;
			case DIV:
				Operation.divBackward(this.precedents.get(0), this.precedents.get(1), this);
				break;
			case MEAN:
				Operation.meanBackward(this.precedents.get(0), this);
				break;
			case SUM:
				Operation.sumBackward(this.precedents.get(0), this);
				break;
			}
			for (Tensor p : precedents)
				p.backward(false);
		}
	}

	/**
	 * 输出张量
	 */
	@Override
	public String toString() {
		StringBuilder info = new StringBuilder();
		// 输出DATA数据
		info.append("{data:[");
		for (float x : this.data)
			info.append(String.format("%12.5f ", x));
		info.append("],");
		info.append(" requiresGrad: " + this.requiresGrad + ", ");
		/*
		 * info.append("precedents: ["); if (precedents != null) { for (Tensor t :
		 * precedents) { info.append(t.toString() + " "); } } else {
		 * info.append("null"); }
		 */
		info.append(", operationType: ");
		info.append(operationType);
		info.append(", name: ");
		info.append(name);
		info.append("}");
		return info.toString();
	}

}

/**
 * 运算类型
 */
enum OperationType {
	/**
	 * 赋值运算
	 */
	EQUAL,
	/**
	 * 取反运算
	 */
	NEG,
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
class Operation {
	/**
	 * 赋值运算
	 * 
	 * @param 赋值运算的输入
	 * @return 赋值运算的输出
	 */
	static Tensor copy(Tensor a) {
		float[] data = a.getData();
		List<Tensor> precedents = addPrecendets(a);
		return new Tensor(data, a.getRequiresGrad(), precedents, OperationType.EQUAL, "equal(" + a.getName() + ")");
	}

	/**
	 * 求反运算
	 * 
	 * @param a 求反运算的输入
	 * @return 求反运算的输出
	 */
	static Tensor neg(Tensor a) {
		float[] data = ArrayMath.mul(a.getData(), -1.0f);
		List<Tensor> precedents = addPrecendets(a);
		return new Tensor(data, a.getRequiresGrad(), precedents, OperationType.NEG, "neg(" + a.getName() + ")");
	}

	/**
	 * 加法运算
	 * 
	 * @param a 输入张量
	 * @param b 输入张量
	 * @return 张量a+b
	 */
	static Tensor add(Tensor a, Tensor b) {
		float[] data = ArrayMath.add(a.getData(), b.getData());
		List<Tensor> precedents = addPrecendets(a, b);
		return new Tensor(data, a.getRequiresGrad() || b.getRequiresGrad(), precedents, OperationType.ADD,
				"(" + a.getName() + "+" + b.getName() + ")");
	}

	/**
	 * 减法运算
	 * 
	 * @param a 输入张量
	 * @param b 输入张量
	 * @return 张量a-b
	 */
	static Tensor sub(Tensor a, Tensor b) {
		float[] data = ArrayMath.sub(a.getData(), b.getData());
		List<Tensor> precedents = addPrecendets(a, b);
		return new Tensor(data, a.getRequiresGrad() || b.getRequiresGrad(), precedents, OperationType.SUB,
				"(" + a.getName() + "-" + b.getName() + ")");
	}

	/**
	 * 乘法运算
	 * 
	 * @param a 输入张量
	 * @param b 输入张量
	 * @return 张量a*b
	 */
	static Tensor mul(Tensor a, Tensor b) {
		float[] data = ArrayMath.mul(a.getData(), b.getData());
		List<Tensor> precedents = addPrecendets(a, b);
		return new Tensor(data, a.getRequiresGrad() || b.getRequiresGrad(), precedents, OperationType.MUL,
				"(" + a.getName() + "*" + b.getName() + ")");
	}

	/**
	 * 除法运算
	 * 
	 * @param a 输入张量
	 * @param b 输入张量
	 * @return 张量a/b
	 */
	static Tensor div(Tensor a, Tensor b) {
		float[] data = ArrayMath.div(a.getData(), b.getData());
		List<Tensor> precedents = addPrecendets(a, b);
		return new Tensor(data, a.getRequiresGrad() || b.getRequiresGrad(), precedents, OperationType.DIV,
				"(" + a.getName() + "/" + b.getName() + ")");
	}

	/**
	 * 均值运算
	 * 
	 * @param a 输入张量
	 * @return 张量mean(a)
	 */
	static Tensor mean(Tensor a) {
		float[] data = new float[] { ArrayMath.sum(a.getData()) / a.getDataShape() };
		List<Tensor> precedents = addPrecendets(a);
		return new Tensor(data, a.getRequiresGrad(), precedents, OperationType.MEAN);
	}

	/**
	 * 求和运算
	 * 
	 * @param a 输入张量
	 * @return 张量sum(a)
	 */
	static Tensor sum(Tensor a) {
		float[] data = new float[] { ArrayMath.sum(a.getData()) };
		List<Tensor> precedents = addPrecendets(a);
		return new Tensor(data, a.getRequiresGrad(), precedents, OperationType.SUM);
	}

	/**
	 * 赋值运算的的伴随预算
	 * 
	 * @param a 赋值运算正向运算的输入
	 * @param c 赋值运算正向运算的输出
	 */
	static void copyBackwardAuto(Tensor a, Tensor c) {
		a.setGradientTensor(copy(c.getGradienTensor()));
	}

	/**
	 * 求反运算的伴随运算
	 * 
	 * @param a 求反运算正向运算的输入
	 * @param c 求反运算正向运算的输出
	 */
	static void negBackwardAuto(Tensor a, Tensor c) {
		a.setGradientTensor(neg(c.getGradienTensor()));
	}

	/**
	 * 求和运算的伴随运算
	 * 
	 * @param a 加法预算正向运算的输入
	 * @param b 加法运算正向运算的输入
	 * @param c 加法运算正向运算的输出
	 */
	static void addBackwardAuto(Tensor a, Tensor b, Tensor c) {
		a.setGradientTensor(copy(c.getGradienTensor()));
		b.setGradientTensor(copy(c.getGradienTensor()));
	}

	/**
	 * 乘法运算的伴随运算
	 * 
	 * @param a 乘法运算正向运算的输入
	 * @param b 乘法运算正向运算的输出
	 * @param c 乘法预算正向运算的输出
	 */
	static void mulBackwardAuto(Tensor a, Tensor b, Tensor c) {
		a.setGradientTensor(mul(b, c.getGradienTensor()));
		b.setGradientTensor(mul(a, c.getGradienTensor()));
	}
	/**
	 * 对赋值运算反传梯度 对应的正运算为 c=a
	 * 
	 * @param a
	 * @param c
	 */
	static void copyBackward(Tensor a, Tensor c) {
		a.setGradient(c.getGradient());
	}

	/**
	 * 对求反运算反传梯度 对应的正运算为 c=-a
	 * 
	 * @param a
	 * @param c
	 */
	static void negBackward(Tensor a, Tensor c) {
		a.setGradient(ArrayMath.mul(c.getGradient(), -1.0f));
	}

	/**
	 * 对于加法运算反传梯度 对应的正运算为 c=a+b。
	 * 
	 * @param a 加法正向运算中的输入a
	 * @param b 加法正向运算中的输入b
	 * @param c 加法正向运算中的输出c=a+b
	 */
	static void addBackward(Tensor a, Tensor b, Tensor c) {
		a.setGradient(c.getGradient());
		b.setGradient(c.getGradient());
	}

	/**
	 * 对于减法运算反传梯度 对应的正运算为c=a-b
	 * 
	 * @param a 减法正向运算中的输入a
	 * @param b 减法正向运算中的输入b
	 * @param c 减法正向运算中的输出c=a-b
	 */
	static void subBackward(Tensor a, Tensor b, Tensor c) {
		a.setGradient(c.getGradient());
		b.setGradient(ArrayMath.mul(-1.0f, c.getGradient()));
	}

	/**
	 * 对于乘法运算反传梯度 对应的正运算为c=a*b
	 * 
	 * @param a 乘法正向运算中的输入a
	 * @param b 乘法正向运算中的输入b
	 * @param c 乘法正向运算中的输出c=a*b
	 */
	static void mulBackward(Tensor a, Tensor b, Tensor c) {
		a.setGradient(ArrayMath.mul(c.getGradient(), b.getData()));
		b.setGradient(ArrayMath.mul(c.getGradient(), a.getData()));
	}

	/**
	 * 对于除法运算的反传梯度 对应的正向运算为c=a/b
	 * 
	 * @param a 除法正向运算中的输入a
	 * @param b 除法正向运算中的输入b
	 * @param c 除法正向运算中的输入c
	 */
	static void divBackward(Tensor a, Tensor b, Tensor c) {

		int n = a.getDataShape();
		float[] gb = new float[n];
		float[] gc = c.getGradient();
		float[] da = a.getData();
		float[] db = b.getData();
		for (int i = 0; i < n; i++)
			gb[i] = -da[i] * gc[i] / pow(db[i], 2.0f);
		a.setGradient(ArrayMath.div(c.getGradient(), b.getData()));
		b.setGradient(gb);
	}

	/**
	 * 对于取均值运算的反传梯度 对应的正向运算为c=mean(a);
	 * 
	 * @param a 均值运算中的输入a
	 * @param c 均值运算中的输出c
	 */
	static void meanBackward(Tensor a, Tensor c) {
		int n = a.getDataShape();
		float[] ga = new float[n];
		float[] gc = c.getGradient();
		for (int i = 0; i < n; i++)
			ga[i] = gc[0] / n;
		a.setGradient(ga);
	}

	/**
	 * 对于求和运算的反传梯度 对应的正向运算为c=sum(a)
	 * 
	 * @param a 求和运算中的输入a
	 * @param c 求和运算中的输出c
	 */
	static void sumBackward(Tensor a, Tensor c) {
		int n = a.getDataShape();
		float[] ga = new float[n];
		float[] gc = c.getGradient();
		for (int i = 0; i < n; i++)
			ga[i] = gc[0];
		a.setGradient(ga);
	}

	// Private
	private static List<Tensor> addPrecendets(Tensor a) {
		List<Tensor> precedents = new ArrayList<>();
		precedents.add(a);
		return precedents;
	}

	private static List<Tensor> addPrecendets(Tensor a, Tensor b) {
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
		Tensor x = new Tensor(new float[] { 1.0f }, true);
		Tensor a = new Tensor(new float[] { 2.0f }, false);
		Tensor f = Operation.add(Operation.mul(x,Operation.mul(x, x)),Operation.mul(a,x));
		logger.info("f="+f.getData()[0]);
		f.autoGrad(true);
		Tensor gx = x.getGradienTensor();
		logger.info("df/dx="+gx.getData()[0]);
		gx.backward(true);
		logger.info("df^2/dx2="+x.getGradient()[0]);
	}
}
