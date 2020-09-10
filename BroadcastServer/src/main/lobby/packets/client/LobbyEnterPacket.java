package main.lobby.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import main.api.packet.Packet;
import main.lobby.Lobby;

public class LobbyEnterPacket extends Packet{
	
	@Getter
	private String lobbyname;
	
	public LobbyEnterPacket() {}
	
	public LobbyEnterPacket(String lobbyname) {
		this.lobbyname=lobbyname;
	}
	
	@Override
	public void parseFromInput(DataInputStream in) throws IOException {
		this.lobbyname=in.readUTF();
	}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {
		out.writeUTF(lobbyname);
	}
}
