package msim.mips;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import msim.Bus;
import msim.Instruction;
import msim.Machine;
import msim.Processor;
import msim.Registers;

@SuppressWarnings("serial")
public class MIPSProcessor implements Processor, Serializable {

	private static final int HALT = 1;
	
	private Bus bus;
	
	private Registers mem;
	
	private long pc;
	
	private int endpoint;
	
	private Map<String,Integer> instructionStats = new HashMap<String,Integer>();
	
	private int instructionCount = 0;

	private static Map<String,Integer> cpi;
	
	static {
		cpi = new HashMap<String,Integer>();
		cpi.put( "add", 4 );
		cpi.put( "sub", 4 );
		cpi.put( "addi", 4 );
		cpi.put( "mult", 20);
		cpi.put( "div", 20);
		cpi.put( "mfhi", 3 );
		cpi.put( "mflo", 3 );
		cpi.put( "and", 4 );
		cpi.put( "or", 4 );
		cpi.put( "andi", 4 );
		cpi.put( "ori", 4 );
		cpi.put( "sll", 4 );
		cpi.put( "srl", 4 );
		cpi.put( "lw", 5 );
		cpi.put( "sw", 4 );
		cpi.put( "lui", 4 );
		cpi.put( "beq", 3 );
		cpi.put( "bne", 3 );
		cpi.put( "slt", 4 );
		cpi.put( "slti", 4 );
		cpi.put( "j", 3 );
		cpi.put( "jr", 3 );
		cpi.put( "jal", 3 );
		cpi.put( "hlt", 2 );
	}
	
	public MIPSProcessor( ) {
		this.mem = new MIPSRegisters();
	}
	
	public Registers getRegisters() {
		return mem;
	}
	
	public boolean next( Machine m ) {
		int data;
		data = bus.read( (int) pc );
		if (pc == endpoint)
			return false;
		pc += 4;
		return data == HALT ? false : execute(new Instruction(data), m);
	}

	public void setEndPoint( int endpoint ) {
		this.endpoint = endpoint;
	}
	
	public void setGlobalPointer( int address ) {
		mem.write(MIPSRegisters.GP, address);
	}
	
	public void setStackPointer( int address ) {
		mem.write(MIPSRegisters.SP, address);
	}
	
	public void setProgramCounter(int address) {
		pc = address;
	}

	public void setBus(Bus bus) {
		this.bus = bus;		
	}
	
