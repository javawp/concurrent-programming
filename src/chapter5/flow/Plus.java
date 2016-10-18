package chapter5.flow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * p1 计算的是加法
 */
public class Plus implements Runnable {

	public static BlockingQueue<Msg> bq = new LinkedBlockingQueue<>();

	@Override
	public void run() {
		while (true) {
			try {
				Msg msg = bq.take();
				msg.j = msg.i + msg.j;
				Multiply.bq.add(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
