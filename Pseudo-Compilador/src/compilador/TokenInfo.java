package compilador;

public interface TokenInfo {

	 enum TokenType {
	        WORD, NUMBER, FLOAT, STRING, LINE,
	        EQUALS, OPERATOR, EOF, 
	        INICIOPROG, FINPROG, OP_RELACIONAL,
	        IDENTIFICADOR, LEER, ESCRIBIR, SI,
	        ENTONCES, MIENTRAS, INICIO, FIN
	    }
	 
	 enum TokenizeState {
	        DEFAULT, WORD, NUMBER, FLOAT, STRING, OP_RELACIONAL, COMMENT
	    }
	 
	    
}
