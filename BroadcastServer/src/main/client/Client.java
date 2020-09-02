package main.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

import lombok.Getter;
import main.Main;
import main.api.events.Event;
import main.api.events.EventManager;
import main.api.events.events.PacketReceiveEvent;
import main.api.events.events.PacketSendEvent;
import main.api.packet.Packet;
import main.api.packet.UnknownPacket;

public class Client implements Runnable{

	@Getter
	private Socket socket;
	
	private DataInputStream input;
	private DataOutputStream output;
	@Getter
	private String name;
	private UUID uuid = UUID.randomUUID();
	
	public Client(Socket socket) throws IOException {
		this.socket = socket;
		init();
	}
	
	public Client(String name, String host, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(host, port);
		this.name = name;
		init();
		
		this.output.writeUTF(this.name);
		this.output.flush();
	}
	
	private void init() throws IOException {
		this.input = new DataInputStream(this.socket.getInputStream());
		this.output = new DataOutputStream(this.socket.getOutputStream());
	}
	
	public void close() {
		try {
			this.socket.close();
			this.input.close();
			this.output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			this.name = this.input.readUTF();
			Main.log(this + " is connected");
			
			while(this.input != null) {
				int length = this.input.readInt();
				int id = this.input.readInt();
				
				byte[] data = new byte[length];
				this.input.read(data, 0, length);
				
				Packet packet = new UnknownPacket(id, data);
				Main.log(this+" -> "+packet.getId() + "(bytes:"+packet.getData().length+")");
				
				EventManager.callEvent(new PacketReceiveEvent(packet,this));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean equals(Client client) {
		return uuid.compareTo(client.uuid) == 0;
	}
	
	public boolean write(Packet packet) {
		try {
			PacketSendEvent event = new PacketSendEvent(packet, this);
			EventManager.callEvent(event);
			
			if(!event.isCancelled()) {
				byte[] packetBytes = packet.toByteArray();
				this.output.writeInt(packetBytes.length);
				this.output.writeInt(packet.getId());
				this.output.write(packetBytes, 0, packetBytes.length);
				this.output.flush();
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String toString() {
		return this.name.isBlank() ? this.socket.getInetAddress().toString() : this.name;
	}
}
