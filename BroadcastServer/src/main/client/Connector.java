package main.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import lombok.Getter;
import main.Main;
import main.api.events.EventManager;
import main.api.events.events.ClientDisconnectEvent;
import main.api.events.events.PacketReceiveEvent;
import main.api.events.events.PacketSendEvent;
import main.api.packet.Packet;
import main.api.packet.UnknownPacket;

public class Connector implements Runnable{
	public static boolean SERVER = false;

	@Getter
	protected Socket socket;
	
	protected DataInputStream input;
	protected DataOutputStream output;
	@Getter
	protected String name;
	protected UUID uuid = UUID.randomUUID();
	protected Thread thread;
	
	protected Connector(Socket socket) throws IOException {
		this.socket = socket;
		this.input = new DataInputStream(this.socket.getInputStream());
		this.output = new DataOutputStream(this.socket.getOutputStream());
	}
	
	protected void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	public void close() {
		try {
			this.socket.close();
			this.input.close();
			this.output.close();
			EventManager.callEvent(new ClientDisconnectEvent(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
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
		}finally {
			close();
		}
	}
	
	public boolean equals(Connector con) {
		return uuid.compareTo(con.uuid) == 0;
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
		return this.name != null ? this.socket.getInetAddress().toString() : this.name;
	}
}
