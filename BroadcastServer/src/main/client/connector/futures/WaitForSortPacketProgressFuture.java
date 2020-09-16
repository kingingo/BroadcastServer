package main.client.connector.futures;

import main.api.packet.Packet;
import main.client.connector.Connector;
import main.client.connector.PacketListener;
import main.client.packets.SortPacket;

public class WaitForSortPacketProgressFuture<T extends Packet> extends BaseProgressFuture<T> implements PacketListener{

	private int order;
	private int packetId = -1;
	private Connector connector;
	
	public WaitForSortPacketProgressFuture(int timeout, Connector connector, Class<? extends Packet> clazz, int order) {
		this(timeout,connector,Packet.getId(clazz),order);
	}
	
	public WaitForSortPacketProgressFuture(int timeout, Connector connector, String packetname, int order) {
		this(timeout,connector,Packet.getId(packetname),order);
	}
	
	public WaitForSortPacketProgressFuture(int timeout, Connector connector, int packetId, int order) {
		super(timeout);
		this.order = order;
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
		if(packet instanceof SortPacket) {
			SortPacket spacket = (SortPacket)packet;
			if(this.order == spacket.getOrder() && spacket.getPacket().getId() == this.packetId) {
				this.connector.unregister(this);
				this.done((T) spacket.getPacket());
				return true;
			}
		}
		
		
		return false;
	}
}
