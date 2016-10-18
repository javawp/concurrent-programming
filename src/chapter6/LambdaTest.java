package chapter6;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

public class LambdaTest {

	@Test
	public void testForEach() throws Exception {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
		numbers.forEach((Integer vaalue) -> System.out.println(vaalue));
	}

	@Test
	public void testOutParam() throws Exception {
		final int num = 2;
		Function<Integer, Integer> stringConverter = (from) -> from * num;
		System.out.println(stringConverter.apply(3));
	}
}
