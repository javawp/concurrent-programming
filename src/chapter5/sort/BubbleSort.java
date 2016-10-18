package chapter5.sort;

import java.util.Arrays;

/**
 * 冒泡排序
 */
public class BubbleSort {

	static int[] arrs = { 5, 52, 6, 3, 4 };

	public static void bubbleSort(int[] arr) {
		for (int i = arr.length - 1; i > 0; i--) {
			for (int j = 0; j < i; j++) {
				if (arr[j] > arr[j + 1]) {
					int temp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = temp;
				}
				System.out.print(" -- > " + Arrays.toString(arrs));
			}
			System.out.println("\n" + Arrays.toString(arrs));
		}
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString(arrs));
		bubbleSort(arrs);
	}
}
