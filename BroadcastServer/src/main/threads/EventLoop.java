package main.threads;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventLoop {
	public static final EventLoop DEFAULT_LOOP = new EventLoop(512);
	public static final EventLoop UNLIMITED_LOOP = new EventLoop(-1);
	
	private int MAX_THREADS = 0;
	private List<Runnable> todo = (List<Runnable>) Collections.synchronizedList(new ArrayList<Runnable>());
	private List<ThreadRunner> threads = (List<ThreadRunner>) Collections.synchronizedList(new ArrayList<ThreadRunner>());
	
	public EventLoop(int maxthreads) {
		this.MAX_THREADS = maxthreads;
	}
	
	public ThreadRunner join(final Runnable run) {
		if (MAX_THREADS <= 0 || threads.size() < MAX_THREADS) {
			ThreadRunner t = ThreadFactory.getInstance().createThread(new Runnable() {
				@Override
				public void run() {
					try {
						run.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
					while (true) {
						Runnable next = null;
						if (todo.size() != 0) {
							next = todo.get(0);
							todo.remove(next);
						} else
							break;
						next.run();
					}
					threads.remove(this);
				}
			});
			t.start();
			threads.add(t);
			return t;
		} else
			todo.add(run);
		return null;
	}
	
	public int getCurruntThreads(){
		synchronized (threads) {
			return threads.size();
		}
	}
	public List<ThreadRunner> getWorkingThreads(){
		synchronized (threads) {
			return Collections.unmodifiableList(threads);
		}
	}
	
	public List<Runnable> getQueue() {
		synchronized (todo) {
			return Collections.unmodifiableList(todo);
		}
	}

	public void terminate() {
		synchronized (todo) {
			todo.clear();
		}
		synchronized (threads) {
			for(ThreadRunner t : new ArrayList<>(threads))
				try{
					t.stop();
				}catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	
	public static void main(String[] args) {
		EventLoop loop = new EventLoop(1);
		loop.join(()->{
			System.out.println("Start 1");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("End 1");
		});
		loop.join(()->{
			System.out.println("Start 2");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("End 2");
		});
	}
}