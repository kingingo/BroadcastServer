package main.lobby.packets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import main.api.packet.Packet;
import main.lobby.Lobby;

public class LobbyErrorPacket extends Packet{
	
	public static final int ENTERED = 0;
	public static final int FULL = 1;
	public static final int NOT_FOUND = 2;
	public static final int NAME_ALREADY_TAKEN = 3;
	public static final int CLOSED = 4;
	
	@Getter
	private int status;
	
	public LobbyErrorPacket() {}
	
	public LobbyErrorPacket(int status) {
		this.status=status;
	}
	
	@Override
	public void parseFromInput(DataInputStream in) throws IOException {
		this.status=in.readInt();
	}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {
		out.writeInt(status);
	}
	
	public String toString() {
		return "StatusCode: "+status;
	}
}
