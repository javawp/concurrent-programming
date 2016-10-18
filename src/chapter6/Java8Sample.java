package chapter6;

import java.util.Arrays;

import org.junit.Test;

public class Java8Sample {

	int[] array = { 1, 3, 4, 5, 6, 7, 8, 9, 10 };

	@Test
	public void testArray() throws Exception {
		Arrays.stream(array).map(x -> x = x + 1).forEach((s) -> {
			System.out.print(s + ",");
		});
		System.out.println();
		Arrays.stream(array).forEach(x -> System.out.print(x + ","));
	}

	@Test
	public void testOddEvent() throws Exception {
		Arrays.stream(array).forEach(e -> System.out.print(e + ","));
		System.out.println();
		Arrays.stream(array).map(x -> x % 2 == 0 ? x : x + 1).forEach(e -> System.out.print(e + ","));
	}
}
