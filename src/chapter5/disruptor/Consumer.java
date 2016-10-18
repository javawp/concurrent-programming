package chapter5.disruptor;

import com.lmax.disruptor.WorkHandler;

/**
 * 消费者的作用是读取数据进行处理.这里,数据的读取已经由Disruptor进行封装, onEvent()方法为框架的回调方法. 
 * 因此, 这里只需要简单地进行数据处理即可.
 */
public class Consumer implements WorkHandler<PCData> {

	@Override
	public void onEvent(PCData event) throws Exception {
		System.out.println(Thread.currentThread().getId() + ":Event: --" + event.get() * event.get() + "--");
	}

}
