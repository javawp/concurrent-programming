package chapter7.particle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 主要包含两个信息: 第一是表示投资规划的方案,即每一年分别需要投资多少钱<br>
 * 第二是这个投资方案的总收益
 */
public final class PsoValue {
	/** 这组投资方案的收益值 */
	final double value;
	/** 每一年分别需要投资多少钱 */
	final List<Double> x;

	public PsoValue(double value, List<Double> x) {
		this.value = value;
		List<Double> b = new ArrayList<Double>(5);
		b.addAll(x);
		this.x = Collections.unmodifiableList(b);
	}

	/** 这组投资方案的收益值 */
	public double getValue() {
		return value;
	}

	/** 每一年分别需要投资多少钱 */
	public List<Double> getX() {
		return x;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("value:").append(value).append("\n").append(x.toString());
		return sb.toString();
	}

}
