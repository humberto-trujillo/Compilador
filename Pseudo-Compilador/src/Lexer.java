import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class Lexer {

	public static void main(String[] args) {
		 if (args.length != 1) {
	            System.out.println("No hay archivo que leer...");
	            return;
	    }
		// Leer Script de Pseudocodigo ----------------------------------------------------- 
		String cadena = readFile(args[0]);
		System.out.println(cadena);
		
		// Imprimir tokens por nombe y tipo -----------------------------------------------------
		List<Token> tokens = tokenize(cadena);
		for(int i = 0; i < tokens.size(); i++) {
            System.out.println("Token: "+tokens.get(i).getText()+ ", Tipo: "+tokens.get(i).getToken());
        }

	}
	// Metodo para leer script de pseudocodigo -----------------------------------------------------
	private static String readFile(String path) {
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
                
                // HACK: The parser expects every statement to end in a newline,
                // even the very last one, so we'll just tack one on here in
                // case the file doesn't have one.
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
    
    /**
     * This function takes a script as a string of characters and chunks it into
     * a sequence of tokens. Each token is a meaningful unit of program, like a
     * variable name, a number, a string, or an operator.
     */
    private static List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<Token>();
        
        String token = "";
        TokenizeState state = TokenizeState.DEFAULT;
        
        // Many tokens are a single character, like operators.
        String charTokens = "\n=+-*/%<>#";
        TokenType[] tokenTypes = { TokenType.LINE, TokenType.EQUALS,
            TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
            TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
            TokenType.OPERATOR, TokenType.OPERATOR
        };

        // Scan through the code one character at a time, building up the list
        // of tokens.
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (state) {
            case DEFAULT:
                if (charTokens.indexOf(c) != -1) {
                    tokens.add(new Token(Character.toString(c), tokenTypes[charTokens.indexOf(c)]));
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
                
            case WORD:
                if (Character.isLetterOrDigit(c)) {
                    token += c;
                } else {
                    tokens.add(new Token(token, TokenType.WORD));
                    token = "";
                    state = TokenizeState.DEFAULT;
                    i--; // Reprocess this character in the default state.
                }
                break;
                
            case NUMBER:
                // HACK: Negative numbers and floating points aren't supported.
                // To get a negative number, just do 0 - <your number>.
                // To get a floating point, divide.
                if (Character.isDigit(c)) {
                    token += c;
                } else {
                    tokens.add(new Token(token, TokenType.NUMBER));
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
        // HACK: Silently ignore any in-progress token when we run out of
        // characters. This means that, for example, if a script has a string
        // that's missing the closing ", it will just ditch it.
        return tokens;
    }
	
	// Token data --------------------------------------------------------------

    /**
     * This defines the different kinds of tokens or meaningful chunks of code
     * that the parser knows how to consume. These let us distinguish, for
     * example, between a string "foo" and a variable named "foo".
     * 
     * HACK: A typical tokenizer would actually have unique token types for
     * each keyword (print, goto, etc.) so that the parser doesn't have to look
     * at the names, but Jasic is a little more crude.
     */
    private enum TokenType {
        WORD, NUMBER, STRING, LINE,
        EQUALS, OPERATOR, EOF, OP_RELACIONAL,
        IDENTIFICADOR, LEER, ESCRIBIR, SI,
        ENTONCES, MIENTRAS, INICIO, FIN
    }
    
    /**
     * This is a single meaningful chunk of code. It is created by the tokenizer
     * and consumed by the parser.
     */
    private static class Token {

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
    
    /**
     * This defines the different states the tokenizer can be in while it's
     * scanning through the source code. Tokenizers are state machines, which
     * means the only data they need to store is where they are in the source
     * code and this one "state" or mode value.
     * 
     * One of the main differences between tokenizing and parsing is this
     * regularity. Because the tokenizer stores only this one state value, it
     * can't handle nesting (which would require also storing a number to
     * identify how deeply nested you are). The parser is able to handle that.
     */
    private enum TokenizeState {
        DEFAULT, WORD, NUMBER, STRING, COMMENT
    }

}
