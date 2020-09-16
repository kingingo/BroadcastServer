package main.lobby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import main.Main;
import main.api.events.EventHandler;
import main.api.events.EventListener;
import main.api.events.EventManager;
import main.api.events.events.ClientConnectEvent;
import main.api.events.events.ClientDisconnectEvent;
import main.api.events.events.PacketReceiveEvent;
import main.api.packet.Packet;
import main.client.Client;
import main.lobby.packets.client.LobbyCreatePacket;
import main.lobby.packets.client.LobbyEnterPacket;
import main.lobby.packets.client.LobbyLeavePacket;
import main.lobby.packets.client.LobbyUpdatePacket;
import main.lobby.packets.server.LobbyErrorPacket;
import main.lobby.packets.server.LobbyListPacket;

public class LobbyController implements EventListener{
	private HashMap<String,Lobby> lobbys = new HashMap<String,Lobby>();
	
	public LobbyController() {
		EventManager.register(this);
	}
	
	public boolean closeLobby(String name) {
		Lobby lobby = getLobby(name);
		
		if(lobby != null) {
			lobby.close();
			this.lobbys.remove(name.toLowerCase());
			Main.log(lobby.getName()+" closed!");
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
	
	public void update() {
		LobbyListPacket packet = new LobbyListPacket(new ArrayList<Lobby>(getLobbys().values()));
		ArrayList<Client> list = Main.server.getClients();
		Client client;
		for(int i = 0; i < list.size(); i++) {
			client = list.get(i);
			if(!client.hasLobby()) {
				client.write(packet);
			}
		}
	}
	
	public HashMap<String,Lobby> getLobbys(){
		return this.lobbys;
	}
	
	@EventHandler
	public void disconnect(ClientDisconnectEvent ev) {
		Client client = (Client) ev.getConnector();
		
		if(client.hasLobby()) {
			Lobby lobby = client.getLobby();
			
			lobby.leave(client);
			update();
		}
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
					update();
				} else {
					client.write(new LobbyErrorPacket(LobbyErrorPacket.NOT_FOUND));
				}
			} else if(ev.getPacket() instanceof LobbyCreatePacket){
				LobbyCreatePacket packet = (LobbyCreatePacket) ev.getPacket();
				
				if(createLobby(packet.getLobbyname())) {
					getLobby(packet.getLobbyname()).enter(client);
					update();
				}else {
					client.write(new LobbyErrorPacket(LobbyErrorPacket.NAME_ALREADY_TAKEN));
				}
			}
		} else {
			Lobby lobby = client.getLobby();
			
			if(ev.getPacket() instanceof LobbyLeavePacket){
				lobby.leave(client);
				update();
			}else if(ev.getPacket() instanceof LobbyUpdatePacket){
				LobbyUpdatePacket packet = (LobbyUpdatePacket)ev.getPacket();
				Main.log(lobby.getName()+" update settings to all clients!");
				lobby.setSettings(packet.getArr());
				lobby.update(Arrays.asList(client));
			} else if(!Packet.KnowPacket(ev.getPacket())){
				Main.log("Got Packet from "+ev.getConnector().getName()+" send to lobby "+lobby.getName());
				lobby.write(ev.getPacket(), Arrays.asList(client));
			}
		}
	}
}
