package intermediatecode;

/*
	 * �﷨
	 * jump 	(jump,����,null,Ŀ��)				��ת
	 * in		(in,null,null,null)				����block
	 * out		(out,null,null,null)			��block
	 * int		(int,null/count,value,name)		����ֵ������,�ڶ�����Ϊnull����ʾ������Ŀ
	 * real		(real,null/count,value,name)	����ֵ������,�ڶ�����Ϊnull����ʾ������Ŀ
	 * read		(read,null/index,null,name)		��ȡ���������и�ֵ,index��ʾ�����±�
	 * write	(write,null/index,null,name)	��ӡ������ֵ,index��ʾ�����±�
	 * assign	(assign,null/index,value,name)	��ֵ���,index��ʾ�����±�
	 * 			(operator,va1,va2,result)		result=va1 operator va2
	 * 			(condi_op,va1,va2,result)		result=va1 condi_op va2
	 * 
	 * 
	 */
public class Quaternary{
	
	private Code code=null;
	private String second=null;
	private String third=null;
	private String four=null;
	
	public Quaternary() {
		// TODO Auto-generated constructor stub
	}
	
	public Quaternary(Code code, String second, String third, String four) {
		super();
		this.code = code;
		this.second = second;
		this.third = third;
		this.four = four;
	}
	public Code getCode() {
		return code;
	}
	public void setCode(Code code) {
		this.code = code;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}
	public String getThird() {
		return third;
	}
	public void setThird(String third) {
		this.third = third;
	}
	public String getFour() {
		return four;
	}
	
	public void setFour(String four) {
		this.four = four;
	}
	
	@Override
	public String toString() {
		return "(" + code.getValue() + ", " + second + ", " + third + ", " + four + ")";
	}
	
}


