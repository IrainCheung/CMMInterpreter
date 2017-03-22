package lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import error.CmmException;
import lexer.Token.Symble;
import parser.Variable.VariableType;

public class Lexer {
	
	
	/*
	 * 分析步骤：
	 * 1.读入字符
	 * 2.获取单个字符进行判断
	 * 3.将判别的字符连带数据封装在一个Token类中，然后添加进一个动态数组中
	 * 需要判别的有：
	 * 保留字
	 * 数字：整数和浮点
	 * 操作符 + - * /
	 * 标识符
	 * ()
	 * {}
	 * []
	 * 
	 * 4.最后返回数组，完成词法分析。
	 * 
	 * 
	 * 数字问题没解决
	 * 注释问题没解决
	 */
	private static BufferedReader mBufferedReader;
//	private static ArrayList<Token> list_token;
	private static char currentChar;
	private static int currentCharID;
	private static int lineNum;
	
//	public static void main(String[] args) throws IOException {
//		// TODO 自动生成的方法存根
//		Lexer lexer=new Lexer();
//		
//		File file=new File("test.cmm");
//		BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
//
//		ArrayList<Token> list=lexer.checkType(bufferedReader);
//		System.out.println("词法分析结果：");
//		System.out.println("length:"+list.size());
//		lexer.printTokenList(list);
//		String errorMessage=CmmError.checkError(list);
//		System.out.println("错误检查结果：\n"+errorMessage);
//	}
	
	/*
	 * 打印tokenlist
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
	 * 进行字符串判断并返回Tokenlist对象
	 */
	public ArrayList<Token> checkType(BufferedReader mbr) throws IOException, CmmException {
		mBufferedReader=mbr;
		lineNum=1;
		currentCharID=mBufferedReader.read();
		currentChar=(char) currentCharID;
		//用于存储数字和标识符
		StringBuffer sBuffer=new StringBuffer();
		ArrayList<Token> list_token=new ArrayList<>();
//		System.out.println("char:"+currentChar);
//		System.out.println("ID:"+currentCharID);
		while (currentCharID!=-1) {
			
			//空白字符
			if (currentChar=='\r'||currentChar==' '||currentChar=='\t'||currentChar=='\f'||currentCharID==10) {
//				System.out.println("进入空白字符");
//				System.out.println("char:"+currentChar);
//				System.out.println("ID:"+currentCharID);
				readNextChar();
				continue;
			}

			//简单操作符
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
//				System.out.println("进入;");
				list_token.add(new Token(Symble.SEMICO, ";", lineNum));
				readNextChar();
				continue;
			}else if (currentChar=='\''||currentChar=='\"'){
				System.out.println("出现未知字符,所在行："+lineNum+"\t"+"未知字符："+currentChar);
				throw new CmmException("出现未知字符,所在行："+lineNum+"\t"+"未知字符："+currentChar);
			}
			
			
			//复杂操作符/ *
			if (currentChar=='/') {
				readNextChar();
				//多行注释
				if (currentChar=='*') {
					while (true) {
						readNextChar();
						if (currentChar=='*') {
							readNextChar();
							if (currentChar=='/') {
								readNextChar();
								break;//注释结束
							}
						}
					}
					continue;
				}
				//单行注释
				else if (currentChar=='/') {
					while (true) {
						if (currentChar=='\n') {
							break;
						}
						readNextChar();
					}
					continue;
				}else {//为除号
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
			
//			System.out.println("数字");
			//数字
			if (currentChar>='0'&&currentChar<='9') {
//				System.out.println("进入数字");
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
//					System.out.println("变量"+list_token.get(list_token.size()-3).toString());
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
			//标识符
			if ((currentChar>='a'&&currentChar<='z')||(currentChar>='A'&&currentChar<='Z')||currentChar=='_') {
//				System.out.println("进入标识符");
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
//					System.out.println("进入int");
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
//					System.out.println("标识符："+word);
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
//			System.out.println("此处的char:"+currentChar);
		}
		
		return list_token;
	}

	
	
	/*
	 * 获取下一个字符
	 */
	public void readNextChar() throws IOException {
		currentCharID= mBufferedReader.read();
		currentChar=(char) currentCharID;
		if (currentChar=='\n') {
			lineNum++;
		}
	}
	
	
}
