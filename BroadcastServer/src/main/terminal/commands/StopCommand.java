package main.terminal.commands;

import main.Main;
import main.terminal.CommandExecutor;
import main.terminal.Terminal;

public class StopCommand implements CommandExecutor{

	@Override
	public void onCommand(String[] args) {
		Main.server.stop();
		Main.log("Terminal closed.");
		Terminal.getInstance().stop();
		System.exit(0);
	}

}
