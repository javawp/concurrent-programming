package chapter7.helloworld;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class HelloWorld extends UntypedActor {

	ActorRef greeter;

	@Override
	public void preStart() throws Exception {
		greeter = getContext().actorOf(Props.create(Greeter.class), "greeter");
		System.out.println("Greeter Actor Path:" + greeter.path());
		greeter.tell(Greeter.Msg.GREET, getSelf());
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg == Greeter.Msg.DONE) {
			getSender().tell(Greeter.Msg.GREET, getSelf());
			getContext().stop(getSelf());
		} else {
			unhandled(msg);
		}
	}

}
