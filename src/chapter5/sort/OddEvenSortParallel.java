package chapter5.sort;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OddEvenSortParallel {

	static int exchFlag = 1; // 记录当前迭代是否发生了数据交换, 1: 发生了交换; 0: 不再进行交换
	static volatile int[] arr = { 5, 52, 6, 3, 4 };
	static final ExecutorService pool = Executors.newCachedThreadPool();

	public static synchronized int getExchFlag() {
		return exchFlag;
	}

	public static synchronized void setExchFlag(int exchFlag) {
		OddEvenSortParallel.exchFlag = exchFlag;
	}

	public static class OddEvenSortTask implements Runnable {

		int i;
		CountDownLatch latch;

		public OddEvenSortTask(int i, CountDownLatch latch) {
			this.i = i;
			this.latch = latch;
		}

		@Override
		public void run() {
			if (arr[i] > arr[i + 1]) {
				int temp = arr[i];
				arr[i] = arr[i + 1];
				arr[i + 1] = temp;
				setExchFlag(1);
			}
			System.out.println(Thread.currentThread().getName() + " : " + Arrays.toString(arr));
			latch.countDown();
		}

	}

	public static void pOddEvenSort(int[] arr) throws InterruptedException {
		int start = 0; // 0: 偶交换; 1: 奇交换
		// 如果上一次比较交换发生了数据交换, 或者当前正在进行的是奇交换, 循环就不会停止, 直到程序不再发生交换, 并且当前进行的是偶数交换
		while (getExchFlag() == 1 || start == 1) {
			setExchFlag(0);
			// 偶数的数组长度, 当start为1时, 只有len/2-1个线程
			CountDownLatch latch = new CountDownLatch(arr.length / 2 - (arr.length % 2 == 0 ? start : 0));
			for (int i = start; i < arr.length - 1; i += 2) {
				pool.submit(new OddEvenSortTask(i, latch));
			}
			// 等待线程结束
			latch.await();
			if (start == 0)
				start = 1;
			else
				start = 0;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		pOddEvenSort(arr);
		System.out.println(Arrays.toString(arr));
		pool.shutdown();
	}
}
