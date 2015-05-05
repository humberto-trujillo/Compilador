package compilador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import compilador.Lexer.Token;

public class Parser implements TokenInfo {
	
	private List<Token> tokens;
	private List<Variable> variables = new ArrayList<Variable>();
	private int pos, aux, aux2; //�ndice para la posici�n del tokenList
	private boolean varAdd = false;
	private String errorType = "Error de sintaxis encontrado! se esperaba ";
	
	//---------Lista de sentencias---------
	List<Sentencia> sentencias = new ArrayList<Sentencia>();
	//---------diccionario de variables----
	private final Map<String, Valor> variables2 = new LinkedHashMap<String, Valor>();
	
	
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
	
	//Este m�todo checa todas las sentencias del programa
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
		if(escribir()){
			
			return true;
		}
			
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
					//agregar nueva sentencia de asignación
					String name = tokens.get(aux).getText();
					Expresion value = expresion();
					sentencias.add(new SentenciaAsignacion(name,value)); //variable y valor float
					return true;
				}
				pos = aux2;
				if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
					if(!varAdd || !checaRepetido(aux)) {
						
						variables.add(new Variable(tokens.get(aux).getText()));
						varAdd = true;
					}
					String name = tokens.get(aux).getText();
					Expresion value = expresion();
					sentencias.add(new SentenciaAsignacion(name,value)); //variable y valor float
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
				sentencias.add(new SentenciaLeer(tokens.get(pos - 1).getText()));
				return true;
			}
		}
		pos = aux;
		return false;
	}
	
	private boolean escribir() {
		aux = pos;
		if(getType(pos++).equals(TokenType.ESCRIBIR)) {
			if(getType(pos).equals(TokenType.IDENTIFICADOR) || getType(pos).equals(TokenType.STRING)) {
				pos++;
				sentencias.add(new SentenciaEscribir(expresion()));
			}
				
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
//----------------Tipos de sentencias---------
	public class SentenciaEscribir implements Sentencia {
        private final Expresion expresion;
        
        public SentenciaEscribir(Expresion expresion) {
            this.expresion = expresion;
        }       
        public void ejecutar() {
            System.out.println(expresion.evaluar().toString());
        }
    }
	
	public class SentenciaAsignacion implements Sentencia {
		private final String name;
		private final Expresion valor;
		public SentenciaAsignacion(String name, Expresion valor){
			this.name = name;
			this.valor = valor;
		}
		public void ejecutar(){
			variables2.put(name, valor.evaluar());
		}
	}
	
	public class SentenciaLeer implements Sentencia {
		private final String name;
		public SentenciaLeer (String name) {
			this.name=name;
		}
		@Override
		public void ejecutar() {
			String input = JOptionPane.showInputDialog("Introduce Valor para "+ name);
			try {
                float value = Float.parseFloat(input);
                variables2.put(name, new ValorNumerico(value));
            } catch (NumberFormatException e) {
                variables2.put(name, new ValorString(input));
            }
		}
		
	}
	//--------Tipos de valor------------------
	public class ValorNumerico implements Valor {
		private final float valor;
		public ValorNumerico(float valor){
			this.valor = valor;
		}
		@Override
		public String toString(){return Float.toString(valor);}
		public float toNumber(){return valor;}
		public Valor evaluar(){return this;}
		
	}
	
	public class ValorString implements Valor {
		private final String valor;
		
		public ValorString (String valor) {
			this.valor = valor;
		}
		
		@Override
		public String toString() {return valor;}
		public float toNumber() { return Float.parseFloat(valor); }
        public Valor evaluar() { return this; }
	}
//------------Tipos de expresiones posibles-------------	
	public class ExpresionVariable implements Expresion {
		private final String name;
		public ExpresionVariable(String name) {
			this.name = name;
		}
		public Valor evaluar(){
			if(variables2.containsKey(name)){
				return variables2.get(name);
			}
			return new ValorNumerico(0);
		}
	}
	
	public class ExpresionOperador implements Expresion {
		private final Expresion derecha;
		private final Expresion izquierda;
		private final String operador;
		
		public ExpresionOperador (Expresion derecha, Expresion izquierda, String operador) {
			this.derecha = derecha;
			this.izquierda = izquierda;
			this.operador = operador;
		}
		@Override
		public Valor evaluar() {
			Valor izqVal = izquierda.evaluar();
			Valor derVal = derecha.evaluar();
			
			switch(operador){
			case "+":
				if (izqVal instanceof ValorNumerico){
					return new ValorNumerico(izqVal.toNumber() + derVal.toNumber());
				}
				else
					return new ValorString(izqVal.toString() + derVal.toString());
			case "-":
				
				return new ValorNumerico(izqVal.toNumber() - derVal.toNumber());
				
			case "*":
				return new ValorNumerico(izqVal.toNumber() * derVal.toNumber());
			case "/":	
				return new ValorNumerico(izqVal.toNumber() / derVal.toNumber());
			case "==":
				if (izqVal instanceof ValorNumerico) {
					return new ValorNumerico((izqVal.toNumber() == derVal.toNumber())? 1:0);
				}
				else
					return new ValorNumerico(izqVal.toString().equals(derVal.toString())? 1:0);
			case "<=":
				if (izqVal instanceof ValorNumerico) {
					return new ValorNumerico((izqVal.toNumber() <= derVal.toNumber())? 1:0);
				}
				else
					return new ValorNumerico((izqVal.toString().compareTo(derVal.toString()) < 0)? 1:0);
			case ">=":	
			}
			throw new Error ("Operador Desconocido!");
		}	
		
	}
	
//---------Evaluar Expresiones------------
	
	private Expresion expresion(){
		return operador();
	}
	
	private Expresion operador(){
		Expresion expresion = atomico();
		return expresion;
	}
	
	private Expresion atomico(){
		if(getType(pos - 1).equals(TokenType.IDENTIFICADOR)){
			return new ExpresionVariable(tokens.get(pos - 1).getText());
		}
		else if(getType(pos - 1).equals(TokenType.FLOAT)){
			return new ValorNumerico(Float.parseFloat(tokens.get(pos - 1).getText()));
		}
		//si no es ID o Float, entonces es String
		else
			return new ValorString(tokens.get(pos - 1).getText());
	}
	
//-------------Obtener Lista de sentencias-----------
	public List<Sentencia> getSentencias(){
		return sentencias;
	}
//-------------Obtener Lista de variables-----------
	public Map<String, Valor> getVariables2(){
		return variables2;
	}
}
