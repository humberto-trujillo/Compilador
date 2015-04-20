package compilador;

public class Variable {

	String nombre = "";
	Float valor = null;
	
	public Variable(String nombre, float valor) {
		this.nombre = nombre;
		this.valor = valor;
	}

	public Variable(String nombre) {
		super();
		this.nombre = nombre;
	}
	
}
