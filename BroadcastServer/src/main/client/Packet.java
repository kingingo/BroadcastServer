package main.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Packet {
	private byte[] data;
	private int Id;
}
