package lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import error.CmmException;
import lexer.Token.Symble;
import parser.Variable.VariableType;

public class Lexer {
	
	
	/*
	 * �������裺
	 * 1.�����ַ�
	 * 2.��ȡ�����ַ������ж�
	 * 3.���б���ַ��������ݷ�װ��һ��Token���У�Ȼ����ӽ�һ����̬������
	 * ��Ҫ�б���У�
	 * ������
	 * ���֣������͸���
	 * ������ + - * /
	 * ��ʶ��
	 * ()
	 * {}
	 * []
	 * 
	 * 4.��󷵻����飬��ɴʷ�������
	 * 
	 * 
	 * ��������û���
	 * ע������û���
	 */
	private static BufferedReader mBufferedReader;
//	private static ArrayList<Token> list_token;
	private static char currentChar;
	private static int currentCharID;
	private static int lineNum;
	
//	public static void main(String[] args) throws IOException {
//		// TODO �Զ����ɵķ������
//		Lexer lexer=new Lexer();
//		
//		File file=new File("test.cmm");
//		BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
//
//		ArrayList<Token> list=lexer.checkType(bufferedReader);
//		System.out.println("�ʷ����������");
//		System.out.println("length:"+list.size());
//		lexer.printTokenList(list);
//		String errorMessage=CmmError.checkError(list);
//		System.out.println("����������\n"+errorMessage);
//	}
	
	/*
	 * ��ӡtokenlist
	 */
	public String printTokenList(ArrayList<Token> list) {
		String listString="";
		for (Token token : list) {
			System.out.println(token.toString());
			listString=listString+token.toString()+"\n";
		}
		return listString;
	}
	
