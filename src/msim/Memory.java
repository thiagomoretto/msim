package msim;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Memory implements Serializable {

	private int size;
	
	private char[] drawer;
	
	public Memory( int size ) {
		this.size = size;
		drawer = new char[this.size];
	}
	
	public int getSize() {
		return size;
	}

	public int readWord( int address ) {
		return ((drawer[address] << 24) + 
				(drawer[address+1] << 16) + 
				(drawer[address+2] << 8) + 
				(drawer[address+3] << 0));
	}
	
	public void writeWord( int address, int data ) {
		drawer[address] = (char) ((data >>> 24) & 0xFF);
		drawer[address+1] = (char) ((data >>> 16) & 0xFF);
		drawer[address+2] = (char) ((data >>>  8) & 0xFF);
		drawer[address+3] = (char) ((data >>>  0) & 0xFF);
	}
	
	public char readByte( int address ) {
		return drawer[address];
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		int addr = 0;
		for (int i=0; i<size;i+=4) {
			buffer.append(String.format("%05d 0x%02x 0x%02x 0x%02x 0x%02x ; %02d %02d %02d %02d\n", 
					i, (int)drawer[i], (int)drawer[i+1], (int)drawer[i+2], (int)drawer[i+3],
						  (int)drawer[i], (int)drawer[i+1], (int)drawer[i+2], (int)drawer[i+3]));
			addr++;
		}
		
		return buffer.toString();
	}
}
