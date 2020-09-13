package main.client;

import main.api.events.EventHandler;
import main.api.events.EventListener;
import main.api.events.EventManager;
import main.api.events.events.PacketReceiveEvent;
import main.client.packets.PingPacket;
import main.client.packets.PongPacket;

public class PlayerClientListener implements EventListener{

	public PlayerClientListener() {
		EventManager.register(this);
	}
	
	@EventHandler
	public void rec(PacketReceiveEvent ev) {
		PlayerClient client = (PlayerClient)ev.getConnector();
		if(ev.getPacket() instanceof PingPacket) {
			client.lastPing = System.currentTimeMillis() - ev.getPacket(PingPacket.class).getTime();
			client.lastPingTime = System.currentTimeMillis();
			
			client.write(new PongPacket(System.currentTimeMillis()));
		}
	}
}
