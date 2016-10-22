package chapter2;

/**
 * 复合原子性操作, 无法避免
 * @作者: 王鹏
 * @创建时间: 2016年10月22日-下午7:39:15
 * @版本: 1.0
 */
public class PlusTask implements Runnable {

	static volatile int i = 0;

	@Override
	public void run() {
		for (int ik = 0; ik < 10000; ik++) {
			i++;
		}
	}

	public static void main(String[] args) {
		Thread[] threads = new Thread[10];
		for (int i = 0; i < 10; i++) {
			threads[i] = new Thread(new PlusTask());
			threads[i].start();
		}
		for (int i = 0; i < 10; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(i);
	}

}
