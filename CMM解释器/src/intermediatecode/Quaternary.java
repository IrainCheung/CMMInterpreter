package intermediatecode;

/*
	 * 语法
	 * jump 	(jump,条件,null,目的)				跳转
	 * in		(in,null,null,null)				进入block
	 * out		(out,null,null,null)			出block
	 * int		(int,null/count,value,name)		变量值，名字,第二个不为null，表示数组数目
	 * real		(real,null/count,value,name)	变量值，名字,第二个不为null，表示数组数目
	 * read		(read,null/index,null,name)		读取变量，进行赋值,index表示数组下标
	 * write	(write,null/index,null,name)	打印变量的值,index表示数组下标
	 * assign	(assign,null/index,value,name)	赋值语句,index表示数组下标
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


