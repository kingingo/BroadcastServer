package main.api.events.events;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import main.api.events.Event;
import main.api.packet.Packet;
import main.client.Client;
import main.client.Connector;

@Getter
@Setter
@AllArgsConstructor
public class ClientConnectEvent extends Event{
	private Connector connector;
}
