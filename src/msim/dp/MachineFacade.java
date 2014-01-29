package msim.dp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import msim.Machine;
import msim.Memory;
import msim.Processor;
import msim.Registers;

/**
 * Implementando uma espécie de façade para
 * centralizar requisições da GUI com o
 * nucleo do simulador.
 * 
 * @author Thiago
 *
 */
public class MachineFacade {

	private Machine machine;
	
	public MachineFacade( Machine m ) {
		assert m != null;
		this.machine = m;
	}
	
	/**
	 * Load program into machine
	 * 
	 */
	public void load( InputStream in ) throws IOException {
		machine.load(in, 0x0000);
		machine.setBootAddress(0x0000);
	}
	
	/**
	 * Request machine to process next instruction
	 *
	 */
	public void requestNextInstruction() {
		machine.next();
	}

	/**
	 * Request machine to reset program counter
	 * to startup address (0x0)
	 *
	 */
	public void requestResetMachine() {
		machine.reset();
	}
	
	/**
	 * Request machine to stop processing.
	 *
	 */
	public void requestStopMachine() {
		machine.stop();
	}
	
	/**
	 * Request to machine to continue processing
	 * 
	 */
	public void requestContinueMachine() {
		machine.cont(1000);
	}
	
	/**
	 * Return a Memory's object.
	 * 
	 */
	public Memory getMemory() {
		return machine.getMemory();
	}
	
	/**
	 * Returns a Processor's object
	 * 
	 */
	public Processor getProcessor() {
		return machine.getProcessor();
	}
	
	/**
	 * Returns a Registers's object
	 * 
	 */
	public Registers getRegisters() {
		return machine.getProcessor().getRegisters();
	}
	
	/**
	 * read snapshot
	 * 
	 */
	public void readSnapshot( File file ) {
		try {
			machine.readSnapshot( new FileInputStream(file) );
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * make snapshot
	 * 
	 */
	
	public void makeSnapshot( File file ) {
		try {
			machine.makeSnapshot( new FileOutputStream(file) );
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
		}
	}
}
