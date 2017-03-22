package error;

import java.util.ArrayList;

import lexer.Token;
import lexer.Token.Symble;
import parser.Variable.VariableType;
/**
 * 此文件无效
 * @author Qideas
 *
 */
public class CmmError {

	/*
	 * 错误处理
	 * 思路：
	 * 1.相同变量检查：已实现
	 * 	遍历list，取出所有变量的值，存在另一个list中，然后进行变量的值得检查。
	 * 	
	 * 2.类型变量检查：已实现
	 * 	int 表示整数 
	 * 	real表示实数
	 * 3.语法错误检查：未实现将在语法分析时实现
	 * 		未加分号
	 * 		缺少关系运算符
	 * 		
	 * 4.出现位置字符错误.已实现
	 * 	
	 * 5.数组下标不是整数错误:已实现
	 * 
	 * 6，使用之前没有声明错误：已实现
	 * 
	 */
	private static final int TYPE_ERR=0;
	private static final int SAME_VARIABLE_ERR=1;
	private static final int GRAMER_ERR=2;
	
	private int errLine=-1;
	
	public static String checkError(ArrayList<Token> list) {
		String errorMessage="";
		
		//检查相同变量
		ArrayList<Token> list_error_sv=checkSameVari(list);
		if (list_error_sv.size()==0) {
			errorMessage=errorMessage+"没有相同变量错误.\n";
		} else {
			errorMessage=errorMessage+"出现相同变量错误:\n";
			
			for (Token token : list_error_sv) {
				errorMessage=errorMessage+"出现错误行："+token.getLineNum()+"\t"
						+"变量类型标志："+token.getSymble().toString()+"\t"
						+"变量类型："+token.getVariableType()+"\t"
						+"变量值："+token.getValue()+"\n";
			}
			
		}
		
		//检查类型错误
		ArrayList<Token> list_error_te=checkTypeError(list);
		if (list_error_te.size()==0) {
			errorMessage=errorMessage+"没有变量类型错误.\n";
		} else {
			errorMessage=errorMessage+"出现变量类型错误:\n";
			
			for (Token token : list_error_te) {
				errorMessage=errorMessage+"出现错误行："+token.getLineNum()+"\t"
						+"变量类型标志："+token.getSymble().toString()+"\t"
						+"变量类型："+token.getVariableType()+"\t"
						+"变量值："+token.getValue()+"\n";
			}
			
		}
		
		//检查数组下标
		ArrayList<Token> list_error_ai=checkArrayIndex(list);
		if (list_error_ai.size()==0) {
			errorMessage=errorMessage+"没有数组下标错误.\n";
		} else {
			errorMessage=errorMessage+"出现数组下标错误:\n";
			
			for (Token token : list_error_ai) {
				errorMessage=errorMessage+"出现错误行："+token.getLineNum()+"\t"
						+"类型："+token.getSymble().toString()+"\t"
						+"数组下标值："+token.getValue()+"\t"
						+"应属类型："+token.getVariableType()+"\n";
			}
			
		}
		
		//检查变量是否声明
		ArrayList<Token> list_error_vnd=checkVariableNotDeclare(list);
		if (list_error_vnd.size()==0) {
			errorMessage=errorMessage+"没有未声明变量错误.\n";
		} else {
			errorMessage=errorMessage+"出现未声明变量错误:\n";
			
			for (Token token : list_error_vnd) {
				errorMessage=errorMessage+"出现错误行："+token.getLineNum()+"\t"
						+"类型："+token.getSymble().toString()+"\t"
						+"未声明变量："+token.getValue()+"\t"
						+"类型："+token.getVariableType()+"\n";
			}
			
		}
		return errorMessage;
	}
	/*
	 * 相同变量名检查
	 */
	private static ArrayList<Token> checkSameVari(ArrayList<Token> list) {
		ArrayList<Token> list_error_sv=new ArrayList<>();
		ArrayList<Token> temp=new ArrayList<>();
		//取出声明的变量
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getSymble()==Symble.IDENTIFIER) {
				if (list.get(i-1).getSymble()==Symble.INT||list.get(i-1).getSymble()==Symble.REAL) {
					temp.add(list.get(i));
				}
			}
		}
		//进行变量检查
		for (int i = 0; i < temp.size(); i++) {
			String value=temp.get(i).getValue();
			for (int j = i+1; j < temp.size(); j++) {
				if (value.equals(temp.get(j).getValue())) {
					list_error_sv.add(temp.get(i));
					list_error_sv.add(temp.get(j));
				}
			}
		}
		
		return list_error_sv;
	}
	
	/*
	 * 变量类型检查
	 */
	private static ArrayList<Token> checkTypeError(ArrayList<Token> list) {
		ArrayList<Token> list_error_te=new ArrayList<>();
		//去除所有的int 和real变量
		ArrayList<Token> temp=new ArrayList<>();
		for (Token token : list) {
			if (token.getSymble()==Symble.DIGIT_INT||token.getSymble()==Symble.DIGIT_REAL) {
				temp.add(token);
			}
		}
		
		//进行类型判断
		for (Token token : temp) {
			
			if (token.getVariableType()==VariableType.INT) {
				Integer value=null;
				try {
					value=new Integer(token.getValue());
//					System.out.println("Value:"+value);
				} catch (Exception e) {
					
				}
				if (value==null) {
					list_error_te.add(token);
				}
			}
			if (token.getVariableType()==VariableType.REAL) {
				Double value=null;
				try {
					value=new Double(token.getValue());
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (value==null) {
					list_error_te.add(token);
				}
			}
		}
		
		
		return list_error_te;
	}
	
	/*
	 * 数组下标检查
	 */
	public static ArrayList<Token> checkArrayIndex(ArrayList<Token> list) {
		ArrayList<Token> list_error_ai=new ArrayList<>();
		ArrayList<Token> temp=new ArrayList<>();
		for (Token token : list) {
			if (token.getVariableType()==VariableType.ARR_INT||token.getVariableType()==VariableType.ARR_REAL) {
				temp.add(token);
			}
		}
		//检查是否为整数
		for (Token token : temp) {
			Integer value=null;
			try {
				value=new Integer(token.getValue());
//				System.out.println("Value:"+value);
			} catch (Exception e) {
				
			}
			if (value==null) {
				list_error_ai.add(token);
			}
		}
		
		return list_error_ai;
	}
	
	/*
	 * 是否使用未声明变量
	 */
	public static ArrayList<Token> checkVariableNotDeclare(ArrayList<Token> list) {
		ArrayList<Token> list_error_vnd=new ArrayList<>();
		ArrayList<Token> temp=new ArrayList<>();
		ArrayList<Token> varDeclared=new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getSymble()==Symble.IDENTIFIER) {
				if (list.get(i-1).getSymble()==Symble.INT||list.get(i-1).getSymble()==Symble.REAL) {
					varDeclared.add(list.get(i));
//					System.out.println("声明的变量："+list.get(i).toString());
				}
				temp.add(list.get(i));
//				System.out.println("变量："+list.get(i).toString());
			}
		}
		//判断是否有未声明的变量
		for (Token token : temp) {
			boolean exist=false;
			for (Token t : varDeclared) {
				if (token.getValue().equals(t.getValue())) {
					exist=true;
				}
			}
			if (!exist) {
				list_error_vnd.add(token);
//				System.out.println("未声明的变量："+token.toString());
			}
		}
		
		return list_error_vnd;
	}
	
	
}
