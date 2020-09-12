package main.lobby.packets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import main.api.packet.Packet;
import main.lobby.Lobby;

public class LobbyListPacket extends Packet{
	
	@Getter
	private HashMap<String,Integer> lobbys = new HashMap<String,Integer>();
	
	public LobbyListPacket() {}
	
	public LobbyListPacket(ArrayList<Lobby> lobbys) {
		lobbys.forEach(lobby -> {this.lobbys.put(lobby.getName(), lobby.getClients().size());});
	}
	
	@Override
	public void parseFromInput(DataInputStream in) throws IOException {
		int length = in.readInt();
		for(int i = 0; i < length; i++) 
			this.lobbys.put(in.readUTF(), in.readInt());
	}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {
		out.writeInt(lobbys.size());
		for(String name : this.lobbys.keySet()) {
			out.writeUTF(name);
			out.writeInt(this.lobbys.get(name));
		}
	}
}
