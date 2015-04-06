package textEditor;
/*
 * Editor de texto para el compilador de pseudocodigo
 * */
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import compilador.Lexer;
import compilador.Lexer.Token;
import javax.swing.JTextArea;

public class TextEditor {

	private JFrame frmEditorDePseudocdigo;
	private JMenuBar jMenuBar1;
	private JMenu jMenu1;
	private JMenuItem loadMenu;
	private JMenuItem saveMenu;
	private JMenuItem saveAsMenu;
	private JMenu jMenu2;
	private JMenuItem copyMenu;
	private JMenuItem cutMenu;
	private JMenuItem pasteMenu;
	private JMenuItem selectAllMenu;
	private JMenuItem clearMenu;
	private JSeparator jSeparator1;
	private JMenuItem exampleTextMenu;
	private JPanel panel;
	private JButton tokenizeButton;
	private JScrollPane jScrollPane1;
	private JEditorPane editPane;
	private JScrollPane jScrollPane2;
	private File fileName = new File("noname.txt");
	private JTextArea textArea;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TextEditor window = new TextEditor();
					window.frmEditorDePseudocdigo.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TextEditor() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEditorDePseudocdigo = new JFrame();
		frmEditorDePseudocdigo.setTitle("Editor de Pseudoc\u00F3digo");
		frmEditorDePseudocdigo.setResizable(false);
		frmEditorDePseudocdigo.setBounds(100, 100, 572, 620);
		frmEditorDePseudocdigo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		jMenuBar1 = new JMenuBar();
		frmEditorDePseudocdigo.setJMenuBar(jMenuBar1);
		
		jMenu1 = new JMenu("Archivo");
		jMenuBar1.add(jMenu1);
		
		loadMenu = new JMenuItem("Abrir");
		loadMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
			    if (fileChooser.showOpenDialog(frmEditorDePseudocdigo) == JFileChooser.APPROVE_OPTION) {
			    	//System.out.println(fileChooser.getSelectedFile());
			        BufferedReader reader;
			        StringBuilder stringBuilder = new StringBuilder();
			        try {
			            reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
			            while (reader.ready()) {
			                stringBuilder.append(reader.readLine() + "\n");
			            }
			            reader.close();
			            editPane.setText(stringBuilder.toString());
			            fileName = fileChooser.getSelectedFile();
			        }
			        catch (IOException ioe) {
			            editPane.setText("No se puede abrir el archivo!");
			        }
			    }
			}
		});
		jMenu1.add(loadMenu);
		
		saveMenu = new JMenuItem("Guardar");
		saveMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedWriter writer;
			    try {
			        writer = new BufferedWriter(new FileWriter(fileName));
			        writer.write(editPane.getText());
			        writer.close();
			    }
			    catch (IOException ioe) {
			        editPane.setText("No se puede guardar el archivo!");
			    }
			}
		});
		jMenu1.add(saveMenu);
		
		saveAsMenu = new JMenuItem("Guardar como...");
		saveAsMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        if (fileChooser.showSaveDialog(frmEditorDePseudocdigo) == JFileChooser.APPROVE_OPTION) {
		            BufferedWriter writer;
		        try {
		            writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()));
		            writer.write(editPane.getText());
		            writer.close();
		        }
		        catch (IOException ioe) {
		            editPane.setText("No se pudo escribir el texto");
		        }
		    }
			}
		});
		jMenu1.add(saveAsMenu);
		
		jMenu2 = new JMenu("Editar");
		jMenuBar1.add(jMenu2);
		
		copyMenu = new JMenuItem("Copiar");
		copyMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editPane.copy();
			}
		});
		jMenu2.add(copyMenu);
		
		cutMenu = new JMenuItem("Cortar");
		cutMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editPane.cut();
			}
		});
		jMenu2.add(cutMenu);
		
		pasteMenu = new JMenuItem("Pegar");
		pasteMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editPane.paste();
			}
		});
		jMenu2.add(pasteMenu);
		
		selectAllMenu = new JMenuItem("Seleccionar Todo");
		selectAllMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editPane.selectAll();
			}
		});
		jMenu2.add(selectAllMenu);
		
		clearMenu = new JMenuItem("Limpiar");
		clearMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editPane.setText("");
			}
		});
		jMenu2.add(clearMenu);
		
		jSeparator1 = new JSeparator();
		jMenu2.add(jSeparator1);
		
		exampleTextMenu = new JMenuItem("C\u00F3digo de Ejemplo");
		exampleTextMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedReader reader;
		        StringBuilder stringBuilder = new StringBuilder();
		        try {
		            reader = new BufferedReader(new FileReader("test.txt"));
		            while (reader.ready()) {
		                stringBuilder.append(reader.readLine() + "\n");
		            }
		            reader.close();
		            editPane.setText(stringBuilder.toString());
		        }
		        catch (IOException ioe) {
		            editPane.setText("No se puede abrir el archivo!");
		        }
			}
		});
		jMenu2.add(exampleTextMenu);
		
		panel = new JPanel();
		frmEditorDePseudocdigo.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		tokenizeButton = new JButton("Tokenize!");
		tokenizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Token> tokens = Lexer.tokenize(editPane.getText());
				for(int i = 0; i < tokens.size(); i++) {
		            //System.out.println(i+"	Token: "+tokens.get(i).getText()+ "		Tipo: "+tokens.get(i).getToken());
					textArea.append(i+"	Token: "+tokens.get(i).getText()+ 
							"		Tipo: "+tokens.get(i).getToken() + "\n");
					
		        }
				BufferedWriter writer;
			    try {
			        writer = new BufferedWriter(new FileWriter(new File("tokens.txt")));
			        writer.write(textArea.getText());
			        writer.close();
			    }
			    catch (IOException ioe) {
			        editPane.setText("No se puede guardar el archivo!");
			    }
			}
		});
		tokenizeButton.setBounds(6, 11, 105, 29);
		panel.add(tokenizeButton);
		
		jScrollPane1 = new JScrollPane();
		jScrollPane1.setBounds(6, 52, 560, 380);
		panel.add(jScrollPane1);
		
		editPane = new JEditorPane();
		jScrollPane1.setViewportView(editPane);
		
		jScrollPane2 = new JScrollPane();
		jScrollPane2.setBounds(6, 453, 560, 95);
		panel.add(jScrollPane2);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		jScrollPane2.setViewportView(textArea);
	}
}
