package main.terminal;
public interface CommandExecutor {
	public default String getCommand() {
		return getClass().getSimpleName().toLowerCase().replaceAll("command", "");
	}
	
	public void onCommand(String[] args);
}
