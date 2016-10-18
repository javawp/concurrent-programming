package chapter5.pattern;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {

	private volatile boolean isRunning = true;
	private BlockingQueue<PCData> queue; // 内存缓冲区
	private static AtomicInteger count = new AtomicInteger(); // 总数, 原子操作
	private static final int SLEEPTIME = 1000;

	public Producer(BlockingQueue<PCData> queue) {
		this.queue = queue;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		PCData data = null;
		Random r = new Random();

		System.out.println("start Producer id=" + Thread.currentThread().getId());

		try {
			while (isRunning) {
				Thread.sleep(r.nextInt(SLEEPTIME));
				data = new PCData(count.incrementAndGet()); // 构造任务数据
				System.out.println(data + " is put into queue");
				if (!queue.offer(data, 2, TimeUnit.SECONDS)) {
					System.out.println("failed to put data:" + data);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupted();
		}
	}

	public void stop() {
		isRunning = false;
	}

}
