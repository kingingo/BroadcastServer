package main.client.connector;

import main.api.packet.Packet;

public interface PacketListener {
	public boolean handle(Packet packet);
}