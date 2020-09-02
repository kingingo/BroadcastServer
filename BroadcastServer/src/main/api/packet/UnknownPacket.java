package main.api.packet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UnknownPacket extends Packet {
	
	private int id;

	public UnknownPacket(int id,byte[] data){
		this.id=id;
		setData(data);
	}
	
	public int getId() {
		return this.id;
	}

	@Override
	public void parseFromInput(DataInputStream in) throws IOException {}
	
	protected void setId(int id) {
		this.id = id;
	}

	@Override
	public void writeToOutput(DataOutputStream out) throws IOException {}
}
