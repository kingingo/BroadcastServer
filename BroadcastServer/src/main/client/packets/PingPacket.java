package main.client.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.api.packet.Packet;

@AllArgsConstructor
public class PingPacket extends Packet{
	
	@Getter
	private long time;
	
	public PingPacket() {}
	
	@Override
	public void parseFromInput(DataInputStream in) throws IOException {
		this.time = in.readLong();
	}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {
		out.writeLong(this.time);
	}
	
	public String toString() {
		return this.getPacketName()+" time:"+getTime();
	}
}
