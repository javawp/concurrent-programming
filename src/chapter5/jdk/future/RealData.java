package chapter5.jdk.future;

import java.util.concurrent.Callable;

public class RealData implements Callable<String> {

	private String para;

	public RealData(String para) {
		this.para = para;
	}

	@Override
	public String call() throws Exception {
		StringBuffer sf = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			sf.append(para);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return sf.toString();
	}

}
