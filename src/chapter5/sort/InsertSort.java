package chapter5.sort;

import java.util.Arrays;

/**
 * 插入排序
 */
public class InsertSort {

	public static void insertSort(int[] arr) {
		int length = arr.length;
		int j, i, key;
		for (i = 1; i < length; i++) {
			// key为要准备插入的元素
			key = arr[i];
			j = i - 1;
			while (j >= 0 && arr[j] > key) {
				arr[j + 1] = arr[j];
				j--;
			}
			// 找到合适的位置, 插入key
			arr[j + 1] = key;
		}
	}

	public static void main(String[] args) {
		int[] arr = { 5, 52, 6, 3, 4 };
		insertSort(arr);
		System.out.println(Arrays.toString(arr));
	}
}
