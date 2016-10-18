package chapter7.particle;

/**
 * 个体最优解
 */
public final class PBestMsg {

	final PsoValue value;

	public PBestMsg(PsoValue value) {
		this.value = value;
	}

	public PsoValue getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
