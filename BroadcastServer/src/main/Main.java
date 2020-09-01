package main;

import main.server.BroadcastServer;
import main.terminal.Terminal;

public class Main {
	public static BroadcastServer server;
	
	public static void main(String[] args) {
		Terminal.loadCommands();
		Terminal.getInstance();
		server = new BroadcastServer(6000);
	}
	
	public static void log(String msg) {
		System.out.println("Server | "+msg);
	}
}
