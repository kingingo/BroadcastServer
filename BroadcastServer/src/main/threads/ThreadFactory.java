package main.threads;
public abstract class ThreadFactory {
	private static ThreadFactory instance;

	public static ThreadFactory getInstance() {
		return instance;
	}

	public static void setInstance(ThreadFactory instance) {
		ThreadFactory.instance = instance;
	}
	
	public static ThreadFactory getFactory(){
		return getInstance();
	}

	static {
		instance = new ThreadFactory() {
			@Override
			public ThreadRunner createThread(final Runnable run) {
					return new ThreadRunner() {
						Thread thread;
						@Override
						public void start() {
							if(thread != null)
								throw new IllegalStateException("Thread is alredy running!");
							thread = new Thread(run);
							thread.start();
						}
						public void stop() {
							if(thread == null)
								throw new IllegalStateException("Thread isnt running!");
							thread.interrupt();
							thread = null;
						};
						@Override
						public Thread getThread() {
							return thread;
						}
					};
			}
		};
	}

	public abstract ThreadRunner createThread(Runnable run);
}