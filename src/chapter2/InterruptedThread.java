package chapter2;

/**
 * 线程中断
 * @作者: 王鹏
 * @创建时间: 2016年10月22日-下午4:04:35
 * @版本: 1.0
 */
public class InterruptedThread {

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread() {
			public void run() {
				while (true) {
					if (Thread.currentThread().isInterrupted()) {
						System.out.println("Interruptrd!");
						break;
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// sleep()休眠时, 如果被中断, 会产生异常
						System.out.println("Interrupted When Sleep");
						// 设置中断状态
						Thread.currentThread().interrupt();
					}
				}
			};
		};
		t1.start();
		Thread.sleep(2000);
		t1.interrupt();
	}

}