	/*
	 * �����ַ����жϲ�����Tokenlist����
	 */
	public ArrayList<Token> checkType(BufferedReader mbr) throws IOException, CmmException {
		mBufferedReader=mbr;
		lineNum=1;
		currentCharID=mBufferedReader.read();
		currentChar=(char) currentCharID;
		//���ڴ洢���ֺͱ�ʶ��
		StringBuffer sBuffer=new StringBuffer();
		ArrayList<Token> list_token=new ArrayList<>();
//		System.out.println("char:"+currentChar);
//		System.out.println("ID:"+currentCharID);
		while (currentCharID!=-1) {
			
			//�հ��ַ�
			if (currentChar=='\r'||currentChar==' '||currentChar=='\t'||currentChar=='\f'||currentCharID==10) {
//				System.out.println("����հ��ַ�");
//				System.out.println("char:"+currentChar);
//				System.out.println("ID:"+currentCharID);
				readNextChar();
				continue;
			}

			//�򵥲�����
			if (currentChar=='+') {
				list_token.add(new Token(Symble.PLUS, "+", lineNum));
				readNextChar();
				continue;
			}else if (currentChar=='-') {
				list_token.add(new Token(Symble.MINUS, "-", lineNum));
				readNextChar();
				continue;
			}else if (currentChar=='*') {
				list_token.add(new Token(Symble.MILTI, "*", lineNum));
				readNextChar();
				continue;
			}else if (currentChar=='(') {
				list_token.add(new Token(Symble.LPAREN, "(", lineNum));
				readNextChar();
				continue;
			}else if (currentChar==')') {
				list_token.add(new Token(Symble.RPAREN, ")", lineNum));
				readNextChar();
				continue;
			}else if (currentChar=='[') {
				list_token.add(new Token(Symble.LBRACKET, "[", lineNum));
				readNextChar();
				continue;
			}else if (currentChar==']') {
				list_token.add(new Token(Symble.RBRACKET, "]", lineNum));
				readNextChar();
				continue;
			}else if (currentChar=='{') {
				list_token.add(new Token(Symble.LBRACE, "{", lineNum));
				readNextChar();
				continue;
			}else if (currentChar=='}') {
				list_token.add(new Token(Symble.RBRACE, "}", lineNum));
				readNextChar();
				continue;
			}else if (currentChar==';') {
//				System.out.println("����;");
				list_token.add(new Token(Symble.SEMICO, ";", lineNum));
				readNextChar();
				continue;
			}else if (currentChar=='\''||currentChar=='\"'){
				System.out.println("����δ֪�ַ�,�����У�"+lineNum+"\t"+"δ֪�ַ���"+currentChar);
				throw new CmmException("����δ֪�ַ�,�����У�"+lineNum+"\t"+"δ֪�ַ���"+currentChar);
			}
			
			
			//���Ӳ�����/ *
			if (currentChar=='/') {
				readNextChar();
				//����ע��
				if (currentChar=='*') {
					while (true) {
						readNextChar();
						if (currentChar=='*') {
							readNextChar();
							if (currentChar=='/') {
								readNextChar();
								break;//ע�ͽ���
							}
						}
					}
					continue;
				}
				//����ע��
				else if (currentChar=='/') {
					while (true) {
						if (currentChar=='\n') {
							break;
						}
						readNextChar();
					}
					continue;
				}else {//Ϊ����
					list_token.add(new Token(Symble.DIVID, "/", lineNum));
					continue;
				}

			}
			else if (currentChar=='=') {
				readNextChar();
				if (currentChar=='=') {
					list_token.add(new Token(Symble.EQ, "==", lineNum));
					readNextChar();
				}else {
					list_token.add(new Token(Symble.ASSIGN, "=", lineNum));
//					readNextChar();
				}
				continue;
			}
			else if (currentChar=='<') {
				readNextChar();
				if (currentChar=='=') {
					list_token.add(new Token(Symble.LSEQ, "<=", lineNum));
					readNextChar();
				}else {
					list_token.add(new Token(Symble.LS, "<", lineNum));
//					readNextChar();
				}
				continue;
			}
			else if (currentChar=='>') {
				readNextChar();
				if (currentChar=='=') {
					list_token.add(new Token(Symble.MOEQ, ">=", lineNum));
					readNextChar();
				}else {
					list_token.add(new Token(Symble.MO, ">", lineNum));
//					readNextChar();
				}
				continue;
			}
			else if (currentChar=='!') {
				readNextChar();
				if (currentChar=='=') {
					list_token.add(new Token(Symble.NEQ, "!=", lineNum));
					readNextChar();
				}
				continue;
			}
			
//			System.out.println("����");
			//����
			if (currentChar>='0'&&currentChar<='9') {
//				System.out.println("��������");
				sBuffer.append(currentChar);
				readNextChar();
				boolean isReal=false;
				while ((currentChar>='0'&&currentChar<='9')||currentChar=='.') {
					if (currentChar=='.') {
						isReal=true;
					}
					sBuffer.append(currentChar);
					readNextChar();
				}
				if (isReal) {
					list_token.add(new Token(Symble.DIGIT_REAL, sBuffer.toString(), lineNum));
					
				}else {
					list_token.add(new Token(Symble.DIGIT_INT, sBuffer.toString(), lineNum));
					
				}
				Token curr_last=null;
				if (list_token.size()>=2) {
					curr_last=list_token.get(list_token.size()-2);
				}
				Token intOrReal=null;
				if (list_token.size()>=4) {
					intOrReal=list_token.get(list_token.size()-4);
				}
//				System.out.println("curr_last:"+curr_last.toString());
				if (curr_last.getValue().equals("[")) {
//					System.out.println("����"+list_token.get(list_token.size()-3).toString());
					if (intOrReal.getValue()=="real") {
						list_token.get(list_token.size()-1).setVariableType(VariableType.ARR_REAL);
						list_token.get(list_token.size()-3).setVariableType(VariableType.ARR_REAL);
					} else {
						list_token.get(list_token.size()-1).setVariableType(VariableType.ARR_INT);
						list_token.get(list_token.size()-3).setVariableType(VariableType.ARR_INT);
					}
					
				}else {
					Token lastvar=null;
					if (list_token.size()>3) {
						lastvar=list_token.get(list_token.size()-3);
					}
					
					
//					System.out.println("lastvar:"+lastvar.toString());
//					for (int i = 1; i < list_token.size(); i++) {
//						if (list_token.get(list_token.size()-i).getSymble()==Symble.IDENTIFIER) {
//							lastvar=list_token.get(list_token.size()-i);
//						}
//						
//					}
					if (lastvar!=null) {
//						System.out.println("lastvar:"+lastvar.toString());
						if (lastvar.getVariableType()==VariableType.INT) {
							list_token.get(list_token.size()-1).setVariableType(VariableType.INT);
						}
						if (lastvar.getVariableType()==VariableType.REAL) {
							list_token.get(list_token.size()-1).setVariableType(VariableType.REAL);
						}
					}
				}
				
				
				
				
				Token curr=list_token.get(list_token.size()-1);
//				System.out.println("curr:"+curr.toString());
				sBuffer.delete(0, sBuffer.length());
				continue;
			}
			//��ʶ��
			if ((currentChar>='a'&&currentChar<='z')||(currentChar>='A'&&currentChar<='Z')||currentChar=='_') {
//				System.out.println("�����ʶ��");
//				System.out.println("char:"+currentChar);
//				System.out.println("ID:"+currentCharID);
//				sBuffer.append(currentChar);
//				readNextChar();
				while ((currentChar>='a'&&currentChar<='z')||(currentChar>='A'&&currentChar<='Z')||(currentChar>='0'&&currentChar<='9')||currentChar=='_') {
					sBuffer.append(currentChar);
//					System.out.println("while-char:"+currentChar);
					readNextChar();
				}
				String word=sBuffer.toString();
				sBuffer.delete(0, sBuffer.length());
//				System.out.println("Word:"+word);
				Token token=null;
				
				if (word.equals("if")) {
					token=new Token(Symble.IF,"if", lineNum);
//					readNextChar();
				}else if (word.equals("else")) {
					token=new Token(Symble.ELSE, "else", lineNum);
//					readNextChar();
				}else if (word.equals("int")) {
//					System.out.println("����int");
					token=new Token(Symble.INT, "int", lineNum);
//					readNextChar();
//					System.out.println("int -char:"+currentChar);
				}else if (word.equals("double")) {
					token=new Token(Symble.DOUBLE, "double", lineNum);
//					readNextChar();
				}else if (word.equals("while")) {
					token=new Token(Symble.WHILE, "while", lineNum);
//					readNextChar();
				}else if (word.equals("for")) {
					token=new Token(Symble.FOR, "for", lineNum);
//					readNextChar();
				}else if (word.equals("real")) {
					token=new Token(Symble.REAL, "real", lineNum);
//					readNextChar();
				}else if (word.equals("break")) {
					token=new Token(Symble.BREAK, "break", lineNum);
//					readNextChar();
				}else if (word.equals("return")) {
					token=new Token(Symble.RETURN, "return", lineNum);
//					readNextChar();
				}else if (word.equals("class")) {
					token=new Token(Symble.CLASS, "class", lineNum);
//					readNextChar();
				}else if (word.equals("read")) {
					token=new Token(Symble.READ, "read", lineNum);
//					readNextChar();
				}else if (word.equals("write")) {
					token=new Token(Symble.WRITE, "write", lineNum);
//					readNextChar();
				}else {
//					System.out.println("��ʶ����"+word);
					token=new Token(Symble.IDENTIFIER, word, lineNum);
					
					Token last=list_token.get(list_token.size()-1);
					if (last.getSymble()==Symble.INT) {
						token.setVariableType(VariableType.INT);
					}
					if (last.getSymble()==Symble.REAL) {
						token.setVariableType(VariableType.REAL);
					}
//					
//					System.out.println("last:"+last.toString());
//					System.out.println("token:"+token.toString());
//					readNextChar();
				}
								
				list_token.add(token);
				continue;
//				readNextChar();
			}
			
//			readNextChar();
//			System.out.println("�˴���char:"+currentChar);
		}
		
		return list_token;
	}

	
	
	/*
	 * ��ȡ��һ���ַ�
	 */
	public void readNextChar() throws IOException {
		currentCharID= mBufferedReader.read();
		currentChar=(char) currentCharID;
		if (currentChar=='\n') {
			lineNum++;
		}
	}
	
	
}
