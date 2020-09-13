package main.lobby.packets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import main.api.packet.Packet;
import main.lobby.Lobby;

public class LobbyClosePacket extends Packet{
	
	public LobbyClosePacket() {}
	
	@Override
	public void parseFromInput(DataInputStream in) throws IOException {}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {}
}
