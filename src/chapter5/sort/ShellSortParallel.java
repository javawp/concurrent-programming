package chapter5.sort;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShellSortParallel {

	static volatile int[] arr = { 30, 20, 1, 83, 89, 95, 55, 87, 78, 35, 22, 98, 53, 21, 94, 73, 9, 32, 60, 26, 42, 67,
			77, 24, 7, 28, 49, 51, 58, 93, 14, 36, 81, 91, 70, 13, 48, 10, 12, 0, 97, 47, 66, 64, 23, 76, 5, 84, 65, 46,
			56, 68, 25, 80, 18, 15, 3, 72 };
	static ExecutorService pool = Executors.newCachedThreadPool();

	public static class ShellSortTask implements Runnable {

		int i = 0;
		int h = 0;
		CountDownLatch l;

		public ShellSortTask(int i, int h, CountDownLatch l) {
			this.i = i;
			this.h = h;
			this.l = l;
		}

		@Override
		public void run() {
			if (arr[i] < arr[i - h]) {
				int tmp = arr[i];
				int j = i - h;
				while (j >= 0 && arr[j] > tmp) {
					arr[j + h] = arr[j];
					j -= h;
				}
				arr[j + h] = tmp;
			}
			l.countDown();
		}

	}

	public static void pShellSort(int[] arr) throws InterruptedException {
		// 计算出最大的h值
		int h = 1;
		CountDownLatch latch = null;
		while (h <= arr.length / 3) {
			h = h * 3 + 1;
		}
		/**
		 * 为控制线程数量, 这里定义并行函数pShellSort()在h大于或等于4时使用并行线程,否则退化为传统的插入排序
		 */
		while (h > 0) {
			System.out.println("h=" + h);
			if (h >= 4)
				latch = new CountDownLatch(arr.length - h);
			for (int i = h; i < arr.length; i++) {
				// 控制线程数量
				if (h >= 4) {
					pool.execute(new ShellSortTask(i, h, latch));
				} else {
					if (arr[i] < arr[i - h]) {
						int tmp = arr[i];
						int j = i - h;
						while (j >= 0 && arr[j] > tmp) {
							arr[j + h] = arr[j];
							j -= h;
						}
						arr[j + h] = tmp;
					}
					System.out.println(Arrays.toString(arr));
				}
			}
			// 等待线程排序完成, 进入下一次排序
			latch.await();
			// 计算出下一个h值
			h = (h - 1) / 3;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		pShellSort(arr);
		System.out.println("最终结果如下: ********************************");
		System.out.println(Arrays.toString(arr));
		pool.shutdown();
	}
}
