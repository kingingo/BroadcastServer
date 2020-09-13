package main.client;

import main.api.events.EventHandler;
import main.api.events.EventListener;
import main.api.events.EventManager;
import main.api.events.events.ClientConnectEvent;
import main.api.events.events.PacketReceiveEvent;
import main.client.packets.PongPacket;

public class ClientListener implements EventListener{

	public ClientListener() {
		EventManager.register(this);
	}
	
	@EventHandler
	public void rec(PacketReceiveEvent ev) {
		Client client = (Client)ev.getConnector();
		if(ev.getPacket() instanceof PongPacket) {
			client.getPingManager().handlePong( ev.getPacket(PongPacket.class) );
			client.getPingManager().ping();
		}
	}
	
	@EventHandler
	public void connect(ClientConnectEvent ev) {
		Client client = (Client)ev.getConnector();
		client.getPingManager().ping();
	}
}
