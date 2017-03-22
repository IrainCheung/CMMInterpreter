package parser;

import java.util.ArrayList;

import error.CmmException;
import error.Error;
import error.Error.ErrorDescription;
import lexer.Token;
import lexer.Token.Symble;

public class Parser {
	
	/*语法分析
	 * program -> statement
	 * number ->INTEGER|REAL
	 * variable ->IDENTIFIER[LBRACKET expression RBRACKET]
	 * statement -> if_stm|else_stm|while_stm|assign_stm|read_stm|write_stm|declare_stm
	 * if_stm ->IF LPAREN condition RPAREN block
	 * else_stm -> ELSE block
	 * while_stm ->WHILE LPAREN condition RPAREN block
	 * assign_stm ->IDENTIFIER ASSIGN expression SEMICO
	 * read_stm ->READ LPAREN expression RPAREN SEMICO
	 * write_stm ->WRITE LPAREN expression RPAREN SEMICO
	 * declare_stm ->(INT|REAL) IDENTIFIER [ASSIGN NUMBER] SEMICO
	 * block ->LBRACKET statement RBRACKET
	 * condition ->expression con_symbol expression
	 * con_symbol ->==|>=|<=|<|>|!=
	 * expression ->term [add_minus term]
	 * add_minus ->+|-
	 * term ->factor [multi_divid factor]
	 * multi_divid ->*|/
	 * factor ->variable|number|LPAREN expression RPAREN
	 */
	//用于存储分析好之后返回的Tokenlist
	
	/**
	 * 存储词法分析返回的list
	 */
	private ArrayList<Token> list_token=new ArrayList<>();
	//当前Token
	private Token curr_token;
	//当前Token的index
	private int curr_index=0;
	//用于存储语法检查中的错误
	private ArrayList<Error> list_error=new ArrayList<>();

	
//	public static void main(String[] args) throws IOException {
//		Lexer lexer=new Lexer();
//		
//		File file=new File("test.cmm");
//		BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
//		//词法分析
//		ArrayList<Token> list=lexer.checkType(bufferedReader);
//		System.out.println("词法分析结果：");
//		System.out.println("length:"+list.size());
//		lexer.printTokenList(list);
//		String errorMessage=CmmError.checkError(list);
//		System.out.println("错误检查结果：\n"+errorMessage);
//		//语法分析
//		Parser parser=new Parser(list);
//		TreeNode root=parser.begainParse();
//		System.out.println("语法树：");
//		parser.printTree(root);
//		parser.printGrammerError();
//		
//		System.out.println(parser.treeString.toString());
//	}

	//构造函数
	public Parser(ArrayList<Token> list) {
		this.list_token=list;
	}
	
	/**
	 * program -> statement
	 * statement -> if_stm|else_stm|while_stm|assign_stm|read_stm|write_stm|declare_stm
	 * 开始解析词法分析返回的list
	 * @param list
	 * @throws CmmException 
	 */
	public TreeNode begainParse() throws CmmException {
		TreeNode root=new TreeNode(StatementName.PROGRAM);
		nextToken();
		while (curr_token!=null) {
			//解析declare_stm 已经完成
			if (curr_token.getSymble()==Symble.INT||curr_token.getSymble()==Symble.REAL) {
				root.add(declare_stm());
				continue;
			}
			//解析赋值语句
			if (curr_token.getSymble()==Symble.IDENTIFIER) {
				root.add(assign_stm());
				continue;
			}
			if (curr_token.getSymble()==Symble.LBRACE) {
				root.add(block_stm());
				continue;
			}
			
			if (curr_token.getSymble()==Symble.IF) {
				root.add(if_stm());
				continue;
			}
			
			if (curr_token.getSymble()==Symble.WHILE) {
				root.add(while_stm());
				continue;
			}
			
			if (curr_token.getSymble()==Symble.READ) {
				root.add(read_stm());
				continue;
			}
			
			if (curr_token.getSymble()==Symble.WRITE) {
				root.add(write_stm());
				continue;
			}else {
				break;
			}
			
		}
		
		return root;
	}
	
