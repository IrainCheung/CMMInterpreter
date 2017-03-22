package intermediatecode;

public enum Code{
	
	JUMP("jump"),
	IN("in"),
	OUT("out"),
	INT("int"),
	REAL("real"),
	READ("read"),
	WRITE("write"),
	ASSIGN("assign"),
	EQ("="),MO(">"),MOEQ(">="),LS("<"),LSEQ("<="),NEQ("!="),ASSIG("="),PLUS("+"),MINUS("-"),MULTI("*"),DIV("/")
	
	;
	
	private String value;
	private Code(String value){
		this.value=value;
	}
	public String getValue() {
		return value;
	}
	
}