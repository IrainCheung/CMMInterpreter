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
 * ���������
 * @author Qideas
 *
 */
public class SemanticParser {

	/**
	 * ˼·��
	 * �����﷨���Ľڵ��ӡֵ���߶���ֵ����ɸ�ֵ�ͱ�����ʼ������������ֻ���עread��write��declare��assign���Ϳ�����
	 * ��table�м��롢��ȡ���ı������ֵ
	 * 
	 * Ȼ����ǿ��������жϵ�if��while
	 * Ŀǰʣ�µ�������ǣ�
	 * 1.��������Ĳ�����⣬�ѽ��
	 * 2.�������ֵ����ȷ�����������ֻ���double����
	 * ���˼·��ͨ��Value������װ���صĽ��������Value�Ķ���
	 * �ڽ���expression��ʱ����㷵�ز��������Value����
	 * �����read����write������ͨ���ж�Value�����еı�־�����������������ݡ�
	 * �ѽ��
	 * 
	 * 3.�м��������
	 * 		��ɣ�declare��assign��read��write,if��while��expression��term��condition���д����ƣ�
	 * 4.����ʽ����		���
	 * 
	 */
	
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
	
	public static void main(String[] args) throws IOException, CmmException {
		Lexer lexer=new Lexer();
		
		File file=new File("while.cmm");
		BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
		//�ʷ�����
		ArrayList<Token> list=lexer.checkType(bufferedReader);
		System.out.println("�ʷ����������");
		System.out.println("length:"+list.size());
		lexer.printTokenList(list);
//		String errorMessage=CmmError.checkError(list);
//		System.out.println("����������\n"+errorMessage);
		//�﷨����
		Parser parser=new Parser(list);
		TreeNode root=parser.begainParse();
		System.out.println("�﷨����");
		parser.printTree(root);
		parser.printGrammerError();
		
		//�������
		SemanticParser semanticParser=new SemanticParser();
		semanticParser.parseTree(root);
		semanticParser.variableTable.print();
		
		
	}
	
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
		