	/**
	 * declare_stm ->(INT|REAL) IDENTIFIER [ASSIGN NUMBER] SEMICO 完成解析
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode declare_stm() throws CmmException {
		TreeNode declare_node=new TreeNode(curr_token,StatementName.DECLARE_STM);
		nextToken();
		
		if (curr_token.getSymble()==Symble.IDENTIFIER) {
			//解析是普通变量还是数组变量
			declare_node.add(variable());
//			variable();
			//解析玩是否是变量后，判断下一个字符是不是赋值等号
//			nextToken();
			if (curr_token.getSymble()==Symble.ASSIGN) {
				//
				nextToken();
				if (curr_token.getSymble()==Symble.DIGIT_INT||curr_token.getSymble()==Symble.DIGIT_REAL) {
					declare_node.add(number());
					//在每个函数的最后都会读取下一个Token到当前Token中，所以在函数后面可以不用读取下一个Token
//					number();
					
					if (curr_token.getSymble()==Symble.SEMICO) {
						//语句结束
						nextToken();
					}else {
						//出错，此处需要一个;
						list_error.add(new Error(ErrorDescription.SEMICO_LACK, curr_token.getLineNum()));
						throw new CmmException(ErrorDescription.SEMICO_LACK,"\tLineNumber:"+curr_token.getLineNum());
					}
				}
				else {
					//否则则有语法错误
					list_error.add(new Error(ErrorDescription.SEMICO_LACK, curr_token.getLineNum()));
					throw new CmmException(ErrorDescription.SEMICO_LACK,"\tLineNumber:"+curr_token.getLineNum());
				}
			}
			//说明只是进行了声明变量，并没有赋值操作
			else if (curr_token.getSymble()==Symble.SEMICO) {
				//声明语句结束
				nextToken();
			}
			//如果是逗号，将会进行多变量的声明，目前先不实现，以后有时间再实现
			else {
				list_error.add(new Error(ErrorDescription.ASSIGN_LACK, curr_token.getLineNum()));
				throw new CmmException(ErrorDescription.ASSIGN_LACK,"\tLineNumber:"+curr_token.getLineNum());
			}
			
		}else {
			list_error.add(new Error(ErrorDescription.IDENTIFIER_LACK, curr_token.getLineNum()));
			throw new CmmException(ErrorDescription.IDENTIFIER_LACK,"\tLineNumber:"+curr_token.getLineNum());
		}
		
		//完成声明语句的解析，并读取下一个字符
		
		return declare_node;
	}
	
	/**
	 * assign_stm ->IDENTIFIER ASSIGN expression SEMICO
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode assign_stm() throws CmmException {
		TreeNode assign_node=new TreeNode(StatementName.ASSIGN_STM);
		//因为已经判断为赋值语句，所以当前token必然是identifier类型，所以直接进行variable() 操作
		assign_node.add(variable());
//		variable();
		//解析完变量之后判断下个是不是赋值等号
		if (curr_token.getSymble()==Symble.ASSIGN) {
			//是赋值符号则继续读
			nextToken();
			assign_node.add(expression_stm());
//			expression_stm();
			//解析完表达式之后，判断下一个是不是分号
			if (curr_token.getSymble()==Symble.SEMICO) {
				//当前语句结束，读取下一个单词
				nextToken();
			}else {
				//否则报错
				list_error.add(new Error(ErrorDescription.SEMICO_LACK, curr_token.getLineNum()));
				throw new CmmException(ErrorDescription.SEMICO_LACK,"\tLineNumber:"+curr_token.getLineNum());
			}
			
//			//判断是不是数字
//			if (curr_token.getSymble()==Symble.DIGIT_INT||curr_token.getSymble()==Symble.DIGIT_REAL) {
//				assign_node.add(number());//在每个函数的最后都会读取下一个Token到当前Token中，所以在函数后面可以不用读取下一个Token
//				if (curr_token.getSymble()==Symble.SEMICO) {
//					//语句结束
//					nextToken();
//				}else {
//					//出错，此处需要一个;
//					list_error.add(new Error("a semicolon was need", curr_token.getLineNum()));
//				}
//			}
//			//判断是不是变量
//			else if (curr_token.getSymble()==Symble.IDENTIFIER) {
//				assign_node.add(variable());
//				if (curr_token.getSymble()==Symble.SEMICO) {
//					//语句结束
//					nextToken();
//				}
//			}
//			//判断是不是表达式
//			else if (curr_token.getSymble()==Symble.LPAREN) {
//				
//			}
			
		}else {
			list_error.add(new Error(ErrorDescription.ASSIGN_LACK, curr_token.getLineNum()));
			throw new CmmException(ErrorDescription.ASSIGN_LACK,"\tLineNumber:"+curr_token.getLineNum());
		}
		
		
		return assign_node;
	}
	
	/**
	 * if_stm ->IF LPAREN condition RPAREN block 完成
	 * else_stm -> ELSE block
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode if_stm() throws CmmException {
		TreeNode if_node=new TreeNode(curr_token,StatementName.IF_STM);
		nextToken();
		if (curr_token.getSymble()==Symble.LPAREN) {
			if_node.add(condition_stm());
//			condition_stm();
			if (curr_token.getSymble()==Symble.RPAREN) {
				nextToken();
				if (curr_token.getSymble()==Symble.LBRACE) {
					if_node.add(block_stm());
					if (curr_token.getSymble()==Symble.RBRACE) {
						//block语句解析完成，继续读取下一个单词
						nextToken();
						//如果存在else语句,就继续解析
						if (curr_token!=null&&curr_token.getSymble()==Symble.ELSE) {
							TreeNode else_node=new TreeNode(StatementName.ELSE_STM);
							if_node.add(else_node);
							nextToken();
							if (curr_token.getSymble()==Symble.LBRACE) {
//								if_node.add(block_stm());
								else_node.add(block_stm());;
								if (curr_token.getSymble()==Symble.RBRACE) {
									//else 语句解析结束，继续读取下一个单词
									nextToken();
								}else {
									list_error.add(new Error(ErrorDescription.RBRACKET_LACK, curr_token.getLineNum()));
									throw new CmmException(ErrorDescription.RBRACKET_LACK,"\tLineNumber:"+curr_token.getLineNum());
								}
								
							}else {
								list_error.add(new Error(ErrorDescription.LBRACKET_LACK, curr_token.getLineNum()));
								throw new CmmException(ErrorDescription.LBRACKET_LACK,"\tLineNumber:"+curr_token.getLineNum());
							}
							
						}
						//没有else则不做任何操作,继续读取下一个单词
						else {
//							nextToken();
						}
					}else {
						list_error.add(new Error(ErrorDescription.RBRACKET_LACK, curr_token.getLineNum()));
						throw new CmmException(ErrorDescription.RBRACKET_LACK,"\tLineNumber:"+curr_token.getLineNum());
					}
					
				}else {
					list_error.add(new Error(ErrorDescription.LBRACKET_LACK, curr_token.getLineNum()));
					throw new CmmException(ErrorDescription.LBRACKET_LACK,"\tLineNumber:"+curr_token.getLineNum());
				}
				
				
			}else {
				list_error.add(new Error(ErrorDescription.RPAEN_LACK, curr_token.getLineNum()));
				throw new CmmException(ErrorDescription.RPAEN_LACK,"\tLineNumber:"+curr_token.getLineNum());
			}
			
			
		}else {
			list_error.add(new Error(ErrorDescription.LPAREN_LACK, curr_token.getLineNum()));
			throw new CmmException(ErrorDescription.LPAREN_LACK,"\tLineNumber:"+curr_token.getLineNum());
		}
		
		
		return if_node;
	}
	
	/**
	 * while_stm ->WHILE LPAREN condition RPAREN block 完成解析
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode while_stm() throws CmmException {
		TreeNode while_node=new TreeNode(curr_token,StatementName.WHILE_STM);
		nextToken();
		if (curr_token.getSymble()==Symble.LPAREN) {
			while_node.add(condition_stm());
//			condition_stm();
			//解析完成
			if (curr_token.getSymble()==Symble.RPAREN) {
				nextToken();
				if (curr_token.getSymble()==Symble.LBRACE) {
					while_node.add(block_stm());
					
					if (curr_token.getSymble()==Symble.RBRACE) {
						//解析完毕，继续读取下一个单词
						nextToken();
					}else {
						list_error.add(new Error(ErrorDescription.RBRACE_LACK, curr_token.getLineNum()));
						throw new CmmException(ErrorDescription.RBRACE_LACK,"\tLineNumber:"+curr_token.getLineNum());
					}
					
				} else {
					list_error.add(new Error(ErrorDescription.LBRACE_LACK, curr_token.getLineNum()));
					throw new CmmException(ErrorDescription.LBRACE_LACK,"\tLineNumber:"+curr_token.getLineNum());
					
				}
			}else {
				list_error.add(new Error(ErrorDescription.RPAEN_LACK, curr_token.getLineNum()));
				throw new CmmException(ErrorDescription.RPAEN_LACK,"\tLineNumber:"+curr_token.getLineNum());
			}
			
		}else {
			list_error.add(new Error(ErrorDescription.LPAREN_LACK, curr_token.getLineNum()));
			throw new CmmException(ErrorDescription.LPAREN_LACK,"\tLineNumber:"+curr_token.getLineNum());
		}
		
		return while_node;
	}
	
	/**
	 * block ->LBRACKET statement RBRACKET
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode block_stm() throws CmmException {
		TreeNode block_node=new TreeNode(StatementName.BLOCK_STM);
		//当遇到{时会跳到这个函数，所以应该继续读取下一个单词
		nextToken();
		while (curr_token.getSymble()!=Symble.RBRACE) {
			if (curr_token.getSymble()==Symble.INT||curr_token.getSymble()==Symble.REAL) {
				block_node.add(declare_stm());
			}else if (curr_token.getSymble()==Symble.IDENTIFIER) {
				block_node.add(assign_stm());
			}else if (curr_token.getSymble()==Symble.WHILE) {
				block_node.add(while_stm());
			}else if (curr_token.getSymble()==Symble.READ) {
				block_node.add(read_stm());
			}else if (curr_token.getSymble()==Symble.WRITE) {
				block_node.add(write_stm());
			}else if (curr_token.getSymble()==Symble.IF) {
				block_node.add(if_stm());
			}else {
				list_error.add(new Error(ErrorDescription.UNKNOWN_GRAMMER, curr_token.getLineNum()));
				throw new CmmException(ErrorDescription.UNKNOWN_GRAMMER,"\tLineNumber:"+curr_token.getLineNum());
			}
		}
		
		
		
		
		
		return block_node;
	}
	
	/**
	 * condition ->expression con_symbol expression
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode condition_stm() throws CmmException {
		TreeNode condition_node=new TreeNode(StatementName.CONDITION_STM);
		nextToken();
		if (curr_token!=null) {
			condition_node.add(expression_stm());
//			expression_stm();
			
			if (curr_token.getSymble()==Symble.EQ||
				curr_token.getSymble()==Symble.NEQ||
				curr_token.getSymble()==Symble.MO||
				curr_token.getSymble()==Symble.MOEQ||
				curr_token.getSymble()==Symble.LS||
				curr_token.getSymble()==Symble.LSEQ) {
				condition_node.add(conditionSymbol());
//				conditionSymbol();
			}
			condition_node.add(expression_stm());
//			expression_stm();
			
		}else {
			
		}
		
		return condition_node;
	}
	
	/**
	 * read_stm ->READ LPAREN expression RPAREN SEMICO
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode read_stm() throws CmmException {
		TreeNode read_node=new TreeNode(StatementName.READ_STM);
		//执行这个函数说明当前单词是read
		nextToken();
		if (curr_token.getSymble()==Symble.LPAREN) {
			nextToken();
			read_node.add(expression_stm());
//			expression_stm();
			if (curr_token.getSymble()==Symble.RPAREN) {
				nextToken();
				if (curr_token.getSymble()==Symble.SEMICO) {
					//解析完毕，继续读取下一个单词
					nextToken();
				}else {
					list_error.add(new Error(ErrorDescription.SEMICO_LACK, curr_token.getLineNum()));
					throw new CmmException(ErrorDescription.SEMICO_LACK,"\tLineNumber:"+curr_token.getLineNum());
				}
			} else {
				list_error.add(new Error(ErrorDescription.RPAEN_LACK, curr_token.getLineNum()));
				throw new CmmException(ErrorDescription.RPAEN_LACK,"\tLineNumber:"+curr_token.getLineNum());
			}
			
		}else {
			list_error.add(new Error(ErrorDescription.LPAREN_LACK, curr_token.getLineNum()));
			throw new CmmException(ErrorDescription.LPAREN_LACK,"\tLineNumber:"+curr_token.getLineNum());
		}
		
		return read_node;
	}
	
	/**
	 * write_stm ->WRITE LPAREN expression RPAREN SEMICO
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode write_stm() throws CmmException {
		TreeNode write_node=new TreeNode(StatementName.WRITE_STM);
		//执行这个函数说明当前单词是write
				nextToken();
				if (curr_token.getSymble()==Symble.LPAREN) {
					nextToken();
					write_node.add(expression_stm());
//					expression_stm();
					if (curr_token.getSymble()==Symble.RPAREN) {
						nextToken();
						if (curr_token.getSymble()==Symble.SEMICO) {
							//解析完毕，继续读取下一个单词
							nextToken();
						}else {
							list_error.add(new Error(ErrorDescription.SEMICO_LACK, curr_token.getLineNum()));
							throw new CmmException(ErrorDescription.SEMICO_LACK,"\tLineNumber:"+curr_token.getLineNum());
						}
					} else {
						list_error.add(new Error(ErrorDescription.RPAEN_LACK, curr_token.getLineNum()));
						throw new CmmException(ErrorDescription.RPAEN_LACK,"\tLineNumber:"+curr_token.getLineNum());
					}
					
				}else {
					list_error.add(new Error(ErrorDescription.LPAREN_LACK, curr_token.getLineNum()));
					throw new CmmException(ErrorDescription.LPAREN_LACK,"\tLineNumber:"+curr_token.getLineNum());
				}
		return write_node;
	}
	
	/**
	 * expression ->term [add_minus term]*
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode expression_stm() throws CmmException {
		TreeNode expression_node=new TreeNode(StatementName.EXPRESSION_STM);
		expression_node.add(term());
		
		while (curr_token.getSymble()!=Symble.RPAREN) {//&&curr_token.getSymble()!=Symble.RBRACKET
			if (curr_token.getSymble()==Symble.PLUS||curr_token.getSymble()==Symble.MINUS) {
				expression_node.add(addMinus());
				expression_node.add(term());
			}else {
				break;
			}
		}
		
		
		return expression_node;
	}
	
	/**
	 * number ->INTEGER|REAL
	 * @return
	 */
	public TreeNode number() {
		TreeNode number_node=new TreeNode(curr_token,StatementName.NUMBER_STM);
		if (curr_token!=null&&(curr_token.getSymble()==Symble.DIGIT_INT||curr_token.getSymble()==Symble.DIGIT_REAL)) {
			//叶子节点，所以继续读取下一个单词
			nextToken();

		}
		return number_node;
	}
	
