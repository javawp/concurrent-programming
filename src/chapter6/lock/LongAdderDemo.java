package chapter6.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import org.junit.Test;

public class LongAdderDemo {

	private static final int MAX_THREADS = 3; // 线程数
	private static final int TASK_COUNT = 3; // 任务数
	private static final int TARGET_COUNT = 10000000; // 目标总数

	private AtomicLong acount = new AtomicLong(0L); // 无锁的院子操作
	private LongAdder lacount = new LongAdder();
	private long count = 0;

	static CountDownLatch cdlsync = new CountDownLatch(TASK_COUNT);
	static CountDownLatch cdlatomic = new CountDownLatch(TASK_COUNT);
	static CountDownLatch cdladdr = new CountDownLatch(TASK_COUNT);

	protected synchronized long inc() { // 有锁的加法
		return ++count;
	}

	protected synchronized long getCount() { // 有锁的操作
		return count;
	}

	public class SyncThread implements Runnable {
		protected String name;
		protected long starttime;
		LongAdderDemo out;

		public SyncThread(LongAdderDemo o, long starttime) {
			this.out = o;
			this.starttime = starttime;
		}

		@Override
		public void run() {
			long v = out.getCount();
			while (v < TARGET_COUNT) { // 在到达目标值前,不停循环
				v = out.inc();
			}
			long endtime = System.currentTimeMillis();
			System.out.println("SyncThread spend: " + (endtime - starttime) + "ms" + " v=" + v);
			cdlsync.countDown();
		}
	}

	public class AtomicThread implements Runnable {
		protected String name;
		protected long starttime;

		public AtomicThread(long starttime) {
			this.starttime = starttime;
		}

		@Override
		public void run() {
			long v = acount.get();
			while (v < TARGET_COUNT) { // 在到达目标值前,不停循环
				v = acount.incrementAndGet();// 无锁的加法
			}
			long endtime = System.currentTimeMillis();
			System.out.println("AtomicThread spend: " + (endtime - starttime) + "ms" + " v=" + v);
			cdlatomic.countDown();
		}

	}

	public class LongAddrThread implements Runnable {
		protected String name;
		protected long starttime;

		public LongAddrThread(long starttime) {
			this.starttime = starttime;
		}

		@Override
		public void run() {
			long v = lacount.sum();
			while (v < TARGET_COUNT) { // 在到达目标值前,不停循环
				lacount.increment();
				v = lacount.sum();
			}
			long endtime = System.currentTimeMillis();
			System.out.println("LongAddrThread spend: " + (endtime - starttime) + "ms" + " v=" + v);
			cdladdr.countDown();
		}
	}

	@Test
	public void testSync() throws InterruptedException {
		ExecutorService exe = Executors.newFixedThreadPool(MAX_THREADS);
		long starttime = System.currentTimeMillis();
		SyncThread sync = new SyncThread(this, starttime);
		for (int i = 0; i < TASK_COUNT; i++) {
			exe.submit(sync); // 提交线程开始计算
		}
		cdlsync.await();
		exe.shutdown();
	}

	@Test
	public void testAtomic() throws InterruptedException {
		ExecutorService exe = Executors.newFixedThreadPool(MAX_THREADS);
		long starttime = System.currentTimeMillis();
		AtomicThread atomic = new AtomicThread(starttime);
		for (int i = 0; i < TASK_COUNT; i++) {
			exe.submit(atomic); // 提交线程开始计算
		}
		cdlatomic.await();
		exe.shutdown();
	}

	@Test
	public void testAtomicLong() throws InterruptedException {
		ExecutorService exe = Executors.newFixedThreadPool(MAX_THREADS);
		long starttime = System.currentTimeMillis();
		LongAddrThread atomic = new LongAddrThread(starttime);
		for (int i = 0; i < TASK_COUNT; i++) {
			exe.submit(atomic); // 提交线程开始计算
		}
		cdladdr.await();
		exe.shutdown();
	}

}