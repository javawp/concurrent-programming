package chapter7.particle;

import java.util.List;

public class Fitness {

	/**
	 * 返回了给定投资方案的适应度. 适应度也就是投资的收益, 我们自然应该更倾向于选择适应度更高的投资方案.
	 * 在这里适应度=√x1+√x2+√x3+√x4
	 * 
	 * @param x 每年的收益
	 * @return 方案的适应度, 即投资的收益
	 */
	public static double fitness(List<Double> x) {
		double sum = 0;
		for (int i = 1; i < x.size(); i++) {
			sum += Math.sqrt(x.get(i));
		}
		return sum;
	}
}