		Variable variable=new Variable();
		Token currentToken=null;
		for (int i = 0; i < root.getChildCount(); i++) {
			
			TreeNode tempNode=(TreeNode)root.getChildAt(i);
			currentToken=tempNode.getCurr_token();
			if (tempNode.getSt()==StatementName.VARIABLE_STM) {
				//���ñ�����
				variable.setName(currentToken.getValue());
				variable.setVt(currentToken.getVariableType());
				variable.setScope(scope);
				variable.setLineNum(currentToken.getLineNum());
				if (tempNode.getChildCount()!=0) {
					//˵����һ�����飬��������Ĵ�С
					Value arr_size=parseExpression((TreeNode) tempNode.getFirstChild());
					if (!arr_size.isDouble()&&arr_size.getValue_int()>=0) {
						variable.setSize(arr_size.getValue_int());
					}else {
						//���������±겻������,�����±��Ǹ���
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
					
				}
				
			}else if (tempNode.getSt()==StatementName.NUMBER_STM) {
				//���ñ�����ֵ
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
		//�жϱ������Ƿ���Ϲ淶
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
		//�ж����ظ�����������
		if (variableTable.contains(variable)) {
			throw new CmmException(ErrorDescription.VARIABLE_DUPLICATE_DECLARE, "\t LineNumber:"+currentToken.getLineNum());
		}
		
		variableTable.add(variable);
	}
	/**
	 * ������ֵ���
	 * @param root
	 * @throws CmmException 
	 */
	public void  parseAssign(TreeNode root) throws CmmException {
		
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
				
				if (tempNode.getChildCount()!=0) {
					//˵����һ�����飬��ȡ�����±�
					arr_index=parseExpression((TreeNode) tempNode.getFirstChild());
					if (arr_index.isDouble()||arr_index.getValue_int()<0) {
						//���������±겻������,�����±��Ǹ���
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
				}
			}else if (tempNode.getSt()==StatementName.EXPRESSION_STM) {
				//����expression��
				Value value=parseExpression(tempNode);
				//���ñ�����ֵ
				if (variable.getVt()==VariableType.ARR_INT) {
					if (arr_index.getValue_int()>=0) {
						variable.getInt_arr()[arr_index.getValue_int()]=value.getValue_int();
					}else {
						//����
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
					
				}else if (variable.getVt()==VariableType.INT) {
					variable.setInt_value(value.getValue_int());
				}else if (variable.getVt()==VariableType.ARR_REAL) {
					if (arr_index.getValue_int()>-1) {
						variable.getReal_arr()[arr_index.getValue_int()]=value.getValue_real();
					}else {
						//����
						throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+currentToken.getLineNum());
					}
				}else if (variable.getVt()==VariableType.REAL) {
					variable.setReal_value(value.getValue_real());
				}
				
			}
			
		}
		
		
		
	}
	
	/**
	 * ����if���
	 * @param root
	 * @throws CmmException 
	 */
	public void  parseIf(TreeNode root) throws CmmException {
		
		
		//����if
		scope++;
		//����condition
		TreeNode conditionNode=(TreeNode) root.getFirstChild();
		boolean condition=parseCondition(conditionNode);
		System.out.println(condition);
		
		
		if (condition) {
			//����blcok
			TreeNode blockNode=(TreeNode) root.getChildAt(1);
			parseBlock(blockNode);
		}else {
			//����else
			TreeNode elseNode=null;
			if (root.getChildCount()==3) {
				elseNode=(TreeNode) root.getChildAt(2);
				TreeNode bNode=(TreeNode) elseNode.getFirstChild();
				parseBlock(bNode);
			}
		}
		
		
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
		
		TreeNode conditionNode=(TreeNode) root.getFirstChild();
		boolean condition=parseCondition(conditionNode);
		TreeNode blcokNode=(TreeNode) root.getChildAt(1);
		
		while (condition) {
			
			parseBlock(blcokNode);
			condition=parseCondition(conditionNode);
			//�˳�while֮ǰ��Ҫɾ����while�����������б���
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
	 * ����write���ڵ�
	 * @param node
	 * @throws CmmException 
	 */
	public void parseWrite(TreeNode root) throws CmmException {
		
		Value value=new Value();
		TreeNode temp=(TreeNode) root.getFirstChild();
		value=parseExpression(temp);

		if (value.isDouble()) {
			System.out.println("write output��"+value.getValue_real());
			result=result+value.getValue_real()+"\n";
		}else {
			System.out.println("write output��"+value.getValue_int());
			result=result+value.getValue_int()+"\n";
		}
		
		
	}
	
	/**
	 * ����read���ڵ�
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
			//������
			Value index=parseExpression((TreeNode) vari.getFirstChild());
			
			if (index.isDouble()||index.getValue_int()<0) {
				//����
				throw new CmmException(ErrorDescription.ARRAY_INDEX_ERROR,"\t LineNumber:"+curr.getLineNum());
			}
			
			
			if (variable.getVt()==VariableType.ARR_INT) {
				input="";
				int value=0;
				input=JOptionPane.showInputDialog("����������");
				if (CMMUtil.isInteger(input)) {
					value=Integer.parseInt(input);
					variable.getInt_arr()[index.getValue_int()]=value;
				}else {
					JOptionPane.showMessageDialog(null, "�������");
					//����
					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
				}
				
				
			}else if (variable.getVt()==VariableType.ARR_REAL) {
				input="";
				double value=0;
				input=JOptionPane.showInputDialog("������real����");
				if (CMMUtil.isDouble(input)) {
					value=Double.parseDouble(input);
					variable.getReal_arr()[index.getValue_int()]=value;
				}else {
					JOptionPane.showMessageDialog(null, "�������");
					//����
					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
				}
				
			}else {
				//����
				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
			}
		}else {
			//��������
			if (variable.getVt()==VariableType.INT) {
				input="";
				int value=0;
				input=JOptionPane.showInputDialog("����������");
				if (CMMUtil.isInteger(input)) {
					value=Integer.parseInt(input);
					variable.setInt_value(value);;
				}else {
					JOptionPane.showMessageDialog(null, "�������");
					//����
					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
				}
				
			}else if (variable.getVt()==VariableType.REAL) {
				input="";
				double value=0;
				input=JOptionPane.showInputDialog("������real����");
				if (CMMUtil.isDouble(input)) {
					value=Double.parseDouble(input);
					variable.setReal_value(value);;
				}else {
					JOptionPane.showMessageDialog(null, "�������");
					//����
					throw new CmmException(ErrorDescription.NOT_A_NUMBER,"\t LineNumber:"+curr.getLineNum());
				}
			}else {
				//����
				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
			}
		}
		
		//read��䲻��Ҫ�����κζ���
		return null;
	}
	
	/**
	 * �����������
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
					//����
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
					//����
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
	 * ����factor���
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
				//����
				throw new CmmException(ErrorDescription.UNKNOWN_TYPE,"\t LineNumber:"+curr.getLineNum());
			}
			
		}else {
			//��������
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
	
}
