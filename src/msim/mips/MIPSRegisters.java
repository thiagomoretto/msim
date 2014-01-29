package msim.mips;

import java.io.Serializable;

import msim.Registers;

/**
 * MIPS Registers
 * http://www.cs.jcu.edu.au/Subjects/cp2003/1998/SPIM/registers.html
 * 
 * @author Thiago Moretto <thiago@moretto.eng.br>
 */
@SuppressWarnings("serial")
public class MIPSRegisters implements Registers, Serializable {
	
	public static final int REG_SIZE = 34;
	
	public static final int ZERO = 0;
	public static final int AT = 1;
	
	public static final int V0 = 2;
	public static final int V1 = 3;
	
	public static final int A0 = 4;
	public static final int A1 = 5;
	public static final int A2 = 6;
	public static final int A3 = 7;
	
	public static final int T0 = 8;
	public static final int T1 = 9;
	public static final int T2 = 10;
	public static final int T3 = 11;
	public static final int T4 = 12;
	public static final int T5 = 13;
	public static final int T6 = 14;
	public static final int T7 = 15;
	
	public static final int S0 = 16;
	public static final int S1 = 17;
	public static final int S2 = 18;
	public static final int S3 = 19;
	public static final int S4 = 20;
	public static final int S5 = 21;
	public static final int S6 = 22;
	public static final int S7 = 23;

	public static final int T8 = 24;
	public static final int T9 = 25;

	public static final int K0 = 26;
	public static final int K1 = 27;

	public static final int GP = 28;
	public static final int SP = 29;
	public static final int FP = 30;
	
	public static final int RA = 31;
	
	public static final int HI = 32;
	public static final int LO = 33;
	
	public char[] cache;
	
	public MIPSRegisters() {
		cache = new char[REG_SIZE * 4];
	}
	
	public int read(int regaddr) {
		return ((cache[4*regaddr] << 24) + 
				(cache[4*regaddr+1] << 16) + 
				(cache[4*regaddr+2] << 8) + 
				(cache[4*regaddr+3] << 0));
	}

	public void write(int regaddr, int data) {
		cache[4*regaddr] =   (char) ((data >>> 24) & 0xFF);
		cache[4*regaddr+1] = (char) ((data >>> 16) & 0xFF);
		cache[4*regaddr+2] = (char) ((data >>>  8) & 0xFF);
		cache[4*regaddr+3] = (char) ((data >>>  0) & 0xFF);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		int addr = 0;
		for (int i=0; i<REG_SIZE*4;i+=4) {
			buffer.append(String.format("%05d 0x%02x 0x%02x 0x%02x 0x%02x ; %02d %02d %02d %02d\n", 
					addr, (int)cache[i], (int)cache[i+1], (int)cache[i+2], (int)cache[i+3],
						  (int)cache[i], (int)cache[i+1], (int)cache[i+2], (int)cache[i+3]));
			addr++;
		}
		
		return buffer.toString();
	}

	public void write(long r, long d) {
		write((int)r, (int)d);
	}

	public int read(long r) {
		return read((int)r);
	}

	public void reset() {
		for (int i=0; i<REG_SIZE;i++) {
			write(i, 0x000000);
		}
	}

	public int readByte(int r) {
		return cache[r];
	}

	public int readByte(long r) {
		return cache[(int)r];
	}
}
