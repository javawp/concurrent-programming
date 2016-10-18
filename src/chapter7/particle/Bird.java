package chapter7.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Bird extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private PsoValue pBest = null;
	private PsoValue gBest = null;

	/** 表示粒子在各个维度上的速度(每一年的投资额认为是一个维度, 当前系统为4个维度, x1,x2,x3,x4) */
	private List<Double> velocity = new ArrayList<>(5);

	/** 每一年的投资额 */
	private List<Double> x = new ArrayList<>();

	/** 随机数 */
	private Random r = new Random();

	/**
	 * 当一个粒子被创建时, 我们需要初始化粒子的当前位置<br>
	 * 粒子的每一个位置都代表一个投资方案
	 */
	@Override
	public void preStart() throws Exception {
		for (int i = 0; i < 5; i++) {
			velocity.add(Double.NEGATIVE_INFINITY);
			x.add(Double.NEGATIVE_INFINITY);
		}

		// x1<=400
		x.set(1, (double) r.nextInt(401));

		// x2<=440-1.1*x1
		double max = 440 - 1.1 * x.get(1);
		if (max <= 0)
			max = 0;
		x.set(2, r.nextDouble() * max);

		// x3<=484-1.21*x1-1.1*x2
		max = 484 - 1.21 * x.get(1) - 1.1 * x.get(2);
		if (max <= 0)
			max = 0;
		x.set(3, r.nextDouble() * max);

		// x4=532.4-1.331*x1-1.21*x2-1.1*x3
		max = 532.4 - 1.331 * x.get(1) - 1.21 * x.get(2) - 1.1 * x.get(3);
		if (max <= 0)
			max = 0;
		x.set(4, r.nextDouble() * max);

		double newFit = Fitness.fitness(x);
		pBest = new PsoValue(newFit, x);
		PBestMsg pBestMsg = new PBestMsg(pBest);

		// 初始化的投资方案自然也就作为当前的个体最优, 并发送给Master
		ActorSelection selection = getContext().actorSelection("/user/masterbird");
		selection.tell(pBestMsg, getSelf());
	}

	/**
	 * 当Master计算出当前全局最优后, 会将全局最优发送给每一个粒子, <br>
	 * 粒子根据全局最优更新自己的运行速度, 并更新自己的速度以及当前位置
	 */
	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof GBestMsg) {
			// 接收全局最优解
			gBest = ((GBestMsg) msg).getValue();
			// 更新速度
			for (int i = 1; i < velocity.size(); i++) {
				updateVelocity(i); // 根据粒子群的标准公式更新自己的速度
			}
			// 更新位置
			for (int i = 1; i < x.size(); i++) {
				updateX(i); // 根据速度, 更新自己的位置
			}
			/**
			 * 由于当前问题是有约束的, 也就是说解空间并不是随意的, <br>
			 * 粒子很可能在更新位置后, 跑出了合理的范围之外, <br>
			 * 因此, 还有必要进行有效性检查
			 */
			validateX();
			// 计算新位置的适应度
			double newFit = Fitness.fitness(x);
			// 如果产生了新的个体最优, 就将其发送给Master
			if (newFit > pBest.getValue()) {
				pBest = new PsoValue(newFit, x);
				PBestMsg pBestMsg = new PBestMsg(pBest);
				getSender().tell(pBestMsg, getSelf());
			}
		}
	}

	private void validateX() {
		if (x.get(1) > 400) {
			x.set(1, (double) r.nextInt(401));
		}

		// x2
		double max = 440 - 1.1 * x.get(1);
		if (x.get(2) > max || x.get(2) < 0) {
			x.set(2, r.nextDouble() * max);
		}

		// x3
		max = 484 - 1.21 * x.get(1) - 1.1 * x.get(2);
		if (x.get(3) > max || x.get(3) < 0) {
			x.set(3, r.nextDouble() * max);
		}

		// x4
		max = 532.4 - 1.331 * x.get(1) - 1.21 * x.get(2) - 1.1 * x.get(3);
		if (x.get(4) > max || x.get(4) < 0) {
			x.set(4, r.nextDouble() * max);
		}
	}

	/**
	 * 更新位置
	 * @param i
	 * @return
	 */
	private double updateX(int i) {
		double newX = x.get(i) + velocity.get(i);
		x.set(i, newX);
		return newX;
	}

	/**
	 * 更新速度
	 * @param i
	 * @return
	 */
	private double updateVelocity(int i) {
		double v = Math.random() * velocity.get(i) + 2 * Math.random() * (pBest.getX().get(i) - x.get(i))
				+ 2 * Math.random() * (gBest.getX().get(i) - x.get(i));
		v = v > 0 ? Math.min(v, 5) : Math.max(v, -5);
		velocity.set(i, v);
		return v;
	}

}
