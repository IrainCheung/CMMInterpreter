package error;

public class Error {

	
	public enum ErrorDescription{

		LPAREN_LACK("a ( symbol was need"),
		RPAEN_LACK("a ) symbol was need"),
		LBRACKET_LACK("a [ symbol was need"),
		RBRACKET_LACK("a ] symbol was need"),
		LBRACE_LACK("a { symbol was need"),
		RBRACE_LACK("a } symbol was need"),
		SEMICO_LACK("a ; symbol was need"),
		ASSIGN_LACK("a = symbol was need"),
		COMPARE_SYM_LACK("a compare operator was need"),
		IDENTIFIER_LACK("Variable name is not standardized,or an identifier was need"),
		UNKNOWN_GRAMMER("unknown word"),
		UNKNOWN_TYPE("unknown type"),
		ADD_MINUS_LACK("a + or - symbol was need"),
		MULTI_DIVID_LACK("a * or / symbol was need"),
		NOT_A_NUMBER("not a number"),
		ARRAY_INDEX_ERROR("array index is not a integer or array index is minus"),
		VARIABLE_NOT_DECLARE("variable is not declared"),
		VARIABLE_TYPE_ERROR("variable type error"),
		VARIABLE_DUPLICATE_DECLARE("variable duplicate declare"),
		VARIABLE_NAME_ERROR("Variable name is not standardized");
		private String description;
		private ErrorDescription(String description) {
			this.description=description;
		}
		
		public String getDescripton() {
			return description;
		}
		
	}
	
	private ErrorDescription errorDescription;
	private int lineNum;
	public Error(ErrorDescription errorDescription, int lineNum) {
		super();
		this.errorDescription = errorDescription;
		this.lineNum = lineNum;
	}
	public ErrorDescription getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(ErrorDescription errorDescription) {
		this.errorDescription = errorDescription;
	}
	public int getLineNum() {
		return lineNum;
	}
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	@Override
	public String toString() {
		return "Error [errorDescription=" + errorDescription.getDescripton() + ", lineNum=" + lineNum + "]";
	}
	
	
	
}
