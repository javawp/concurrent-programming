package chapter7.particle;

/**
 * 全局最优解
 */
public final class GBestMsg {

	final PsoValue value;

	public GBestMsg(PsoValue value) {
		this.value = value;
	}

	public PsoValue getValue() {
		return value;
	}

}
