package msim;

public interface Bus {

	public int read ( int address );
	
	public void write ( int address, int data );
	
}
