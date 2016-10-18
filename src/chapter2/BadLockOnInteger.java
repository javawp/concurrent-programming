package chapter2;

public class BadLockOnInteger implements Runnable {

	public static Integer i = 0;
	static BadLockOnInteger instance = new BadLockOnInteger();

	@Override
	public void run() {
		for (int j = 0; j < 10000000; j++) {
			synchronized (i) {
				/**
				 * 在Java中,Integer属于不变对象,也就是对象一创建,就不可以被修改,如果要修改, 只能新建一个Integer; <br>
				 * i++真实执行时变成了: i = Integer.valueOf(i.intValue()+1); <br>
				 * 由于多个线程间, 并不一定能够看到同一个Integer对象, 因此 , 两个线程加锁可能都加在了不同的对象实例上,<br>
				 * 从而导致对临界区代码控制出现问题.
				 */
				i++;
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread a1 = new Thread(instance);
		Thread a2 = new Thread(instance);
		a1.start();
		a2.start();
		a1.join();
		a2.join();
		System.out.println(i);
	}
}
