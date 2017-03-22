package semantic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import error.CmmException;
import error.Error.ErrorDescription;
import intermediatecode.Code;
import intermediatecode.Quaternary;
import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import parser.StatementName;
import parser.TreeNode;
import parser.Variable;
import parser.VariableTable;
import parser.Variable.VariableType;
import util.CMMUtil;

/**
 * 语义分析器
 * @author Qideas
 *
 */
public class SemanticParser {

	/**
	 * 思路：
	 * 根据语法树的节点打印值或者读入值，完成赋值和变量初始化操作，所以只需关注read，write，declare，assign语句就可以了
	 * 向table中加入、读取、改变变量的值
	 * 
	 * 然后就是考虑条件判断的if，while
	 * 目前剩下的问题就是：
	 * 1.解决变量的层次问题，已解决
	 * 2.解决变量值的正确输出，而不是只输出double类型
	 * 解决思路：通过Value类来封装返回的结果，返回Value的对象，
	 * 在解析expression的时候逐层返回操作过后的Value对象，
	 * 最后在read或者write函数中通过判断Value对象中的标志，来输入或者输出数据。
	 * 已解决
	 * 
	 * 3.中间代码生成
	 * 		完成：declare，assign，read，write,if，while，expression，term，condition（有待改善）
	 * 4.多项式计算		完成
	 * 
	 */
	
	/**
	 * 变量表
	 */
	private VariableTable variableTable=new VariableTable();
	/**
	 * 变量的层次
	 */
	private static int scope=0;
	/**
	 * 保存输出结果
	 */
	private static String result="";
	
	public static void main(String[] args) throws IOException, CmmException {
		Lexer lexer=new Lexer();
		
		File file=new File("while.cmm");
		BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
		//词法分析
		ArrayList<Token> list=lexer.checkType(bufferedReader);
		System.out.println("词法分析结果：");
		System.out.println("length:"+list.size());
		lexer.printTokenList(list);
//		String errorMessage=CmmError.checkError(list);
//		System.out.println("错误检查结果：\n"+errorMessage);
		//语法分析
		Parser parser=new Parser(list);
		TreeNode root=parser.begainParse();
		System.out.println("语法树：");
		parser.printTree(root);
		parser.printGrammerError();
		
		//语义分析
		SemanticParser semanticParser=new SemanticParser();
		semanticParser.parseTree(root);
		semanticParser.variableTable.print();
		
		
	}
	
	/**
	 * 解析语法树
	 * @param root
	 * @throws CmmException 
	 */
	public String parseTree(TreeNode root) throws CmmException {
		result="";
		for (int i = 0; i < root.getChildCount(); i++) {
			
			if (((TreeNode) root.getChildAt(i)).getSt()==StatementName.DECLARE_STM) {
				parseDeclare((TreeNode) root.getChildAt(i));
			}else if (((TreeNode) root.getChildAt(i)).getSt()==StatementName.ASSIGN_STM) {
				parseAssign((TreeNode) root.getChildAt(i));
			}else if (((TreeNode) root.getChildAt(i)).getSt()==StatementName.IF_STM) {
				parseIf((TreeNode) root.getChildAt(i));
			}else if (((TreeNode) root.getChildAt(i)).getSt()==StatementName.WHILE_STM) {
				parseWhile((TreeNode) root.getChildAt(i));
			}else if (((TreeNode) root.getChildAt(i)).getSt()==StatementName.WRITE_STM) {
				parseWrite((TreeNode) root.getChildAt(i));
			}else if (((TreeNode) root.getChildAt(i)).getSt()==StatementName.READ_STM) {
				parseRead((TreeNode) root.getChildAt(i));
			}
			
		}
		
		return result;
	}
	
