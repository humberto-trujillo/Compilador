package compilador;

import java.util.ArrayList;
import java.util.List;

import compilador.Lexer.Token;

public class Parser implements TokenInfo {
	
	private List<Token> tokens;
	private List<Variable> variables = new ArrayList<Variable>();
	private int pos, aux, aux2; //índice para la posición del tokenList
	private boolean varAdd = false;
	private String errorType = "Error de sintaxis encontrado! se esperaba ";
	
	public Parser (List<Token> tokens) {
		this.tokens = tokens;
		pos = aux = aux2 = 0;
	}
	
	public Boolean programa() {
		if(!inicioDePrograma())
			return false;
		if(!listaSentencias())
			return false;
		return true;
	}
	
	//Este método checa todas las sentencias del programa
	private boolean listaSentencias() {
		aux = pos;
		while(!getType(pos).equals(TokenType.FINPROG)) {
				if(!sentencia()) {
					print("Sentencia no valida");
					pos = aux;
					return false;
				}
		}
		return true;
	}
	
	private boolean bloque() {
		aux = pos;
		if(getType(pos++).equals(TokenType.INICIO)) {
			while(!getType(pos).equals(TokenType.FIN)) {
				if(!sentencia()) {
					pos = aux;
					return false;
				}
			}
			pos++;
			return true;
		}
		pos = aux;
		return false;
	}
	
	
	private boolean sentencia() {
		if(leer())
			return true;
		if(escribir())
			return true;
		if(asignacion())
			return true;
		if(si())
			return true;
		if(mientras())
			return true;
		
		return false;
	}
	
	private boolean asignacion() {
		aux = pos;
		if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
			if(getType(pos++).equals(TokenType.EQUALS)) {
				aux2 = pos;
				if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
					if(getType(pos++).equals(TokenType.OPERATOR))
						if(getType(pos++).equals(TokenType.FLOAT)){
							if(!varAdd || !checaRepetido(aux)) {
								variables.add(new Variable(tokens.get(aux).getText()));
								varAdd = true;
							}
							return true;
						}
				}
				pos = aux2;
				if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
					if(getType(pos++).equals(TokenType.OPERATOR))
						if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
							if(!varAdd || !checaRepetido(aux)) {
								variables.add(new Variable(tokens.get(aux).getText()));
								varAdd = true;
							}
							return true;
						}
				}
				pos = aux2;
				if(getType(pos++).equals(TokenType.FLOAT)) {
					if(!varAdd || !checaRepetido(aux)) {
						variables.add(new Variable(tokens.get(aux).getText()));
						varAdd = true;
					}
					return true;
				}
				pos = aux2;
				if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
					if(!varAdd || !checaRepetido(aux)) {
						variables.add(new Variable(tokens.get(aux).getText()));
						varAdd = true;
					}
					return true;
				}
			}
		}
		pos = aux;
		return false;
	}
	
	private boolean checaRepetido(int i) {
		for(int j = 0; j < variables.size(); j++)
			if(variables.get(j).getNombre().equals(tokens.get(i).getText())) {
				return true;
			}
		return false;
	}
	
	private boolean leer() {
		aux = pos;
		if(getType(pos++).equals(TokenType.LEER)) {
			if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
				if(!varAdd || !checaRepetido(pos-1)) {
					variables.add(new Variable(tokens.get(pos-1).getText()));
					varAdd = true;
				}
				return true;
			}
		}
		pos = aux;
		return false;
	}
	
	private boolean escribir() {
		aux = pos;
		if(getType(pos++).equals(TokenType.ESCRIBIR)) {
			if(getType(pos).equals(TokenType.IDENTIFICADOR) || getType(pos).equals(TokenType.STRING))
				pos++;
				return true;
		}
		pos = aux;
		return false;
	}
	
	private boolean mientras() {
		aux = pos;
		if(getType(pos++).equals(TokenType.MIENTRAS)) {
			if(condicion())
				if(bloque() || sentencia())
				return true;
		}
		pos = aux;
		return false;
	}
	
	private boolean si() {
		aux = pos;
		if(getType(pos++).equals(TokenType.SI)) {
			if(condicion())
				if(getType(pos++).equals(TokenType.ENTONCES))
					if(bloque() || sentencia()) {
						return true;
					}
		}
		pos = aux;
		return false;
	}
	
	private boolean condicion() {
		aux = pos;
		if(getType(pos++).equals(TokenType.IDENTIFICADOR))
			if(getType(pos++).equals(TokenType.OP_RELACIONAL))
				if(getType(pos++).equals(TokenType.IDENTIFICADOR))
					return true;
		pos = aux;		
		return false;
	}
	
	private boolean inicioDePrograma() {
		if(getType(pos).equals(TokenType.INICIOPROG)) {
			pos++;
			return true;
		}
		Mensajes.despliegaError(errorType + "Inicio de programa");
		return false;
	}
	
	private TokenType getType(int i) {
		try{
			return tokens.get(i).getToken();
		}
		catch(IndexOutOfBoundsException e) {
			return TokenType.EOF;
		}
	}
	
	public List<Variable> getVariables() {
		return variables;
	}
	
	private void print(String s) {
		System.out.println(s);
	}
	
}
