package chapter5.sort;

public class OddEvenSortSerial {

	public static void oddEvenSort(int[] arr) {
		int exchFlag = 1; // 记录当前迭代是否发生了数据交换, 1: 发生了交换; 0: 不再进行交换
		int start = 0; // 0: 偶交换; 1: 奇交换
		// 如果上一次比较交换发生了数据交换, 或者当前正在进行的是奇交换, 循环就不会停止, 直到程序不再发生交换, 并且当前进行的是偶数交换
		while (exchFlag == 1 || start == 1) {
			exchFlag = 0;
			for (int i = start; i < arr.length - 1; i += 2) {
				if (arr[i] > arr[i + 1]) {
					int temp = arr[i];
					arr[i] = arr[i + 1];
					arr[i + 1] = temp;
					exchFlag = 1;
				}
			}
			if (start == 0)
				start = 1;
			else
				start = 0;
		}
	}
}
