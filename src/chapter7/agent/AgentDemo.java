package chapter7.agent;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.agent.Agent;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;
import akka.dispatch.OnComplete;

import com.typesafe.config.ConfigFactory;

public class AgentDemo {

	public static Agent<Integer> counterAgent = Agent.create(0, ExecutionContexts.global());
	static ConcurrentLinkedQueue<Future<Integer>> futures = new ConcurrentLinkedQueue<>();

	public static void main(String[] args) throws TimeoutException {
		final ActorSystem system = ActorSystem.create("agentdemo", ConfigFactory.load("samplehello.conf"));
		ActorRef[] counter = new ActorRef[10];
		for (int i = 0; i < counter.length; i++) {
			counter[i] = system.actorOf(Props.create(CounterActor.class), "counter_" + i);
		}
		final Inbox inbox = Inbox.create(system);
		for (int i = 0; i < counter.length; i++) {
			inbox.send(counter[i], 1);
			inbox.watch(counter[i]);
		}

		int closeCount = 0;
		// 等待所有Actor全部结束
		while (true) {
			Object msg = inbox.receive(Duration.create(1, TimeUnit.SECONDS));
			if (msg instanceof Terminated) {
				closeCount++;
				if (closeCount == counter.length) {
					break;
				}
			} else {
				System.out.println(msg);
			}
		}

		// 等待所有的累加线程完成,因为他们都是异步的
		Futures.sequence(futures, system.dispatcher()).onComplete(new OnComplete<Iterable<Integer>>() {

			@Override
			public void onComplete(Throwable arg0, Iterable<Integer> arg1) throws Throwable {
				System.out.println("counterAgent=" + counterAgent.get());
				system.shutdown();
			}
		}, system.dispatcher());

	}
}
