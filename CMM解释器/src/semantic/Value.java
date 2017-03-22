package semantic;

/**
 * 用于存储expression解析返回的结果
 * @author Qideas
 *
 */
public class Value {

	private boolean isDouble;
	private boolean isRealArr;
	
	private int value_int;
	private double value_real;
	public boolean isDouble() {
		return isDouble;
	}
	public void setDouble(boolean isDouble) {
		this.isDouble = isDouble;
	}
	public int getValue_int() {
		return value_int;
	}
	public void setValue_int(int value_int) {
		this.value_int = value_int;
	}
	public double getValue_real() {
		return value_real;
	}
	public void setValue_real(double value_real) {
		this.value_real = value_real;
	}
	
	public double getOnlyValue() {
		if (isDouble) {//可能有Bug
			return value_real;
		}else {
			return value_int;
		}
	}
}
