package chapter7.stm;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.transactor.Coordinated;
import akka.util.Timeout;

import com.typesafe.config.ConfigFactory;

@SuppressWarnings("deprecation")
public class STMDemo {

	/** 公司账户 */
	public static ActorRef company = null;
	/** 雇员账户 */
	public static ActorRef employee = null;

	public static void main(String[] args) throws Exception {
		final ActorSystem system = ActorSystem.create("transactionDemo", ConfigFactory.load("samplehello.conf"));
		company = system.actorOf(Props.create(CompanyActor.class), "company");
		employee = system.actorOf(Props.create(EmployeeActor.class), "employee");

		Timeout timeout = new Timeout(1, TimeUnit.SECONDS);

		/**
		 * 尝试进行19次汇款,第一次汇款额度为1元,第二次为2元,依次类推,最后一笔汇款为19元
		 */
		for (int i = 1; i < 20; i++) {
			// 新建一个Coordinated协调者, 并且将这个协调者当做消息发送给company,
			// 当company收到这个协调者消息后, 自动成为这个事务的第一个成员
			company.tell(new Coordinated(i, timeout), ActorRef.noSender());
			Thread.sleep(200);
			// 询问公司账户
			Integer companyCount = (Integer) Await.result(ask(company, "GetCount", timeout), timeout.duration());
			// 询问雇员账户
			Integer employeeCount = (Integer) Await.result(ask(employee, "GetCount", timeout), timeout.duration());

			System.out.println("company count=" + companyCount);
			System.out.println("employee count=" + employeeCount);
			System.out.println("===================");
		}

	}
}
