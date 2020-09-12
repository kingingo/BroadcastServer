package main.client.connector.futures;

import main.api.packet.Packet;
import main.client.connector.Connector;
import main.client.connector.PacketListener;

public class WaitForPacketProgressFuture<T extends Packet> extends BaseProgressFuture<T> implements PacketListener{

	private int packetId = -1;
	private Connector connector;
	
	public WaitForPacketProgressFuture(Connector connector, Class<? extends Packet> clazz) {
		this(connector,Packet.getId(clazz));
	}
	
	public WaitForPacketProgressFuture(Connector connector, String packetname) {
		this(connector,Packet.getId(packetname));
	}
	
	public WaitForPacketProgressFuture(Connector connector, int packetId) {
		this.packetId = packetId;
		this.connector=connector;
		this.connector.register(this);
	}
	
	@Override
	protected void done(T response) {
		super.done(response);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean handle(Packet packet) {
		if(packet.getId() == this.packetId) {
			this.connector.unregister(this);
			this.done((T) packet);
			return true;
		}
		return false;
	}
}
