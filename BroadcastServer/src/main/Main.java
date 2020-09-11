package main;

import lombok.Getter;
import main.lobby.LobbyController;
import main.server.BroadcastServer;
import main.terminal.Terminal;

public class Main {
	public static BroadcastServer server;
	@Getter
	public static LobbyController lobbyController;
	
	public static void main(String[] args) {
		Terminal.loadCommands();
		Terminal.getInstance();
		lobbyController = new LobbyController();
		server = new BroadcastServer(6001);
	}
	
	public static void log(String msg) {
		System.out.println("INFO | "+msg);
	}
}
