package chapter2;

public class DaemonDemo {

	public static class DaemonT extends Thread {

		public void run() {
			while (true) {
				System.out.println("I am alive");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread t = new DaemonT();
		// 设置为守护线程, 必须在线程start之前设置,否则会设置失败, 被当成是用户线程执行, 永远也停不下来.
		t.setDaemon(true);
		t.start();
		Thread.sleep(2000);
	}
}
