package main.api.events.events;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import main.api.events.Event;
import main.api.packet.Packet;
import main.client.Client;

@Getter
@Setter
@AllArgsConstructor
public class PacketReceiveEvent extends Event{
	private Packet packet;
	private Client client;
}
