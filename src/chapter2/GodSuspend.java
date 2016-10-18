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
				synchronized (this) {
					while (suspendme) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
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
		Thread.sleep(1000);
		t1.suspendMe();
		System.out.println("suspendme t1 2 sec");
		Thread.sleep(2000);
		System.out.println("resume t1");
		t1.resumeMe();
	}
}
