package chapter6;

import java.util.stream.IntStream;

import org.junit.Test;

public class PrimeUtil {

	public static boolean isPrime(int number) {
		int tmp = number;
		if (tmp < 2) {
			return false;
		}
		for (int i = 2; Math.sqrt(tmp) >= i; i++) {
			if (tmp % i == 0) {
				return false;
			}
		}
		return true;
	}

	@Test
	public void testStream() throws Exception {
		long e1 = System.currentTimeMillis();
		IntStream.range(1, 1_000_000).filter(PrimeUtil::isPrime).count();
		long e2 = System.currentTimeMillis();
		System.out.println("顺序流耗时: " + (e2 - e1));
	}

	@Test
	public void testParallel() throws Exception {
		long e1 = System.currentTimeMillis();
		IntStream.range(1, 1_000_000).parallel().filter(PrimeUtil::isPrime).count();
		long e2 = System.currentTimeMillis();
		System.out.println("并行流耗时: " + (e2 - e1));
	}
}
