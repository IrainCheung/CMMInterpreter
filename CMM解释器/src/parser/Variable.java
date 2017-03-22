package parser;

import java.util.Arrays;

/**
 * 用于存储解析过程中遇到的变量
 * @author Qideas
 *
 */
public class Variable {
	
	//定义变量可能的值
	private int int_value;
	private double real_value;
	private int [] int_arr;
	private double [] real_arr;
	/**
	 * 存储数组大小
	 */
	private int size;
	/**
	 * 变量类型
	 * @author Qideas
	 *
	 */
	public enum VariableType{
		INT,REAL,ARR_INT,ARR_REAL
	}
	
	/**
	 * 存储变量类型
	 */
	private VariableType vt;
	/**
	 * 存储变量层次
	 */
	private int scope;
	/**
	 * 存储变量名字
	 */
	private String name;
	/**
	 * 行号
	 */
	private int lineNum;
	/**
	 * 获取int值
	 * @return
	 */
	public int getInt_value() {
		return int_value;
	}
	/**
	 * 设置int值
	 * @param int_value
	 */
	public void setInt_value(int int_value) {
		this.int_value = int_value;
	}
	/**
	 * 获取real值
	 * @return
	 */
	public double getReal_value() {
		return real_value;
	}
	/**
	 * 设置real值
	 * @return
	 */
	public void setReal_value(double real_value) {
		this.real_value = real_value;
	}

	public int[] getInt_arr() {
		return int_arr;
	}

	public void setInt_arr(int[] int_arr) {
		this.int_arr = int_arr;
	}

	public double[] getReal_arr() {
		return real_arr;
	}

	public void setReal_arr(double[] real_arr) {
		this.real_arr = real_arr;
	}
	/**
	 * 获取变量类型
	 * @return
	 */
	public VariableType getVt() {
		return vt;
	}
	/**
	 * 设置变量类型
	 * @param vt
	 */
	public void setVt(VariableType vt) {
		this.vt = vt;
	}
	/**
	 * 获取变量层次
	 * @return
	 */
	public int getScope() {
		return scope;
	}
	/**
	 * 设置变量层次
	 * @param scope
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}
	/**
	 * 获取变量名字
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置变量名字
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	/**
	 * 获取数组大小
	 * @return
	 */
	public int getSize() {
		return size;
	}
	/**
	 * 设置数组大小
	 */
	public void setSize(int size) {
		this.size = size;
		//进行数组初始化
		if (vt==VariableType.ARR_INT) {
			int_arr=new int[size];
			for (int i = 0; i < int_arr.length; i++) {
				int_arr[i]=0;
			}
		}else if (vt==VariableType.ARR_REAL) {
			real_arr=new double[size];
			for (int i = 0; i < real_arr.length; i++) {
				real_arr[i]=0;
			}
		}else {
			//报错
		}
		
	}
	/**
	 * 获取int数组的index位置处的变量的值
	 * @param index
	 * @return
	 */
	public int getIntArrAtIndexOf(int index) {
		return int_arr[index];
	}
	@Override
	public String toString() {
		return "Variable [int_value=" + int_value + ", real_value=" + real_value + ", int_arr="
				+ Arrays.toString(int_arr) + ", real_arr=" + Arrays.toString(real_arr) + ", size=" + size + ", vt=" + vt
				+ ", scope=" + scope + ", name=" + name + ", lineNum=" + lineNum + "]";
	}
	/**
	 * 获取real数组的index位置处的变量的值
	 * @param index
	 * @return
	 */
	public double getDoubleArrAtIndexOf(int index) {
		return real_arr[index];
	}
	
	public Variable() {
		super();
	}

	/**
	 * 构造函数
	 * @param vt
	 * @param scope
	 * @param name
	 * @param lineNum
	 */
	public Variable(VariableType vt, int scope, String name, int lineNum) {
		super();
		this.vt = vt;
		this.scope = scope;
		this.name = name;
		this.lineNum = lineNum;
	}
	
	
	
	
	
}
