package main.client.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import main.api.packet.Packet;

public class SortPacket extends Packet{

	private int order;
	private Packet packet;
	
	public SortPacket() {}
	
	public SortPacket(int order,Packet packet) {
		this.order = order;
		this.packet=packet;
	}

	@Override
	public void parseFromInput(DataInputStream in) throws IOException {
		this.order = in.readInt();
		
		int id = in.readInt();
		int length = in.readInt();
		byte[] arr = new byte[length];
		in.read(arr, 0, length);
		this.packet = Packet.create(id, arr);
	}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {
		out.writeInt(this.order);
		out.writeInt(this.packet.getId());
		byte[] arr = this.packet.getData();
		out.writeInt(arr.length);
		out.write(arr);
	}
	
	
	
}
