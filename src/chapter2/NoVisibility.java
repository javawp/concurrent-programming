package chapter2;

public class NoVisibility {

	private static volatile boolean ready;

	private volatile static int number;

	private static class ReaderThread extends Thread {

		@Override
		public void run() {
			while (!ready) {
				if (number != 0)
					System.out.println("number 改变了  " + number);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new ReaderThread().start();
		Thread.sleep(10000);
		number = 42;
		ready = true;
		Thread.sleep(5000);
	}

}
