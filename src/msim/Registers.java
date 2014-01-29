package msim;

public interface Registers {

	public int read ( int r );
	
	public int read ( long r );
	
	public int readByte( int r);
	
	public int readByte( long r );
	
	public void write ( long r , long d );
	
	public void write ( int r , int d );

	public void reset();
	
	public String toString();
}
