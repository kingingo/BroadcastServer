package main.lobby.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import main.api.packet.Packet;

public class LobbyUpdatePacket extends Packet{
	
	@Getter
	private byte[] arr;
	
	public LobbyUpdatePacket() {}
	
	public LobbyUpdatePacket(byte[] arr) {
		this.arr=arr;
	}
	
	@Override
	public void parseFromInput(DataInputStream in) throws IOException {
		int length;
		arr = new byte[length=in.readInt()];
		in.read(arr, 0, length);
	}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {
		out.writeInt(arr.length);
		out.write(arr, 0, arr.length);
	}
}
