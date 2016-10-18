package chapter7.stm;

import scala.concurrent.stm.Ref;
import scala.concurrent.stm.japi.STM;
import akka.actor.UntypedActor;
import akka.transactor.Coordinated;

@SuppressWarnings("deprecation")
public class CompanyActor extends UntypedActor {

	/** 公司账户有100元钱 */
	private Ref.View<Integer> count = STM.newRef(100);

	@Override
	public void onReceive(Object msg) throws Exception {
		/**
		 * 首先判断接收的msg是否是Coordinated.如果是Coordinated, 则表示这是一个新事务的开始.
		 */
		if (msg instanceof Coordinated) {
			final Coordinated c = (Coordinated) msg;
			// 获得事务的参数也就是需要转账的金额
			final int downCount = (int) c.getMessage();
			// 调用Coordinated.coordinate()方法, 将employee也加入到当前事务中, 这样这个事务中就有两个参与者了
			STMDemo.employee.tell(c.coordinate(downCount), getSelf());
			/**
			 * 定义了原子执行块作为这个事务的一部分, 在这个执行块中, 对公司账户余额进行调整, <br>
			 * 但是当汇款余额大于可用余额时, 就会抛出异常, 宣告失败.
			 */
			try {
				c.atomic(new Runnable() {
					@Override
					public void run() {
						if (count.get() < downCount) {
							throw new RuntimeException("less than " + downCount);
						}
						STM.increment(count, -downCount);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("GetCount".equals(msg)) {
			// 用于处理GetCount消息, 返回当前账户余额
			getSender().tell(count.get(), getSelf());
		} else {
			unhandled(msg);
		}
	}
}
