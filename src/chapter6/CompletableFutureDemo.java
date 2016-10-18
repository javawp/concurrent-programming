package chapter6;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

public class CompletableFutureDemo {

	public static Integer calc(Integer para) {
		try {
			// 模拟一个长时间的执行
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return para * para;
	}

	public static Integer calcException(Integer para) {
		return para / 0;
	}

	public static Integer calc2(Integer para) {
		return para / 2;
	}

	@Test
	public void testCombine() throws Exception {
		CompletableFuture<Integer> intFuture1 = CompletableFuture.supplyAsync(() -> calc2(50));
		CompletableFuture<Integer> intFuture2 = CompletableFuture.supplyAsync(() -> calc2(25));

		CompletableFuture<Void> fu = intFuture1.thenCombine(intFuture2, (i, j) -> i + j)
				.thenApply(str -> "\"" + str + "\"").thenAccept(System.out::println);
		fu.get();
	}

	@Test
	public void testCompose() throws Exception {
		CompletableFuture<Void> fu = CompletableFuture.supplyAsync(() -> calc2(50))
				.thenCompose(i -> CompletableFuture.supplyAsync(() -> calc2(i))).thenApply(str -> "\"" + str + "\"")
				.thenAccept(System.out::println);
		fu.get();
	}

	@Test
	public void testException() throws Exception {
		CompletableFuture<Void> fu = CompletableFuture.supplyAsync(() -> calcException(50)).exceptionally(ex -> {
			System.out.println(ex.toString());
			return 0;
		}).thenApply(i -> Integer.toString(i)).thenApply(str -> "\"" + str + "\"").thenAccept(System.out::println);
		fu.get();
	}

	@Test
	public void testFuture() throws Exception {
		/**
		 * 在supplyAsync()函数中, 它会在一个新的线程中,执行传入的参数. 在这里, 它会执行calc()方法.
		 * 而calc()方法的执行可能比较慢, 但是不影响CompletableFuture实例的构造速度,
		 * 因此supplyAsync()会立即返回,它返回的CompletableFuture对象实例就可以作为这次调用的契约, 在将来任何场合,
		 * 用于获得最终的计算结果.如果当前计算没有完成, 则调用get()方法的线程就会等待.
		 */
		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> calc(50));
		System.out.println(future.get());
	}

	@Test
	public void testStreamAPI() throws Exception {
		CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> calc(60))
				.thenApply(i -> Integer.toString(i)).thenApply(str -> "\"" + str + "\"")
				.thenAccept(System.out::println);
		/**
		 * 目的是等待calc()函数执行完成,如果不进行这个等待调用, 由于CompletableFuture异步执行的缘故,
		 * 主函数不等calc()方法执行完毕就会退出,随着主函数的结束, 所有的Daemon线程都会立即退出,从而导致calc()方法无法正常完成.
		 */
		future.get();
	}
}
