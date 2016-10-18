package chapter6;

import java.util.Comparator;

public class ComparatorTest {

	public static void main(String[] args) {
		Comparator<String> cmp = Comparator.comparingInt(String::length).thenComparing(String.CASE_INSENSITIVE_ORDER);
		int compare = cmp.compare("abc", "acd");
		System.out.println(compare);
	}
}
