package error;

import error.Error.ErrorDescription;

public class CmmException extends Exception {

	public CmmException(ErrorDescription errorDescription,String des) {
		// TODO 自动生成的构造函数存根
		super(errorDescription.getDescripton()+des);
	}
	
	/**
	 * 自定义错误
	 * @param exception
	 */
	public CmmException(String exception) {
		super(exception);
		// TODO 自动生成的构造函数存根
	}
	
	
	
}
