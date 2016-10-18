package chapter5.pattern;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 生产者-消费者模式很好地对生产者线程和消费者线程进行解耦, 优化了系统整体结构. 
 * 同时, 由于缓冲区的作用, 允许生产者线程和消费者线程存在执行上的性能差异,
 * 从一定程度上缓解了性能瓶颈对系统的影响
 */
public class ProducerConsumerMain {

	public static void main(String[] args) throws InterruptedException {
		// 建立缓冲区
		BlockingQueue<PCData> queue = new LinkedBlockingQueue<>(10);
		// 建立生产者
		Producer producer1 = new Producer(queue);
		Producer producer2 = new Producer(queue);
		Producer producer3 = new Producer(queue);
		// 建立消费者
		Consumer consumer1 = new Consumer(queue);
		Consumer consumer2 = new Consumer(queue);
		Consumer consumer3 = new Consumer(queue);
		// 建立线程池
		ExecutorService service = Executors.newCachedThreadPool();
		// 运行生产者
		service.execute(producer1);
		service.execute(producer2);
		service.execute(producer3);
		// 运行消费者
		service.execute(consumer1);
		service.execute(consumer2);
		service.execute(consumer3);

		Thread.sleep(10 * 1000);
		// 停止生产者
		producer1.stop();
		producer2.stop();
		producer3.stop();

		Thread.sleep(3000);

		service.shutdown();
	}
}
