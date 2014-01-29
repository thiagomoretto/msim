package msim;

public interface Processor {

	public void setProgramCounter( int address );
	
	public int getProgramCounter(  );
	
	public void setBus( Bus bus );
	
	public void setGlobalPointer( int address );
	
	public void setStackPointer( int address );
	
	public void setEndPoint( int endpoint ); 
	
	public void reset();
	
	public Registers getRegisters();
	
	public boolean next( Machine m );
	
	public void generateStats( Machine m );
}
