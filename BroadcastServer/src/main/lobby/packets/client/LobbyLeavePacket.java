package main.lobby.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import main.api.packet.Packet;
import main.lobby.Lobby;

public class LobbyLeavePacket extends Packet{
	
	public LobbyLeavePacket() {}
	
	@Override
	public void parseFromInput(DataInputStream in) throws IOException {}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {}
}
