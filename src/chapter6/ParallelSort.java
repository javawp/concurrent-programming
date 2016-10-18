package chapter6;

import java.util.Arrays;
import java.util.Random;

public class ParallelSort {

	public static void main(String[] args) {
		Random r = new Random();
		int[] array = new int[1000000];
		Arrays.parallelSetAll(array, i -> r.nextInt());
		Arrays.parallelSort(array);
	}
}
