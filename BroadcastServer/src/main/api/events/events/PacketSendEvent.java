package main.api.events.events;


import lombok.Getter;
import lombok.Setter;
import main.api.events.Cancellable;
import main.api.events.Event;
import main.api.packet.Packet;
import main.client.connector.Connector;

@Getter
@Setter
public class PacketSendEvent extends Event implements Cancellable{
	private Packet packet;
	private Connector connector;
	private boolean cancelled;
	
	public PacketSendEvent(Packet packet,Connector connector) {
		this.packet = packet;
		this.connector = connector;
	}
	
	@SuppressWarnings({ "unchecked", "FinalStaticMethod" })
	public <P extends Packet> P getPacket(Class<P> clazz){
		return ((P) packet);
	}
}