	/**
	 * 解析声明树节点
	 * @throws CmmException 
	 */
	public void parseDeclare(TreeNode root) throws CmmException {
		
		Variable variable=new Variable();
		Token currentToken=null;
		for (int i = 0; i < root.getChildCount(); i++) {
			
			TreeNode tempNode=(TreeNode)root.getChildAt(i);
			currentToken=tempNode.getCurr_token();
			if (tempNode.getSt()==StatementName.VARIABLE_STM) {
				//设置变量名
				variable.setName(currentToken.getValue());
				variable.setVt(currentToken.getVariableType());
				variable.setScope(scope);
				variable.setLineNum(currentToken.getLineNum());
				if (tempNode.getChildCount()!=0) {
					//说明是一个数组，设置数组的大小
					Value arr_size=parseExpression((TreeNode) tempNode.getFirstChild());
					if (!arr_size.isDouble()&&arr_size.getValue_int()>=0) {
						variable.setSize(arr_size.getValue_int());
					}else {
						//报错，数组下标不是整数,或者下标是负数
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
					
				}
				
			}else if (tempNode.getSt()==StatementName.NUMBER_STM) {
				//设置变量的值
				if (currentToken.getVariableType()==VariableType.INT) {
					if (CMMUtil.isInteger(currentToken.getValue())) {
						variable.setInt_value(Integer.parseInt(currentToken.getValue()));
					}else {
						throw new CmmException(ErrorDescription.VARIABLE_TYPE_ERROR, "\t LineNumber:"+currentToken.getLineNum());
					}
					
				}else if (currentToken.getVariableType()==VariableType.REAL) {
					variable.setReal_value(Double.parseDouble(currentToken.getValue()));
				}
			}
			
		}
		//判断变量名是否符合规范
		String pattern="^[A-Za-z]\\w*";
		Pattern p=Pattern.compile(pattern);
		Matcher matcher=p.matcher(variable.getName());
		char[] te=variable.getName().toCharArray();
		if (te[te.length-1]=='_') {
			throw new CmmException(ErrorDescription.VARIABLE_NAME_ERROR,"\t LineNumber:"+currentToken.getLineNum());
		}
		System.out.println("name"+variable.getName());
		if (!matcher.find()) {
			throw new CmmException(ErrorDescription.VARIABLE_NAME_ERROR,"\t LineNumber:"+currentToken.getLineNum());
		}
		//判断是重复声明变量名
		if (variableTable.contains(variable)) {
			throw new CmmException(ErrorDescription.VARIABLE_DUPLICATE_DECLARE, "\t LineNumber:"+currentToken.getLineNum());
		}
		
		variableTable.add(variable);
	}
	/**
	 * 解析赋值语句
	 * @param root
	 * @throws CmmException 
	 */
	public void  parseAssign(TreeNode root) throws CmmException {
		
		Variable variable=new Variable();
		//数组下标，可能用到
		Value arr_index=new Value();
		for (int i = 0; i < root.getChildCount(); i++) {
			
			TreeNode tempNode=(TreeNode) root.getChildAt(i);
			Token currentToken=tempNode.getCurr_token();
			if (tempNode.getSt()==StatementName.VARIABLE_STM) {
				//保存变量的名字，变量的类型
				variable.setName(currentToken.getValue());
				variable.setVt(currentToken.getVariableType());
				variable.setScope(scope);
				variable=variableTable.getVariable(variable);
				
				if (tempNode.getChildCount()!=0) {
					//说明是一个数组，获取数组下标
					arr_index=parseExpression((TreeNode) tempNode.getFirstChild());
					if (arr_index.isDouble()||arr_index.getValue_int()<0) {
						//报错，数组下标不是整数,或者下标是负数
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
				}
			}else if (tempNode.getSt()==StatementName.EXPRESSION_STM) {
				//解析expression树
				Value value=parseExpression(tempNode);
				//设置变量的值
				if (variable.getVt()==VariableType.ARR_INT) {
					if (arr_index.getValue_int()>=0) {
						variable.getInt_arr()[arr_index.getValue_int()]=value.getValue_int();
					}else {
						//报错
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
				}else if (variable.getVt()==VariableType.INT) {
					variable.setInt_value(value.getValue_int());
				}else if (variable.getVt()==VariableType.ARR_REAL) {
					if (arr_index.getValue_int()>-1) {
						variable.getReal_arr()[arr_index.getValue_int()]=value.getValue_real();
					}else {
						//报错
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
				}else if (variable.getVt()==VariableType.REAL) {
					variable.setReal_value(value.getValue_real());
				}
				
			}
			
		}
		
		
		
	}
	
	/**
	 * 解析if语句
	 * @param root
	 * @throws CmmException 
	 */
	public void  parseIf(TreeNode root) throws CmmException {
		
		
		//进入if
		scope++;
		//解析condition
		TreeNode conditionNode=(TreeNode) root.getFirstChild();
		boolean condition=parseCondition(conditionNode);
		System.out.println(condition);
		
		
		if (condition) {
			//解析blcok
			TreeNode blockNode=(TreeNode) root.getChildAt(1);
			parseBlock(blockNode);
		}else {
			//解析else
			TreeNode elseNode=null;
			if (root.getChildCount()==3) {
				elseNode=(TreeNode) root.getChildAt(2);
				TreeNode bNode=(TreeNode) elseNode.getFirstChild();
				parseBlock(bNode);
			}
		}
		
		
		//if语句解析完毕
		//退出if之前需要删除在if中声明的所有变量
		for (int i = 0; i < variableTable.size(); i++) {
			if (variableTable.getVariableAtIndexOf(i).getScope()==scope) {
				variableTable.remove(i);
			}
		}
		
		scope--;
		
	}
	
	/**
	 * 解析while语句
	 * @param root
	 * @throws CmmException 
	 */
	public void  parseWhile(TreeNode root) throws CmmException {
		scope++;
		
		TreeNode conditionNode=(TreeNode) root.getFirstChild();
		boolean condition=parseCondition(conditionNode);
		TreeNode blcokNode=(TreeNode) root.getChildAt(1);
		
		while (condition) {
			
			parseBlock(blcokNode);
			condition=parseCondition(conditionNode);
			//退出while之前需要删除在while中声明的所有变量
			for (int i = 0; i < variableTable.size(); i++) {
				Variable temp=variableTable.getVariableAtIndexOf(i);
				if (temp.getScope()==scope) {
					variableTable.remove(i);
					i--;
				}
			}
		}
		
		
		scope--;
	}
	
	/**
	 * 解析write树节点
	 * @param node
	 * @throws CmmException 
	 */
	public void parseWrite(TreeNode root) throws CmmException {
		
		Value value=new Value();
		TreeNode temp=(TreeNode) root.getFirstChild();
		value=parseExpression(temp);

		if (value.isDouble()) {
			System.out.println("write output："+value.getValue_real());
			result=result+value.getValue_real()+"\n";
		}else {
			System.out.println("write output："+value.getValue_int());
			result=result+value.getValue_int()+"\n";
		}
		
		
	}
	
	/**
	 * 解析read树节点
	 * @param node
	 * @return
	 * @throws CmmException 
	 */
	public Value parseRead(TreeNode node) throws CmmException {
		
		
		TreeNode exp=(TreeNode) node.getFirstChild();
		TreeNode term=(TreeNode) exp.getFirstChild();
		TreeNode factor=(TreeNode) term.getFirstChild();
		TreeNode vari=(TreeNode) factor.getFirstChild();
		Token curr=vari.getCurr_token();
		Variable variable=new Variable();
		
		variable.setName(curr.getValue());
		variable.setScope(scope);
		variable=variableTable.getVariable(variable);
		String input="";
//		Scanner scanner=new Scanner(System.in);
		if (vari.getChildCount()>0) {
			//是数组
			Value index=parseExpression((TreeNode) vari.getFirstChild());
			
			if (index.isDouble()||index.getValue_int()<0) {
				//报错
				throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+curr.getLineNum());
			}
			
			
			if (variable.getVt()==VariableType.ARR_INT) {
				input="";
				int value=0;
				input=JOptionPane.showInputDialog("请输入整数");
				if (CMMUtil.isInteger(input)) {
					value=Integer.parseInt(input);
					variable.getInt_arr()[index.getValue_int()]=value;
				}else {
					JOptionPane.showMessageDialog(null, "输入错误");
					//报错
					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
				}
				
				
			}else if (variable.getVt()==VariableType.ARR_REAL) {
				input="";
				double value=0;
				input=JOptionPane.showInputDialog("请输入real类型");
				if (CMMUtil.isDouble(input)) {
					value=Double.parseDouble(input);
					variable.getReal_arr()[index.getValue_int()]=value;
				}else {
					JOptionPane.showMessageDialog(null, "输入错误");
					//报错
					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
				}
				
			}else {
				//报错
				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
			}
		}else {
			//不是数组
			if (variable.getVt()==VariableType.INT) {
				input="";
				int value=0;
				input=JOptionPane.showInputDialog("请输入整数");
				if (CMMUtil.isInteger(input)) {
					value=Integer.parseInt(input);
					variable.setInt_value(value);;
				}else {
					JOptionPane.showMessageDialog(null, "输入错误");
					//报错
					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
				}
				
			}else if (variable.getVt()==VariableType.REAL) {
				input="";
				double value=0;
				input=JOptionPane.showInputDialog("请输入real类型");
				if (CMMUtil.isDouble(input)) {
					value=Double.parseDouble(input);
					variable.setReal_value(value);;
				}else {
					JOptionPane.showMessageDialog(null, "输入错误");
					//报错
					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
				}
			}else {
				//报错
				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
			}
		}
		
		//read语句不需要返回任何东西
		return null;
	}
	
	/**
	 * 解析条件语句
	 * @param root
	 * @throws CmmException 
	 */
	public boolean  parseCondition(TreeNode root) throws CmmException {
		boolean result = false;
		TreeNode exp1=(TreeNode) root.getChildAt(0);
		Value value1=parseExpression((TreeNode) exp1);
		if (root.getChildCount()>2) {
			String conditionOperator=parseConditionOperator((TreeNode) root.getChildAt(1));
			Value value2;
			TreeNode exp2=(TreeNode) root.getChildAt(2);
			value2=parseExpression((TreeNode) exp2);
			
			switch (conditionOperator) {
			case ">":
				result= value1.getOnlyValue()>value2.getOnlyValue();
				break;
			case ">=":
				result= value1.getOnlyValue()>=value2.getOnlyValue();
				break;
			case "<":
				result= value1.getOnlyValue()<value2.getOnlyValue();
				break;
			case "<=":
				result= value1.getOnlyValue()<=value2.getOnlyValue();
				break;
			case "!=":
				result= value1.getOnlyValue()!=value2.getOnlyValue();
				break;
			case "==":
				result= value1.getOnlyValue()==value2.getOnlyValue();
				break;
			default:
				result=false;
				break;
			}
			
		}else {
			if (value1==null) {
				result=false;
			}else {
				result=true;
			}
		}
		
		return result;
	}
	
	/**
	 * 解析block语句
	 * @param root
	 * @throws CmmException 
	 */
	public void  parseBlock(TreeNode root) throws CmmException {
		for (int i = 0; i < root.getChildCount(); i++) {
			TreeNode temp=(TreeNode) root.getChildAt(i);
			if (temp.getSt()==StatementName.DECLARE_STM) {
				parseDeclare(temp);
			}else if (temp.getSt()==StatementName.ASSIGN_STM) {
				parseAssign(temp);
			}else if (temp.getSt()==StatementName.IF_STM) {
				parseIf(temp);
			}else if (temp.getSt()==StatementName.WHILE_STM) {
				parseWhile(temp);
			}else if (temp.getSt()==StatementName.READ_STM) {
				parseRead(temp);
			}else if (temp.getSt()==StatementName.WRITE_STM) {
				parseWrite(temp);
			}
		}
	}
	
	/**
	 * 解析expression语句
	 * @param root
	 * @throws CmmException 
	 */
	public Value  parseExpression(TreeNode root) throws CmmException {
		Value valueExp=new Value();
		Value value1;
		String operator="";
		int i=0;
		TreeNode temp=(TreeNode) root.getChildAt(i);//i=0
		value1=parseTerm(temp);
		int number=root.getChildCount();
		
		if (number>1) {
			number=number-1;
			while (number>0) {
				i=i+2;
				Value value2=parseTerm((TreeNode) root.getChildAt(i));//i=2
				operator=parseLogicOperator((TreeNode) root.getChildAt(i-1));//1
				if (operator.equals("+")) {
					if (value1.isDouble()) {
						valueExp.setDouble(true);
						if (value2.isDouble()) {
							valueExp.setValue_real(value1.getValue_real()+value2.getValue_real());
						}else {
							valueExp.setValue_real(value1.getValue_real()+value2.getValue_int());
						}
					}else {
						if (value2.isDouble()) {
							valueExp.setDouble(true);
							valueExp.setValue_real(value1.getValue_int()+value2.getValue_real());
						}else {
							valueExp.setDouble(false);
							valueExp.setValue_int(value1.getValue_int()+value2.getValue_int());
						}
					}
				} else if (operator.equals("-")) {
					if (value1.isDouble()) {
						valueExp.setDouble(true);
						if (value2.isDouble()) {
							valueExp.setValue_real(value1.getValue_real()-value2.getValue_real());
						}else {
							valueExp.setValue_real(value1.getValue_real()-value2.getValue_int());
						}
					}else {
						if (value2.isDouble()) {
							valueExp.setDouble(true);
							valueExp.setValue_real(value1.getValue_int()-value2.getValue_real());
						}else {
							valueExp.setDouble(false);
							valueExp.setValue_int(value1.getValue_int()-value2.getValue_int());
						}
					}
				}else {
					//报错
					throw new CmmException(ErrorDescription.UNKNOWN_GRAMMER,"\t LineNumber:"+temp.getCurr_token().getLineNum());
				}
				number=number-2;
				value1=valueExp;
			}
			
			
		}else {
			valueExp=value1;
		}
		
		return valueExp;
	}
	
	/**
	 * 解析term语句
	 * @param root
	 * @throws CmmException 
	 */
	public Value  parseTerm(TreeNode root) throws CmmException {
		Value valueTerm=new Value();
		Value value1;
		String operator="";
		int i=0;
		TreeNode temp=(TreeNode) root.getChildAt(i);
		value1=parseFactor(temp);
		
		int number=root.getChildCount();
		if (root.getChildCount()>1) {
			number=number-1;
			while (number>0) {
				i=i+2;
				operator=parseLogicOperator((TreeNode) root.getChildAt(i-1));//1
				Value value2=parseFactor((TreeNode) root.getChildAt(i));//i=2
				if (operator.equals("*")) {
					if (value1.isDouble()) {
						valueTerm.setDouble(true);
						if (value2.isDouble()) {
							valueTerm.setValue_real(value1.getValue_real()*value2.getValue_real());
						}else {
							valueTerm.setValue_real(value1.getValue_real()*value2.getValue_int());
						}
					}else {
						if (value2.isDouble()) {
							valueTerm.setDouble(true);
							valueTerm.setValue_real(value1.getValue_int()*value2.getValue_real());
						}else {
							valueTerm.setDouble(false);
							valueTerm.setValue_int(value1.getValue_int()*value2.getValue_int());
						}
					}
				}else if (operator.equals("/")) {
					if (value1.isDouble()) {
						valueTerm.setDouble(true);
						if (value2.isDouble()) {
							valueTerm.setValue_real(value1.getValue_real()/value2.getValue_real());
						}else {
							valueTerm.setValue_real(value1.getValue_real()/value2.getValue_int());
						}
					}else {
						if (value2.isDouble()) {
							valueTerm.setDouble(true);
							valueTerm.setValue_real(value1.getValue_int()/value2.getValue_real());
						}else {
							valueTerm.setDouble(false);
							valueTerm.setValue_int(value1.getValue_int()/value2.getValue_int());
						}
					}
				}else {
					//报错
					throw new CmmException(ErrorDescription.UNKNOWN_GRAMMER,"\t LineNumber:"+temp.getCurr_token().getLineNum());
				}
				number=number-2;
				value1=valueTerm;
			}
			

		}else {
			valueTerm=value1;
		}
		return valueTerm;
	}
	
	
	/**
	 * 解析factor语句
	 * @param root
	 * @throws CmmException 
	 */
	public Value  parseFactor(TreeNode root) throws CmmException {
		Value valueFactor=new Value();
		if (root.getChildCount()==0) {
			valueFactor.setValue_int(0);
			valueFactor.setDouble(false);
			valueFactor.setValue_real(0);
			return valueFactor;
		}
		TreeNode temp=(TreeNode) root.getFirstChild();
		if (temp.getSt()==StatementName.NUMBER_STM) {
			valueFactor=parseNumber(temp);
		}else if (temp.getSt()==StatementName.VARIABLE_STM) {
			valueFactor=parseVariable(temp);
		}else {
			//报错
			throw new CmmException(ErrorDescription.UNKNOWN_GRAMMER,"\t LineNumber:"+temp.getCurr_token().getLineNum());
		}
		return valueFactor;
	}
	
	/**
	 * 解析变量树节点
	 * @param node
	 * @return
	 * @throws CmmException 
	 */
	public Value parseVariable(TreeNode node) throws CmmException {
		Value valueObj=new Value();
		Token curr=node.getCurr_token();
		double value = 0;
		Variable variable=new Variable();
		variable.setName(curr.getValue());
		variable.setVt(curr.getVariableType());
		//层次设置
		variable.setScope(scope);
		
		if (node.getChildCount()>0) {
			//是数组
			Value index=parseExpression((TreeNode) node.getFirstChild());
			if (index.isDouble()||index.getValue_int()<0) {
				//报错
				throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+curr.getLineNum());
			}
			
			variable=variableTable.getVariable(variable);
			if (variable==null) {
				throw new CmmException(ErrorDescription.VARIABLE_NOT_DECLARE, "\t LineNumber:"+curr.getLineNum());
			}
			if (variable.getVt()==VariableType.ARR_INT) {
				valueObj.setDouble(false);
				valueObj.setValue_int(variable.getInt_arr()[index.getValue_int()]);
			} else if (variable.getVt()==VariableType.ARR_REAL) {
				valueObj.setDouble(true);
				value=variable.getReal_arr()[index.getValue_int()];
				valueObj.setValue_real(value);
			}else {
				//报错
				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
			}
			
		}else {
			//不是数组
			variable=variableTable.getVariable(variable);
			if (variable==null) {
				throw new CmmException(ErrorDescription.VARIABLE_NOT_DECLARE, "\t LineNumber:"+curr.getLineNum());
			}
			if (variable.getVt()==VariableType.INT) {
				valueObj.setDouble(false);
				int value_int=variableTable.getVariable(variable).getInt_value();
				valueObj.setValue_int(value_int);
			}else if (variable.getVt()==VariableType.REAL) {
				valueObj.setDouble(true);
				value=variableTable.getVariable(variable).getReal_value();
				valueObj.setValue_real(value);
			}else {
				//报错
				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
			}
		}
		
		return valueObj;
	}
	
	/**
	 * 解析数字树节点
	 * @param node
	 * @return
	 */
	public Value parseNumber(TreeNode node) {
		Value valueObj=new Value();
		boolean isDouble=isDouble(node.getCurr_token().getValue());
		
		if (isDouble) {
			double value=Double.parseDouble(node.getCurr_token().getValue());
			valueObj.setDouble(true);
			valueObj.setValue_real(value);
		} else {
			int value=Integer.parseInt(node.getCurr_token().getValue());
			valueObj.setDouble(false);
			valueObj.setValue_int(value);
		}
		
		return valueObj;
	}
	
	private boolean isDouble(String value) {
		boolean isDouble = false;
		Integer intValue=null;
		try {
			intValue=new Integer(value);
			isDouble=false;
		} catch (Exception e) {
			// TODO: handle exception
			isDouble=true;
		}
		
		return isDouble;
	}
	
	/**
	 * 解析算术运算操作符
	 * @param node
	 * @return
	 */
	public String parseLogicOperator(TreeNode node) {
		
		return node.getCurr_token().getValue();
	}
	
	/**
	 * 解析条件运算操作符
	 * @param node
	 * @return
	 */
	public String parseConditionOperator(TreeNode node) {
		
		return node.getCurr_token().getValue();
	}
	
}
