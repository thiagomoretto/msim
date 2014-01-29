package msim;

public class Instruction {

	private long instruction;
	
	public Instruction(long i) {
		instruction = i;
	}
	
	public long extract(int mask) {
		return instruction & mask;
	}

	public long extract(int mask, int sl) {
		return (instruction & mask) >>> sl;
	}
	
	public Instruction extractInstruction(int mask) {
		return Instruction.toInstruction(instruction & mask);
	}

	public Instruction extractInstruction(int mask, int sl) {
		return Instruction.toInstruction((instruction >>> sl) & mask);
	}

	public long getLong() {
		return instruction;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<32; i++) {
			buffer.append(((instruction >>> 31-i) & 0x01) == 1 ? "1" : "0");
		}
		return buffer.toString();
	}
	
	public static  Instruction toInstruction(long i) {
		return new Instruction(i);
	}
}
