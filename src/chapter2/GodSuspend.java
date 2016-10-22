package chapter2;

public class GodSuspend {
	public static Object u = new Object();

	public static class ChangeObjectThread extends Thread {

		public ChangeObjectThread(String name) {
			super.setName(name);
		}

		volatile boolean suspendme = false;

		public void suspendMe() {
			suspendme = true;
		}

		public void resumeMe() {
			suspendme = false;
			synchronized (this) {
				notify();
			}
		}

		@Override
		public void run() {
			while (true) {
				while (suspendme) {
					try {
						synchronized (this) {
							wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				synchronized (u) {
					System.out.println("in " + Thread.currentThread().getName());
				}
				Thread.yield();
			}
		}
	}

	public static class ReadObjectThread extends Thread {

		public ReadObjectThread(String name) {
			super.setName(name);
		}

		@Override
		public void run() {
			while (true) {
				synchronized (u) {
					System.out.println("in " + Thread.currentThread().getName());
				}
				Thread.yield();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ChangeObjectThread t1 = new ChangeObjectThread("ChangeObjectThread");
		ReadObjectThread t2 = new ReadObjectThread("ReadObjectThread");
		t1.start();
		t2.start();
		Thread.sleep(1000); // 保证线程t1进入方法
		t1.suspendMe(); // 线程等待
		Thread.sleep(2000); // 当前实例等待2秒, 再去唤醒
		t1.resumeMe(); // 唤醒当前实例, 重新执行
	}
}
