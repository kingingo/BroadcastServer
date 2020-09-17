package main;

import lombok.Getter;
import main.api.packet.Packet;
import main.client.ClientListener;
import main.lobby.LobbyController;
import main.server.BroadcastServer;
import main.terminal.Terminal;

public class Main {
	public static BroadcastServer server;
	@Getter
	public static LobbyController lobbyController;
	public static ClientListener listener;
	
	public static void main(String[] args) {
		Log.init();
		Packet.loadPackets();
		Terminal.loadCommands();
		Terminal.getInstance();
		lobbyController = new LobbyController();
		listener = new ClientListener();
		server = new BroadcastServer(6001);
	}
	
	public static void log(String msg) {
		System.out.println("INFO | "+msg);
	}
}
