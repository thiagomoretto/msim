package msim.ui;

import msim.Machine;
import msim.Memory;
import msim.dp.MachineFacade;
import msim.mips.MIPSProcessor;

public class Startup {
	public static void main(String ... args ){
		Machine machine = new Machine(new MIPSProcessor(), new Memory(1024));
		MachineFacade facade = new MachineFacade(machine);
		Interface f = new Interface(facade);
		f.displayAll();
		machine.add(f);
	}
}
