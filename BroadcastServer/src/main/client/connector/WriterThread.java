package main.client.connector;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import main.Main;
import main.api.events.EventManager;
import main.api.events.events.PacketSendEvent;
import main.api.packet.Packet;

public class WriterThread implements Runnable{

	private Thread thread;
	private Connector connector;
	protected DataOutputStream output;
	protected ArrayList<Packet> queuedPackets = new ArrayList<Packet>();
	
	public WriterThread(Connector connector, DataOutputStream output) {
		this.output=output;
		this.connector = connector;
	}
	
	public void start(){
		if(this.thread == null) {
			this.thread = new Thread(this);
			this.thread.start();
		}
	}	
	
	public void run() {
		while (connector.isActive()) {
			Packet packet = null;
			synchronized (queuedPackets) {
				if(queuedPackets.size() > 0){
					packet = queuedPackets.get(0);
					queuedPackets.remove(0);
				}
			}
			if(packet != null){
				try{
					write0(packet);
				}catch(Exception e){
					handleException(e);
				}
			} else
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		
		thread = null;
	}
	
	private void handleException(Exception e){
		if (e.getMessage().equalsIgnoreCase("Broken pipe") || e.getMessage().equalsIgnoreCase("Connection reset"))
			return;
		if (e.getMessage().equalsIgnoreCase("Socket closed") || e.getMessage().equalsIgnoreCase("Datenübergabe unterbrochen (broken pipe)")) {
			connector.close();
			return;
		}
		e.printStackTrace();
	}
	
	public void write(Packet packet) {
		synchronized (queuedPackets) {
			this.queuedPackets.add(packet);
		}
	}
	
	private boolean write0(Packet packet) {
		try {
			PacketSendEvent event = new PacketSendEvent(packet, connector);
			EventManager.callEvent(event);
			
			if(!event.isCancelled()) {
				byte[] packetBytes = packet.toByteArray();
				int zero = 0;
				for(byte b : packetBytes) {
					if(b == 0) {
						zero++;
					}else zero = 0;
				}
				if(packet.getId() != 8 && packet.getId() != 9)
				Main.log(connector.getName() + " -> WRITE ID:"+packet.getId()+" LENGTH:"+packetBytes.length+" ZEROS:"+zero);
				this.output.writeInt(packetBytes.length);
				this.output.writeInt(packet.getId());
				this.output.write(packetBytes, 0, packetBytes.length);
				this.output.flush();
				return true;
			}
		}catch (SocketException e) {
			connector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
