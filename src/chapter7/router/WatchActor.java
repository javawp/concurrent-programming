package chapter7.router;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.BroadcastRoutingLogic;
import akka.routing.RandomRoutingLogic;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import akka.routing.SmallestMailboxRoutingLogic;
import chapter7.inbox.MyWorker;

public class WatchActor extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	public Router router;

	{
		List<Routee> routees = new ArrayList<Routee>();
		for (int i = 0; i < 5; i++) {
			ActorRef worker = getContext().actorOf(Props.create(MyWorker.class), "worker_" + i);
			getContext().watch(worker);
			routees.add(new ActorRefRoutee(worker));
		}
		router = new Router(new RoundRobinRoutingLogic(), routees); // 轮询消息发送
//		router = new Router(new BroadcastRoutingLogic(), routees); // 广播策略
//		router = new Router(new RandomRoutingLogic(), routees); // 随机投递策略
//		router = new Router(new SmallestMailboxRoutingLogic(), routees); // 空闲Actor优先投递策略
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof MyWorker.Msg) {
			router.route(msg, getSender());
		} else if (msg instanceof Terminated) {
			router = router.removeRoutee(((Terminated) msg).actor());
			System.out.println(((Terminated) msg).actor().path() + " is closed,rootees=" + router.routees().iterator().size());
			if (router.routees().iterator().size() == 0) {
				System.out.println("Close system");
				RouteMain.flag.send(false);
				getContext().system().shutdown();
			}
		} else {
			unhandled(msg);
		}
	}
}
