package msim.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;

import javax.swing.JFrame;

import msim.Memory;
import msim.Processor;
import msim.Registers;
import msim.dp.MachineFacade;
import msim.mips.MIPSRegisters;

@SuppressWarnings("serial")
public class MemoryUI extends JFrame implements Displayable {

	private Memory memory;
	
	private Registers registers;
	
	private Processor processor;
	
	private TextArea textArea;
	
	public MemoryUI( MachineFacade f ) {
		super( String.format("Memory %dkb", f.getMemory().getSize()) );
		setSize(450, 550);
		memory = f.getMemory();
		registers = f.getRegisters();
		processor = f.getProcessor();
		
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setSize(450, 550);
		textArea.setBackground(Color.white);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		refresh();
		
		setLocation(70, 70);
		
		add( textArea );
	}
	
	public void refresh() {
		StringBuffer buffer = new StringBuffer();
		
		int gp,sp,pc;
		
		gp = registers.read(MIPSRegisters.GP);
		sp = registers.read(MIPSRegisters.SP);
		pc = processor.getProgramCounter();
		
		int addr = 0;
		for (int i=0; i<memory.getSize();i+=4) {
			buffer.append(String.format("%2s %2s %2s Hex %05d 0x%02x 0x%02x 0x%02x 0x%02x Dec %03d %03d %03d %03d\n", 
					(pc == i) ? "PC" : "..", (gp == i) ? "GP" : "..", (sp == i) ? "SP" : "..", i, 
					(int)memory.readByte(i), (int)memory.readByte(i+1), (int)memory.readByte(i+2), (int)memory.readByte(i+3),
					(int)memory.readByte(i), (int)memory.readByte(i+1), (int)memory.readByte(i+2), (int)memory.readByte(i+3)
			));
			addr++;
		}
		
		textArea.setText( buffer.toString() );
	}
	
	public void display() {
		setVisible(true);
	}

	public void update(Object o) {
		MachineFacade f = (MachineFacade)o;
		memory = f.getMemory();
		registers = f.getRegisters();
		processor = f.getProcessor();
	}
}
