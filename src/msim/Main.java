package msim;

import java.io.File;
import java.io.FileInputStream;

import msim.mips.MIPSProcessor;

public class Main {

	public static void main(String[] args) 
	{
		Memory memory01 = new Memory(1024); // 1Kb
		
		Machine machine = new Machine(
				new MIPSProcessor(), 
				memory01	);
		
		try {
			if(args.length > 0) {
				File program = new File(args[0]);
			
				if (program.canRead()) {
					machine.load( new FileInputStream(program) , 0x0000 );
					machine.setBootAddress(0x0000);
				} else {
					System.out.println("Can't read the program.");	
				}
			} 
			else {
				System.out.println("usage:");
				System.out.println("\tmsim [mips program]");
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
