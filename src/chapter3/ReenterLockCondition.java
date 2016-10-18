package chapter3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReenterLockCondition implements Runnable {

	public static ReentrantLock lock = new ReentrantLock();
	public static Condition condition = lock.newCondition();

	@Override
	public void run() {
		try {
			lock.lock();
			/**
			 * await()方法会使当前线程等待,同时释放当前锁,当其他线程使用signal()或者signalAll()方法时,
			 * 线程会重新获得锁并继续执行.或者当线程被中断时,也能跳出等待.
			 */
			condition.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ReenterLockCondition r1 = new ReenterLockCondition();
		Thread t1 = new Thread(r1);
		t1.start();
		Thread.sleep(2000);
		// 通知线程t1继续执行
		lock.lock();
		condition.signal();
		lock.unlock();
	}

}
