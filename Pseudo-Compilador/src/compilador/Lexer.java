package compilador;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class Lexer implements TokenInfo{

//	public static void main(String[] args) {
//		 if (args.length != 1) {
//	            System.out.println("No hay archivo que leer...");
//	            return;
//	    }
//		// Leer Script de Pseudocodigo ----------------------------------------------------- 
//		String cadena = readFile(args[0]);
//		System.out.println(cadena);
//		
//		// Imprimir tokens por nombre y tipo -----------------------------------------------------
//		List<Token> tokens = tokenize(cadena);
//		for(int i = 0; i < tokens.size(); i++) {
//            System.out.println(i+"	Token: "+tokens.get(i).getText()+ "		Tipo: "+tokens.get(i).getToken());
//        }
//
//	}
	// Metodo para leer script de pseudocodigo -----------------------------------------------------
	public static String readFile(String path) {
        try {
            FileInputStream stream = new FileInputStream(path);
            
            try {
                InputStreamReader input = new InputStreamReader(stream, Charset.defaultCharset());
                Reader reader = new BufferedReader(input);
                
                StringBuilder builder = new StringBuilder();
                char[] buffer = new char[8192];
                int read;
                
                while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                    builder.append(buffer, 0, read);
                }
                
              
                builder.append("\n");
                
                return builder.toString();
            } finally {
                stream.close();
            }
        } catch (IOException ex) {
            return null;
        }
    }

// Tokenizing (lexing) -----------------------------------------------------
    
  
    public static List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<Token>();
        
        String token = "";
        TokenizeState state = TokenizeState.DEFAULT;
        
       
        String charTokens = "+-*/%#=<>";
        TokenType[] tokenTypes = {
            TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
            TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
            TokenType.EQUALS,   TokenType.OPERATOR, TokenType.OPERATOR
        };
        // Arreglo de palabras reserverdas del pseudocodigo
        String[] stringTokens = {"INICIO-DE-PROGRAMA", "FIN-DE-PROGRAMA",
                "LEER", "ESCRIBIR", "SI", "ENTONCES", "MIENTRAS", 
                "INICIO", "FIN"};
    
        TokenType[] stringTokenTypes = {TokenType.INICIOPROG, TokenType.FINPROG,
        		TokenType.LEER, TokenType.ESCRIBIR, TokenType.SI, TokenType.ENTONCES, TokenType.MIENTRAS,
        		TokenType.INICIO, TokenType.FIN};
        // Scan through the code one character at a time, building up the list
        // of tokens.
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (state) {
            case DEFAULT:
                if (charTokens.indexOf(c) != -1) {
                	if(charTokens.indexOf(c) > 5){
                		token += c;
                		state = TokenizeState.OP_RELACIONAL;
                	}
                	else{
                        tokens.add(new Token(Character.toString(c), tokenTypes[charTokens.indexOf(c)]));
                	}
                } else if (Character.isLetter(c)) {
                    token += c;
                    state = TokenizeState.WORD;
                } else if (Character.isDigit(c)) {
                    token += c;
                    state = TokenizeState.NUMBER;
                } else if (c == '"') {
                    state = TokenizeState.STRING;
                } else if (c == '@') {
                    state = TokenizeState.COMMENT;
                }
                break;
                
            case OP_RELACIONAL:
            	if(c == '='){
            		token += c;
            		tokens.add(new Token(token,TokenType.OP_RELACIONAL));           		
            	}else{
            		if(token.equals("=")){
            			tokens.add(new Token(token,TokenType.EQUALS));           			
            		}else{
            			tokens.add(new Token(token,TokenType.OP_RELACIONAL));
            		}
            	}
            	token = "";
        		state = TokenizeState.DEFAULT;
                //i--; // Reprocess this character in the default state.
            	break;
            	
            case WORD:
                if (Character.isLetterOrDigit(c) || c=='-') {
                    token += c;
                } else{
                	//Comparar con stringTokens
                	boolean found=false;
                	for(int j = 0; j < stringTokens.length; j++){
                		if (token.toUpperCase().equals(stringTokens[j])){
                			tokens.add(new Token(token, stringTokenTypes[j]));
                			found=true;
                			break;
                		}
                	}
                	if(!found)
                		tokens.add(new Token(token, TokenType.IDENTIFICADOR));
                    token = "";
                    state = TokenizeState.DEFAULT;
                    i--; // Reprocess this character in the default state.
                }

                break;
                
            case NUMBER:     	
                if(Character.isDigit(c)){
                	token += c;
                }
                else if (c=='.') {
                    token += c;
                    state = TokenizeState.FLOAT;
                } else {
                    //tokens.add(new Token(token, TokenType.NUMBER));
                	System.out.println("ERROR: No se aceptan Enteros!, en: "+ token);
                    token = "";
                    state = TokenizeState.DEFAULT;
                    i--; // Reprocess this character in the default state.
                }
                break;
            case FLOAT:
            	if (Character.isDigit(c)) {
                    token += c;
                } else {
                    tokens.add(new Token(token, TokenType.FLOAT));
                    token = "";
                    state = TokenizeState.DEFAULT;
                    i--; // Reprocess this character in the default state.
                }
            	break;
            case STRING:
                if (c == '"') {
                    tokens.add(new Token(token, TokenType.STRING));
                    token = "";
                    state = TokenizeState.DEFAULT;
                } else {
                    token += c;
                }
                break;
                
            case COMMENT:
                if (c == '\n') {
                    state = TokenizeState.DEFAULT;
                }
                break;
            }
        }       
     
        return tokens;
    }
	
	// Token data --------------------------------------------------------------

   
    // public static enum TokenType {
    //     WORD, NUMBER, FLOAT, STRING, LINE,
    //     EQUALS, OPERATOR, EOF, 
    //     INICIOPROG, FINPROG, OP_RELACIONAL,
    //     IDENTIFICADOR, LEER, ESCRIBIR, SI,
    //     ENTONCES, MIENTRAS, INICIO, FIN
    // }
    
    
    public static class Token {

        public Token(String text, TokenType type) {
            this.text = text;
            this.type = type;
        }
        public final String text;
        public final TokenType type;
        
        public TokenType getToken(){
        	return type;
        }
        public String getText(){
        	return text;
        }

    }
    
    
    // private enum TokenizeState {
    //     DEFAULT, WORD, NUMBER, FLOAT, STRING, OP_RELACIONAL, COMMENT
    // }

}
