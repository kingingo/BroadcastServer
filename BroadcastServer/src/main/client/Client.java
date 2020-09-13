package main.client;

import java.io.IOException;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;
import main.Main;
import main.api.events.EventListener;
import main.api.events.EventManager;
import main.api.events.events.ClientConnectEvent;
import main.client.connector.Connector;
import main.lobby.Lobby;

public class Client extends Connector {

	@Setter
	@Getter
	private Lobby lobby;
	@Getter
	private PingManager pingManager;
	
	public Client(Socket socket) throws IOException {
		super(socket);
		this.pingManager = new PingManager(this);
		start();
	}
	
	public void close() {
		super.close();
		System.out.println("Lost connection to "+getName()+" ("+pingManager.getCurrentPing()+"ms)");
	}

	public boolean hasLobby() {
		return this.lobby != null;
	}
}
