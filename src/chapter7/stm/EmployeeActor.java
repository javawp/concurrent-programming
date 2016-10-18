package chapter7.stm;

import scala.concurrent.stm.Ref;
import scala.concurrent.stm.japi.STM;
import akka.actor.UntypedActor;
import akka.transactor.Coordinated;

@SuppressWarnings("deprecation")
public class EmployeeActor extends UntypedActor {

	/** 雇员账户初始金额是50元 */
	private Ref.View<Integer> count = STM.newRef(50);

	@Override
	public void onReceive(Object msg) throws Exception {
		/**
		 * 如果是Coordinated, 则当前Actor会自动加入Coordinated指定的事务中 , 定义原子操作,
		 * 在这个操作中将修改雇员账户余额. 在这里, 我们并没有给出异常情况的判断, 只要接收到转入金额, 一律将其增加到雇员账户中
		 */
		if (msg instanceof Coordinated) {
			final Coordinated c = (Coordinated) msg;
			final int downCount = (int) c.getMessage();
			try {
				c.atomic(new Runnable() {
					@Override
					public void run() {
						STM.increment(count, downCount);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("GetCount".equals(msg)) {
			getSender().tell(count.get(), getSelf());
		} else {
			unhandled(msg);
		}
	}
}
