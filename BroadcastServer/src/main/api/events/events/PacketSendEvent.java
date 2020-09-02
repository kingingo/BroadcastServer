package main.api.events.events;


import lombok.Getter;
import lombok.Setter;
import main.api.events.Cancellable;
import main.api.events.Event;
import main.api.packet.Packet;
import main.client.Client;

@Getter
@Setter
public class PacketSendEvent extends Event implements Cancellable{
	private Packet packet;
	private Client client;
	private boolean cancelled;
	
	public PacketSendEvent(Packet packet,Client client) {
		this.packet = packet;
		this.client = client;
	}
}
