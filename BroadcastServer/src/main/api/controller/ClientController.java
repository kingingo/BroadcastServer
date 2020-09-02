package main.api.controller;

import java.io.IOException;
import java.net.UnknownHostException;

import main.api.packet.Packet;
import main.client.Client;

public class ClientController {

	private Client client;
	
	public ClientController(String name, String host, int port) {
		try {
			this.client = new Client(name, host,port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(Packet packet) {
		this.client.write(packet);
	}
}
