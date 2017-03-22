package intermediatecode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import error.CmmException;
import error.Error.ErrorDescription;
import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import parser.StatementName;
import parser.TreeNode;
import parser.Variable;
import parser.Variable.VariableType;
import parser.VariableTable;
import semantic.Value;

/**
 * 
 * @author Qideas
 *
 */
public class GenerateCode {

	/**
	 * 存储四元式
	 */
	public HashMap<Integer, Quaternary> hashMap=new HashMap<>();
	/**
	 * 存储四元式行数
	 */
	private int line=0;
	
	private static String tempVari="*temp";
	private static int variNumber=0;
	
	private String generateText="";
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
		//添加四元式
		Quaternary quaternary=new Quaternary();
		
		Variable variable=new Variable();
		for (int i = 0; i < root.getChildCount(); i++) {
			
			TreeNode tempNode=(TreeNode)root.getChildAt(i);
			Token currentToken=tempNode.getCurr_token();
			if (tempNode.getSt()==StatementName.VARIABLE_STM) {
				//设置变量名
				variable.setName(currentToken.getValue());
				variable.setVt(currentToken.getVariableType());
				variable.setScope(scope);
				variable.setLineNum(currentToken.getLineNum());
				//-----------------------------------------------------------------------------
				//设置四元式名字,类型
				quaternary.setFour(variable.getName());
				if (variable.getVt()==VariableType.ARR_INT||variable.getVt()==VariableType.INT) {
					quaternary.setCode(Code.INT);
				}else {
					quaternary.setCode(Code.REAL);
				}
				//---------------------------------------------------------
				if (tempNode.getChildCount()!=0) {
					//说明是一个数组，设置数组的大小
					Value arr_size=parseExpression((TreeNode) tempNode.getFirstChild());
					if (!arr_size.isDouble()&&arr_size.getValue_int()>=0) {
						variable.setSize(arr_size.getValue_int());
						//设置四元式数组大小---------------------------------------------------
						quaternary.setSecond(Integer.toString(arr_size.getValue_int()));
					}else {
						//报错，数组下标不是整数,或者下标是负数
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
					
				}
				
			}else if (tempNode.getSt()==StatementName.NUMBER_STM) {
				//设置变量的值
				if (currentToken.getVariableType()==VariableType.INT) {
					variable.setInt_value(Integer.parseInt(currentToken.getValue()));
					//---------------------------------------------------
					quaternary.setThird(currentToken.getValue());
				}else if (currentToken.getVariableType()==VariableType.REAL) {
					variable.setReal_value(Double.parseDouble(currentToken.getValue()));
					//-------------------------------------------------------
					quaternary.setThird(currentToken.getValue());
				}
			}
			
		}
		//添加四元式，行数自增
		hashMap.put(line, quaternary);
		line++;
		
		variableTable.add(variable);
	}
	/**
	 * 解析赋值语句
	 * @param root
	 * @throws CmmException 
	 */
	public void  parseAssign(TreeNode root) throws CmmException {
		//四元式
		Quaternary quaternary=new Quaternary();
		
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
				
				//-----------------------------------------------------------------------------
				//设置四元式变量名字
				quaternary.setFour(variable.getName());
				quaternary.setCode(Code.ASSIGN);
				//---------------------------------------------------------
				
				if (tempNode.getChildCount()!=0) {
					//说明是一个数组，获取数组下标
					arr_index=parseExpression((TreeNode) tempNode.getFirstChild());
					if (arr_index.isDouble()||arr_index.getValue_int()<0) {
						//报错，数组下标不是整数,或者下标是负数
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
					//设置四元式数组下标
					quaternary.setSecond(Integer.toString(arr_index.getValue_int()));
				}
			}else if (tempNode.getSt()==StatementName.EXPRESSION_STM) {
				//解析expression树
				Value value=parseExpression(tempNode);
				//设置变量的值
				if (variable.getVt()==VariableType.ARR_INT) {
					if (arr_index.getValue_int()>=0) {
						variable.getInt_arr()[arr_index.getValue_int()]=value.getValue_int();
						//设置四元式第三个
						quaternary.setThird(Integer.toString(value.getValue_int()));
					}else {
						//报错
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
				}else if (variable.getVt()==VariableType.INT) {
					variable.setInt_value(value.getValue_int());
					//设置四元式第三个
					quaternary.setThird(Integer.toString(value.getValue_int()));
				}else if (variable.getVt()==VariableType.ARR_REAL) {
					if (arr_index.getValue_int()>-1) {
						variable.getReal_arr()[arr_index.getValue_int()]=value.getValue_real();
						//设置四元式第三个
						quaternary.setThird(Double.toString(value.getValue_real()));
					}else {
						//报错
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
				}else if (variable.getVt()==VariableType.REAL) {
					variable.setReal_value(value.getValue_real());
					//设置四元式第三个
					quaternary.setThird(Double.toString(value.getValue_real()));
				}
				
			}
			
		}
		
		//添加四元式，行数自增
		hashMap.put(line, quaternary);
		line++;
		
		
	}
	
	/**
	 * 解析if语句
	 * @param root
	 * @throws CmmException 
	 */
	public void  parseIf(TreeNode root) throws CmmException {
		
		
		Quaternary jump=new Quaternary();
		int temp=line;
		line++;
		jump.setCode(Code.JUMP);
		//进入if
		scope++;
		//解析condition
		TreeNode conditionNode=(TreeNode) root.getFirstChild();
		boolean condition=parseCondition(conditionNode);
		
		jump.setSecond(condition+"");
		
		//解析blcok
		TreeNode blockNode=(TreeNode) root.getChildAt(1);
		//进入block
		hashMap.put(line, new Quaternary(Code.IN, null, null, null));
		line++;
		parseBlock(blockNode);
		//出block
		hashMap.put(line, new Quaternary(Code.OUT, null, null, null));
		line++;
		jump.setFour(""+line);
		hashMap.put(temp, jump);
		
		Quaternary elsejump=new Quaternary();
		int elseTemp=line;
		elsejump.setCode(Code.JUMP);
		//解析else
		TreeNode elseNode=null;
		if (root.getChildCount()==3) {
			elseNode=(TreeNode) root.getChildAt(2);
			TreeNode bNode=(TreeNode) elseNode.getFirstChild();
			hashMap.put(line, new Quaternary(Code.IN,null,null,null));
			line++;
			parseBlock(bNode);
			hashMap.put(line, new Quaternary(Code.OUT,null,null,null));
			line++;
		}	
		
		elsejump.setFour(line+"");
		hashMap.put(elseTemp, elsejump);
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
		
		Quaternary jump=new Quaternary();
		int templine=line;
		line++;
		jump.setCode(Code.JUMP);
		
		TreeNode conditionNode=(TreeNode) root.getFirstChild();
		boolean condition=parseCondition(conditionNode);
		TreeNode blcokNode=(TreeNode) root.getChildAt(1);
		
		jump.setSecond(""+condition);
		
		hashMap.put(line, new Quaternary(Code.IN, null, null, null));
		line++;
		parseBlock(blcokNode);
		hashMap.put(line, new Quaternary(Code.OUT, null, null, null));
		line++;
		condition=parseCondition(conditionNode);	
		Quaternary conjump=new Quaternary();
		conjump.setCode(Code.JUMP);
		conjump.setFour(""+templine);
		
		hashMap.put(line, conjump);
		line++;
		jump.setFour(""+line);
		hashMap.put(templine, jump);
		//退出while之前需要删除在while中声明的所有变量
		for (int i = 0; i < variableTable.size(); i++) {
			Variable temp=variableTable.getVariableAtIndexOf(i);
			if (temp.getScope()==scope) {
				variableTable.remove(i);
				i--;
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
		//定义四元式
		Quaternary quaternary=new Quaternary();
		quaternary.setCode(Code.WRITE);
		
		Value value=new Value();
		TreeNode temp=(TreeNode) root.getFirstChild();
		value=parseExpression(temp);

		if (value.isDouble()) {
			result=result+value.getValue_real()+"\n";
			quaternary.setFour(Double.toString(value.getValue_real()));
		}else {
			result=result+value.getValue_int()+"\n";
			quaternary.setFour(Integer.toString(value.getValue_int()));
		}
		
		//添加四元式，行数自增
		hashMap.put(line, quaternary);
		line++;
		
	}
	
	/**
	 * 解析read树节点
	 * @param node
	 * @return
	 * @throws CmmException 
	 */
	public Value parseRead(TreeNode node) throws CmmException {
		//四元式
		Quaternary quaternary=new Quaternary();
		
		
		TreeNode exp=(TreeNode) node.getFirstChild();
		TreeNode term=(TreeNode) exp.getFirstChild();
		TreeNode factor=(TreeNode) term.getFirstChild();
		TreeNode vari=(TreeNode) factor.getFirstChild();
		Token curr=vari.getCurr_token();
		Variable variable=new Variable();
		
		variable.setName(curr.getValue());
		variable.setScope(scope);
		variable=variableTable.getVariable(variable);
		//设置四元式
		quaternary.setCode(Code.READ);
		quaternary.setFour(variable.getName());
		
//		String input="";
//		if (vari.getChildCount()>0) {
//			//是数组
//			Value index=parseExpression((TreeNode) vari.getFirstChild());
//			
//			if (index.isDouble()||index.getValue_int()<0) {
//				//报错
//				throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+curr.getLineNum());
//			}
//			
//			quaternary.setSecond(Integer.toString(index.getValue_int()));
//			
//			if (variable.getVt()==VariableType.ARR_INT) {
//				input="";
//				int value=0;
//				input=JOptionPane.showInputDialog("请输入整数");
//				if (CMMUtil.isInteger(input)) {
//					value=Integer.parseInt(input);
//					variable.getInt_arr()[index.getValue_int()]=value;
//				}else {
//					JOptionPane.showMessageDialog(null, "输入错误");
//					//报错
//					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
//				}
//				
//				
//			}else if (variable.getVt()==VariableType.ARR_REAL) {
//				input="";
//				double value=0;
//				input=JOptionPane.showInputDialog("请输入real类型");
//				if (CMMUtil.isDouble(input)) {
//					value=Double.parseDouble(input);
//					variable.getReal_arr()[index.getValue_int()]=value;
//				}else {
//					JOptionPane.showMessageDialog(null, "输入错误");
//					//报错
//					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
//				}
//				
//			}else {
//				//报错
//				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
//			}
//		}else {
//			//不是数组
//			if (variable.getVt()==VariableType.INT) {
//				input="";
//				int value=0;
//				input=JOptionPane.showInputDialog("请输入整数");
//				if (CMMUtil.isInteger(input)) {
//					value=Integer.parseInt(input);
//					variable.setInt_value(value);;
//				}else {
//					JOptionPane.showMessageDialog(null, "输入错误");
//					//报错
//					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
//				}
//				
//			}else if (variable.getVt()==VariableType.REAL) {
//				input="";
//				double value=0;
//				input=JOptionPane.showInputDialog("请输入real类型");
//				if (CMMUtil.isDouble(input)) {
//					value=Double.parseDouble(input);
//					variable.setReal_value(value);;
//				}else {
//					JOptionPane.showMessageDialog(null, "输入错误");
//					//报错
//					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
//				}
//			}else {
//				//报错
//				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
//			}
//		}
		
		//添加四元式，行数自增
		hashMap.put(line, quaternary);
		line++;
		
		//read语句不需要返回任何东西
		return null;
	}
	
	/**
	 * 解析条件语句
	 * @param root
	 * @throws CmmException 
	 */
	public boolean  parseCondition(TreeNode root) throws CmmException {
		Quaternary con=new Quaternary();
		
		boolean result = false;
		TreeNode exp1=(TreeNode) root.getChildAt(0);
		Value value1=parseExpression((TreeNode) exp1);
		con.setSecond(value1.getOnlyValue()+"");
		if (root.getChildCount()>2) {
			String conditionOperator=parseConditionOperator((TreeNode) root.getChildAt(1));
			Value value2;
			TreeNode exp2=(TreeNode) root.getChildAt(2);
			value2=parseExpression((TreeNode) exp2);
			con.setThird(value2.getOnlyValue()+"");
			switch (conditionOperator) {
			case ">":
				con.setCode(Code.MO);
				result= value1.getOnlyValue()>value2.getOnlyValue();
				break;
			case ">=":
				con.setCode(Code.MOEQ);
				result= value1.getOnlyValue()>=value2.getOnlyValue();
				break;
			case "<":
				con.setCode(Code.LS);
				result= value1.getOnlyValue()<value2.getOnlyValue();
				break;
			case "<=":
				con.setCode(Code.LSEQ);
				result= value1.getOnlyValue()<=value2.getOnlyValue();
				break;
			case "!=":
				con.setCode(Code.NEQ);
				result= value1.getOnlyValue()!=value2.getOnlyValue();
				break;
			case "==":
				con.setCode(Code.EQ);
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
		con.setFour(tempVari+variNumber);
		variNumber++;
		hashMap.put(line, con);
		line++;
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
				Quaternary quaternary=new Quaternary();
				i=i+2;
				Value value2=parseTerm((TreeNode) root.getChildAt(i));//i=2
				operator=parseLogicOperator((TreeNode) root.getChildAt(i-1));//1
				if (operator.equals("+")) {
					quaternary.setCode(Code.PLUS);
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
					quaternary.setCode(Code.MINUS);
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
				
				quaternary.setSecond(value1.getOnlyValue()+"");
				quaternary.setThird(value2.getOnlyValue()+"");
				quaternary.setFour(tempVari+variNumber);
				variNumber++;
				hashMap.put(line, quaternary);
				line++;
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
				Quaternary quaternary=new Quaternary();
				operator=parseLogicOperator((TreeNode) root.getChildAt(i-1));//1
				Value value2=parseFactor((TreeNode) root.getChildAt(i));//i=2
				if (operator.equals("*")) {
					quaternary.setCode(Code.MULTI);
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
					quaternary.setCode(Code.DIV);
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
				quaternary.setSecond(value1.getOnlyValue()+"");
				quaternary.setThird(value2.getOnlyValue()+"");
				quaternary.setFour(tempVari+variNumber);
				variNumber++;
				hashMap.put(line, quaternary);
				line++;
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
	
	public String getGenerateText() {
		for (int i = 0; i < hashMap.size(); i++) {
			System.out.println(i+":"+hashMap.get(i));
			generateText=generateText+i+":"+hashMap.get(i)+"\n";
			
		}
		return generateText;
	}
}
