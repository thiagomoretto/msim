package msim.dp;

public interface MachineObserver {

	public void notifyExecution();
	
	public void notifyMessage( String message );
	
	public void updateSnapshot();
}
