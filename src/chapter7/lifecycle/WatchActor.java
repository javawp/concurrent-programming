package chapter7.lifecycle;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class WatchActor extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public WatchActor(ActorRef ref) {
		getContext().watch(ref);
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Terminated) {
			log.info(String.format("%s has terminated, shutting down system", ((Terminated) msg).getActor().path()));
			getContext().system().shutdown();
		} else {
			unhandled(msg);
		}
	}
}
