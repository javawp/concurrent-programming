package chapter7.helloworld;

import akka.actor.UntypedActor;

public class Greeter extends UntypedActor {

	public static enum Msg {
		GREET, DONE;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg == Msg.GREET) {
			System.out.println("Hello World!");
			getSender().tell(Msg.DONE, getSelf());
		} else {
			unhandled(msg);
		}
	}

}
