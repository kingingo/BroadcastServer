package main.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lombok.Getter;
import main.Main;

public class Client implements Runnable{

	@Getter
	private Socket socket;
	
	private DataInputStream input;
	private DataOutputStream output;
	@Getter
	private String name;
	
	public Client(Socket socket) throws IOException {
		this.socket = socket;
		this.input = new DataInputStream(socket.getInputStream());
		this.output = new DataOutputStream(socket.getOutputStream());
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
				
				Packet packet = new Packet(data,id);
				Main.log(this+" -> "+packet.getId() + "(bytes:"+packet.getData().length+")");
				Main.server.receive(this, packet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean write(Packet packet) {
		try {
			byte[] packetBytes = packet.getData();
			this.output.writeInt(packetBytes.length);
			this.output.writeInt(packet.getId());
			this.output.write(packetBytes, 0, packetBytes.length);
			this.output.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String toString() {
		return this.name.isBlank() ? this.socket.getInetAddress().toString() : this.name;
	}
}
