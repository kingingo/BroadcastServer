package main.lobby;

import java.util.ArrayList;
import java.util.HashMap;

import main.Main;
import main.api.events.EventHandler;
import main.api.events.EventListener;
import main.api.events.EventManager;
import main.api.events.events.ClientConnectEvent;
import main.api.events.events.PacketReceiveEvent;
import main.client.Client;
import main.lobby.packets.client.LobbyCreatePacket;
import main.lobby.packets.client.LobbyEnterPacket;
import main.lobby.packets.server.LobbyErrorPacket;
import main.lobby.packets.server.LobbyListPacket;

public class LobbyController implements EventListener{
	private HashMap<String,Lobby> lobbys = new HashMap<String,Lobby>();
	
	public LobbyController() {
		EventManager.register(this);
		
		createLobby("Lobby 1");
	}
	
	public boolean closeLobby(String name) {
		Lobby lobby = getLobby(name);
		
		if(lobby != null) {
			lobby.close();
			this.lobbys.remove(name.toLowerCase());
			return true;
		}
		return false;
	}
	
	public boolean createLobby(String name) {
		if(this.lobbys.containsKey(name.toLowerCase())) {
			return false;
		}
		
		this.lobbys.put(name.toLowerCase(),new Lobby(name));
		return true;
	}
	
	public Lobby getLobby(String name) {
		if(this.lobbys.containsKey(name.toLowerCase()))
			return this.lobbys.get(name.toLowerCase());
		else 
			return null;
	}
	
	public HashMap<String,Lobby> getLobbys(){
		return this.lobbys;
	}
	
	@EventHandler
	public void connect(ClientConnectEvent ev) {
		ev.getConnector().write(new LobbyListPacket( new ArrayList<Lobby>(getLobbys().values()) ));
		Main.log(ev.getConnector() + " send LobbyListPacket");
	}

	@EventHandler
	public void rec(PacketReceiveEvent ev) {
		Client client = (Client) ev.getConnector();
		if(!client.hasLobby()) {
			if(ev.getPacket() instanceof LobbyEnterPacket) {
				LobbyEnterPacket packet = (LobbyEnterPacket) ev.getPacket();
				
				Lobby lobby = getLobby(packet.getLobbyname());
				
				if(lobby != null) {
					lobby.enter(client);
				} else {
					client.write(new LobbyErrorPacket(LobbyErrorPacket.NOT_FOUND));
				}
			} else if(ev.getPacket() instanceof LobbyCreatePacket){
				LobbyCreatePacket packet = (LobbyCreatePacket) ev.getPacket();
				
				if(createLobby(packet.getLobbyname())) {
					getLobby(packet.getLobbyname()).enter(client);
				}else {
					client.write(new LobbyErrorPacket(LobbyErrorPacket.NAME_ALREADY_TAKEN));
				}
			}
		}
	}
}
