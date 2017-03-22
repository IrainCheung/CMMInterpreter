package lexer;

import parser.Variable.VariableType;

public class Token {

	
	public static final int ID_ERROR=-1; 
	public static final int ID_NULL=0;
	
	
	
	
	public enum Symble{
		INT(1),DOUBLE(2),REAL(3),FOR(4),WHILE(5),IF(6),ELSE(7),BREAK(8),RETURN(9),CLASS(10),
		PLUS(11),MINUS(12),MILTI(13),DIVID(14),EQ(15),NEQ(16),LS(17),MO(18),LSEQ(19),MOEQ(20),
		IDENTIFIER(21),
		LBRACKET(22),RBRACKET(23),
		LPAREN(25),RPAREN(26),
		LBRACE(27),RBRACE(28),
		COMMA(29),ASSIGN(30),SEMICO(35),
		DIGIT_INT(31),DIGIT_REAL(32),
		READ(33),WRITE(34)
		;
		
		private int id;
		
		private Symble(int id) {
			this.id=id;
		}

		public int getId() {
			return id;
		}
		
		
	};
	private boolean isArray=false;
	private Symble symble;
	private String value;
	private int lineNum;
	//定义变量的类型
//	private String variableType="";
	private VariableType variableType;
	public Token(Symble symble, String value, int lineNum) {
		super();
		this.symble = symble;
		this.value = value;
		this.lineNum = lineNum;
	}
	
	public Symble getSymble() {
		return symble;
	}
	public void setSymble(Symble symble) {
		this.symble = symble;
	}
	
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getLineNum() {
		return lineNum;
	}
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	
	public VariableType getVariableType() {
		return variableType;
	}

	public void setVariableType(VariableType variableType) {
		this.variableType = variableType;
	}

	public boolean isArray() {
		return isArray;
	}

	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}

	@Override
	public String toString() {
		return "Token [symble=" + symble + ",\t value=" + value + ",\t lineNum=" + lineNum + ",\t variableType="
				+ variableType + "]";
	}

	
	

}
