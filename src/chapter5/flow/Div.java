package chapter5.flow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * p3 计算的是除法
 */
public class Div implements Runnable {

	public static BlockingQueue<Msg> bq = new LinkedBlockingQueue<>();

	@Override
	public void run() {
		while (true) {
			try {
				Msg msg = bq.take();
				msg.i = msg.i / 2;
				System.out.println(msg.orgStr + "=" + msg.i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
