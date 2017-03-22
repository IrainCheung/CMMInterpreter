package parser;

import javax.swing.tree.DefaultMutableTreeNode;

import lexer.Token;
//�﷨���ڵ�
public class TreeNode extends DefaultMutableTreeNode{
	
	/**
	 * ��ǰ�ڵ�
	 */
	private Token curr_token;
	/**
	 * ���ڵ�
	 */
	private TreeNode parent;
	/**
	 * �ӽڵ�
	 */
	private TreeNode child;
	/**
	 * ��ǰ��������
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
