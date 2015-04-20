package compilador;

import javax.swing.JOptionPane;

public class Mensajes extends JOptionPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void despliegaMensaje( String titulo, String mensaje ){
		showMessageDialog( null, mensaje, titulo, PLAIN_MESSAGE );
	}
	
	public static void despliegaMensaje( String mensaje ){
		showMessageDialog( null, mensaje, "Mensaje", PLAIN_MESSAGE );
	}
	
	public static void despliegaAlerta( String mensaje ){
		showMessageDialog( null, mensaje, "\u00a1Alerta!", WARNING_MESSAGE );
	}
	
	public static void despliegaError( String mensaje ){
		showMessageDialog( null, mensaje, "\u00a1Error!", ERROR_MESSAGE );
	}
	
	public static boolean confirmacion( String pregunta ){
	      int n = showConfirmDialog( null, pregunta, "Confirmaci\u00f3n", YES_NO_OPTION );
	      return (n == 0);
	}
	
	public static boolean confirmacion( String pregunta, String afirmativo, String negativo ){
		 Object[] opciones = { afirmativo, negativo };
	     int n = showOptionDialog( null, pregunta, "Confirmaci\u00f3n",WARNING_MESSAGE, 
	    		 WARNING_MESSAGE, null, opciones, opciones[0] );
	     return (n == 0);
	}
}