	/**
	 * 解析变量
	 * variable ->IDENTIFIER[LBRACKET expression RBRACKET]
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode variable() throws CmmException {
		TreeNode variable_node=new TreeNode(curr_token,StatementName.VARIABLE_STM);
		//执行这个函数说明当前单词是identifier
		nextToken();
		if (curr_token.getSymble()==Symble.LBRACKET) {
			//说明是数组
			variable_node.getCurr_token().setArray(true);
			nextToken();
			variable_node.add(expression_stm());
//			expression_stm();
			
			if (curr_token.getSymble()==Symble.RBRACKET) {
				//解析结束
				nextToken();
			}else {
				list_error.add(new Error(ErrorDescription.RBRACKET_LACK, curr_token.getLineNum()));
				throw new CmmException(ErrorDescription.RBRACKET_LACK,"\tLineNumber:"+curr_token.getLineNum());
			}
		}else {
			//不是数组
		}
		
		return variable_node;
	}
	
	/**
	 * con_symbol ->==|>=|<=|<|>|!=
	 * @return
	 */
	public TreeNode conditionSymbol() {
		TreeNode con_sym=new TreeNode(curr_token,StatementName.CONDITION_SYMBOL);
		//说明是比较操作符，继续读取下一个单词
		nextToken();
		return con_sym;
	}
	
