package main.threads;

public interface ThreadRunner {
	public void start();
	public void stop();
	public Thread getThread();
}