package main.client.connector.futures;

import main.api.packet.Packet;
import main.client.connector.Callback;
import main.client.connector.ProgressFuture;
import main.threads.ThreadFactory;

public abstract class BaseProgressFuture<T extends Packet> implements ProgressFuture<T>{
	protected int timeout = 5000;
	private int sleep = 25;
	private T response = null;
	private boolean hasRespond;
	
	protected BaseProgressFuture(int timeout) {
		this.timeout = timeout;
	}
	
	protected void done(T response) {
		this.response = response;
		this.hasRespond = true;
	}

	public boolean hasRespond() {
		return hasRespond;
	}

	public T getSync() {
		return getSync(timeout);
	}

	public T getSync(int timeout){
		long start = System.currentTimeMillis();
		while (!hasRespond) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (start + timeout < System.currentTimeMillis()) {
				throw new RuntimeException("Timeout");
			}
		}
		return response;
	}

	public void getAsync(Callback<T> call) {
		getAsync(call, timeout);
	}

	public void getAsync(Callback<T> call, int timeout) {
		ThreadFactory.getFactory().createThread(new Runnable() {
			@Override
			public void run() {
				T out = null;
				Exception ex = null;
				try {
					out = getSync(timeout);
				} catch (Exception e) {
					ex = e;
				}
				call.call(out,ex);
			}
		}).start();
	}
}