	/**
	 * add_minus ->+|-
	 * @return
	 */
	public TreeNode addMinus() {
		TreeNode add_minus=new TreeNode(curr_token,StatementName.ADD_MINUS);
		nextToken();
		return add_minus;
	}
	
	/**
	 * term ->factor [multi_divid factor]
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode term() throws CmmException {
		TreeNode term=new TreeNode(StatementName.TERM);
		term.add(factor());
		while (curr_token.getSymble()!=Symble.PLUS&&curr_token.getSymble()!=Symble.MINUS) {
			if (curr_token.getSymble()==Symble.MILTI||curr_token.getSymble()==Symble.DIVID) {
				term.add(multiDivid());
				term.add(factor());
			}else {
				break;
			}
		}
		
		
		return term;
	}
	
	/**
	 * multi_divid ->*|/
	 * @return
	 */
	public TreeNode multiDivid() {
		TreeNode multi_divid=new TreeNode(curr_token,StatementName.MULTI_DIVID);
		nextToken();
		return multi_divid;
	}
	
	/**
	 * factor ->variable|number|LPAREN expression RPAREN
	 * @return
	 * @throws CmmException 
	 */
	public TreeNode factor() throws CmmException {
		TreeNode factor=new TreeNode(StatementName.FACTOR);
		if (curr_token!=null) {
			if (curr_token.getSymble()==Symble.IDENTIFIER) {
				factor.add(variable());
//				variable();
			}else if (curr_token.getSymble()==Symble.DIGIT_INT||curr_token.getSymble()==Symble.DIGIT_REAL) {
				factor.add(number());
//				number();
			}else if (curr_token.getSymble()==Symble.LPAREN) {
				factor.add(expression_stm());
//				expression_stm();
				if (curr_token.getSymble()==Symble.RPAREN) {
					//解析结束
					nextToken();
				}else {
					list_error.add(new Error(ErrorDescription.RPAEN_LACK, curr_token.getLineNum()));
					throw new CmmException(ErrorDescription.RPAEN_LACK,"\tLineNumber:"+curr_token.getLineNum());
				}
			}
			
		}
		return factor;
	}
	
