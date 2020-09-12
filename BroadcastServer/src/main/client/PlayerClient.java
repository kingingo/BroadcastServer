package main.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import main.api.events.EventManager;
import main.api.events.events.ClientConnectEvent;

public class PlayerClient extends Connector{

	protected PlayerClient(String name, String host, int port) throws UnknownHostException, IOException {
		super(new Socket(host, port));
		this.name = name;
		this.output.writeUTF(this.name);
		this.output.flush();
		EventManager.callEvent(new ClientConnectEvent(this));
		
		start();
	}

}
