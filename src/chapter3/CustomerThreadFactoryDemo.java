package chapter3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomerThreadFactoryDemo {

	public static class MyTask implements Runnable {

		@Override
		public void run() {
			System.out.println(System.currentTimeMillis() + "Thread ID: " + Thread.currentThread().getId());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		MyTask task = new MyTask();
		ExecutorService es = new ThreadPoolExecutor(5, 5, 0l, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
				new ThreadFactory() {

					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setDaemon(true);
						System.out.println("create " + t);
						return t;
					}
				});

		for (int i = 0; i < 5; i++) {
			es.submit(task);
		}
		Thread.sleep(10000);
	}
}
