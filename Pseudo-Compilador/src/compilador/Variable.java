package compilador;

public class Variable {

	String nombre = "";
	Float valor = null;
	
	public Variable(String nombre, float valor) {
		this.nombre = nombre;
		this.valor = valor;
	}

	public Variable(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Float getValor() {
		return valor;
	}

	public void setValor(Float valor) {
		this.valor = valor;
	}
	
	
}
