package main.api.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.reflections.Reflections;

import lombok.Getter;
import lombok.Setter;
import main.Main;

public abstract class Packet implements IData{

	private static final HashMap<String,Class<? extends Packet>> packets = new HashMap<String, Class<? extends Packet>>();
	private static final HashMap<String, Integer> packet_ids = new HashMap<>();
	@Getter
	private static int packetIdMax =1;
	
	public static void loadPackets() {
		Reflections reflections = new Reflections( "main.api.packet.packets" );
		List<Class<? extends Packet>> moduleClasses = new ArrayList<>( reflections.getSubTypesOf( Packet.class ) );

		Collections.sort(moduleClasses, new Comparator<Class<? extends Packet>>() {
		    @Override
		    public int compare(Class<? extends Packet> o1, Class<? extends Packet> o2) {
		        return o1.getSimpleName().compareTo(o2.getSimpleName());
		    }
		});
		int id=0;
		for ( Class<? extends Packet> clazz : moduleClasses ){
			if(clazz == UnknownPacket.class)continue;
			try {
				packet_ids.put(clazz.newInstance().getPacketName(), id++);
				packets.put((id-1)+"", clazz);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
					e.printStackTrace();
			}
			
		}
		packetIdMax=id-1;
		for(String packet : packet_ids.keySet())Main.log(packet+" -> "+packet_ids.get(packet));
	}
	
	public static int getId(Class<? extends Packet> clazz) {
		for(String id : packets.keySet()) {
			if(packets.get(id) == clazz) {
				return Integer.valueOf(id);
			}
		}
		return -1;
	}
	
	public static boolean KnowPacket(Packet packet) {
		try {
			getPacketName(packet.getId());
			
			return true;
		}catch(NullPointerException e) {
			return false;
		}
	}
	
	public static String getPacketName(int packetId) {
		if(packets.get(packetId+"")==null) {
			for(String packetName : packet_ids.keySet()) {
				if(packet_ids.get(packetName) == packetId) {
					Main.log("Found packet "+packetId+" "+packetName);
					return packetName;
				}
			}
			
			throw new NullPointerException("Couldn't find packet!? ID:"+packetId+", packets_size="+packets.size());
		}
		
		return packets.get(packetId+"").getSimpleName();
	}
	
	public static  <T extends Packet> boolean toEncrypt(int packetId) {
		if(packets.containsKey(packetId+"")) {
			Class<T> clazz = (Class<T>) packets.get(packetId+"");
			try {
				return clazz.newInstance().toEncrypt();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static int getId(String packetName) {
		return packet_ids.get(packetName);
	}
	
	public static <T extends Packet> T create(Class<T> clazz, byte[] data) {
		try {
			T packet = (T) clazz.newInstance();
			packet.parseFromInput(data);
			
			if(packet.toByteArray().length == data.length) {
				return (T) packet;
			}else {
				Main.log("The length doesn't suit to the packet... "+packet.toByteArray().length+" != "+data.length);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new NullPointerException();
	}
	
	@SuppressWarnings({ "unchecked", "FinalStaticMethod" })
	public static <T extends Packet> T create(int packetId, byte[] data) {
		if(packets.containsKey(packetId+"")) {
			Class<T> clazz = (Class<T>) packets.get(packetId+"");
			return create(clazz,data);
		}
		return (T) (new UnknownPacket(packetId, data));
	}

	@Getter
	@Setter
	private byte[] data;
	
	public boolean toEncrypt() {
		return true;
	}
	
	public void parseFromInput(byte[] bytes) throws IOException {
		this.parseFromInput(new DataInputStream( new ByteArrayInputStream(bytes) ));
	}
	
	public int getId() {
		return this.packet_ids.get(getPacketName());
	}
	
	public String getPacketName(){
		String simpleName = this.getClass().getSimpleName();
		simpleName = simpleName.substring(0, simpleName.indexOf("Packet"));
		return simpleName.toUpperCase();
	}
	
	public byte[] toByteArray(){
		if(this instanceof UnknownPacket)return ((UnknownPacket)this).getData();
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream( baos );
			writeToOutput(out);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
}
