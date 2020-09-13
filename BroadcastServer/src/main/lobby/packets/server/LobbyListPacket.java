package main.lobby.packets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.api.packet.Packet;
import main.lobby.Lobby;

public class LobbyListPacket extends Packet{
	
	@AllArgsConstructor
	public class LobbySettings{
		public String lobbyname;
		public int size;
		public boolean open;
	} 
	
	@Getter
	private ArrayList<LobbySettings> lobbys = new ArrayList<LobbySettings>();
	
	public LobbyListPacket() {}
	
	public LobbyListPacket(ArrayList<Lobby> lobbys) {
		lobbys.forEach(lobby -> { this.lobbys.add(new LobbySettings(lobby.getName(), lobby.getClients().size(), lobby.isOpen())); });
	}
	
	@Override
	public void parseFromInput(DataInputStream in) throws IOException {
		int length = in.readInt();
		for(int i = 0; i < length; i++) 
			this.lobbys.add(new LobbySettings(in.readUTF(), in.readInt(), in.readBoolean()));
	}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {
		out.writeInt(lobbys.size());
		for(LobbySettings settings : this.lobbys) {
			out.writeUTF(settings.lobbyname);
			out.writeInt(settings.size);
			out.writeBoolean(settings.open);
		}
	}
}
