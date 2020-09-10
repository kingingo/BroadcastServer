package main.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import lombok.Getter;
import main.Main;
import main.api.events.EventHandler;
import main.api.events.EventListener;
import main.api.events.EventManager;
import main.api.events.events.ClientDisconnectEvent;
import main.api.events.events.PacketReceiveEvent;
import main.api.packet.Packet;
import main.client.Client;

public class BroadcastServer implements Runnable, EventListener{

	@Getter
	private ArrayList<Client> clients = new ArrayList<Client>();
	
	private ServerSocket server;
	private Thread thread;
	
	private boolean active = false;
	private int port;
	
	public BroadcastServer(int port) {
		this.thread = new Thread(this);
		this.port = port;
		EventManager.register(this);
		start();
	}
	
	public boolean start() {
		if(!this.active) {
			this.active=true;
			try {
				this.server = new ServerSocket(this.port);
				this.thread.start();
				Main.log("started on "+this.server.getInetAddress().toString()+":"+this.port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void stop() {
		if(this.active) {
			this.active=false;
			
			try {
				this.server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for(int i = 0; i < this.clients.size(); i++)
				this.clients.get(i).close();
			
			Main.log("server stopped...");
			EventManager.unregister(this);
		}
	}
	
//	@EventHandler
//	public void receive(PacketReceiveEvent ev) {
//		
//		Client client;
//		for(int i = 0; i < this.clients.size(); i++) {
//			client = this.clients.get(i);
//			if(!ev.getConnector().equals(client))
//				client.write(ev.getPacket());
//		}
//	}
	
	@EventHandler
	public void disconnect(ClientDisconnectEvent ev) {
		this.clients.remove(ev.getConnector());
		Main.log("Client "+ev.getConnector()+" disconnected");
	}
	
	@Override
	public void run() {
		Main.log("listining...");
		while(this.active) {
			try {
				Socket socket = this.server.accept();
				this.clients.add(new Client(socket));
			} catch(SocketException e) {
				if(e.getMessage().equalsIgnoreCase("socket closed"))return;
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
