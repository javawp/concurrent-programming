package chapter7.future;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import chapter7.future.AskMain.Msg;

public class Printer extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Integer) {
			System.out.println("Printer:" + msg);
		}
		if (msg == Msg.DONE) {
			log.info("Stop working");
			getSender().tell(Msg.CLOSE, getSelf());
			getContext().stop(getSelf());
		} else {
			unhandled(msg);
		}
	}

}
