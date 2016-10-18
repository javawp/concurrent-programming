package chapter7.supervisor;

import java.util.concurrent.TimeUnit;

import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;
import scala.concurrent.duration.Duration;

public class Supervisor extends UntypedActor {

	private static SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create(1, TimeUnit.MINUTES),
			new Function<Throwable, SupervisorStrategy.Directive>() {

				@Override
				public Directive apply(Throwable t) throws Exception {
					if (t instanceof ArithmeticException) {
						System.out.println("meet ArithmeticException, just resume");
						return SupervisorStrategy.resume();
					} else if (t instanceof NullPointerException) {
						System.out.println("meet NullPointerException, restart");
						return SupervisorStrategy.restart();
					} else if (t instanceof IllegalArgumentException) {
						return SupervisorStrategy.stop();
					} else {
						return SupervisorStrategy.escalate();
					}
				}
			});

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	};

	@Override
	public void onReceive(Object o) throws Exception {
		if (o instanceof Props) {
			getContext().actorOf((Props) o, "restartActor");
		} else {
			unhandled(o);
		}
	}
}
