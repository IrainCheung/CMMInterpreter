package error;

import error.Error.ErrorDescription;

public class CmmException extends Exception {

	public CmmException(ErrorDescription errorDescription,String des) {
		// TODO �Զ����ɵĹ��캯�����
		super(errorDescription.getDescripton()+des);
	}
	
	/**
	 * �Զ������
	 * @param exception
	 */
	public CmmException(String exception) {
		super(exception);
		// TODO �Զ����ɵĹ��캯�����
	}
	
	
	
}
