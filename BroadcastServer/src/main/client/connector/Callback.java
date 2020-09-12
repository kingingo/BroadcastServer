package main.client.connector;
public interface Callback<T> {
	public void call(T obj,Throwable exception);
}