package compilador;

public class TipoValor {
	//--------Tipos de valor------------------
		public static class ValorNumerico implements Valor {
			private final float valor;
			public ValorNumerico(float valor){
				this.valor = valor;
			}
			@Override
			public String toString(){return Float.toString(valor);}
			public float toNumber(){return valor;}
			public Valor evaluar(){return this;}
			
		}
		
		public static class ValorString implements Valor {
			private final String valor;
			
			public ValorString (String valor) {
				this.valor = valor;
			}
			
			@Override
			public String toString() {return valor;}
			public float toNumber() { return Float.parseFloat(valor); }
	        public Valor evaluar() { return this; }
		}
		
		
}
