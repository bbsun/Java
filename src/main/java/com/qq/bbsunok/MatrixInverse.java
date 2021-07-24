package com.qq.bbsunok;
import edu.mines.jtk.la.DMatrix;
import java.util.logging.Logger;
/**
 * 采用镶边法计算矩阵的逆。
 * @author bbsun
 * @version 20210723
 */
public class MatrixInverse {
	public static void main(String[] args) {
		Logger logger = Logger.getGlobal();
		logger.info("采用镶边法求矩阵的逆");
		DMatrix m  = new DMatrix(new double[][]{{100,4,5},{7,1,4},{10,20,30}});
		String info = "";
		info +=("\n 矩阵为 \n"+m.toString());
		DMatrix im = runInverse(m); 
		info +=("\n 逆矩阵为\n"+im.toString());
		info +=(" \n 矩阵乘以其逆矩阵为 \n"+m.times(im).toString());
		logger.info(info);
	}
	/**
	 * 计算矩阵的逆。
	 * @param m 输入矩阵
	 * @throws ArithemeticException 
	 * @return 输入矩阵的逆矩阵
	 */
	public static DMatrix runInverse(DMatrix m) throws ArithmeticException {
		int n1 = m.getRowCount();
		int n2 = m.getColumnCount();
		if(m.get(0,0)==0)
			throw new ArithmeticException("矩阵第一行第一列元素需为非零元素。");
		if(n1!=n2)
			throw new ArithmeticException("矩阵需要为方阵。");
		int n = n1;
		if(n==1) {
			return new DMatrix(1,1,1.0/m.get(0,0));
		}else {
			DMatrix x;
			DMatrix y;
			DMatrix w;
			DMatrix ia =  new DMatrix(1,1,1.0/m.get(0,0));
			for(int i=0;i<(n-1);i++) {
				int ic = i + 1;
				int br = 0;
				int er = i;
				int ir = i + 1;
				int bc = 0;
				int ec = i;
				int[] indr;
				int[] indc;
				indr = (br!=er)? new int[] {br,er}:new int[] {br};
				indc = (bc!=ec)? new int[] {bc,ec}:new int[] {bc};
				double  g = m.get(i+1,i+1);
				DMatrix f = m.get(indr,ic);
				DMatrix e = m.get(ir,indc);
				double z = 1.0/(g-e.times(ia.times(f)).get(0,0));
				if(z==0) {
					throw new ArithmeticException("矩阵为奇异矩阵。");
				}
				y = ia.times(f).times(z).times(-1.0);
				x = e.times(ia).times(1.0/(e.times(ia).times(f).get(0,0)-g));
				w = ia.minus(ia.times(f).times(x));
				ia = new DMatrix(i+2,i+2);
				ia.set(indr,indc,w);
				ia.set(indr,ic,y);
				ia.set(ir,indc,x);
				ia.set(i+1,i+1,z);
			}
			return ia;
		}
	}
	
}
