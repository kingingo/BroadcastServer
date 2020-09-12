package main.lobby.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import main.api.packet.Packet;
import main.lobby.LobbySettings;
import main.lobby.LobbySettingsFactory;

public class LobbyUpdatePacket extends Packet{
	@Setter
	public static LobbySettingsFactory factory;
	
	@Getter
	private LobbySettings settings;
	
	public LobbyUpdatePacket() {
//		if(factory == null)throw new NullPointerException("First you have to set a LobbySettingsFactory!");
	}
	
	public LobbyUpdatePacket(LobbySettings settings) {
		this();
		this.settings=settings;
	}
	
	@Override
	public void parseFromInput(DataInputStream in) throws IOException {
		int length;
		byte[] bytes = new byte[length=in.readInt()];
		in.read(bytes, 0, length);
		this.settings = factory.parseFromInput(bytes);
	}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {
		byte[] bytes = factory.toByteArr(settings);
		out.writeInt(bytes.length);
		out.write(bytes);
	}
}
