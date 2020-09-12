package main.client.connector;

import main.api.packet.Packet;

public interface ProgressFuture<T extends Packet> {
	public boolean hasRespond();

	public T getSync();

	public T getSync(int timeout);

	public void getAsync(Callback<T> call);

	public void getAsync(Callback<T> call, int timeout);
}