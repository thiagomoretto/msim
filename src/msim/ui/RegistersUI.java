package msim.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import msim.Registers;
import msim.mips.MIPSRegisters;

@SuppressWarnings("serial")
public class RegistersUI extends JFrame implements Displayable {

	private Registers registers;
	
	private TextArea textArea;
	
	private static Map<Integer, String> regmap = new HashMap<Integer, String>();
	
	static {
		regmap.put(0,  "ZR");
		regmap.put(1,  "AT");
		
		regmap.put(2,  "V0");
		regmap.put(3,  "V1");
		
		regmap.put(4,  "A0");
		regmap.put(5,  "A1");
		regmap.put(6,  "A2");
		regmap.put(7,  "A3");
		
		regmap.put(8,  "T0");
		regmap.put(9,  "T1");
		regmap.put(10, "T2");
		regmap.put(11, "T3");
		regmap.put(12, "T4");
		regmap.put(13, "T5");
		regmap.put(14, "T6");
		regmap.put(15, "T7");
		
		regmap.put(16, "S0");
		regmap.put(17, "S1");
		regmap.put(18, "S2");
		regmap.put(19, "S3");
		regmap.put(20, "S4");
		regmap.put(21, "S5");
		regmap.put(22, "S6");
		regmap.put(23, "S7");
		
		regmap.put(24, "T8");
		regmap.put(25, "T9");
		
		regmap.put(26, "K0");
		regmap.put(27, "K1");
		
		regmap.put(28, "GP");
		regmap.put(29, "SP");
		regmap.put(30, "FP");
		
		regmap.put(31, "RA");
		
		regmap.put(32, "HI");
		regmap.put(33, "LO");
	}
	
	public RegistersUI( Registers r ) {
		super( "Registers" );
		setSize(335, 550);
		
		registers = r;
		
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setSize(335, 550);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textArea.setBackground(Color.white);
		refresh();
		
		setLocation(90, 90);
		
		add( textArea );
	}
	
	public void refresh() {
		StringBuffer buffer = new StringBuffer();
		
		int index = 0;
		for (int key=0; key<MIPSRegisters.REG_SIZE; key++ ) {
			index = key*4;
			buffer.append(String.format("%.2s %02d Hex 0x%02x 0x%02x 0x%02x 0x%02x Dec %09d\n",
						regmap.get(key), 
						key, 
						registers.readByte(index)  , 
						registers.readByte(index+1),
						registers.readByte(index+2),
						registers.readByte(index+3),
						registers.read(key) 
				));
		}
		
		textArea.setText( buffer.toString() );
	}

	public void display() {
		setVisible(true);
	}

	public void update(Object o) {
		registers = (Registers)o;
	}
}
