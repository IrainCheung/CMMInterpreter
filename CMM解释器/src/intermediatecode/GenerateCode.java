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
	 * �洢��Ԫʽ
	 */
	public HashMap<Integer, Quaternary> hashMap=new HashMap<>();
	/**
	 * �洢��Ԫʽ����
	 */
	private int line=0;
	
	private static String tempVari="*temp";
	private static int variNumber=0;
	
	private String generateText="";
	/**
	 * ������
	 */
	private VariableTable variableTable=new VariableTable();
	/**
	 * �����Ĳ��
	 */
	private static int scope=0;
	/**
	 * ����������
	 */
	private static String result="";
	
	
	/**
	 * �����﷨��
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
	 * �����������ڵ�
	 * @throws CmmException 
	 */
	public void parseDeclare(TreeNode root) throws CmmException {
		//�����Ԫʽ
		Quaternary quaternary=new Quaternary();
		
		Variable variable=new Variable();
		for (int i = 0; i < root.getChildCount(); i++) {
			
			TreeNode tempNode=(TreeNode)root.getChildAt(i);
			Token currentToken=tempNode.getCurr_token();
			if (tempNode.getSt()==StatementName.VARIABLE_STM) {
				//���ñ�����
				variable.setName(currentToken.getValue());
				variable.setVt(currentToken.getVariableType());
				variable.setScope(scope);
				variable.setLineNum(currentToken.getLineNum());
				//-----------------------------------------------------------------------------
				//������Ԫʽ����,����
				quaternary.setFour(variable.getName());
				if (variable.getVt()==VariableType.ARR_INT||variable.getVt()==VariableType.INT) {
					quaternary.setCode(Code.INT);
				}else {
					quaternary.setCode(Code.REAL);
				}
				//---------------------------------------------------------
				if (tempNode.getChildCount()!=0) {
					//˵����һ�����飬��������Ĵ�С
					Value arr_size=parseExpression((TreeNode) tempNode.getFirstChild());
					if (!arr_size.isDouble()&&arr_size.getValue_int()>=0) {
						variable.setSize(arr_size.getValue_int());
						//������Ԫʽ�����С---------------------------------------------------
						quaternary.setSecond(Integer.toString(arr_size.getValue_int()));
					}else {
						//���������±겻������,�����±��Ǹ���
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
					
				}
				
			}else if (tempNode.getSt()==StatementName.NUMBER_STM) {
				//���ñ�����ֵ
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
		//�����Ԫʽ����������
		hashMap.put(line, quaternary);
		line++;
		
		variableTable.add(variable);
	}
	/**
	 * ������ֵ���
	 * @param root
	 * @throws CmmException 
	 */
	public void  parseAssign(TreeNode root) throws CmmException {
		//��Ԫʽ
		Quaternary quaternary=new Quaternary();
		
		Variable variable=new Variable();
		//�����±꣬�����õ�
		Value arr_index=new Value();
		for (int i = 0; i < root.getChildCount(); i++) {
			
			TreeNode tempNode=(TreeNode) root.getChildAt(i);
			Token currentToken=tempNode.getCurr_token();
			if (tempNode.getSt()==StatementName.VARIABLE_STM) {
				//������������֣�����������
				variable.setName(currentToken.getValue());
				variable.setVt(currentToken.getVariableType());
				variable.setScope(scope);
				variable=variableTable.getVariable(variable);
				
				//-----------------------------------------------------------------------------
				//������Ԫʽ��������
				quaternary.setFour(variable.getName());
				quaternary.setCode(Code.ASSIGN);
				//---------------------------------------------------------
				
				if (tempNode.getChildCount()!=0) {
					//˵����һ�����飬��ȡ�����±�
					arr_index=parseExpression((TreeNode) tempNode.getFirstChild());
					if (arr_index.isDouble()||arr_index.getValue_int()<0) {
						//���������±겻������,�����±��Ǹ���
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
					//������Ԫʽ�����±�
					quaternary.setSecond(Integer.toString(arr_index.getValue_int()));
				}
			}else if (tempNode.getSt()==StatementName.EXPRESSION_STM) {
				//����expression��
				Value value=parseExpression(tempNode);
				//���ñ�����ֵ
				if (variable.getVt()==VariableType.ARR_INT) {
					if (arr_index.getValue_int()>=0) {
						variable.getInt_arr()[arr_index.getValue_int()]=value.getValue_int();
						//������Ԫʽ������
						quaternary.setThird(Integer.toString(value.getValue_int()));
					}else {
						//����
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
				}else if (variable.getVt()==VariableType.INT) {
					variable.setInt_value(value.getValue_int());
					//������Ԫʽ������
					quaternary.setThird(Integer.toString(value.getValue_int()));
				}else if (variable.getVt()==VariableType.ARR_REAL) {
					if (arr_index.getValue_int()>-1) {
						variable.getReal_arr()[arr_index.getValue_int()]=value.getValue_real();
						//������Ԫʽ������
						quaternary.setThird(Double.toString(value.getValue_real()));
					}else {
						//����
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
				}else if (variable.getVt()==VariableType.REAL) {
					variable.setReal_value(value.getValue_real());
					//������Ԫʽ������
					quaternary.setThird(Double.toString(value.getValue_real()));
				}
				
			}
			
		}
		
		//�����Ԫʽ����������
		hashMap.put(line, quaternary);
		line++;
		
		
	}
	
	/**
	 * ����if���
	 * @param root
	 * @throws CmmException 
	 */
	public void  parseIf(TreeNode root) throws CmmException {
		
		
		Quaternary jump=new Quaternary();
		int temp=line;
		line++;
		jump.setCode(Code.JUMP);
		//����if
		scope++;
		//����condition
		TreeNode conditionNode=(TreeNode) root.getFirstChild();
		boolean condition=parseCondition(conditionNode);
		
		jump.setSecond(condition+"");
		
		//����blcok
		TreeNode blockNode=(TreeNode) root.getChildAt(1);
		//����block
		hashMap.put(line, new Quaternary(Code.IN, null, null, null));
		line++;
		parseBlock(blockNode);
		//��block
		hashMap.put(line, new Quaternary(Code.OUT, null, null, null));
		line++;
		jump.setFour(""+line);
		hashMap.put(temp, jump);
		
		Quaternary elsejump=new Quaternary();
		int elseTemp=line;
		elsejump.setCode(Code.JUMP);
		//����else
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
		//if���������
		//�˳�if֮ǰ��Ҫɾ����if�����������б���
		for (int i = 0; i < variableTable.size(); i++) {
			if (variableTable.getVariableAtIndexOf(i).getScope()==scope) {
				variableTable.remove(i);
			}
		}
		
		scope--;
		
	}
	
	/**
	 * ����while���
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
		//�˳�while֮ǰ��Ҫɾ����while�����������б���
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
	 * ����write���ڵ�
	 * @param node
	 * @throws CmmException 
	 */
	public void parseWrite(TreeNode root) throws CmmException {
		//������Ԫʽ
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
		
		//�����Ԫʽ����������
		hashMap.put(line, quaternary);
		line++;
		
	}
	
	/**
	 * ����read���ڵ�
	 * @param node
	 * @return
	 * @throws CmmException 
	 */
	public Value parseRead(TreeNode node) throws CmmException {
		//��Ԫʽ
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
		//������Ԫʽ
		quaternary.setCode(Code.READ);
		quaternary.setFour(variable.getName());
		
//		String input="";
//		if (vari.getChildCount()>0) {
//			//������
//			Value index=parseExpression((TreeNode) vari.getFirstChild());
//			
//			if (index.isDouble()||index.getValue_int()<0) {
//				//����
//				throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+curr.getLineNum());
//			}
//			
//			quaternary.setSecond(Integer.toString(index.getValue_int()));
//			
//			if (variable.getVt()==VariableType.ARR_INT) {
//				input="";
//				int value=0;
//				input=JOptionPane.showInputDialog("����������");
//				if (CMMUtil.isInteger(input)) {
//					value=Integer.parseInt(input);
//					variable.getInt_arr()[index.getValue_int()]=value;
//				}else {
//					JOptionPane.showMessageDialog(null, "�������");
//					//����
//					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
//				}
//				
//				
//			}else if (variable.getVt()==VariableType.ARR_REAL) {
//				input="";
//				double value=0;
//				input=JOptionPane.showInputDialog("������real����");
//				if (CMMUtil.isDouble(input)) {
//					value=Double.parseDouble(input);
//					variable.getReal_arr()[index.getValue_int()]=value;
//				}else {
//					JOptionPane.showMessageDialog(null, "�������");
//					//����
//					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
//				}
//				
//			}else {
//				//����
//				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
//			}
//		}else {
//			//��������
//			if (variable.getVt()==VariableType.INT) {
//				input="";
//				int value=0;
//				input=JOptionPane.showInputDialog("����������");
//				if (CMMUtil.isInteger(input)) {
//					value=Integer.parseInt(input);
//					variable.setInt_value(value);;
//				}else {
//					JOptionPane.showMessageDialog(null, "�������");
//					//����
//					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
//				}
//				
//			}else if (variable.getVt()==VariableType.REAL) {
//				input="";
//				double value=0;
//				input=JOptionPane.showInputDialog("������real����");
//				if (CMMUtil.isDouble(input)) {
//					value=Double.parseDouble(input);
//					variable.setReal_value(value);;
//				}else {
//					JOptionPane.showMessageDialog(null, "�������");
//					//����
//					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
//				}
//			}else {
//				//����
//				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
//			}
//		}
		
		//�����Ԫʽ����������
		hashMap.put(line, quaternary);
		line++;
		
		//read��䲻��Ҫ�����κζ���
		return null;
	}
	
	/**
	 * �����������
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
	 * ����block���
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
	 * ����expression���
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
					//����
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
	 * ����term���
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
					//����
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
	 * ����factor���
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
			//����
			throw new CmmException(ErrorDescription.UNKNOWN_GRAMMER,"\t LineNumber:"+temp.getCurr_token().getLineNum());
		}
		return valueFactor;
	}
	
	/**
	 * �����������ڵ�
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
		//�������
		variable.setScope(scope);
		
		if (node.getChildCount()>0) {
			//������
			Value index=parseExpression((TreeNode) node.getFirstChild());
			if (index.isDouble()||index.getValue_int()<0) {
				//����
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
				//����
				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
			}
			
		}else {
			//��������
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
				//����
				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
			}
		}
		
		return valueObj;
	}
	
	/**
	 * �����������ڵ�
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
	 * �����������������
	 * @param node
	 * @return
	 */
	public String parseLogicOperator(TreeNode node) {
		
		return node.getCurr_token().getValue();
	}
	
	/**
	 * �����������������
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
