package chapter5.search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelSearch {

	static int[] arr = { 30, 20, 1, 83, 89, 95, 55, 87, 78, 35, 22, 98, 53, 21, 94, 73, 9, 32, 60, 26, 42, 67, 77, 24,
			7, 28, 49, 51, 58, 93, 14, 36, 81, 91, 70, 13, 48, 10, 12, 0, 97, 47, 66, 64, 23, 76, 5, 84, 65, 46, 56,
			68, 25, 80, 18, 15, 3, 72 };
	
	static ExecutorService pool = Executors.newCachedThreadPool();
	static final int Thread_Num = 2;
	static AtomicInteger result = new AtomicInteger(-1);

	public static int search(int searchValue, int beginPos, int endPos) {
		for (int i = beginPos; i < endPos; i++) {
			// 首先通过result判断是否已经有其他线程找到了需要的结果,如果已经找到,则立即返回不再进行查找,如果没有找到,则进行下一步的搜索
			if (result.get() >= 0) {
				return result.get();
			}
			// 当前线程找到了需要的数据,那么就会将结果保存到result变量中,CAS失败,表示其他线程先一步找到了结果
			if (arr[i] == searchValue) {
				// 如果设置失败,说明其他线程已经先找到了
				if (!result.compareAndSet(-1, i)) {
					return result.get();
				}
				return i;
			}
		}
		return -1;
	}

	/**
	 * 定义一个线程进行查找,它会调用前面的search方法
	 */
	public static class SearchTask implements Callable<Integer> {

		int begin, end, searchValue;

		public SearchTask(int begin, int end, int searchValue) {
			this.begin = begin;
			this.end = end;
			this.searchValue = searchValue;
		}

		@Override
		public Integer call() throws Exception {
			System.out.println(Thread.currentThread().getName() + ": " + begin + " --> " + end + " 查询数据.");
			int re = search(searchValue, begin, end);
			return re;
		}

	}

	/**
	 * 根据线程数量对arr数组进行划分, 并建立对应的任务提交给线程池处理<br>
	 * 比如: 数组长度为11, subArrSize: 11/2+1 = 6, 那么第1个查询从0~6之间查询,第2个查询从6~11之间查询
	 * @param searchValue 搜索的值
	 * @return 数组的索引
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static int pSearch(int searchValue) throws InterruptedException, ExecutionException {
		int subArrSize = arr.length / Thread_Num + 1;
		List<Future<Integer>> re = new ArrayList<Future<Integer>>();
		for (int i = 0; i < arr.length; i += subArrSize) {
			int end = i + subArrSize;
			if (end >= arr.length)
				end = arr.length;
			re.add(pool.submit(new SearchTask(i, end, searchValue)));
		}
		for (Future<Integer> fu : re) {
			if (fu.get() > 0)
				return fu.get();
		}
		return -1;
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		System.out.println("并行搜索成功,索引为: " + pSearch(3));
		pool.shutdown();
	}

}
