package compilador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import textEditor.TextEditor;
import compilador.Lexer.Token;
import compilador.TipoValor.*;

public class Parser implements TokenInfo {
	
	private List<Token> tokens;
	private int pos, aux, aux2; //�ndice para la posici�n del tokenList
	private String errorType = "Error de sintaxis encontrado! se esperaba ";
	
	//---------Lista de sentencias---------
	List<Sentencia> sentencias = new ArrayList<Sentencia>();
	//---------diccionario de variables----
	private final Map<String, Valor> variables = new LinkedHashMap<String, Valor>();
	//--------diccionario o maoa de etiquetas (indice de cada sentencia)
	private final Map<String, Integer> etiquetas = new LinkedHashMap<String, Integer>();
	//-----------sentencias actual--------
	private int sentenciaActual=0;
	
	//-----------indices de salto para sentencias SI y Mientras------
	private int labelIndex = 0;
	private int brinco = 0;
	private int brincoMientras = 0;
	
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
		String label;
	
		if(getType(pos++).equals(TokenType.INICIO)) {
			label = "Inicio"+labelIndex;
			etiquetas.put(label, sentencias.size() - 1);
			
			while(!getType(pos).equals(TokenType.FIN)) {
				if(!sentencia()) {
					pos = aux;
					return false;
				}
			}
			//Se coloca una etiqueta a la siguiente sentencia despues del token FIN
			label = "Fin"+labelIndex++;
			etiquetas.put(label, sentencias.size());
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
		String name = "";
		Expresion value;
		
		aux = pos;
		if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
			if(getType(pos++).equals(TokenType.EQUALS)) {
				aux2 = pos;
				if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
					if(getType(pos++).equals(TokenType.OPERATOR))
						if(getType(pos++).equals(TokenType.FLOAT)){
							name = tokens.get(aux).getText();
							value = new ExpresionOperador(new ValorNumerico(Float.parseFloat(tokens.get(pos - 1).getText())), new ExpresionVariable(tokens.get(pos - 3).getText()),tokens.get(pos-2).getText());
							sentencias.add(new SentenciaAsignacion(name,value));
							return true;
						}
				}
				pos = aux2;
				if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
					if(getType(pos++).equals(TokenType.OPERATOR))
						if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
							name = tokens.get(aux).getText();
							value = new ExpresionOperador(new ExpresionVariable(tokens.get(pos - 1).getText()), new ExpresionVariable(tokens.get(pos - 3).getText()),tokens.get(pos-2).getText());
							sentencias.add(new SentenciaAsignacion(name,value));
							return true;
						}
				}
				pos = aux2;
				if(getType(pos++).equals(TokenType.FLOAT)) {
					//agregar nueva sentencia de asignación
					name = tokens.get(aux).getText();
					value = expresion();
					sentencias.add(new SentenciaAsignacion(name,value)); //variable y valor float
					return true;
				}
				pos = aux2;
				if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {

					name = tokens.get(aux).getText();
					value = expresion();
					sentencias.add(new SentenciaAsignacion(name,value)); //variable y valor float
					return true;
				}
			}
		}
		pos = aux;
		return false;
	}
	
	
	private boolean leer() {
		aux = pos;
		if(getType(pos++).equals(TokenType.LEER)) {
			if(getType(pos++).equals(TokenType.IDENTIFICADOR)) {
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
		Expresion condicion;
		aux = pos;
		if(getType(pos++).equals(TokenType.MIENTRAS)) {
			if(getType(pos).equals(TokenType.IDENTIFICADOR)) {
				pos++;
				if(getType(pos++).equals(TokenType.OP_RELACIONAL)) {
					if(getType(pos).equals(TokenType.IDENTIFICADOR)){
							pos++;
							condicion = new ExpresionOperador(new ExpresionVariable(tokens.get(pos - 1).getText()), new ExpresionVariable(tokens.get(pos - 3).getText()),tokens.get(pos-2).getText());
							sentencias.add(new SentenciaMientras(condicion,"Fin"+brinco++));
							if(bloque()) {
								sentencias.add(new SentenciaCheckMientras(condicion,"Inicio"+brincoMientras++));
								return true;
							}
					}
					if(getType(pos).equals(TokenType.FLOAT)){
						pos++;
						condicion = new ExpresionOperador(new ValorNumerico(Float.parseFloat(tokens.get(pos - 1).getText())), new ExpresionVariable(tokens.get(pos - 3).getText()),tokens.get(pos-2).getText());
						sentencias.add(new SentenciaMientras(condicion,"Fin"+brinco++));
						if(bloque()) {
							sentencias.add(new SentenciaCheckMientras(condicion,"Inicio"+brincoMientras++));
							return true;
						}
					}
					
				}
			}	
			if(getType(pos).equals(TokenType.FLOAT)) {
				pos++;
				if(getType(pos++).equals(TokenType.OP_RELACIONAL)) {
					if(getType(pos).equals(TokenType.IDENTIFICADOR)){
							pos++;
							condicion = new ExpresionOperador(new ExpresionVariable(tokens.get(pos - 1).getText()), new ValorNumerico(Float.parseFloat(tokens.get(pos - 3).getText())),tokens.get(pos-2).getText());
							sentencias.add(new SentenciaMientras(condicion,"Fin"+brinco++));
							if(bloque()) {
								sentencias.add(new SentenciaCheckMientras(condicion,"Inicio"+brincoMientras++));
								return true;
							}
					}
					if(getType(pos).equals(TokenType.FLOAT)){
						pos++;
						condicion = new ExpresionOperador(new ValorNumerico(Float.parseFloat(tokens.get(pos - 1).getText())), new ValorNumerico(Float.parseFloat(tokens.get(pos - 3).getText())),tokens.get(pos-2).getText());
						sentencias.add(new SentenciaMientras(condicion,"Fin"+brinco++));
						if(bloque()) {
							sentencias.add(new SentenciaCheckMientras(condicion,"Inicio"+brincoMientras++));
							return true;
						}
					}
					
				}
			}	
		}
		pos = aux;
		return false;
	}
	private boolean si() {
		Expresion condicion;
		aux = pos;
		if(getType(pos++).equals(TokenType.SI)) {
			if(getType(pos).equals(TokenType.IDENTIFICADOR) || getType(pos).equals(TokenType.FLOAT)){
				pos++;
				if(getType(pos++).equals(TokenType.OP_RELACIONAL))
					if(getType(pos).equals(TokenType.IDENTIFICADOR) || getType(pos).equals(TokenType.FLOAT)){
						pos++;
						if(getType(pos++).equals(TokenType.ENTONCES)) {
							
							if(getType(pos - 4).equals(TokenType.IDENTIFICADOR) && getType(pos - 2).equals(TokenType.IDENTIFICADOR)){
								condicion = new ExpresionOperador(new ExpresionVariable(tokens.get(pos - 2).getText()), new ExpresionVariable(tokens.get(pos - 4).getText()),tokens.get(pos-3).getText());
								sentencias.add(new SentenciaSi(condicion,"Fin"+brinco++));		
							}
							else if(getType(pos - 4).equals(TokenType.IDENTIFICADOR) && getType(pos - 2).equals(TokenType.FLOAT)){
								condicion = new ExpresionOperador(new ValorNumerico(Float.parseFloat(tokens.get(pos - 2).getText())), new ExpresionVariable(tokens.get(pos - 4).getText()),tokens.get(pos-3).getText());
								sentencias.add(new SentenciaSi(condicion,"Fin"+brinco++));		
							}
							else if(getType(pos - 4).equals(TokenType.FLOAT) && getType(pos - 2).equals(TokenType.FLOAT)){
								condicion = new ExpresionOperador(new ValorNumerico(Float.parseFloat(tokens.get(pos - 2).getText())), new ValorNumerico(Float.parseFloat(tokens.get(pos - 4).getText())),tokens.get(pos-3).getText());
								sentencias.add(new SentenciaSi(condicion,"Fin"+brinco++));		
							}
							else if(getType(pos - 4).equals(TokenType.FLOAT) && getType(pos - 2).equals(TokenType.IDENTIFICADOR)){
								condicion = new ExpresionOperador(new ExpresionVariable(tokens.get(pos - 2).getText()),new ValorNumerico(Float.parseFloat(tokens.get(pos - 4).getText())),tokens.get(pos-3).getText());
								sentencias.add(new SentenciaSi(condicion,"Fin"+brinco++));		
							}	
							if(bloque()) {
								return true;
							}	
						}
					}
			}
			
		}
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
            TextEditor.textArea.append(expresion.evaluar().toString() + "\n");
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
			variables.put(name, valor.evaluar());
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
			
			while(input == null || input.isEmpty())
				input = JOptionPane.showInputDialog("Introduce Valor valido para "+ name);
			
			try {
                float value = Float.parseFloat(input);
                variables.put(name, new ValorNumerico(value));
            } catch (NumberFormatException e) {
                variables.put(name, new ValorString(input));
            }
		}
		
	}
	
	public class SentenciaSi implements Sentencia {
		private final Expresion expresion;
		private final String label;
		
		public SentenciaSi (Expresion expresion, String label) {
			this.expresion = expresion;
			this.label = label;
		}
		@Override
		public void ejecutar() {
			if(etiquetas.containsKey(label)){
				float valor = expresion.evaluar().toNumber();
				if (valor != 1){
					System.out.println("False");
					sentenciaActual=etiquetas.get(label).intValue();
				}
				else
					System.out.println("True");
			}
		}		
	}
	
	public class SentenciaMientras implements Sentencia {
		private final Expresion expresion;
		private final String label;
		
		public SentenciaMientras (Expresion expresion, String label) {
			this.expresion = expresion;
			this.label = label;
		}
		@Override
		public void ejecutar() {
			if(etiquetas.containsKey(label)){
				float valor = expresion.evaluar().toNumber();				
				if (valor != 1){
					System.out.println("False");
					sentenciaActual=etiquetas.get(label).intValue();
				}
				else{
					System.out.println("True");
				}										
			}
		}
	}
	
	public class SentenciaCheckMientras implements Sentencia{
		private final Expresion expresion;
		private final String label;
		public SentenciaCheckMientras(Expresion expresion, String label){
			this.expresion = expresion;
			this.label = label;
		}
		@Override
		public void ejecutar() {
			if(etiquetas.containsKey(label)){
				float valor = expresion.evaluar().toNumber();
				if(valor != 0){
					sentenciaActual = etiquetas.get(label).intValue();
				}
			}
		}
	}
	
//------------Tipos de expresiones posibles-------------	
	public class ExpresionVariable implements Expresion {
		private final String name;
		public ExpresionVariable(String name) {
			this.name = name;
		}
		public Valor evaluar(){
			if(variables.containsKey(name)){
				return variables.get(name);
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
			case "%": 
				return new ValorNumerico(izqVal.toNumber() % derVal.toNumber());
			case "==":
				if (izqVal instanceof ValorNumerico) {
					return new ValorNumerico((izqVal.toNumber() == derVal.toNumber())? 1:0);
				}
				else
					return new ValorNumerico(izqVal.toString().equals(derVal.toString())? 1:0);
			case "#":
				if (izqVal instanceof ValorNumerico) {
					return new ValorNumerico((izqVal.toNumber() == derVal.toNumber())? 0:1);
				}
				else
					return new ValorNumerico(izqVal.toString().equals(derVal.toString())? 0:1);
			case "<=":
				if (izqVal instanceof ValorNumerico) {
					return new ValorNumerico((izqVal.toNumber() <= derVal.toNumber())? 1:0);
				}
				else
					return new ValorNumerico((izqVal.toString().compareTo(derVal.toString()) < 0)? 1:0);
			case ">=":
				if (izqVal instanceof ValorNumerico) {
					return new ValorNumerico((izqVal.toNumber() >= derVal.toNumber())? 1:0);
				}
				else
					return new ValorNumerico((izqVal.toString().compareTo(derVal.toString()) > 0)? 1:0);
			case "<":
				if (izqVal instanceof ValorNumerico) {
					return new ValorNumerico((izqVal.toNumber() < derVal.toNumber())? 1:0);
				}
				else
					return new ValorNumerico((izqVal.toString().compareTo(derVal.toString()) < 0)? 1:0);
			case ">":
				if (izqVal instanceof ValorNumerico) {
					return new ValorNumerico((izqVal.toNumber() > derVal.toNumber())? 1:0);
				}
				else
					return new ValorNumerico((izqVal.toString().compareTo(derVal.toString()) > 0)? 1:0);
			}
			throw new Error ("Operador Desconocido:" + operador);
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
	public Map<String, Valor> getVariables(){
		return variables;
	}
//-------------Obtener Etiquetas de sentencias--------
	public Map<String, Integer> getEtiquetas(){
		return etiquetas;
	}
//------------obtener Sentencia actual----------
	public int getSentenciaActual(){
		return sentenciaActual;
	}
//------------Interpretar------------
	public void interpretar () {
		if (sentencias.isEmpty()){
			TextEditor.textArea.setText("No hay sentencias que ejecutar!\n");
			return;
		}
		sentenciaActual = 0;
		while (sentenciaActual < sentencias.size()) {
            int thisStatement = sentenciaActual;
            sentenciaActual++;
            sentencias.get(thisStatement).ejecutar();
        }
	}
}
