package compilador;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import compilador.Parser.ExpresionOperador;
import compilador.Parser.ExpresionVariable;
import compilador.Parser.SentenciaAsignacion;
import compilador.Parser.SentenciaEscribir;
import compilador.Parser.SentenciaLeer;
import compilador.Parser.SentenciaMientras;
import compilador.Parser.SentenciaRepite;
import compilador.Parser.SentenciaSi;
import compilador.Parser.SentenciaSiNo;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiagramaDeFlujo extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Sentencia> sentencias = new ArrayList<Sentencia>();
	private Map<String, Integer> etiquetas = new LinkedHashMap<String, Integer>();
	
	private int decisionW = 324, decisionH = 118, escribirW = 148, escribirH = 94, 
			finW = 94, finH = 42, inicioW = 96, inicioH = 78, leerW = 186, leerH = 88,
			procesoW = 152, procesoH = 88, flecha_abajoW = 12, flecha_abajoH = 44,
			flechaW = 44, flechaH = 12, linea_horizontalW = 32, linea_horizontalH = 2, 
			linea_verticalW = 2, linea_verticalH = 32;
	private int mainX, mainY, _mainX, _mainY;
	private int sentenciaActual;
//	private int iProceso, iDecision, iEscribir, iLeer, iFlecha_abajo, iFlecha_izquierda, iFlecha_derecha,
//				iLinea_vertical, iLinea_horizontal;
	private BufferedImage decision, escribir, leer, proceso, flecha_abajo, flecha_izquierda,
	 	flecha_derecha, linea_vertical, linea_horizontal;
	private BufferedImage fin, inicio;
	private Image dbImage;
	private Graphics graficos, g2;
	private boolean flagG = false, flagImg = false;
	private JPanel ventana;

	/**
	 * Create the application.
	 */
	public DiagramaDeFlujo(List<Sentencia> sentencias, Map<String, Integer> etiquetas) {
		super("Diagrama de Flujo");
		this.sentencias = sentencias;
		this.etiquetas = etiquetas;
		initialize();
	}

	public DiagramaDeFlujo() {
		super("Diagrama de Flujo");
		initialize();
	}
	
	
	private void generaDiagrama() {
//		g2.setColor(Color.WHITE);
//		g2.fillRect(0, 0, getWidth(), getHeight());
		dibujaInicio();
//		dibujaProceso("LOLAZO", "XDDD");
//		dibujaProceso("LOLAZO", "XD", "+", "DDD");
//		dibujaEscribir("Pfff LOLAZO");
//		dibujaLeer("MEGALOL");
//		dibujaSi("AYYYY", "<", "LMAO");
		sentenciaActual = 0;
		while (sentenciaActual < sentencias.size()) {
			dibujaSentencia(sentencias.get(sentenciaActual++));
		}
		
		dibujaFin();
	}
	
	private void dibujaSentencia(Sentencia sentencia) {
		if(sentencia instanceof SentenciaAsignacion) {
			print("Es un proceso XD");
			if(((SentenciaAsignacion) sentencia).simple)
				dibujaProceso(((SentenciaAsignacion) sentencia).name, 
						((SentenciaAsignacion) sentencia).valor.toString());
			//else if(((SentenciaAsignacion) sentencia).valor instanceof ExpresionOperador)
			else {
				dibujaProceso(((SentenciaAsignacion) sentencia).name, 
						((ExpresionOperador)((SentenciaAsignacion) sentencia).valor).izquierda.toString(),
						((ExpresionOperador)((SentenciaAsignacion) sentencia).valor).operador, 
						((ExpresionOperador)((SentenciaAsignacion) sentencia).valor).derecha.toString());
				print(((SentenciaAsignacion)sentencia).valor.toString());
			}
			
		}
		if(sentencia instanceof SentenciaEscribir) {
			print("Es un Escribir CX");
			dibujaEscribir(((SentenciaEscribir) sentencia).expresion.toString());
		}
		if(sentencia instanceof SentenciaLeer) {
			print("LOL esto es un leer XDD");
			dibujaLeer(((SentenciaLeer) sentencia).name);
		}
		
//		if(sentencia instanceof SentenciaSi) {
//			
//		}
//		if(sentencia instanceof SentenciaSiNo) {
//			
//		}
//		if(sentencia instanceof SentenciaMientras) {
//			
//		}
//		if(sentencia instanceof SentenciaRepite) {
//			
//		}
	}
	
	private void dibujaSentencia(Sentencia sentencia, int xS, int yS) {
		
	}
	
	private void dibujaInicio() {
		g2.drawImage(inicio, mainX, mainY, null);
		mainY += inicioH;
	}
	
	private void dibujaFin() {
		g2.drawImage(fin, mainX, mainY, null);
	}
	

	private void dibujaProceso(String variable, String valor) {
		int xP = mainX - (inicioW / 4) - 4;
		g2.drawImage(proceso, xP, mainY, null);
		g2.drawString(variable + " = " + valor, xP + 30, mainY + (procesoH / 2 - 8));
		mainY += procesoH;
	}
	
	private void dibujaProceso(String variable, String valor1, String signo, String valor2) {
		int xP = mainX - (inicioW / 4) - 4;
		g2.drawImage(proceso, xP, mainY, null);
		g2.drawString(variable + " = " + valor1 + " " + signo + " " + valor2, xP + 15, 
				mainY + (procesoH / 2 - 8));
		mainY += procesoH;
	}
	
	private void dibujaProceso(String variable, String valor, int xP) {
		g2.drawImage(proceso, xP, mainY, null);
		g2.drawString(variable + " = " + valor, xP + 30, mainY + (procesoH / 2 - 8));
		mainY += procesoH;
	}
	
	private void dibujaProceso(String variable, String valor1, String signo, String valor2, int xP) {
		g2.drawImage(proceso, xP, mainY, null);
		g2.drawString(variable + " = " + valor1 + " " + signo + " " + valor2, xP + 15, 
				mainY + (procesoH / 2 - 8));
		mainY += procesoH;
	}
	
	private void dibujaEscribir(String cadena) {
		int xE = mainX - (inicioW / 4) - 4;
		g2.drawImage(escribir, xE, mainY, null);
		g2.drawString("Escribir " + cadena, xE + 15, mainY + (escribirH / 2 - 16));
		mainY += escribirH;
	}
	
	private void dibujaEscribir(String cadena, int xE) {
		//int xE = mainX - (inicioW / 4) - 4;
		g2.drawImage(escribir, xE, mainY, null);
		g2.drawString("Escribe " + cadena, xE + 15, mainY + (escribirH / 2 - 16));
		mainY += escribirH;
	}
	
	private void dibujaLeer(String variable) {
		int xL = mainX - (inicioW / 3) - 10;
		g2.drawImage(leer, xL, mainY, null);
		g2.drawString("Leer " + variable, xL + 40, mainY + (escribirH / 2 - 16));
		mainY += leerH;
	}
	
	private void dibujaLeer(String variable, int xL) {
		//int xL = mainX - (inicioW / 3) - 10;
		g2.drawImage(leer, xL, mainY, null);
		g2.drawString("Leer " + variable, xL + 40, mainY + (escribirH / 2 - 16));
		mainY += leerH;
	}
	
	private void dibujaSi(String valor1, String simbolo, String valor2) {
		int xD = mainX - (inicioW / 3) - 28;
		g2.drawImage(decision, xD, mainY, null);
		g2.drawString(valor1 + " " + simbolo + " " + valor2, xD + 50, mainY + (escribirH / 2 - 6));
		mainY += decisionH;
	}
	
	private void dibujaSiNo() {
		
	}
	
	private void dibujaMientras() {
		
	}
	
	private void dibujaRepite() {
		
	}
	
	
	
	public void paint(Graphics g) {
		if(!flagG) {
			_mainX = mainX;
			_mainY = mainY;
			flagG = true;
		}
		else {
			mainX = _mainX;
			mainY = _mainY;
		}
		dbImage = ventana.createImage(getWidth(), getHeight());
		graficos = dbImage.getGraphics();
		//paintComponent(graficos);
		g2 = graficos;
		generaDiagrama();
		g.drawImage(dbImage, 0, 0, ventana);
//		
//		System.out.println("La mainY es: " + mainY);
//		System.out.println("La mainX es: " + mainX);
	}
	
	public void print(Object objeto) {
		System.out.println(objeto);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setBounds(200, 10, 700, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana = new JPanel();
		setContentPane(ventana);
		ventana.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(getWidth(), 0, 2, 450);
		ventana.add(scrollPane);
		//ventana.setBackground(Color.WHITE);
		try {
			//figuras
			fin = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/Fin.png"));
			inicio = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/Inicio.png"));
			decision = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/Decision.png"));
			escribir = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/Escribir.png"));
			leer = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/Leer.png"));
			proceso = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/Proceso.png"));
			
			//lineas y flechas
			flecha_abajo = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/flecha_abajo.png"));
			flecha_izquierda = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/flecha_izquierda.png"));
			flecha_derecha = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/flecha_derecha.png"));
			linea_vertical = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/linea_vertical.png"));
			linea_horizontal = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/linea_horizontal.png"));
		} catch(IOException e) {
			
		}
		
		mainX = (getWidth() / 2) - 65;
		mainY = 30;
	}
	
	

}
