package main.client.connector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;
import main.Main;
import main.api.events.EventManager;
import main.api.events.events.ClientConnectEvent;
import main.api.events.events.ClientDisconnectEvent;
import main.api.events.events.PacketReceiveEvent;
import main.api.events.events.PacketSendEvent;
import main.api.packet.Packet;
import main.client.Client;
import main.client.PingManager;
import main.client.connector.futures.WaitForPacketProgressFuture;

public abstract class Connector implements Runnable{

	public static boolean log = false;
	
	@Getter
	protected Socket socket;
	private boolean active = false;
	
	protected DataInputStream input;
	protected DataOutputStream output;
	private ArrayList<PacketListener> listeners = new ArrayList<PacketListener>();
	
	@Getter
	protected String name;
	protected UUID uuid = UUID.randomUUID();
	protected Thread thread;
	
	private HashMap<Class<? extends Packet>, ArrayList<Packet>> packetQueue = new HashMap<>();
	
	protected Connector(Socket socket) throws IOException {
		this.socket = socket;
		this.input = new DataInputStream(this.socket.getInputStream());
		this.output = new DataOutputStream(this.socket.getOutputStream());
	}
	
	public void addToQueue(Class<? extends Packet> clazz) {
		if(!this.packetQueue.containsKey(clazz))
			this.packetQueue.put(clazz, new ArrayList<Packet>());
	}
	
	public <T extends Packet> WaitForPacketProgressFuture<T> createWaitFor(Class<? extends Packet> clazz){
		WaitForPacketProgressFuture<T> waitFor = new WaitForPacketProgressFuture<T>(1000 * 60 * 2,this, clazz); //2min TimeOut Default
		
		if(this.packetQueue.containsKey(clazz) && !this.packetQueue.get(clazz).isEmpty()) {
			waitFor.handle(this.packetQueue.get(clazz).get(0));
			this.packetQueue.get(clazz).remove(0);
		}else {
			this.listeners.add(waitFor);
		}
		
		return waitFor;
	}
	
	public void unregister(PacketListener listener) {
		this.listeners.remove(listener);
	}
	
	public void register(PacketListener listener) {
		this.listeners.add(listener);
	}
	
	public boolean isConnected(){
		return this.socket != null && this.socket.isConnected();
	}	
	
	protected void start() {
		this.active = true;
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	public void close() {
		if(active) {
			this.active=false;
			try {
				this.socket.close();
				this.input.close();
				this.output.close();
				EventManager.callEvent(new ClientDisconnectEvent(this));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		try {
			while(active && this.input != null) {
				if(!this.socket.isConnected() || this instanceof Client && ((Client)this).getPingManager().getCurrentPing() > 1000 * 3) {
					close();
					break;
				}else if(this.input.available() > 0) {
					if(this.name == null) {
						this.name = this.input.readUTF();
						Main.log(this + " is connected");
						EventManager.callEvent(new ClientConnectEvent(this));
					} else {
						int length = this.input.readInt();
						int id = this.input.readInt();
						try {
							byte[] data = new byte[length];
							this.input.read(data, 0, length);
							
							Packet packet = Packet.create(id, data);
//							if(ev.getPacket() .getId()) {
//								
//							}
							
							if(!this.listeners.isEmpty()) {
								boolean handled = false;
								for(int i = 0; i < this.listeners.size(); i++) 
									if(this.listeners.get(i).handle(packet)) {
										this.listeners.remove(i);
										handled=true;
										break;
									}
								
								if(!handled) {
									if(this.packetQueue.containsKey(packet.getClass()))
										this.packetQueue.get(packet.getClass()).add(packet);
								}else continue;
							}
							
							EventManager.callEvent(new PacketReceiveEvent(packet,this));
						}catch(NegativeArraySizeException e) {
							System.out.println("LENGTH: "+length+" "+id);
							e.printStackTrace();
							throw new NegativeArraySizeException();
						}
					}
				}else {
					Thread.sleep(10);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
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
		}catch (SocketException e) {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String toString() {
		return this.name == null ? this.socket.getInetAddress().toString() : this.name;
	}
}
