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
import javax.swing.ScrollPaneConstants;

public class DiagramaDeFlujo extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Sentencia> sentencias = new ArrayList<Sentencia>();
	private List<Integer> sentenciasBloque = new ArrayList<Integer>();
	private List<Integer> _sentenciasBloque = new ArrayList<Integer>();
	private Map<String, Integer> etiquetas = new LinkedHashMap<String, Integer>();
	
	private int decisionW = 324, decisionH = 118, escribirW = 148, escribirH = 94, 
			finW = 94, finH = 42, inicioW = 96, inicioH = 78, leerW = 186, leerH = 88,
			procesoW = 152, procesoH = 88, flecha_abajoW = 12, flecha_abajoH = 44,
			flechaW = 44, flechaH = 12, linea_horizontalW = 32, linea_horizontalH = 2, 
			linea_verticalW = 2, linea_verticalH = 32;
	private int mainX, mainY, _mainX, _mainY;
	private int sentenciaActual = 0, _sentenciaActual;
//	private int iProceso, iDecision, iEscribir, iLeer, iFlecha_abajo, iFlecha_izquierda, iFlecha_derecha,
//				iLinea_vertical, iLinea_horizontal;
	private BufferedImage decision, escribir, leer, proceso, flecha_abajo, flecha_izquierda,
	 	flecha_derecha, linea_vertical, linea_horizontal, repite;
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
		sentenciaActual = 0;
		sentenciasBloque = new ArrayList<Integer>();
		while (sentenciaActual < sentencias.size()) {
			dibujaSentencia(sentencias.get(sentenciaActual));
			sentenciaActual++;
		}
		
		dibujaFin();
	}
	
	private void dibujaSentencia(Sentencia sentencia) {
		if(sentencia instanceof SentenciaAsignacion) {
			if(((SentenciaAsignacion) sentencia).simple) {
				dibujaProceso(((SentenciaAsignacion) sentencia).name, 
						((SentenciaAsignacion) sentencia).valor.toString());
			}
			else {
				dibujaProceso(((SentenciaAsignacion) sentencia).name, 
						((ExpresionOperador)((SentenciaAsignacion) sentencia).valor).izquierda.toString(),
						((ExpresionOperador)((SentenciaAsignacion) sentencia).valor).operador, 
						((ExpresionOperador)((SentenciaAsignacion) sentencia).valor).derecha.toString());
			}
			
		}
		
		if(sentencia instanceof SentenciaEscribir) {
			dibujaEscribir(((SentenciaEscribir) sentencia).expresion.toString());
		}
		
		if(sentencia instanceof SentenciaLeer) {
			dibujaLeer(((SentenciaLeer) sentencia).name);
		}
		
		if(sentencia instanceof SentenciaSi) {			
			sentenciasBloque.add(sentenciaActual);
			print(sentenciasBloque);
			List<Sentencia> _sentencias = new ArrayList<Sentencia>();
			int n  = etiquetas.get("Fin" + (sentenciasBloque.size() - 1)) - 
					etiquetas.get("Inicio" + (sentenciasBloque.size() - 1));
			for(int i = 1; i <= n; i++) {
				_sentencias.add(sentencias.get(sentenciaActual + i));
			}
			print(_sentencias.size());
			print("Valor de n: " + n);
			dibujaSi(((ExpresionOperador)((SentenciaSi) sentencia).expresion).izquierda.toString(),
					((ExpresionOperador)((SentenciaSi) sentencia).expresion).operador, 
					((ExpresionOperador)((SentenciaSi) sentencia).expresion).derecha.toString(), _sentencias);
			
			sentenciaActual += n;
		}
		
//		else if(sentencia instanceof SentenciaSiNo) {
//			
//		}
		
		if(sentencia instanceof SentenciaMientras) {
			sentenciasBloque.add(sentenciaActual);
			List<Sentencia> _sentencias = new ArrayList<Sentencia>();
			int n  = etiquetas.get("Fin" + (sentenciasBloque.size() - 1)) - 
					etiquetas.get("Inicio" + (sentenciasBloque.size() - 1));
			for(int i = 1; i <= n; i++) {
				_sentencias.add(sentencias.get(sentenciaActual + i));
			}
			dibujaMientras(((ExpresionOperador)((SentenciaMientras) sentencia).expresion).izquierda.toString(),
					((ExpresionOperador)((SentenciaMientras) sentencia).expresion).operador, 
					((ExpresionOperador)((SentenciaMientras) sentencia).expresion).derecha.toString(), _sentencias);
			
			sentenciaActual += n;
		}
		
		if(sentencia instanceof SentenciaRepite) {
			sentenciasBloque.add(sentenciaActual);
			List<Sentencia> _sentencias = new ArrayList<Sentencia>();
			int n  = etiquetas.get("Fin" + (sentenciasBloque.size() - 1)) - 
					etiquetas.get("Inicio" + (sentenciasBloque.size() - 1));
			for(int i = 1; i <= n; i++) {
				_sentencias.add(sentencias.get(sentenciaActual + i));
			}
			dibujaRepite(((SentenciaRepite) sentencia).expresion.toString(), _sentencias);
			
			sentenciaActual += n;
		}
		
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

	
	private void dibujaEscribir(String cadena) {
		int xE = mainX - (inicioW / 4) - 4;
		g2.drawImage(escribir, xE, mainY, null);
		g2.drawString("Escribir " + cadena, xE + 15, mainY + (escribirH / 2 - 16));
		mainY += escribirH;
	}
	

	
	private void dibujaLeer(String variable) {
		int xL = mainX - (inicioW / 3) - 10;
		g2.drawImage(leer, xL, mainY, null);
		g2.drawString("Leer " + variable, xL + 40, mainY + (escribirH / 2 - 16));
		mainY += leerH;
	}

	
	private void dibujaSi(String valor1, String simbolo, String valor2, List<Sentencia> _sentencias) {
		int xD = mainX - (inicioW / 3) - 28;
		int yD = mainY + (decisionH / 2) + 5;
		g2.drawImage(decision, xD, mainY, null);
		g2.drawString(valor1 + " " + simbolo + " " + valor2, xD + 50, mainY + (escribirH / 2 - 6));
		mainY += decisionH;
		for(Sentencia s : _sentencias) {
			dibujaSentencia(s);
		}
		int nX = (xD + decisionW - 5 - mainX - inicioW / 2) / linea_horizontalW - 1;
		int nY = _sentencias.size() * 100 / (linea_verticalH) + 1;
		print("nx: " + nX );
		for(int i = 0; i < nY; i++)
			g2.drawImage(linea_vertical, xD + decisionW - 5, linea_verticalH * i + yD, null);
		for(int i = 1; i <= nX; i++)
			g2.drawImage(linea_horizontal, xD + decisionW - 5 - linea_horizontalW * i, linea_verticalH * nY + yD, null);
		g2.drawImage(flecha_izquierda, (xD + decisionW - 5 - linea_horizontalW * (nX + 1)), 
				linea_verticalH * nY + yD - 5, null);
	}
	
	private void dibujaSiNo() {
		
	}
	
	private void dibujaMientras(String valor1, String simbolo, String valor2, List<Sentencia> _sentencias) {
		int xD = mainX - (inicioW / 3) - 28;
		int yD = mainY + (decisionH / 2) + 5;
		int _yD = mainY;
		g2.drawImage(decision, xD, mainY, null);
		g2.drawString("Mientras", xD + 70, mainY + (escribirH / 2 - 18));
		g2.drawString(valor1 + " " + simbolo + " " + valor2, xD + 50, mainY + (escribirH / 2 - 6));
		mainY += decisionH;
		for(Sentencia s : _sentencias) {
			dibujaSentencia(s);
		}
		int nX = (xD + decisionW - 5 - mainX - inicioW / 2) / linea_horizontalW - 1;
		int nY = _sentencias.size() * 100 / (linea_verticalH) + 1;
		print("nx: " + nX );
		for(int i = 0; i < nY; i++) {
			g2.drawImage(linea_vertical, xD + decisionW - 5, linea_verticalH * i + yD, null);
			g2.drawImage(linea_vertical, xD - linea_horizontalW * (nX ) + decisionW / 3, 
					linea_verticalH * i + yD - 15, null);
			if(i == nY - 1) {
				g2.drawImage(linea_vertical, xD - linea_horizontalW * (nX) + decisionW / 3, 
						_yD - 14, null);
				g2.drawImage(linea_vertical, xD - linea_horizontalW * (nX) + decisionW / 3, 
						_yD + 18, null);
			}
		}
		for(int i = 1; i <= nX; i++) {
			g2.drawImage(linea_horizontal, xD + decisionW - 5 - linea_horizontalW * i, linea_verticalH * nY + yD, null);
			g2.drawImage(linea_horizontal, xD - linea_horizontalW * i + decisionW / 3, 
					linea_verticalH * nY + yD - 15, null);
			if(i != 1)
				g2.drawImage(linea_horizontal, xD - linea_horizontalW * i + decisionW / 3, 
					_yD - 14, null);
		}
		g2.drawImage(flecha_izquierda, (xD + decisionW - 5 - linea_horizontalW * (nX + 1)), 
				linea_verticalH * nY + yD - 5, null);
		g2.drawImage(flecha_derecha, xD + decisionW - 5 - linea_horizontalW * (nX + 3) - 4, 
				_yD - 20, null);
	}
	
	private void dibujaRepite(String valor, List<Sentencia> _sentencias) {
		int xD = mainX - (inicioW / 3) - 28;
		int yD = mainY + (decisionH / 2) + 5;
		int _yD = mainY;
		g2.drawImage(repite, xD, mainY - 4, null);
		g2.drawString("Repite", xD + 80, mainY + (escribirH / 2 - 18));
		g2.drawString(valor + " " + "veces", xD + 70, mainY + (escribirH / 2 - 6));
		mainY += decisionH;
		for(Sentencia s : _sentencias) {
			dibujaSentencia(s);
		}
		int nX = (xD + decisionW - 5 - mainX - inicioW / 2) / linea_horizontalW - 1;
		int nY = _sentencias.size() * 100 / (linea_verticalH) + 1;
		print("nx: " + nX );
		for(int i = 0; i < nY; i++) {
			g2.drawImage(linea_vertical, xD + decisionW - 10, linea_verticalH * i + yD, null);
			g2.drawImage(linea_vertical, xD - linea_horizontalW * (nX ) + decisionW / 3, 
					linea_verticalH * i + yD - 15, null);
			if(i == nY - 1) {
				g2.drawImage(linea_vertical, xD - linea_horizontalW * (nX) + decisionW / 3, 
						_yD - 14, null);
				g2.drawImage(linea_vertical, xD - linea_horizontalW * (nX) + decisionW / 3, 
						_yD + 18, null);
			}
		}
		for(int i = 1; i <= nX; i++) {
			g2.drawImage(linea_horizontal, xD + decisionW - 5 - linea_horizontalW * i, linea_verticalH * nY + yD, null);
			g2.drawImage(linea_horizontal, xD - linea_horizontalW * i + decisionW / 3, 
					linea_verticalH * nY + yD - 15, null);
			if(i != 1)
				g2.drawImage(linea_horizontal, xD - linea_horizontalW * i + decisionW / 3, 
					_yD - 14, null);
		}
		g2.drawImage(flecha_izquierda, (xD + decisionW - 5 - linea_horizontalW * (nX + 1)), 
				linea_verticalH * nY + yD - 5, null);
		g2.drawImage(flecha_derecha, xD + decisionW - 5 - linea_horizontalW * (nX + 3) - 4, 
				_yD - 20, null);
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
		setResizable(false);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(getWidth() - 5, 0, 2, 450);
		scrollPane.createHorizontalScrollBar();
		scrollPane.createVerticalScrollBar();
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
			repite = ImageIO.read(DiagramaDeFlujo.class.getResource("/figuras/Repite.png"));
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
