package chapter7.agent;

import scala.concurrent.Future;
import akka.actor.UntypedActor;
import akka.dispatch.Mapper;

public class CounterActor extends UntypedActor {

	// 定义了累加动作
	Mapper<Integer, Integer> addMapper = new Mapper<Integer, Integer>() {

		@Override
		public Integer apply(Integer i) {
			return i + 1;
		}

	};

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Integer) {
			for (int i = 0; i < 10000; i++) {
				// 我希望能够知道future何时结束
				Future<Integer> f = AgentDemo.counterAgent.alter(addMapper); // alert()指定累加动作
				AgentDemo.futures.add(f);
			}
			getContext().stop(getSelf());
		} else {
			unhandled(msg);
		}
	}

}
