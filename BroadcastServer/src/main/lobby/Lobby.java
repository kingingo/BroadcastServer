package main.lobby;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import main.Main;
import main.api.packet.Packet;
import main.client.Client;
import main.lobby.packets.client.LobbyUpdatePacket;
import main.lobby.packets.server.LobbyClosePacket;
import main.lobby.packets.server.LobbyErrorPacket;
import main.lobby.packets.server.LobbyPlayersPacket;

@Getter
public class Lobby{
	
	private ArrayList<Client> clients = new ArrayList<Client>();
	private String name;
	private String owner;
	private boolean open = true;
	@Setter
	private byte[] settings;
	
	public Lobby(String name) {
		this.name = name;
	}
	
	public void close() {
		write(new LobbyClosePacket());
		for(int i = 0; i < this.clients.size(); i++)
			this.clients.get(i).setLobby(null);
		this.clients.clear();
	}
	
	public void leave(Client client) {
		try {
			Main.log(client+" left "+getName());
			client.setLobby(null);
			this.clients.remove(client);
			if(client.getName().equalsIgnoreCase(owner)) {
				Main.lobbyController.closeLobby(name);
			}else if(this.clients.isEmpty()) {
				Main.lobbyController.closeLobby(name);
			}else write(new LobbyPlayersPacket(getName(), isOpen(), getOwner(), getClientnames()));
		}catch(ConcurrentModificationException e) {
			e.printStackTrace();
		}
	}
	
	public void enter(Client client) {
		if(clients.size()>=7) {
			client.write(new LobbyErrorPacket(LobbyErrorPacket.FULL));
			return;
		}

		if(clients.size() == 0) {
			this.owner = client.getName();
		}
		
		Main.log(client+" entered "+name);
		client.setLobby(this);
		this.clients.add(client);
		write(new LobbyPlayersPacket(name,open,owner, getClientnames()));
	}
	
	public ArrayList<String> getClientnames(){
		ArrayList<String> names = new ArrayList<String>();
		this.clients.forEach(client -> {names.add(client.getName());});
		return names;
	}
	
	public void update(List<Client> blacklist) {
		write(new LobbyUpdatePacket(settings),blacklist);
	}
	
	public void write(final Packet packet) {
		write(packet,null);
	}
	
	public void write(final Packet packet, List<Client> blacklist) {
		for(int i = 0; i < this.clients.size(); i++) 
			if(blacklist==null || !blacklist.contains(this.clients.get(i))) 
				this.clients.get(i).write(packet);
	}
}
