package main.lobby.packets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import main.api.packet.Packet;
import main.lobby.Lobby;

public class LobbyPlayersPacket extends Packet{
	@Getter
	private ArrayList<String> players;
	
	public LobbyPlayersPacket() {}
	
	public LobbyPlayersPacket(ArrayList<String> players) {
		this.players=players;
	}
	
	@Override
	public void parseFromInput(DataInputStream in) throws IOException {
		this.players = new ArrayList<String>();
		int length = in.readInt();
		for(int i = 0; i < length; i++)
			this.players.add(in.readUTF());
	}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {
		out.writeInt(players.size());
		for(String name : this.players) {
			out.writeUTF(name);
		}
	}
}
