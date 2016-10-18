package chapter5.future;

public class RealData implements Data {

	protected final String result;

	public RealData(String param) {
		// RealData的构造可能很慢,需要用户等待很久,这里使用sleep模拟
		StringBuffer sf = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			sf.append(param);
		}
		try {
			// 这里使用sleep, 代替一个很慢的操作过程
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		result = sf.toString();
	}

	@Override
	public String getResult() {
		return result;
	}

}
