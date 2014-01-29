package msim;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Snapshot implements Serializable {

	private Registers registers;
	
	private Processor processor;
	
	private Memory memory;
	
	public Snapshot( Processor p , Registers r, Memory m ) {
		processor = p;
		registers = r;
		memory = m;
	}

	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public Registers getRegisters() {
		return registers;
	}

	public void setRegisters(Registers registers) {
		this.registers = registers;
	}
}
