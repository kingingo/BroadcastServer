package main.lobby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import main.Main;
import main.api.events.EventHandler;
import main.api.events.EventListener;
import main.api.events.EventManager;
import main.api.events.events.ClientDisconnectEvent;
import main.api.events.events.PacketReceiveEvent;
import main.api.packet.Packet;
import main.client.Client;
import main.lobby.packets.client.LobbyEnterPacket;
import main.lobby.packets.client.LobbyLeavePacket;
import main.lobby.packets.client.LobbyUpdatePacket;
import main.lobby.packets.server.LobbyErrorPacket;
import main.lobby.packets.server.LobbyPlayersPacket;

public class Lobby implements EventListener{
	
	@Getter
	private ArrayList<Client> clients = new ArrayList<Client>();
	@Getter
	private String name;
	private String owner;
	private byte[] settings;
	
	public Lobby(String name) {
		this.name = name;
		EventManager.register(this);
	}
	
	public void close() {
		//CLOSE PACKET
		EventManager.unregister(this);
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
		this.clients.add(client);
		write(new LobbyPlayersPacket(name,owner, getClientnames()));
	}
	
	public ArrayList<String> getClientnames(){
		ArrayList<String> names = new ArrayList<String>();
		this.clients.forEach(client -> {names.add(client.getName());});
		return names;
	}
	
	public void write(final Packet packet) {
		write(packet,null);
	}
	
	public void write(final Packet packet, List<Client> blacklist) {
		this.clients.forEach(client -> {
			if(blacklist==null || !blacklist.contains(client)) {
				client.write(packet);
			}
		});;
	}
	
	@EventHandler
	public void rec(PacketReceiveEvent ev) {
		Client client = (Client)ev.getConnector();
		if(this.clients.contains(client) && ev.getPacket() instanceof LobbyLeavePacket){
			Main.log(client+" leaved "+name);
			this.clients.remove(client);
			write(new LobbyPlayersPacket(name,owner, getClientnames()));
		}else if(ev.getPacket() instanceof LobbyUpdatePacket){
			LobbyUpdatePacket packet = (LobbyUpdatePacket)ev.getPacket();
			
			this.settings=packet.getArr();
			update(Arrays.asList(client));
		} else if(!Packet.KnowPacket(ev.getPacket())){
			write(ev.getPacket(), Arrays.asList(client));
		}
	}
	
	private void update(List<Client> blacklist) {
		write(new LobbyUpdatePacket(settings),blacklist);
	}
	
	@EventHandler
	public void disconnect(ClientDisconnectEvent ev) {
		if(clients.contains((Client)ev.getConnector())) {
			clients.remove((Client) ev.getConnector());
			Main.log(ev.getConnector()+" leaved "+name);
			
			if(clients.isEmpty()) {
				Main.lobbyController.closeLobby(name);
			}
		}
	}
}
