package main.api.events.events;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import main.api.events.Event;
import main.api.packet.Packet;
import main.client.Client;
import main.client.connector.Connector;

@Getter
@Setter
@AllArgsConstructor
public class PacketReceiveEvent extends Event{
	private Packet packet;
	private Connector connector;
	
	@SuppressWarnings({ "unchecked", "FinalStaticMethod" })
	public <P extends Packet> P getPacket(Class<P> clazz){
		return ((P) packet);
	}
}
