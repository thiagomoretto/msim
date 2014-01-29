package msim;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import msim.dp.MachineObserver;

@SuppressWarnings("serial")
public class Machine implements Bus, Runnable, Serializable {

	private Memory memory;
	
	private Processor processor;
	
	private int bootAddress = 0;
	
	private boolean cont = false;
	
	private int interval = 200; /** 200ms **/
	
	private int initialStackPointer, initialGlobalPointer;
		
	private transient Set<Integer> program = new HashSet<Integer>();
	
	private transient List<MachineObserver> observers = new LinkedList<MachineObserver>();
	
	private int instructionCount, variablesCount;
	
	/**
	 * Machine Constructor
	 * 
	 * @param processor
	 * @param memory
	 */
	public Machine (Processor p, Memory m) {
		memory = m;
		processor = p; 
		processor.setBus(this);
	}
	
	/**
	 * Load program into memory
	 * 
	 * @param param assembled file
	 * @param address initial address to read a program
	 * @throws FileNotFoundException if file not found 
	 * @throws IOException if problems encoutered in read
	 */
	public void load( InputStream input , int address ) 
		throws FileNotFoundException, IOException 
		{
		assert(input != null);
		DataInputStream in;
		in = new DataInputStream(input);
		int data;
		try {
			int dec = 0;
			
			for ( ;; ) {
				if (in.available() == 0)
					break;
				
				data = 	(in.readUnsignedShort() << 16) +
						(in.readUnsignedShort() << 0);
				
				if (dec == 0) {
					instructionCount = data;
					console(String.format("%d instructions", instructionCount));
					console(String.format("global pointer: %d", instructionCount*4));
					processor.setGlobalPointer(instructionCount*4);
					initialGlobalPointer = instructionCount*4;
					processor.setEndPoint((instructionCount*4)-4);
				} else if (dec == 1) {
					variablesCount = data;
					processor.setStackPointer((instructionCount*4)+(variablesCount*4)-4);
					initialStackPointer = (instructionCount*4)+(variablesCount*4)-4;
				} else {
					program.add( data );
					write(address,data);
					address+=4;
				}
				dec++;
			}
		} catch( EOFException eof ) {
			eof.printStackTrace( System.err );
		} finally {
			/** do nothing **/
		}
	}
	
	/** Make a snapshot **/
	public void makeSnapshot( OutputStream dest ) {
		Snapshot sp = new Snapshot( processor, processor.getRegisters(), memory );
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream (dest);
			oos.writeObject( sp );
			oos.close();
			console( "snapshot saved with succefully" );
		} catch (IOException e) {
			e.printStackTrace();
			console( "failed to save a snapshot" );
		}
	}
	
	/** Read a snapshot **/
	public void readSnapshot( InputStream input ) {
		try {
			ObjectInputStream ois = new ObjectInputStream( input );
			Snapshot sp = (Snapshot) ois.readObject();
			processor = sp.getProcessor();
			memory = sp.getMemory();
			console( "snapshot loaded");
			notifyObservers();
		} catch (ClassNotFoundException e) {
			console( "failed to read a snapshot: " + e.getMessage() );
		} catch (IOException e) {
			console( "failed to read a snapshot: " + e.getMessage() );
		}
	}
	
	
	/** TODO: for debug **/
	@SuppressWarnings("unused")
	public void print(long address, long instruction) {
		System.out.println(String.format("0x%04x %s", address, toBinaryString(instruction)));
	}
	
	/** TODO: for debug **/
	public String toBinaryString(long instruction) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<32; i++)
			buffer.append(((instruction >>> 31-i) & 0x01));
		return buffer.toString();
	}
	
	/**
	 * write int memory
	 * 
	 * @param address in memory.
	 * @param data int 32 data.
	 */
	public void write(int address, int data) {
		memory.writeWord(address, data);
	}
	
	/**
	 * read int memory
	 * 
	 * @param address Address of word
	 * @return A word of 32 bits
	 */
	public int read( int address ) {
		return memory.readWord(address);
	}
	
	public void setBootAddress( int address ) {
		processor.setProgramCounter( getBootAddress() );
		bootAddress = address;
	}
	
	public int getBootAddress() {
		return bootAddress;
	}

	/**
	 * TODO
	 *
	 */
	public void reset() {
		stop();
		setBootAddress(0x0000);
		processor.reset();
		processor.setGlobalPointer(initialGlobalPointer);
		processor.setStackPointer(initialStackPointer);
	}
	
	public void stop() {
		cont = false;
	}
	
	public void run() {
		while (cont) {
			next();
			try {
				Thread.sleep(interval);
			} catch (InterruptedException iex) {
				iex.printStackTrace(System.err);
			}
		}
	}

	public void cont( long interval ) {
		cont = true;
		new Thread(this).start();
	}
	
	public void next() {
		if (!processor.next(this)) {
			console( "program execution end" );
			cont = false;
			processor.generateStats(this);
		}
		notifyObservers();
	}

	public Memory getMemory() {
		return memory;
	}

	public Processor getProcessor() {
		return processor;
	}
	
	public void console( String message ) {
		synchronized (this) {
			for (MachineObserver mo : observers)
				mo.notifyMessage(message);
		}
		
	}

	public void notifyObservers() {
		synchronized (this) {
			for (MachineObserver mo : observers)
				mo.notifyExecution();
		}
	}
	
	public boolean add(MachineObserver o) {
		return observers.add(o);
	}
}
