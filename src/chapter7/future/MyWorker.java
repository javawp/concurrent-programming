package chapter7.future;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import chapter7.future.AskMain.Msg;

public class MyWorker extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Integer) {
			int i = (Integer) msg;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			getSender().tell(i * i, getSelf());
		}
		if (msg == Msg.DONE) {
			log.info("Stop working");
		}
		if (msg == Msg.CLOSE) {
			log.info("I will shutdown");
			getSender().tell(Msg.CLOSE, getSelf());
			getContext().stop(getSelf());
		} else {
			unhandled(msg);
		}
	}
}
