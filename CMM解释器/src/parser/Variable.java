package parser;

import java.util.Arrays;

/**
 * ���ڴ洢���������������ı���
 * @author Qideas
 *
 */
public class Variable {
	
	//����������ܵ�ֵ
	private int int_value;
	private double real_value;
	private int [] int_arr;
	private double [] real_arr;
	/**
	 * �洢�����С
	 */
	private int size;
	/**
	 * ��������
	 * @author Qideas
	 *
	 */
	public enum VariableType{
		INT,REAL,ARR_INT,ARR_REAL
	}
	
	/**
	 * �洢��������
	 */
	private VariableType vt;
	/**
	 * �洢�������
	 */
	private int scope;
	/**
	 * �洢��������
	 */
	private String name;
	/**
	 * �к�
	 */
	private int lineNum;
	/**
	 * ��ȡintֵ
	 * @return
	 */
	public int getInt_value() {
		return int_value;
	}
	/**
	 * ����intֵ
	 * @param int_value
	 */
	public void setInt_value(int int_value) {
		this.int_value = int_value;
	}
	/**
	 * ��ȡrealֵ
	 * @return
	 */
	public double getReal_value() {
		return real_value;
	}
	/**
	 * ����realֵ
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
	 * ��ȡ��������
	 * @return
	 */
	public VariableType getVt() {
		return vt;
	}
	/**
	 * ���ñ�������
	 * @param vt
	 */
	public void setVt(VariableType vt) {
		this.vt = vt;
	}
	/**
	 * ��ȡ�������
	 * @return
	 */
	public int getScope() {
		return scope;
	}
	/**
	 * ���ñ������
	 * @param scope
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}
	/**
	 * ��ȡ��������
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * ���ñ�������
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
	 * ��ȡ�����С
	 * @return
	 */
	public int getSize() {
		return size;
	}
	/**
	 * ���������С
	 */
	public void setSize(int size) {
		this.size = size;
		//���������ʼ��
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
			//����
		}
		
	}
	/**
	 * ��ȡint�����indexλ�ô��ı�����ֵ
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
	 * ��ȡreal�����indexλ�ô��ı�����ֵ
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
	 * ���캯��
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
