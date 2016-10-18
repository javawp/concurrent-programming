package chapter6;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ListParallel {

	class Student {
		int id;
		String name;
		int score;

		public Student(int id, String name, int score) {
			this.id = id;
			this.name = name;
			this.score = score;
		}

	}

	List<Student> ss = null;

	@Before
	public void setUp() throws Exception {
		ss = new ArrayList<>();
		for (int i = 0; i < 100000; i++) {
			ss.add(new Student(i, "x" + i, i));
		}
	}

	@Test
	public void testStream() throws Exception {
		long e1 = System.currentTimeMillis();
		double ave = ss.stream().mapToInt(s -> s.score).average().getAsDouble();
		long e2 = System.currentTimeMillis();
		System.out.println(ave);
		System.out.println("顺序流耗时: " + (e2 - e1));
	}

	@Test
	public void testParallel() throws Exception {
		long e1 = System.currentTimeMillis();
		double ave = ss.parallelStream().mapToInt(s -> s.score).average().getAsDouble();
		long e2 = System.currentTimeMillis();
		System.out.println(ave);
		System.out.println("并行流耗时: " + (e2 - e1));
	}
}
