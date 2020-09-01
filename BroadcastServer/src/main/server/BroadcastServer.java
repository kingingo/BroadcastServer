package main.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import lombok.Getter;
import main.Main;
import main.client.Client;
import main.client.Packet;

public class BroadcastServer implements Runnable{

	@Getter
	private ArrayList<Client> clients = new ArrayList<Client>();
	
	private ServerSocket server;
	private Thread thread;
	
	private boolean active = false;
	private int port;
	
	public BroadcastServer(int port) {
		this.thread = new Thread(this);
		this.port = port;
		
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
			
			for(Client client : this.clients) 
				client.close();
			
			Main.log("server stopped...");
		}
	}
	
	public void receive(Client sender, Packet packet) {
		for(Client client : this.clients) {
			client.write(packet);
		}
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
