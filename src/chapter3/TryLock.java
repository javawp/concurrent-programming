package chapter3;

import java.util.concurrent.locks.ReentrantLock;

public class TryLock implements Runnable {

	public static ReentrantLock lock1 = new ReentrantLock();
	public static ReentrantLock lock2 = new ReentrantLock();
	int lock;

	public TryLock(int lock) {
		this.lock = lock;
	}

	@Override
	public void run() {
		if (this.lock == 1) {
			while (true) {
				if (lock1.tryLock()) {
					try {
						Thread.sleep(500);
						if (lock2.tryLock()) {
							try {
								System.out.println(Thread.currentThread().getName() + ": My job done");
								return;
							} finally {
								lock2.unlock();
								System.out.println("释放了 lock2");
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock1.unlock();
						System.out.println("释放了 lock1");
					}
				}
			}
		} else {
			while (true) {
				if (lock2.tryLock()) {
					try {
						Thread.sleep(500);
						if (lock1.tryLock()) {
							try {
								System.out.println(Thread.currentThread().getName() + ": My job done");
								return;
							} finally {
								lock1.unlock();
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock2.unlock();
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		TryLock r1 = new TryLock(1);
		TryLock r2 = new TryLock(2);
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		t1.start();
		t2.start();
	}

}