	/**
	 * 获取当前Token
	 * @return
	 */
	public Token nextToken() {
		
		if (curr_index<=(list_token.size()-1)) {
			curr_token=list_token.get(curr_index);
		}else {
			curr_token=null;
			curr_index--;
		}
		curr_index++;
		return curr_token;
	}
	
	/**
	 * 打印分析出来的错误信息
	 * @param list
	 */
	public void printError(ArrayList<Error> list) {
		for (Error error : list) {
			System.out.println(error.toString());
		}
	}
	
	/**
	 * 返回语法树的字符串结构
	 * @return
	 */
	public String getTreeString() {
		return treeString.toString();
	}

	private StringBuffer treeString=new StringBuffer();
	
	/**
	 * 打印语法树
	 * 默认count从0开始
	 * @param root
	 */
	public String printTree(TreeNode root) {
		StringBuffer stringBuffer=new StringBuffer();
		stringBuffer=printTree(root, 0);
		return stringBuffer.toString();
	}
	
	/**
	 * 打印整棵语法树
	 * @param root
	 * @param count 缩进次数
	 */
	public StringBuffer printTree(TreeNode root,int count) {
		
		for (int i = 0; i < count; i++) {
			System.out.print("|--");
			treeString.append("|--");
		}
		System.out.println(root.toString());
		treeString.append(root.toString()+"\n");
		for (int i = 0; i < root.getChildCount(); i++) {
			printTree((TreeNode) root.getChildAt(i),count+1);
		}
		
		return treeString;
	}
	
	/**
	 * 打印语法错误
	 * @param list
	 */
	public void printGrammerError() {
		System.out.println("语法错误：");
		if (list_error.size()==0) {
			System.out.println("没有语法错误");
		}else {
			for (Error error : list_error) {
				System.out.println(error.toString());
			}
			
		}
		
	}
	
	
}