	public boolean execute( Instruction instruction, Machine m ) {
		Instruction opcode = instruction.extractInstruction(0x0000003F, 26);
		Instruction funct, rs, rt, rd, address, shamt;
		
		switch((int) opcode.getLong()) {
			/** Instruções tipo R **/
			case 0:
				funct = instruction.extractInstruction(0x0000003F);
				rs = instruction.extractInstruction(0x0000001F, 21);
				rt = instruction.extractInstruction(0x0000001F, 16);
				rd = instruction.extractInstruction(0x0000001F, 11);
				shamt = instruction.extractInstruction(0x000007C0, 6);
				
				switch ((int) funct.getLong()) {
					
					/** div **/
					case 26:
						m.console("processing instruction 'div'");
						mem.write(MIPSRegisters.HI, 
								(mem.read(rs.getLong()) / mem.read(rt.getLong())) );
						mem.write(MIPSRegisters.LO,  
								(mem.read(rs.getLong()) %  mem.read(rt.getLong())) );
						inc( "div" );
					break;
					
					/** mult **/
					case 24:
						m.console("processing instruction 'mult'");
						mem.write(MIPSRegisters.HI, 0xFFFF0000 & 
								(mem.read(rs.getLong()) *  mem.read(rt.getLong())) );
						mem.write(MIPSRegisters.LO, 0x0000FFFF & 
								(mem.read(rs.getLong()) *  mem.read(rt.getLong())) );
						inc( "mult" );
					break;
					
					/** mfhi **/
					case 16:
						m.console("processing instruction 'mfhi'");
						mem.write(rd.getLong(), mem.read(MIPSRegisters.HI));
						inc( "mfhi" );
					break;
					
					/** mflo **/
					case 18:
						m.console("processing instruction 'mflo'");
						mem.write(rd.getLong(), mem.read(MIPSRegisters.LO));
						inc( "mflo" );
					break;
					
					/** add **/
					case 32:
						m.console("processing instruction 'add'");
						mem.write(rd.getLong(), mem.read(rs.getLong()) +  mem.read(rt.getLong()));
						inc( "add" );
					break;
					
					/** sub **/
					case 34:
						m.console("processing instruction 'sub'");
						mem.write(rd.getLong(), mem.read(rs.getLong()) +  mem.read(rt.getLong()));
						inc( "sub" );
					break;
					
					/** and **/
					case 36:
						m.console("processing instruction 'and'");
						mem.write(rd.getLong(), mem.read(rs.getLong()) & mem.read(rt.getLong()));
						inc( "and" );
					break;
					
					/** or **/
					case 37:
						m.console("processing instruction 'or'");
						mem.write(rd.getLong(), mem.read(rs.getLong()) | mem.read(rt.getLong()));
						inc( "or" );
					break;
					
					/** sll **/
					case 0:
						m.console("processing instruction 'sll'");
						mem.write(rd.getLong(), mem.read(rs.getLong()) << shamt.getLong());
						inc( "sll" );
					break;
					
					/** srl **/
					case 2:
						m.console("processing instruction 'srl'");
						mem.write(rd.getLong(), mem.read(rs.getLong()) >>> shamt.getLong());
						inc( "srl" );
					break;
					
					/** slt **/
					case 42:
						m.console("processing instruction 'slt'");
						if (mem.read(rs.getLong()) < mem.read(rt.getLong()))
							mem.write(rd.getLong(), 1);
						else
							mem.write(rd.getLong(), 0);
						inc( "slt" );
					break;
					
					/** jr **/
					case 8:
						m.console("processing instruction 'jr'");
						pc = mem.read(rs.getLong());
						inc( "jr" );
					break;
					
					default:
						break;
				}
			break;
		
			/** Instruções tipo J **/
			
			/** j **/
			case 2: 
				m.console("processing instruction 'j'");
				address = instruction.extractInstruction(0x03FFFFFF);
				pc = (pc & 0xF0000000) | ((int)address.getLong()<<2);
				inc( "j" );
			break;
			
			/** jal **/
			case 3:
				m.console("processing instruction 'jal'");
				address = instruction.extractInstruction(0x03FFFFFF);
				mem.write(MIPSRegisters.RA, pc+4);
				pc = (pc & 0xF0000000) | ((int)address.getLong()<<2);
				inc( "jal" );
			break;
			
			/** Instruções tipo I **/
			
			/** slti **/
			case 10:
				rs = instruction.extractInstruction(0x0000001F, 21);
				rt = instruction.extractInstruction(0x0000001F, 16);
				address = instruction.extractInstruction(0x000000FF);
				if (mem.read(rs.getLong()) < address.getLong())
					mem.write(rt.getLong(), 1);
				else
					mem.write(rt.getLong(), 0);
				m.console("processing instruction 'slti'");
				inc( "slti" );
			break;
			
			/** lui **/
			case 15:
				rs = instruction.extractInstruction(0x0000001F, 21);
				rt = instruction.extractInstruction(0x0000001F, 16);
				address = instruction.extractInstruction(0x000000FF);
				mem.write(rt.getLong(), (long)(address.getLong() * Math.pow(2, 16)));
				m.console("processing instruction 'lui'");
				inc( "lui" );
				break;
			
			/** addi **/
			case 8: 
				rs = instruction.extractInstruction(0x0000001F, 21);
				rt = instruction.extractInstruction(0x0000001F, 16);
				address = instruction.extractInstruction(0x0000FFFF);
				mem.write(rt.getLong(), mem.read(rs.getLong()) + ((short)address.getLong()));
				m.console("processing instruction 'addi'");
				inc( "addi" );
			break;
			
			/** andi **/
			case 12: 
				rs = instruction.extractInstruction(0x0000001F, 21);
				rt = instruction.extractInstruction(0x0000001F, 16);
				address = instruction.extractInstruction(0x000000FF);
				mem.write(rt.getLong(), mem.read(rs.getLong()) & address.getLong());
				m.console("processing instruction 'andi'");
				inc( "andi" );
			break;
			
			/** ori **/
			case 13: 
				rs = instruction.extractInstruction(0x0000001F, 21);
				rt = instruction.extractInstruction(0x0000001F, 16);
				address = instruction.extractInstruction(0x0000FFFF);
				mem.write(rt.getLong(), mem.read(rs.getLong()) | address.getLong());
				m.console("processing instruction 'ori'");
				inc( "ori" );
			break;
			
			/** beq **/
			case 4: 
				rs = instruction.extractInstruction(0x0000001F, 21);
				rt = instruction.extractInstruction(0x0000001F, 16);
				address = instruction.extractInstruction(0x0000FFFF);
				
				m.console("processing instruction 'beq'");

				if (mem.read(rs.getLong()) == mem.read(rt.getLong())) {
					pc = (address.getLong() << 2);
					m.console("program counter pointer at: " + pc);
				}
				inc( "beq" );
			break;
			
			/** bne **/
			case 5: 
				rs = instruction.extractInstruction(0x0000001F, 21);
				rt = instruction.extractInstruction(0x0000001F, 16);
				address = instruction.extractInstruction(0x0000FFFF);
				
				m.console("processing instruction 'bne'");
				
				if (mem.read(rs.getLong()) != mem.read(rt.getLong())) {
					pc = (address.getLong() << 2);
					m.console("program counter pointer at: " + pc);
				}
				inc( "bne" );
			break;
			
			/** lw **/
			case 35: 
				rs = instruction.extractInstruction(0x0000001F, 21);
				rt = instruction.extractInstruction(0x0000001F, 16);
				address = instruction.extractInstruction(0x0000FFFF);
				mem.write(  rt.getLong(), bus.read((int) ( mem.read(rs.getLong()) +	 address.getLong())));

				m.console("processing instruction 'lw'");
				inc( "lw" );
			break;
			
			/** sw **/
			case 43:
				rs = instruction.extractInstruction(0x0000001F, 21);
				rt = instruction.extractInstruction(0x0000001F, 16);
				address = instruction.extractInstruction(0x0000FFFF);
				bus.write( (int)(mem.read((int) rs.getLong())+address.getLong()), (int) mem.read((int) rt.getLong())   );

				m.console("processing instruction 'sw'");
				inc( "sw" );
			break;
			
			default:
					m.console( "WARNING: undefined instruction" );
				break;
		}
		
		return true;
	}

	private void inc( String instname ) {
		if (instructionStats.containsKey(instname)) {
			 instructionStats.put(instname, instructionStats.get(instname)+1);
		} else {
			 instructionStats.put(instname, 1);
		}
		instructionCount++;
	}
	
	public void generateStats( Machine machine ) {
		int times;
		int totaltime = 0;
		machine.console(String.format("%d instructions executed", instructionCount));
		for (String key : instructionStats.keySet()) {
			times= instructionStats.get(key);
			machine.console(String.format("instruction %s executed %d times representing %.2f%% of total",
					key,times,(times*100)/(double) instructionCount ));
			totaltime += cpi.get(key)*times;
		}
		machine.console(String.format("total time (100MHz): %.8fs", (double)totaltime/100000.00));
	}
	
	public int getProgramCounter() {
		return (int)pc;
	}

	public void reset() {
		instructionCount = 0;
		instructionStats.clear();
		mem.reset();
	}
}
