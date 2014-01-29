package msim.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Console extends JFrame implements Displayable {

	private TextArea textArea;
	
	public Console() {
		super("Console");
		setSize(600, 200);
		
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setBackground(Color.WHITE);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		setLocation(250, 200);
		
		add (textArea);
	}
	
	public void addText( String message ) {
		textArea.setText(">> " + message + "\n" + textArea.getText());
		textArea.setCaretPosition(textArea.getRows());
	}
	
	public void refresh() {
	}

	public void display() {
		setVisible(true);
	}

	public void update(Object o) {
		textArea.setText("");
	}
}
