package parser;

import javax.swing.tree.DefaultMutableTreeNode;

import lexer.Token;
//语法树节点
public class TreeNode extends DefaultMutableTreeNode{
	
	/**
	 * 当前节点
	 */
	private Token curr_token;
	/**
	 * 父节点
	 */
	private TreeNode parent;
	/**
	 * 子节点
	 */
	private TreeNode child;
	/**
	 * 当前语句的名字
	 */
	private StatementName st;
	
	public TreeNode(StatementName statementName) {
		super(statementName);
		this.st=statementName;
	}

	public TreeNode(Token curr_token, StatementName st) {
		super();
		this.curr_token = curr_token;
		this.st = st;
	}

	public StatementName getSt() {
		return st;
	}

	public void setSt(StatementName st) {
		this.st = st;
	}

	public Token getCurr_token() {
		return curr_token;
	}

	public void setCurr_token(Token curr_token) {
		this.curr_token = curr_token;
	}

	@Override
	public String toString() {
		if (curr_token==null) {
			return "["+st + "]";
		}
		return "["+st +":"+curr_token.getValue()+ "]";
	}
	
	
	
}
