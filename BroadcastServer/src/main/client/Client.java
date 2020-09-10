package main.client;

import java.io.IOException;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;
import main.Main;
import main.api.events.EventManager;
import main.api.events.events.ClientConnectEvent;
import main.lobby.Lobby;

public class Client extends Connector{

	@Setter
	@Getter
	private Lobby lobby;
	
	public Client(Socket socket) throws IOException {
		super(socket);
		this.name = this.input.readUTF();
		Main.log(this + " is connected");
		EventManager.callEvent(new ClientConnectEvent(this));
		start();
	}

	public boolean hasLobby() {
		return this.lobby != null;
	}
}
