package chapter6;

import java.util.Arrays;
import java.util.function.IntConsumer;

public class HelloFunction {

	static int[] arr = { 1, 3, 4, 5, 6, 7, 8, 9, 10 };

	public static void main(String[] args) {
		Arrays.stream(arr).forEach((x) -> System.out.println(x));
		// 通过方法引用的推导
		Arrays.stream(arr).forEach(System.out::println);

		IntConsumer outprintln = System.out::println;
		IntConsumer errprintln = System.err::println;

		Arrays.stream(arr).forEach(outprintln.andThen(errprintln));
	}
}
