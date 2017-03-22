package error;

import java.util.ArrayList;

import lexer.Token;
import lexer.Token.Symble;
import parser.Variable.VariableType;
/**
 * ���ļ���Ч
 * @author Qideas
 *
 */
public class CmmError {

	/*
	 * ������
	 * ˼·��
	 * 1.��ͬ������飺��ʵ��
	 * 	����list��ȡ�����б�����ֵ��������һ��list�У�Ȼ����б�����ֵ�ü�顣
	 * 	
	 * 2.���ͱ�����飺��ʵ��
	 * 	int ��ʾ���� 
	 * 	real��ʾʵ��
	 * 3.�﷨�����飺δʵ�ֽ����﷨����ʱʵ��
	 * 		δ�ӷֺ�
	 * 		ȱ�ٹ�ϵ�����
	 * 		
	 * 4.����λ���ַ�����.��ʵ��
	 * 	
	 * 5.�����±겻����������:��ʵ��
	 * 
	 * 6��ʹ��֮ǰû������������ʵ��
	 * 
	 */
	private static final int TYPE_ERR=0;
	private static final int SAME_VARIABLE_ERR=1;
	private static final int GRAMER_ERR=2;
	
	private int errLine=-1;
	
	public static String checkError(ArrayList<Token> list) {
		String errorMessage="";
		
		//�����ͬ����
		ArrayList<Token> list_error_sv=checkSameVari(list);
		if (list_error_sv.size()==0) {
			errorMessage=errorMessage+"û����ͬ��������.\n";
		} else {
			errorMessage=errorMessage+"������ͬ��������:\n";
			
			for (Token token : list_error_sv) {
				errorMessage=errorMessage+"���ִ����У�"+token.getLineNum()+"\t"
						+"�������ͱ�־��"+token.getSymble().toString()+"\t"
						+"�������ͣ�"+token.getVariableType()+"\t"
						+"����ֵ��"+token.getValue()+"\n";
			}
			
		}
		
		//������ʹ���
		ArrayList<Token> list_error_te=checkTypeError(list);
		if (list_error_te.size()==0) {
			errorMessage=errorMessage+"û�б������ʹ���.\n";
		} else {
			errorMessage=errorMessage+"���ֱ������ʹ���:\n";
			
			for (Token token : list_error_te) {
				errorMessage=errorMessage+"���ִ����У�"+token.getLineNum()+"\t"
						+"�������ͱ�־��"+token.getSymble().toString()+"\t"
						+"�������ͣ�"+token.getVariableType()+"\t"
						+"����ֵ��"+token.getValue()+"\n";
			}
			
		}
		
		//��������±�
		ArrayList<Token> list_error_ai=checkArrayIndex(list);
		if (list_error_ai.size()==0) {
			errorMessage=errorMessage+"û�������±����.\n";
		} else {
			errorMessage=errorMessage+"���������±����:\n";
			
			for (Token token : list_error_ai) {
				errorMessage=errorMessage+"���ִ����У�"+token.getLineNum()+"\t"
						+"���ͣ�"+token.getSymble().toString()+"\t"
						+"�����±�ֵ��"+token.getValue()+"\t"
						+"Ӧ�����ͣ�"+token.getVariableType()+"\n";
			}
			
		}
		
		//�������Ƿ�����
		ArrayList<Token> list_error_vnd=checkVariableNotDeclare(list);
		if (list_error_vnd.size()==0) {
			errorMessage=errorMessage+"û��δ������������.\n";
		} else {
			errorMessage=errorMessage+"����δ������������:\n";
			
			for (Token token : list_error_vnd) {
				errorMessage=errorMessage+"���ִ����У�"+token.getLineNum()+"\t"
						+"���ͣ�"+token.getSymble().toString()+"\t"
						+"δ����������"+token.getValue()+"\t"
						+"���ͣ�"+token.getVariableType()+"\n";
			}
			
		}
		return errorMessage;
	}
	/*
	 * ��ͬ���������
	 */
	private static ArrayList<Token> checkSameVari(ArrayList<Token> list) {
		ArrayList<Token> list_error_sv=new ArrayList<>();
		ArrayList<Token> temp=new ArrayList<>();
		//ȡ�������ı���
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getSymble()==Symble.IDENTIFIER) {
				if (list.get(i-1).getSymble()==Symble.INT||list.get(i-1).getSymble()==Symble.REAL) {
					temp.add(list.get(i));
				}
			}
		}
		//���б������
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
	 * �������ͼ��
	 */
	private static ArrayList<Token> checkTypeError(ArrayList<Token> list) {
		ArrayList<Token> list_error_te=new ArrayList<>();
		//ȥ�����е�int ��real����
		ArrayList<Token> temp=new ArrayList<>();
		for (Token token : list) {
			if (token.getSymble()==Symble.DIGIT_INT||token.getSymble()==Symble.DIGIT_REAL) {
				temp.add(token);
			}
		}
		
		//���������ж�
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
	 * �����±���
	 */
	public static ArrayList<Token> checkArrayIndex(ArrayList<Token> list) {
		ArrayList<Token> list_error_ai=new ArrayList<>();
		ArrayList<Token> temp=new ArrayList<>();
		for (Token token : list) {
			if (token.getVariableType()==VariableType.ARR_INT||token.getVariableType()==VariableType.ARR_REAL) {
				temp.add(token);
			}
		}
		//����Ƿ�Ϊ����
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
	 * �Ƿ�ʹ��δ��������
	 */
	public static ArrayList<Token> checkVariableNotDeclare(ArrayList<Token> list) {
		ArrayList<Token> list_error_vnd=new ArrayList<>();
		ArrayList<Token> temp=new ArrayList<>();
		ArrayList<Token> varDeclared=new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getSymble()==Symble.IDENTIFIER) {
				if (list.get(i-1).getSymble()==Symble.INT||list.get(i-1).getSymble()==Symble.REAL) {
					varDeclared.add(list.get(i));
//					System.out.println("�����ı�����"+list.get(i).toString());
				}
				temp.add(list.get(i));
//				System.out.println("������"+list.get(i).toString());
			}
		}
		//�ж��Ƿ���δ�����ı���
		for (Token token : temp) {
			boolean exist=false;
			for (Token t : varDeclared) {
				if (token.getValue().equals(t.getValue())) {
					exist=true;
				}
			}
			if (!exist) {
				list_error_vnd.add(token);
//				System.out.println("δ�����ı�����"+token.toString());
			}
		}
		
		return list_error_vnd;
	}
	
	
}
