package chapter5.pattern;

/**
 * 任务相关的数据
 */
public class PCData {

	/** 数据 */
	private final int intData;

	public PCData(int d) {
		intData = d;
	}

	public PCData(String d) {
		intData = Integer.valueOf(d);
	}

	public int getData() {
		return intData;
	}

	@Override
	public String toString() {
		return "data:" + intData;
	}
}
