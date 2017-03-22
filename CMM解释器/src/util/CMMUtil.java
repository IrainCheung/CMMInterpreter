package util;

public class CMMUtil {

	
	
	public static boolean isNumber(String string) {
		
		
		return false;
	}
	
	public static boolean isInteger(String number) {
		boolean isInteger=false;
		
		try {
			int a=Integer.parseInt(number);
			isInteger=true;
		} catch (Exception e) {
			// TODO: handle exception
			isInteger=false;
		}
		
		
		return isInteger;
	}
	public static boolean isDouble(String number) {
		boolean isInteger=false;
		
		try {
			double a=Double.parseDouble(number);
			isInteger=true;
		} catch (Exception e) {
			// TODO: handle exception
			isInteger=false;
		}
		
		
		return isInteger;
	}
	
}
