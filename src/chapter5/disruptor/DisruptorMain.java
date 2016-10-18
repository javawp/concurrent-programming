package chapter5.disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class DisruptorMain {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException {
		Executor executor = Executors.newCachedThreadPool();
		PCDataFactory factory = new PCDataFactory();
		// Specify the size of the ring buffer, must be power of 2.
		int bufferSize = 1024; // 设置缓冲区大小, 2的整数次幂
		Disruptor<PCData> disruptor = new Disruptor<>(factory, bufferSize, executor, ProducerType.MULTI,
				new BlockingWaitStrategy()); // 创建disruptor对象, 封装disruptor库, 提供一些便捷的API

		// 设置用于处理数据的消费者, 系统会为每一个消费者实例映射到一个线程中, 这里提供了4个消费者线程
		disruptor.handleEventsWithWorkerPool(new Consumer(), new Consumer(), new Consumer(), new Consumer());
		disruptor.start(); // 启动并初始化disruptor系统

		RingBuffer<PCData> ringBuffer = disruptor.getRingBuffer();
		Producer producer = new Producer(ringBuffer);
		ByteBuffer bb = ByteBuffer.allocate(8);
		// 由一个生产者不断地向缓冲区中存入数据
		for (long l = 0; true; l++) {
			bb.putLong(0, l);
			producer.pushData(bb);
			Thread.sleep(100);
			System.out.println("add data " + l);
		}

	}
}
